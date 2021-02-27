package com.aptl.googleplayservicesdemo;

import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class GooglePlayServiceDemo extends Activity {

    private static final String TAG = "GooglePlayServiceDemo";
    private static final int ACCOUNT_REQUEST = 1001;
    private static final int TOKEN_REQUEST = 2002;
    private static final String PREFS_IS_AUTHORIZED = "isAuthorized";
    private static final String PREFS_SELECTED_ACCOUNT = "selectedAccount";
    private static final String PREFS_AUTH_TOKEN = "authToken";
    private SharedPreferences mPrefs;

    public static final String AUTH_SCOPE =
            "oauth2:https://www.googleapis.com/auth/drive.appdata " +
                    "https://www.googleapis.com/auth/userinfo.profile " +
                    "https://www.googleapis.com/auth/plus.me";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    }


    public void doConnectAccounts(MenuItem menuItem) {
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                new String[]{"com.google"}, false,
                "Pick one of your Google accounts to connect.",
                null, null, null);
        startActivityForResult(intent, ACCOUNT_REQUEST);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ACCOUNT_REQUEST) { // Account picked
            if(resultCode == RESULT_OK) {
                String accountName
                        = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                mPrefs.edit().putBoolean(PREFS_IS_AUTHORIZED, true)
                        .putString(PREFS_SELECTED_ACCOUNT, accountName).apply();
                invalidateOptionsMenu();
                new MyAuthTokenTask().execute(accountName);
            } else {
                Log.e(TAG, "No account picked...");
            }
        } else if(requestCode == TOKEN_REQUEST) { // Token requested
            if(resultCode == RESULT_OK) {
                // Try again...
                new MyAuthTokenTask().
                        execute(mPrefs.getString(PREFS_SELECTED_ACCOUNT, null));
            }
        }
    }


    class MyAuthTokenTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... accountName) {
            String authToken = null;
            try {
                authToken = GoogleAuthUtil.getToken(GooglePlayServiceDemo.this,
                        accountName[0], AUTH_SCOPE);
            } catch (IOException e) {
                Log.e(TAG, "Error getting auth token.", e);
            } catch (UserRecoverableAuthException e) {
                Log.d(TAG, "User recoverable error.");
                cancel(true);
                startActivityForResult(e.getIntent(), TOKEN_REQUEST);
            } catch (GoogleAuthException e) {
                Log.e(TAG, "Error getting auth token.", e);
            }
            return authToken;
        }

        @Override
        protected void onPostExecute(String result) {
            // Auth token acquired – start performing API requests
            if (result != null) {
                mPrefs.edit().putString(PREFS_AUTH_TOKEN, result).apply();
            }
        }
    }


    public Drive createDriveService(String accountName) {
        try {
            GoogleAccountCredential googleAccountCredential =
                    GoogleAccountCredential.usingOAuth2(this,
                            Arrays.asList(DriveScopes.DRIVE_APPDATA));
            googleAccountCredential.setSelectedAccountName(accountName);
            Log.d(TAG, "Token: " + googleAccountCredential.getToken());
            Drive.Builder builder =
                    new Drive.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(),
                            googleAccountCredential);
            return builder.build();
        } catch (IOException e) {
            Log.e(TAG, "Error", e);
        } catch (GoogleAuthException e) {
            Log.e(TAG, "Error", e);
        }
        return null;
    }

    class MyGoogleDriveAppDataTask extends AsyncTask<JSONObject, Void, Integer> {

        @Override
        protected Integer doInBackground(JSONObject... jsonObjects) {
            String accountName = mPrefs.getString(PREFS_SELECTED_ACCOUNT, null);
            Drive drive = createDriveService(accountName);
            int insertedFiles = 0;
            for (JSONObject jsonObject : jsonObjects) {
                String dataString = jsonObject.toString();
                String md5 = getMD5String(dataString.getBytes());
                File file = new File();
                file.setTitle(md5);
                String mimeType = "application/json";
                file.setMimeType(mimeType);
                file.setParents(Arrays.
                        asList(new ParentReference().setId("appdata")));

                ByteArrayContent content
                        = new ByteArrayContent(mimeType, dataString.getBytes());

                try {
                    drive.files().insert(file,content).execute();
                    insertedFiles++;
                } catch (IOException e) {
                    Log.e(TAG, "Failed to insert file with content "
                            + dataString, e);
                }
            }
            return insertedFiles;
        }

        private String getMD5String(byte[] data) {
            MessageDigest mdEnc = null;
            try {
                mdEnc = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                Log.e(TAG, "Error retrieving MD5 function!", e);
                return null;
            }
            mdEnc.update(data, 0, data.length);
            return new BigInteger(1, mdEnc.digest()).toString(16);
        }

    }

}

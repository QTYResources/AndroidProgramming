package com.aptl;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author Erik Hellman
 */
public class NetworkService extends IntentService
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = "NetworkService";
    private boolean mWifiOnly;

    public NetworkService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
        mWifiOnly = preferences.getBoolean(Constants.NETWORK_WIFI_ONLY,
                false);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)
                getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo
                = connectivityManager.getActiveNetworkInfo();
        int type = networkInfo.getType();
        if (mWifiOnly && type != ConnectivityManager.TYPE_WIFI) {
            Log.d(TAG, "We should only perform network I/O over WiFi.");
            return;
        }

        performNetworkOperation(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences,
                                          String key) {
        if (Constants.NETWORK_WIFI_ONLY.equals(key)) {
            mWifiOnly = preferences
                    .getBoolean(Constants.NETWORK_WIFI_ONLY, false);
            if(mWifiOnly) {
                cancelNetworkOperationIfNecessary();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void cancelNetworkOperationIfNecessary() {
        // TODO Cancel network operation if any active...
    }

    private void performNetworkOperation(Intent intent) {
        // TODO Perform actual network operation...
    }
}

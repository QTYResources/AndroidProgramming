package com.apptl.networking;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Erik Hellman
 */
public class HttpGetWithJson {
    private static final String TAG = "HttpGetWithJson";

    public JSONObject getJsonFromServer(URL url,
                                        long lastModifiedTimestamp) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setInstanceFollowRedirects(true);
            urlConnection.setIfModifiedSince(lastModifiedTimestamp);
            urlConnection.setUseCaches(true);
            urlConnection.connect();
            if (urlConnection.getResponseCode()
                    == HttpURLConnection.HTTP_OK) {
                if (urlConnection.getContentType().
                        contains("application/json")) {
                    int length = urlConnection.getContentLength();
                    InputStream inputStream = urlConnection.
                            getInputStream();
                    String jsonString = readStreamToString(inputStream, length);
                    return new JSONObject(jsonString);
                }
            } else {
                // TODO: Error handling...
            }
        } catch (IOException e) {
            Log.e(TAG, "Error perform HTTP call!", e);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON!", e);
        }
        return null;
    }

    private String readStreamToString(InputStream inputStream, int length)
            throws IOException {
        try {
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder(length);
            char[] buffer = new char[length];
            int charsRead;
            while ((charsRead = bufferedReader.read(buffer)) != -1) {
                stringBuilder.append(buffer, 0, charsRead);
            }
            return stringBuilder.toString();
        } finally {
            inputStream.close();
        }
    }

}

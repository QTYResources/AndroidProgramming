package com.apptl.networking;

import com.squareup.okhttp.OkHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Erik Hellman
 */
public class OkHttpDemo {
    private final OkHttpClient mOkHttpClient;

    public OkHttpDemo() {
        mOkHttpClient = new OkHttpClient();
    }

    public String okHttpDemo(URL url) throws IOException {
        HttpURLConnection urlConnection = mOkHttpClient.open(url);
        InputStream inputStream = null;
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            inputStream = urlConnection.getInputStream();
            return readStreamToString(inputStream,
                    urlConnection.getContentLength());
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

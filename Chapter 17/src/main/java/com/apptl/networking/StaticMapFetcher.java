package com.apptl.networking;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author Erik Hellman
 */
public class StaticMapFetcher {
    public static final String BASE_URL
            = "http://maps.googleapis.com/maps/api/staticmap";
    // TOOD Create this before release!
    public static final String API_KEY = null;
    public static final String UTF8 = "UTF-8";
    private static final String TAG = "StaticMapsFetcher";

    public static Bitmap fetchMapWithMarkers(String address,
                                             int width,
                                             int height,
                                             String maptype,
                                             List<String> markers) {
        HttpURLConnection urlConnection = null;
        try {
            StringBuilder queryString = new StringBuilder("?");

            if (address != null) {
                queryString.append("center=").
                        append(URLEncoder.encode(address, UTF8)).
                        append("&");
            }
            if (width > 0 && height > 0) {
                queryString.append("size=").
                        append(String.format("%dx%d", width, height)).
                        append("&");
            }
            if (maptype != null) {
                queryString.append("maptype=").
                        append(maptype).append("&");
            }
            if (markers != null) {
                for (String marker : markers) {
                    queryString.append("markers=").
                            append(URLEncoder.encode(marker, UTF8));
                }
            }
            if (API_KEY != null) {
                queryString.append("key=").append(API_KEY).append("&");
            }

            queryString.append("sensor=false");

            URL url = new URL(BASE_URL + queryString.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            if (urlConnection.getResponseCode()
                    == HttpURLConnection.HTTP_OK) {
                BufferedInputStream bufferedInputStream
                        = new BufferedInputStream(urlConnection.getInputStream());
                return BitmapFactory.decodeStream(bufferedInputStream);
            } else {
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error fetching map!", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }
}

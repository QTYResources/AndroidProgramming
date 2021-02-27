package com.apptl.chapter2.threads;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import com.apptl.chapter2.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RecurringOperationDemo extends Activity implements Handler.Callback {
    public static final int SYNC_DATA = 10;
    public static final int PING_SERVER = 20;
    private static final String PING_URL = "http://www.server.com/ping";
    private static final int SIXTY_SECONDS_IN_MILLISECONDS = 60 * 1000;
    private Handler mHandler;
    private boolean mPingServer = false;
    private int mFailedPings = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Start a new thread with a Looper
        HandlerThread handlerThread = new HandlerThread("BackgroundThread");
        handlerThread.start();
        // Create your new Handler
        mHandler = new Handler(handlerThread.getLooper(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPingServer = true;
        mHandler.sendEmptyMessage(PING_SERVER);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPingServer = false;
        mHandler.removeMessages(PING_SERVER);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shut down the Looper thread
        mHandler.getLooper().quit();
    }

    private void pingServer() {
        HttpURLConnection urlConnection = null;
        try {
            URL pingUrl = new URL(PING_URL);
            urlConnection = (HttpURLConnection) pingUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                mFailedPings = 0;
            } // Here you should also handle network failures...
        } catch (IOException e) {
            // Network error should be handled here as well...
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }


        if (mPingServer) {
            mHandler.sendEmptyMessageDelayed(PING_SERVER,
                    SIXTY_SECONDS_IN_MILLISECONDS);
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case PING_SERVER:
                pingServer();
                break;
            case SYNC_DATA:
                // TODO Sync data here..
                break;
        }
        return true;
    }
}

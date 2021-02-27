package com.aptl.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import java.util.concurrent.*;

/**
 * Example of a service performs multiple long-running operations
 * in the background using the <code>ExecutorService</code>.
 *
 * @author Erik Hellman
 */
public class MediaTranscoder extends Service {
    private static final int NOTIFICATION_ID = 1001;
    public static final String ACTION_TRANSCODE_MEDIA = "com.aptl.services.TRANSCODE_MEDIA";
    public static final String EXTRA_OUTPUT_TYPE = "outputType";
    private ExecutorService mExecutorService;
    private int mRunningJobs = 0;
    private final Object mLock = new Object();
    private boolean mIsForeground = false;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mExecutorService = Executors.newCachedThreadPool();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if(ACTION_TRANSCODE_MEDIA.equals(action)) {
            String outputType = intent.getStringExtra(EXTRA_OUTPUT_TYPE);

            // Start new job and increase the running job counter
            synchronized (mLock) {
                TranscodeRunnable transcodeRunnable = new TranscodeRunnable(intent.getData(), outputType);
                mExecutorService.execute(transcodeRunnable);
                mRunningJobs++;
                startForegroundIfNeeded();
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mExecutorService.shutdownNow();
        synchronized (mLock) {
            mRunningJobs = 0;
            stopForegroundIfAllDone();
        }
    }

    public void startForegroundIfNeeded() {
        if(!mIsForeground) {
            Notification notification = buildNotification();
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    private Notification buildNotification() {
        Notification notification = null;
        // TODO Build the notification here...
        return notification;
    }

    private void stopForegroundIfAllDone() {
        if(mRunningJobs == 0) {
            stopForeground(true);
            mIsForeground = false;
        }
    }

    private class TranscodeRunnable implements Runnable {
        private Uri mInData;
        private String mOutputType;

        private TranscodeRunnable(Uri inData, String outputType) {
            mInData = inData;
            mOutputType = outputType;
        }

        @Override
        public void run() {
            // TODO Perform transcoding here...

            // Decrease counter when we're done...
            synchronized (mLock) {
                mRunningJobs--;
                stopForegroundIfAllDone();
            }
        }
    }
}

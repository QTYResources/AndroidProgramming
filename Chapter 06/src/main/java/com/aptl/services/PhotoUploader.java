package com.aptl.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.IBinder;

/**
 * Example of photo-sharing Service using Intent.
 *
 * This class could easily be refactored to use IntentService instead...
 *
 * @author Erik Hellman
 */
public class PhotoUploader extends Service {
    public static final String ACTION_SHARE_PHOTO = "com.aptl.services.SHARE_PHOTO";
    public static final String EXTRA_PHOTO_BITMAP = "photoBitmap";
    public static final String EXTRA_PHOTO_TEXT = "photoText";
    private static final int NOTIFICATION_ID = 10;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) { // We might receive a null intent since we start with START_NOT_STICKY
            String action = intent.getAction();


            if (ACTION_SHARE_PHOTO.equals(action)) {
                // Build the notification to be shown
                Notification.Builder builder = new Notification.Builder(this);
                builder.setSmallIcon(R.drawable.ic_launcher);
                builder.setContentTitle(getString(R.string.notification_title));
                builder.setContentText(getString(R.string.notification_text));
                Notification notification = builder.build();
                // Start the service in the foreground
                startForeground(NOTIFICATION_ID, notification);

                // Upload the photo
                String photoText = intent.getStringExtra(EXTRA_PHOTO_TEXT);
                Bitmap photoBitmap = intent.getParcelableExtra(EXTRA_PHOTO_BITMAP);
                uploadPhotoWithText(photoBitmap, photoText);
            }
        }
        return START_NOT_STICKY;
    }

    private void uploadPhotoWithText(Bitmap photoBitmap, String photoText) {
        // TODO Upload the photo on a background thread...


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}

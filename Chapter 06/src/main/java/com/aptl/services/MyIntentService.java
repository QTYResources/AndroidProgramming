package com.aptl.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;

/**
 * @author Erik Hellman
 */
public class MyIntentService extends IntentService {
    private static final String NAME = "MyIntentService";
    public static final String ACTION_UPLOAD_PHOTO = "com.aptl.services.UPLOAD_PHOTO";
    public static final String EXTRA_PHOTO = "bitmapPhoto";
    public static final String ACTION_SEND_MESSAGE = "com.aptl.services.SEND_MESSAGE";
    public static final String EXTRA_MESSAGE = "messageText";
    public static final String EXTRA_RECIPIENT = "messageRecipient";

    public static final String BROADCAST_UPLOAD_COMPLETED = "com.aptl.services.UPLOAD_COMPLETED";

    public MyIntentService() {
        super(NAME);
        // We don't want intents redelivered in case we're shut down unexpectedly
        setIntentRedelivery(false);
    }

    /**
     * This method is executed on its own thread, one intent at a time...
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        if(ACTION_SEND_MESSAGE.equals(action)) {
            String messageText = intent.getStringExtra(EXTRA_MESSAGE);
            String messageRecipient = intent.getStringExtra(EXTRA_RECIPIENT);
            sendMessage(messageRecipient, messageText);
        } else if(ACTION_UPLOAD_PHOTO.equals(action)) {
            Bitmap photo = intent.getParcelableExtra(EXTRA_PHOTO);
            uploadPhoto(photo);
        }
    }

    private void sendMessage(String messageRecipient, String messageText) {
        // TODO Make network call...
    }

private void uploadPhoto(Bitmap photo) {
    // TODO Make network call...

    sendBroadcast(new Intent(BROADCAST_UPLOAD_COMPLETED));
}
}

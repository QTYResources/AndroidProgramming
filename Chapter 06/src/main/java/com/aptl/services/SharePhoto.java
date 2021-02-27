package com.aptl.services;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

/**
 * @author Erik Hellman
 */
public class SharePhoto extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_photo);
    }

    // Activity code left out for brevity...

    public void sharePhoto(Bitmap photo, String description) {
        Intent sharePhotoIntent =
                new Intent(PhotoUploader.ACTION_SHARE_PHOTO);
        sharePhotoIntent.
                putExtra(PhotoUploader.EXTRA_PHOTO_BITMAP, photo);
        sharePhotoIntent.
                putExtra(PhotoUploader.EXTRA_PHOTO_TEXT, description);
        startService(sharePhotoIntent);
    }
}
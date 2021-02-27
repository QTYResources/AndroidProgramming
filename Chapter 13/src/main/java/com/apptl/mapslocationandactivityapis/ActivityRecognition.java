package com.mycompany.myapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import com.apptl.mapslocationandactivityapis.MyGeofenceService;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.ActivityRecognitionClient;

public class ActivityRecognition extends Activity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {
    private static final long THIRTY_SECONDS = 1000 * 30;
    private static final long FIVE_SECONDS = 1000 * 5;
    private boolean mActivityRecognitionReady = false;
    private ActivityRecognitionClient mActivityRecognitionClient;
    private PendingIntent mPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityRecognitionReady = false;
        setContentView(R.layout.activity_recognition);
        mActivityRecognitionClient =
                new ActivityRecognitionClient(this, this, this);
        mActivityRecognitionClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mActivityRecognitionClient != null
                && mActivityRecognitionClient.isConnected()) {
            mActivityRecognitionClient.disconnect();
            mActivityRecognitionClient = null;
        }
    }

    public void doStartActivityRecognition(View view) {
        if (mActivityRecognitionReady) {
            Intent intent = new Intent(MyActivityService.
                    ACTION_NOTIFY_ACTIVITY_DETECTED);
            mPendingIntent =
                    PendingIntent.getService(this, 2001, intent, 0);
            mActivityRecognitionClient.
                    requestActivityUpdates(FIVE_SECONDS,
                            mPendingIntent);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mActivityRecognitionReady = true;
        findViewById(R.id.start_activity_recognition_btn).setEnabled(false);
    }

    @Override
    public void onDisconnected() {
        mActivityRecognitionReady = false;
        findViewById(R.id.start_activity_recognition_btn).setEnabled(true);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mActivityRecognitionReady = false;
        findViewById(R.id.start_activity_recognition_btn).setEnabled(true);
        // Error handling...
    }
}

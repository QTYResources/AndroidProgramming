package com.apptl.mapslocationandactivityapis;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import java.util.List;

/**
 * @author Erik Hellman
 */
public class MyLocationService extends IntentService {
    public static final String ACTION_NOTIFY_ACTIVITY_DETECTED = "activityDetected";
    public static final String ACTION_NOTIFY_ENTERED_GEOFENCE = "enteredGeofence";
    private static final String TAG = "MyLocationService";
    private int mLastDetectedActivity = DetectedActivity.UNKNOWN;

    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        if (ACTION_NOTIFY_ACTIVITY_DETECTED.equals(action)){
            if (ActivityRecognitionResult.hasResult(intent)) {
                ActivityRecognitionResult result = ActivityRecognitionResult.
                        extractResult(intent);
                DetectedActivity detectedActivity =
                        result.getMostProbableActivity();
                Log.d(TAG, "Detected activity: " + detectedActivity);
                if(detectedActivity.getType() != mLastDetectedActivity) {
                    mLastDetectedActivity = detectedActivity.getType();
                    showNotification(detectedActivity);
                }
            }
        } else if (ACTION_NOTIFY_ENTERED_GEOFENCE.equals(action)) {
            if (LocationClient.hasError(intent)) {
                // TODO: Error handling...
            } else {
                List<Geofence> geofences =
                        LocationClient.getTriggeringGeofences(intent);
                for (Geofence geofence : geofences) {
                    showNotification(geofence);
                }
            }
        }
    }

    private void showNotification(Geofence geofence) {
        // TODO Build notification for geofence...
    }

    private void showNotification(DetectedActivity detectedActivity) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Activity change!");
        builder.setContentText("Activity changed to: "
                + getActivityName(detectedActivity.getType()));
        builder.setSmallIcon(R.drawable.ic_launcher);
        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(2001, builder.build());
    }

    private String getActivityName(int type) {
        switch (type) {
            case DetectedActivity.IN_VEHICLE:
                return "In vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "On bicycle";
            case DetectedActivity.ON_FOOT:
                return "On foot";
            case DetectedActivity.STILL:
                return "Still";
        }
        return "Unknown";
    }
}

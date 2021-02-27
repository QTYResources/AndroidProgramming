package com.apptl.mapslocationandactivityapis;

import android.app.IntentService;
import android.content.Intent;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import java.util.List;

/**
 * @author Erik Hellman
 */
public class MyGeofenceService extends IntentService {
    public static final String TAG = "MyGeofenceService";
    public static final String ACTION_NOTIFY_ENTERED_GEOFENCE =
            "com.aptl.locationandmapsdemo.NOTIFY_ENTER_GEOFENCE";
    private int mNextNotificationId = 1;

    public MyGeofenceService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

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

    private void showNotification(Geofence geofence) {
        // TODO: Show notification to user
    }

}

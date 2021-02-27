package com.apptl.mapslocationandactivityapis;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends Activity {

    // Yes, this is where I live. Third floor.
    // Why don't you drop by for a coffee and show me some cool Android code? :)
    private static final LatLng MY_HOME = new LatLng(55.596268, 12.981482);
    private static final float TWENTYFIVE_METERS = 25f;
    private static final long FIVE_MIUTES = 5 * 60 * 1000;
    private static final long ONE_WEEK = 7 * 24 * 60 * 60 * 1000;
    private MapFragment mMapFragment;
    private MyLocationCallbacks mLocationCallbacks;
    private LocationClient mLocationClient;
    private GoogleMap mMap;
    // TODO Store this list somewhere and recreate in onCreate()
    private List<Circle> mGeoReminders = Collections.synchronizedList(new LinkedList<Circle>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mMapFragment = (MapFragment) getFragmentManager().
                findFragmentById(R.id.map);
        GoogleMap map = mMapFragment.getMap();
        mMap = map;
        map.setTrafficEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(MY_HOME).
                zoom(17).
                bearing(90).
                tilt(30).build();
        map.animateCamera(CameraUpdateFactory.
                newCameraPosition(cameraPosition));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        map.setIndoorEnabled(true);

        mLocationCallbacks = new MyLocationCallbacks();
        mLocationClient = new LocationClient(this,
                mLocationCallbacks, mLocationCallbacks);
        mLocationClient.connect();
    }

    public void onMapLongClick(LatLng latLng) {
        Geofence.Builder builder = new Geofence.Builder();
        builder.setCircularRegion(latLng.latitude,
                latLng.longitude,
                TWENTYFIVE_METERS);
        builder.setExpirationDuration(ONE_WEEK);
        // For now, use the lat/long as ID.
        String geofenceRequestId = latLng.latitude + ","
                + latLng.longitude;
        builder.setRequestId(geofenceRequestId);
        // Only interested in entering the geofence for now...
        builder.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER);
        List<Geofence> geofences = new ArrayList<Geofence>();
        geofences.add(builder.build());
        Intent intent = new Intent(MyGeofenceService.
                ACTION_NOTIFY_ENTERED_GEOFENCE);
        PendingIntent pendingIntent = PendingIntent.getService(this,
                1001,
                intent,
                0);
        mLocationClient.addGeofences(geofences, pendingIntent,
                new LocationClient.OnAddGeofencesResultListener() {
                    @Override
                    public void onAddGeofencesResult(int status,
                                                     String[] strings) {
                        if (status == LocationStatusCodes.SUCCESS) {
                            double latitude = Double.parseDouble(strings[0]);
                            double longitude = Double.parseDouble(strings[0]);
                            LatLng latLng = new LatLng(latitude, longitude);
                            Circle circle = mMap.addCircle(new CircleOptions().
                                    fillColor(Color.GREEN).
                                    strokeWidth(5).
                                    strokeColor(Color.BLACK).
                                    center(latLng).
                                    visible(true).
                                    radius(TWENTYFIVE_METERS));
                            mGeoReminders.add(circle);
                        } else {
                            // TODO: Error handling...
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null && mLocationClient.isConnected()) {
            mLocationClient.disconnect();
            mLocationClient = null;
        }
    }

    private class MyLocationCallbacks
            implements GooglePlayServicesClient.ConnectionCallbacks,
            GooglePlayServicesClient.OnConnectionFailedListener,
            LocationListener {


        @Override
        public void onConnected(Bundle bundle) {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setSmallestDisplacement(TWENTYFIVE_METERS);
            locationRequest.setExpirationDuration(FIVE_MIUTES);
            mLocationClient.requestLocationUpdates(locationRequest, this);
        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            // TODO Error handling...
        }

        @Override
        public void onLocationChanged(Location location) {
            LatLng latLng = new LatLng(location.getLatitude(),
                    location.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(17)
                    .bearing(90)
                    .tilt(30)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.
                    newCameraPosition(cameraPosition));
        }
    }

}

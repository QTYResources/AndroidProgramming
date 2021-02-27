package com.apptl.communicatingwithremotedevices;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.wifi.WifiConfiguration;
import android.os.HandlerThread;
import android.os.IBinder;

/**
 * @author Erik Hellman
 */
public class RestletService extends Service
        implements LocationListener {
    public static final String ACTION_START_SERVER
            = "com.aptl.myrestletdemo.START_SERVER";
    public static final String ACTION_STOP_SERVER
            = "com.aptl.myrestletdemo.STOP_SERVER";
    private static final int SERVER_PORT = 8081;
    public static final long ONE_MINUTE = 1000 * 60;
    public static final float MIN_DISTANCE = 50;
    private static final String TAG = "RestletService";
    private HandlerThread mLocationThread;
    private Location mLocation;
    private Component mServer;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if(ACTION_START_SERVER.equals(action)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    initRestlet();
                }
            }).start();
        } else if(ACTION_STOP_SERVER.equals(action)) {
            if (mServer != null) {
                shutdownRestlet();
            }
        }

        return START_REDELIVER_INTENT;
    }

    private void initRestlet() {
        try {
            mLocationThread = new HandlerThread("LocationUpdates");
            mLocationThread.start();
            LocationManager locationManager =
                    (LocationManager) getSystemService(LOCATION_SERVICE);
            mLocation = locationManager.
                    getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setCostAllowed(true);
            criteria.setSpeedRequired(true);
            criteria.setAltitudeRequired(true);
            locationManager.requestLocationUpdates(ONE_MINUTE,
                    MIN_DISTANCE,
                    criteria,
                    this,
                    mLocationThread.getLooper());

            mServer =  new Component();
            mServer.getServers().add(WifiConfiguration.Protocol.HTTP, SERVER_PORT);
            Router router = new Router(mServer.getContext()
                    .createChildContext());
            router.attachDefault(new Restlet() {
                @Override
                public void handle(Request request,
                                   Response response) {
                    response.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
                }
            });
            router.attach("/location", new LocationRestlet());
            mServer.getDefaultHost().attach(router);
            mServer.start();
        } catch (Exception e) {
            Log.e(TAG, "Error starting server.", e);
        }
    }

    private void shutdownRestlet() {
        if (mServer != null) {
            try {
                mServer.stop();
            } catch (Exception e) {
                Log.e(TAG, "Error stopping server.", e);
            }
        }

        LocationManager locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.removeUpdates(this);
        if (mLocationThread != null) {
            mLocationThread.getLooper().quit();
            mLocationThread = null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public class LocationRestlet extends Restlet {
        @Override
        public void handle(Request request, Response response) {
            if(Method.GET.equals(request.getMethod())) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("latitude", mLocation.getLatitude());
                    jsonObject.put("longitude", mLocation.getLongitude());
                    jsonObject.put("time", mLocation.getTime());
                    jsonObject.put("altitude", mLocation.getAltitude());
                    jsonObject.put("speed", mLocation.getSpeed());
                    response.setStatus(Status.SUCCESS_OK);
                    response.setEntity(jsonObject.toString(),
                            MediaType.APPLICATION_JSON);
                } catch (JSONException e) {
                    response.setStatus(Status.SERVER_ERROR_INTERNAL);
                }
            } else {
                response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            }
        }
    }
}

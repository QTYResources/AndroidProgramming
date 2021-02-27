package com.aptl.chapter8;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

/**
 * @author Erik Hellman
 */
public class SensorDemo extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Handler mSensorHandler;

    @Override
    protected void onResume() {
        super.onResume();
        HandlerThread handlerThread = new HandlerThread("SensorListenerThread");
        handlerThread.start();
        mSensorHandler = new Handler(handlerThread.getLooper());
        if (mSensorManager == null) {
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        }
        Sensor gyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_GAME, mSensorHandler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
        if(mSensorHandler != null) {
            mSensorHandler.getLooper().quit();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Note: This is executed on our SensorListenerThread, not the main thread
        // TODO: Process the sensor data and release the event as quickly as possible
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // This method can be used to detect when calibration of a sensor is needed
    }
}
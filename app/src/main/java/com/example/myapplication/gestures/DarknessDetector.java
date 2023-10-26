package com.example.myapplication.gestures;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public class DarknessDetector implements SensorEventListener {

    private static final float LIGHT_THRESHOLD = 10.0f; // Sensitivity for darkness detection in lux

    private OnDarknessListener mListener;

    public interface OnDarknessListener {
        void onDarknessDetected();
    }

    public DarknessDetector(OnDarknessListener listener) {
        mListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lightLevel = event.values[0];

            // Detect darkness (when light level is below the threshold)
            if (lightLevel < LIGHT_THRESHOLD) {
                mListener.onDarknessDetected();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing here
    }
}

package com.example.myapplication.gestures;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

public class TiltDetector implements SensorEventListener {

    private static final float TILT_THRESHOLD = 8f; // Sensitivity for tilt detection in degrees
    private static final float CENTRE_THRESHOLD = 3f; // Device returned to centre
    private static final long RESET_DELAY = 5000; // ms allowed for user to tilt both ways
    private boolean tiltedLeft = false;
    private boolean tiltedRight = false;
    private boolean tiltedCentre = false;

    private OnTiltListener mListener;

    public interface OnTiltListener {
        void onTiltedBothWays();

        void initialTilt();
    }

    public TiltDetector(OnTiltListener listener) {
        mListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0]; // X-axis tilt

            // Detect left tilt
            if (x > TILT_THRESHOLD) {
                if (tiltedRight && tiltedCentre){
                    mListener.onTiltedBothWays();
                } else {
                    mListener.initialTilt();
                    tiltedLeft = true;
                    tiltedCentre = false;
                    tiltedRight = false;

                    // Reset tiltedLeft to false after 500 milliseconds
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tiltedLeft = false;
                        }
                    }, RESET_DELAY);
                }
            }

            // Detect right tilt
            else if (x < -TILT_THRESHOLD) {
                if (tiltedLeft && tiltedCentre){
                    mListener.onTiltedBothWays();
                } else {
                    mListener.initialTilt();
                    tiltedRight = true;
                    tiltedCentre = false;
                    tiltedLeft = false;

                    // Reset tiltedLeft to false after 500 milliseconds
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tiltedRight = false;
                        }
                    }, RESET_DELAY);
                }
            }

            if (x < CENTRE_THRESHOLD && x > -CENTRE_THRESHOLD){
                tiltedCentre = true;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing here
    }
}

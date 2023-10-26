package com.example.myapplication;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.gestures.ShakeDetector;
import com.example.myapplication.gestures.TiltDetector;

public class CheckIn extends AppCompatActivity implements
        ShakeDetector.OnShakeListener, TiltDetector.OnTiltListener {

    private ShakeDetector shakeDetector;
    private TiltDetector tiltDetector;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        shakeDetector = new ShakeDetector(this);
        tiltDetector = new TiltDetector(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(tiltDetector, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(shakeDetector);
    }

    @Override
    public void onShake() {
        phoneShaken();
    }

    @Override
    public void initialTilt(){
        TextView output = findViewById(R.id.gestureOutput);
        output.setText("Tilt 1");
    }

    @Override
    public void onTiltedBothWays() {
        TextView output = findViewById(R.id.gestureOutput);
        output.setText("Phone tilted");
    }

    private void phoneShaken(){
//        TextView output = findViewById(R.id.gestureOutput);
//        output.setText("Phone shaken");
    }

    public void returnText(){
        TextView output = findViewById(R.id.gestureOutput);
        output.setText("Output");
    }
}

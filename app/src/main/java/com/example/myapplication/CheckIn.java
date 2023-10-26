package com.example.myapplication;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.gestures.DarknessDetector;
import com.example.myapplication.gestures.ShakeDetector;
import com.example.myapplication.gestures.TiltDetector;

public class CheckIn extends AppCompatActivity implements
        ShakeDetector.OnShakeListener,
        TiltDetector.OnTiltListener,
        DarknessDetector.OnDarknessListener {

    private ShakeDetector shakeDetector;
    private TiltDetector tiltDetector;
    private DarknessDetector darknessDetector;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        shakeDetector = new ShakeDetector(this);
        tiltDetector = new TiltDetector(this);
        darknessDetector = new DarknessDetector(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (accelerometer != null) {
            sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(tiltDetector, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(darknessDetector, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(shakeDetector);
    }

    //TODO Link this to other activity
    private void checkIn(){
        TextView output = findViewById(R.id.gestureOutput);
        output.setText("Checked In");
    }

    @Override
    public void onShake() {
        //checkIn();
    }

    @Override
    public void initialTilt(){
        // TODO Give haptic feedback that user is halfway done with gesture
    }

    @Override
    public void onTiltedBothWays() {
        //checkIn();
    }

    @Override
    public void onDarknessDetected() {
        checkIn();
    }
}

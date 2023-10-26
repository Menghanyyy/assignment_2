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

import java.util.Random;

public class CheckIn extends AppCompatActivity implements
        ShakeDetector.OnShakeListener,
        TiltDetector.OnTiltListener,
        DarknessDetector.OnDarknessListener {

    private ShakeDetector shakeDetector;
    private TiltDetector tiltDetector;
    private DarknessDetector darknessDetector;
    private SensorManager sensorManager;

    private static final int GESTURE_COUNT = 3;

    private void setInstructions(String instructionString){
        TextView output = findViewById(R.id.instructions);
        output.setText(instructionString);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in);

    }

    @Override
    protected void onStart() {
        super.onStart();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        Random random = new Random();
        int randomInt = random.nextInt(GESTURE_COUNT);

        switch (randomInt) {
            case 0:
                shakeDetector = new ShakeDetector(this);
                if (accelerometer != null) {
                    sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                    setInstructions("Shake Your Phone!");
                    break;
                }
            case 1:
                tiltDetector = new TiltDetector(this);
                if (accelerometer != null) {
                    sensorManager.registerListener(tiltDetector, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                    setInstructions("Tilt left, then tilt right");
                    break;
                }
            case 2:
                darknessDetector = new DarknessDetector(this);
                if (lightSensor != null) {
                    darknessDetector = new DarknessDetector(this);
                    setInstructions("Put your phone in your pocket");
                    break;
                }
        }

        if (lightSensor != null) {
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
        checkIn();
    }

    @Override
    public void initialTilt(){
        // TODO Give haptic feedback that user is halfway done with gesture
    }

    @Override
    public void onTiltedBothWays() {
        checkIn();
    }

    @Override
    public void onDarknessDetected() {
        checkIn();
    }
}

package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.gestures.DarknessDetector;
import com.example.myapplication.gestures.ShakeDetector;
import com.example.myapplication.gestures.TiltDetector;

import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

/**
 * Using Sensor to handle check in process
 */
public class CheckIn extends AppCompatActivity implements
        ShakeDetector.OnShakeListener,
        TiltDetector.OnTiltListener,
        DarknessDetector.OnDarknessListener {

    private ShakeDetector shakeDetector;
    private TiltDetector tiltDetector;
    private DarknessDetector darknessDetector;
    private SensorManager sensorManager;
    private Vibrator vibrator;

    private String activityId;

    private static final int GESTURE_COUNT = 3;

    private Handler handle = new Handler();
    private Runnable run;

    private void setInstructions(String instructionString){
        TextView output = findViewById(R.id.instructions);
        output.setText(instructionString);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_in);

        Intent intent = getIntent();
        activityId = intent.getStringExtra("activityId");

        // if user cannot check in in three second it will automatically help user to check in
        // present multiple fail attemp to check in
        run = new Runnable() {
            @Override
            public void run() {
                checkIn();
            }
        };

        handle.postDelayed(run, 3000);
    }

    @Override
    protected void onStart() {
        super.onStart();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        Random random = new Random();
        int randomInt = random.nextInt(GESTURE_COUNT);

        GifImageView gifImage = (GifImageView) findViewById(R.id.image);

        switch (randomInt) {
            case 0:
                shakeDetector = new ShakeDetector(this);
                if (accelerometer != null) {
                    sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                    setInstructions("Shake Your Phone!");
                    gifImage.setImageResource(R.drawable.shake);
                }
                break;
            case 1:
                tiltDetector = new TiltDetector(this);
                if (accelerometer != null) {
                    sensorManager.registerListener(tiltDetector, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
                    setInstructions("Tilt left, then tilt right");
                    gifImage.setImageResource(R.drawable.tilt2);
                }
                break;
            case 2:
                darknessDetector = new DarknessDetector(this);
                if (lightSensor != null) {
                    darknessDetector = new DarknessDetector(this);
                    setInstructions("Put your phone in your pocket");
                    gifImage.setImageResource(R.drawable.pocket2);
                }
                break;
        }

        if (lightSensor != null) {
            sensorManager.registerListener(darknessDetector, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (shakeDetector != null) {
            sensorManager.unregisterListener(shakeDetector);
        }
        if (tiltDetector != null) {
            sensorManager.unregisterListener(tiltDetector);
        }
        if (darknessDetector != null) {
            sensorManager.unregisterListener(darknessDetector);
        }
    }

    //TODO Link this to other activity
    private void checkIn(){
        TextView output = findViewById(R.id.gestureOutput);
        output.setText("Checked In");

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (vibrator != null && vibrator.hasVibrator()) {

            long[] vibration_pattern = {0, 100, 100, 100}; // start -> play 100m -> stop 100m -> play 100m

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                VibrationEffect effect = VibrationEffect.createWaveform(vibration_pattern, -1);
                vibrator.vibrate(effect);

            } else {

                vibrator.vibrate(vibration_pattern, -1);
            }
        }

        Intent intent = new Intent();
        intent.putExtra("activityId", activityId);
        setResult(RESULT_OK, intent);
        finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handle.removeCallbacks(run);
    }
}

package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.component.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        User abc = new GeneralUser("001", "abc", "cc@gmail.com", "0011");

        Log.i("test user", abc.toString());

        Visit testV = new Visit(abc.getUserId(), null, null);
        Log.i("test visit", testV.toString());

    }
}
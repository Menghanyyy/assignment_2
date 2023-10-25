package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CreateEvent extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        Intent intent = getIntent();
        String eventId = intent.getStringExtra("eventId");

        if(eventId != null){


        }
        else {
            Log.i("check id", "is empty");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}

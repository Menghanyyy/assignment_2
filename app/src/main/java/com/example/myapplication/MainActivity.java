package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.component.*;
import com.example.myapplication.database.DatabaseCallback;
import com.example.myapplication.database.DatabaseManager;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView username = (TextView)findViewById(R.id.userName);
        TextView password = (TextView)findViewById(R.id.password);

        MaterialButton loginBt= (MaterialButton)findViewById(R.id.signInButton);
        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().equals("admin") &&
                        password.getText().toString().equals("123")){
                    Toast.makeText(MainActivity.this, "Successful Login", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this, "Failed Login", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        User abc = new GeneralUser("001", "abc", "cc@gmail.com", "0011");

        Log.i("test user", abc.toString());

        Visit testV = new Visit(abc.getUserId(), null, null, null);
        Log.i("test visit", testV.toString());

        // Testing Database Connection
        DatabaseManager databaseManager = new DatabaseManager(this);
        databaseManager.visitCountForUser(1, new DatabaseCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                Log.i("Visit Count for user 1", String.valueOf(result));
            }

            @Override
            public void onError(String error) {
                Log.println(Log.ASSERT, "Error getting count", error);
            }
        });

        databaseManager.getAllEvents(new DatabaseCallback<ArrayList<Event>>() {
            @Override
            public void onSuccess(ArrayList<Event> result) {
                Log.i("Arraylist Size", String.valueOf(result.size()));

                Log.i("Got Arraylist: ", result.get(0).getEventId());
                Log.i("Got Arraylist: ", result.get(0).getEventName());
            }

            @Override
            public void onError(String error) {
                Log.println(Log.ASSERT, "Error getting count", error);
            }
        });
    }
}
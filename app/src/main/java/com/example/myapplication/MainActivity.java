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

        // TESTING
        DatabaseManager databaseManager = new DatabaseManager(this);
        databaseManager.dummy(new DatabaseCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                Log.println(Log.ASSERT, "Good result", String.valueOf(result));
            }

            @Override
            public void onError(String error) {
                Log.println(Log.ASSERT, "Good result", error);
            }
        });
//        Log.println(Log.ASSERT, "Test DBManager", Integer.toString(databaseManager.dummy()));
//        Log.println(Log.ASSERT, "Get All Events", databaseManager.getAllEvents().toString());
    }
}
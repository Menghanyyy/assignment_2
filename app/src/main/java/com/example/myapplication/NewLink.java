package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.database.DatabaseCallback;
import com.example.myapplication.database.DatabaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class NewLink extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_link);

        DatabaseManager databaseManager = new DatabaseManager(this);

        MaterialButton insertBt= (MaterialButton)findViewById(R.id.insertButton);

        TextView linkField = findViewById(R.id.link);

        insertBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String link = linkField.getText().toString();

                databaseManager.joinEvent(MyApplication.getCurrentUser().getUserId(), link, new DatabaseCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.i("join event success", result.toString());

                        Intent i = new Intent(NewLink.this, Home.class);
                        startActivity(i);
                    }

                    @Override
                    public void onError(String error) {
                        Log.println(Log.ASSERT, "Error joining", error);
                    }
                });


            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.join);
        bottomNavigationView.setOnItemSelectedListener(
                new BottomNavigationView.OnItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.events:
                                MyApplication.getInstance().eventPagerTabIndex = 0;
                                finish();
                                break;
                            case R.id.profile:
                                MyApplication.getInstance().eventPagerTabIndex = 2;
                                finish();
                                break;
                        }
                        return true;
                    }
                }
        );
    }
}
package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EventPageActivity extends AppCompatActivity {
    TextView tv_map;
    ImageView iv_back;
    BottomNavigationView bottomNavigationView ;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_info);
        tv_map = findViewById(R.id.tv_map);
        iv_back = findViewById(R.id.iv_back);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });
        tv_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.events:
                                MyApplication.getInstance().eventPagerTabIndex = 0;
                                finish();


                                break;
                            case R.id.map:
                                MyApplication.getInstance().eventPagerTabIndex = 1;
                                finish();
                                break;
                            case R.id.profile:
                                MyApplication.getInstance().eventPagerTabIndex = 2;
                                finish();

                                break;

                        }
                        return false;
                    }
                });
    }
}
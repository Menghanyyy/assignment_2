package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.component.Event;
import com.example.myapplication.database.DatabaseCallback;
import com.example.myapplication.database.DatabaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EventPageActivity extends AppCompatActivity {

    TextView tv_map;
    ImageView iv_back;
    BottomNavigationView bottomNavigationView;

    DatabaseManager databaseManager;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_info);
        Intent intent = getIntent();
        String eventId = intent.getStringExtra("eventId");

        databaseManager = new DatabaseManager(this);

        databaseManager.getEventByID(Integer.parseInt(eventId), new DatabaseCallback<Event>() {
            @Override
            public void onSuccess(Event result) {

                ImageView image = findViewById(R.id.eventImage);
                TextView title = findViewById(R.id.eventTitle);
                TextView desc = findViewById(R.id.eventDescription);
                TextView location = findViewById(R.id.eventLocation);

                TextView organisation = findViewById(R.id.eventOrganisation);

                image.setImageResource(R.mipmap.aaaa);

                title.setText(result.getEventName());
                desc.setText("Scientific evidence shows that Indigenous sdsdsdsd mkmkmkeqweqeqwemkmkmmkmkmkmkmkmmkmkmjnjnjnjnjnjnjnjnnjnjnjnk people understand and manage their environment better than anyone else: 80% of Earth’s biodiversity can be found in Indigenous territories. The best way knnknknknknknknknknknknknknknknknknknknknknknknknknknknkto protect biodiversity is therefore to.");
                location.setText("Melbourne");
                organisation.setText(result.getOrganisationName());

            }

            @Override
            public void onError(String error) {
                Log.println(Log.ASSERT, "Error Retrieving json", error);
            }
        });


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


        bottomNavigationView.setOnItemSelectedListener(
                new BottomNavigationView.OnItemSelectedListener() {
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
                }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView desc = findViewById(R.id.eventDescription);
        TextView textExpend = findViewById(R.id.eventDescriptionExpend);

        textExpend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (desc.getMaxLines() == 5) {
                    // If currently showing limited lines, show all
                    desc.setMaxLines(Integer.MAX_VALUE);
                    textExpend.setText("Read Less");
                } else {
                    // If currently showing all, revert back to limited lines
                    desc.setMaxLines(5);
                    textExpend.setText("Read More");
                }
            }
        });
    }
}
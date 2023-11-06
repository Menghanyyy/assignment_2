package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.component.Event;
import com.example.myapplication.database.DatabaseCallback;
import com.example.myapplication.database.DatabaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.Point;

import java.lang.reflect.Type;
import java.util.List;

public class EventPageActivity extends AppCompatActivity {

    TextView tv_map;
    ImageView iv_back;
    BottomNavigationView bottomNavigationView;

    DatabaseManager databaseManager;
    TextView tv_gotomap;
    TextView invite;
    TextView link_view;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_info);

        Intent intent = getIntent();
        String eventId = intent.getStringExtra("eventId");
        String pointsJson = intent.getStringExtra("bbox");

        databaseManager = new DatabaseManager(this);

        databaseManager.getEventByID(Integer.parseInt(eventId), new DatabaseCallback<Event>() {
            @Override
            public void onSuccess(Event result) {

                ImageView image = findViewById(R.id.eventImage);
                TextView title = findViewById(R.id.eventTitle);
                TextView desc = findViewById(R.id.eventDescription);
                TextView location = findViewById(R.id.eventLocation);

                TextView organisation = findViewById(R.id.eventOrganisation);

                byte[] decodedImageBytes = Base64.decode(result.getImage(), Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.length);
                image.setImageBitmap(decodedBitmap);


                title.setText(result.getEventName());
                desc.setText(result.getDescription());
                location.setText("Melbourne");
                organisation.setText(result.getOrganisationName());
            }

            @Override
            public void onError(String error) {
                Log.println(Log.ASSERT, "Error Retrieving json", error);
            }
        });

        invite = findViewById(R.id.invite);
        link_view = findViewById(R.id.invite_link);
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make the new TextView visible
                link_view.setVisibility(View.VISIBLE);
                link_view.setText("Loading ...");

                databaseManager.getEventLinkByID(Integer.parseInt(eventId), new DatabaseCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        link_view.setText(result);
                    }

                    @Override
                    public void onError(String error) {
                        Log.println(Log.ASSERT, "Error getting Link:", error);
                    }
                });
            }
        });

        tv_gotomap = findViewById(R.id.tv_gotomap);
        tv_gotomap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent  mapIntent = new Intent(EventPageActivity.this,MapActivity.class);
                mapIntent.putExtra("eventId", eventId);
                mapIntent.putExtra("bbox", pointsJson);

                startActivity(mapIntent);

            }
        });



        ImageView iv_edit = findViewById(R.id.eventEdit);
        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eventInfo = new Intent(EventPageActivity.this, CreateEditEvent.class);//src to tagactivity
                eventInfo.putExtra("eventId", eventId);
                startActivity(eventInfo);
            }
        });


        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
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
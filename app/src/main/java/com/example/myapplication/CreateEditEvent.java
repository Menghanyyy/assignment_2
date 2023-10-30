package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.component.*;
import com.example.myapplication.database.DatabaseCallback;
import com.example.myapplication.database.DatabaseManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;


public class CreateEditEvent extends AppCompatActivity {


    // Views for Create, Edit, and Activity
    View create_event_layout, edit_event_layout, activity_layout;
    TextView create_event_name, create_event_description, create_event_organisation, create_event_address;
    TextView edit_event_name, edit_event_description, edit_event_organisation, edit_event_address;
    TextView event_activity_name;
    Button create_event_btn, edit_event_btn, activity_add_button, activity_event_confirm_button;
    ViewGroup activity_list;

    private DatabaseManager databaseManager;

    private Event createEvent;

    private int activityNum = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_edit_event);

        // Initialize views here for Create
        create_event_layout = findViewById(R.id.event_create_layout);
        create_event_name = findViewById(R.id.create_event_name);
        create_event_description = findViewById(R.id.create_event_description);
        create_event_organisation = findViewById(R.id.create_event_organisation);
        create_event_address = findViewById(R.id.create_event_address);
        create_event_btn = findViewById(R.id.create_event_button);

        // Initialize views for Edit
        edit_event_layout = findViewById(R.id.event_edit_layout);
        edit_event_name = findViewById(R.id.edit_event_name);
        edit_event_description = findViewById(R.id.edit_event_description);
        edit_event_organisation = findViewById(R.id.edit_event_organisation);
        edit_event_address = findViewById(R.id.edit_event_address);
        edit_event_btn = findViewById(R.id.edit_event_button);

        // Initialize views for Activity
        activity_layout = findViewById(R.id.activity_layout);
        event_activity_name = findViewById(R.id.activity_event_title);
        activity_add_button = findViewById(R.id.add_activity_button);
        activity_list = findViewById(R.id.event_activity_list);
        activity_event_confirm_button = findViewById(R.id.event_activity_confirm_btn);

        databaseManager = new DatabaseManager(this);

        Intent intent = getIntent();
        String eventId = intent.getStringExtra("eventId");

        if(eventId != null){

            Log.i("check id", "is not empty should be edit");


        }
        else {
            Log.i("check id", "is empty should be create");

            create_event_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Retrieve the text from each TextView and trim extra spaces
                    String eventName = create_event_name.getText().toString().trim();
                    String eventDescription = create_event_description.getText().toString().trim();
                    String eventOrganisation = create_event_organisation.getText().toString().trim();
                    String eventAddress = create_event_address.getText().toString().trim();

                    // Check if any of the fields are empty
                    if (eventName.isEmpty() || eventDescription.isEmpty() || eventOrganisation.isEmpty() || eventAddress.isEmpty()) {
                        // Display error message to user
                        Toast.makeText(getApplicationContext(), "Please fill out all the fields.", Toast.LENGTH_SHORT).show();
                    } else {

                        List<Point> testP = new ArrayList<Point>();
                        testP.add(Point.fromLngLat(0,0));
                        Log.i("getcurretnuser", Home.currentUser.getName());
                        createEvent = new Event(
                                "0",
                                eventName,
                                Home.currentUser,
                                Point.fromLngLat(0,0),
                                testP,
                                eventOrganisation, eventDescription);


                        // Proceed to the next activity using an Intent
                        create_event_layout.setVisibility(View.GONE);
                        edit_event_layout.setVisibility(View.GONE);
                        activity_layout.setVisibility(View.VISIBLE);

                        event_activity_name.setText(eventName + " Event");

                        activity_add_button.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(CreateEditEvent.this, AddRemoveActivity.class);
                                activityResultLauncher.launch(intent);

                            }
                        });

                        activity_event_confirm_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                databaseManager.addEvent(createEvent, new DatabaseCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        try{
                                            Integer eventID = Integer.parseInt(result);
                                            createEvent.setEventId(eventID+"");

                                            for(int i=0 ; i < createEvent.getEventActivity().size(); i++) {

                                                Activity activity = createEvent.getEventActivity().get(i);
                                                activity.setHostedEvent(createEvent);

                                                databaseManager.addActivity(activity, new DatabaseCallback<String>() {
                                                    @Override
                                                    public void onSuccess(String result) {
                                                        try{
                                                            Integer activityID = Integer.parseInt(result);
                                                            Log.i("Success (Activity ID)", String.valueOf(activityID));

                                                            if(createEvent.getEventActivity().indexOf(activity) + 1 >= createEvent.getEventActivity().size()) {
                                                                Intent i = new Intent(CreateEditEvent.this, Home.class);
                                                                startActivity(i);
                                                            }
                                                        }
                                                        catch (Exception e){
                                                            Log.i("Activity bad string", result);
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(String error) {
                                                        Log.println(Log.ASSERT, "Error adding activity", error);
                                                    }
                                                });
                                            }
                                        }
                                        catch (Exception e){
                                            Log.i("Event bad string", result);
                                        }
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Log.println(Log.ASSERT, "Error adding event:", error);
                                    }
                                });

                            }
                        });


                    }
                }
            });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void AddingActivity(String name, String description, String organisation, String address, Point center, ArrayList<Point> range) {

        Activity tmpActivity = new Activity("0",
                name,
                Home.currentUser,
                null,
                center,
                range,
                description,
                address,
                null,
                "2023-09-21T12:00:00Z",
                "2023-09-21T12:00:00Z",
                "/9j/4AAQSkZJRgABAQEAAAAAAAD/4QBYRXhpZgAATU0AKgAAAAgAAkAAAAMAAAABAAEAQAAEAA");

        createEvent.addEventActivity(tmpActivity);

        Log.i("activity", String.valueOf(range));

        LayoutInflater inflater = LayoutInflater.from(this);

        // Inflate the card layout
        View cardView = inflater.inflate(R.layout.event_activity_item, activity_list, false);

        // Find views within the card and populate them
        TextView activityName = cardView.findViewById(R.id.activity_name);
        TextView activityIndex = cardView.findViewById(R.id.activity_num);

        activityName.setText(name);
        activityIndex.setText("0"+activityNum);
        activityNum += 1;


        activity_list.addView(cardView);
    }


    // Define an ActivityResultLauncher
    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult> () {

                        /**
                         * Called when result is available
                         *
                         * @param result
                         */
                        @Override
                        public void onActivityResult(ActivityResult result) {

                            if(result.getResultCode() == RESULT_OK) {

                                Intent intent = result.getData();
                                String activity_name = intent.getStringExtra("activityName");
                                String activity_description = intent.getStringExtra("activityDescription");
                                String activity_organisation = intent.getStringExtra("activityOrganisation");
                                String activity_address = intent.getStringExtra("activityAddress");

                                LatLng activity_center = intent.getParcelableExtra("activityCenter");
                                ArrayList<LatLng> activity_range = intent.getParcelableArrayListExtra("activityRange");

                                Point activity_center_point = Point.fromLngLat(activity_center.getLongitude(), activity_center.getLatitude());

                                ArrayList<Point> activity_range_points = new ArrayList<>();
                                for(LatLng latlng: activity_range) {
                                    activity_range_points.add(Point.fromLngLat(latlng.getLongitude(), latlng.getLatitude()));
                                }

                                AddingActivity(activity_name, activity_description, activity_organisation, activity_address, activity_center_point, activity_range_points);

                            }
                        }
                    }
            );


}

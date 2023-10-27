package com.example.myapplication;

import android.app.Activity;
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

import com.example.myapplication.component.Event;

import java.util.ArrayList;


public class CreateEditEvent extends AppCompatActivity {

    // Assuming you're in an activity
    int ADD_ACTIVITY_REQUEST_CODE = 1; // this can be any number

    // Views for Create, Edit, and Activity
    View create_event_layout, edit_event_layout, activity_layout;
    TextView create_event_name, create_event_description, create_event_organisation, create_event_address;
    TextView edit_event_name, edit_event_description, edit_event_organisation, edit_event_address;
    TextView event_activity_name;
    Button create_event_btn, edit_event_btn, activity_add_button, activity_event_confirm_button;
    ViewGroup activity_list;


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
                                Intent i = new Intent(CreateEditEvent.this, Home.class);
                                startActivity(i);
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

    private void AddingActivity(String a) {

        LayoutInflater inflater = LayoutInflater.from(this);

        // Inflate the card layout
        View cardView = inflater.inflate(R.layout.event_activity_item, activity_list, false);

        // Find views within the card and populate them
        TextView activityName = cardView.findViewById(R.id.activity_name);

        activityName.setText(a);



        // and similarly for other views...
        // Populate the views with data from the event
        // mainImage.setImageResource(event.getImageResource());  // Assuming Event has a method to provide image resource
        // title.setText(event.getTitle());
        // ... similarly, populate other views ...

        // Add the populated card to the parent layout
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

                                Intent a = result.getData();
                                String text = a.getStringExtra("test");
                                Log.i("text", text+"");

                                AddingActivity(text);


                            }
                        }
                    });






}

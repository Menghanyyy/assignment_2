package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.component.*;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

public class AddRemoveActivity extends AppCompatActivity {

    TextView activity_name;
    TextView activity_description;
    TextView activity_organisation;
    TextView activity_address;

    Button add_activity_button;

    Button confirm_activity_button;

    ViewGroup registerMapCenterStatus;
    ViewGroup registerMapRangeStatus;

    LatLng activityCenter;
    ArrayList<LatLng> activityRange;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);


        activity_name = findViewById(R.id.activity_name);
        activity_description = findViewById(R.id.activity_description);
        activity_organisation = findViewById(R.id.activity_organisation);
        activity_address = findViewById(R.id.activity_address);

        add_activity_button = findViewById(R.id.activity_register_map_button);
        confirm_activity_button = findViewById(R.id.activity_button);

        registerMapCenterStatus = findViewById(R.id.activity_map_center_register_layout);
        registerMapRangeStatus = findViewById(R.id.activity_map_range_register_layout);

        add_activity_button.setVisibility(View.VISIBLE);
        confirm_activity_button.setVisibility(View.GONE);
        registerMapCenterStatus.setVisibility(View.GONE);
        registerMapRangeStatus.setVisibility(View.GONE);

        add_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String activityName = activity_name.getText().toString().trim();
                String activityDescription = activity_description.getText().toString().trim();
                String activityOrganisation = activity_organisation.getText().toString().trim();
                String activityAddress = activity_address.getText().toString().trim();

                if(activityName.isEmpty() || activityDescription.isEmpty() || activityOrganisation.isEmpty() || activityAddress.isEmpty()) {

                    Toast.makeText(getApplicationContext(), "Please fill out all the fields.", Toast.LENGTH_SHORT).show();

                }
                else {

                    Intent intent = new Intent(AddRemoveActivity.this, EditableMapActivity.class);
                    intent.putExtra("activityName", activityName);
                    activityResultLauncher.launch(intent);

                }
            }
        });

        confirm_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String activityName = activity_name.getText().toString().trim();
                String activityDescription = activity_description.getText().toString().trim();
                String activityOrganisation = activity_organisation.getText().toString().trim();
                String activityAddress = activity_address.getText().toString().trim();

                Intent intent = new Intent();
                intent.putExtra("activityName", activityName);
                intent.putExtra("activityDescription", activityDescription);
                intent.putExtra("activityOrganisation", activityOrganisation);
                intent.putExtra("activityAddress", activityAddress);

                intent.putExtra("activityCenter", activityCenter);
                intent.putParcelableArrayListExtra("activityRange", activityRange);

                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    // Define an ActivityResultLauncher
    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {

                        /**
                         * Called when result is available
                         *
                         * @param result
                         */
                        @Override
                        public void onActivityResult(ActivityResult result) {

                            if(result.getResultCode() == RESULT_OK) {

                                add_activity_button.setVisibility(View.GONE);
                                registerMapCenterStatus.setVisibility(View.VISIBLE);
                                registerMapRangeStatus.setVisibility(View.VISIBLE);
                                confirm_activity_button.setVisibility(View.VISIBLE);

                                Intent a = result.getData();
                                LatLng centerPoint = a.getParcelableExtra("activityCenter");
                                ArrayList<LatLng> range = a.getParcelableArrayListExtra("activityRange");

                                activityCenter = centerPoint;
                                activityRange = range;

                                Log.i("centerPoint", String.valueOf(centerPoint));
                                Log.i("activityRange", String.valueOf(activityRange));


                            }
                        }
                    });
}

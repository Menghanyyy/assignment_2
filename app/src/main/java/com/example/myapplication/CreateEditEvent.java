package com.example.myapplication;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.component.*;
import com.example.myapplication.database.DatabaseCallback;
import com.example.myapplication.database.DatabaseManager;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreateEditEvent extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 1001;

    // Views for Create, Edit, and Activity
    View create_event_layout, edit_event_layout, activity_layout;
    TextView create_event_name, create_event_description, create_event_organisation;
    TextView edit_event_name, edit_event_description, edit_event_organisation, edit_event_address;
    TextView event_activity_name, activity_event_confirm_button, activity_add_button;
    Button edit_event_btn;
    TextView create_event_btn;

    ViewGroup activity_list;

    ImageView uploadImageView;

    AutoCompleteTextView create_event_address;


    private ArrayAdapter<String> adapter;

    private DatabaseManager databaseManager;

    private Event createEvent;

    private int activityNum = 1;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.create_edit_event);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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

        uploadImageView = findViewById(R.id.create_eventImage);
        uploadImageView.setDrawingCacheEnabled(true);
        uploadImageView.buildDrawingCache();

        databaseManager = new DatabaseManager(this);

        Intent intent = getIntent();
        String eventId = intent.getStringExtra("eventId");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        create_event_address.setAdapter(adapter);

        if(eventId != null){

            Log.i("check id", "is not empty should be edit");


        }
        else {
            Log.i("check id", "is empty should be create");

            create_event_address.setOnItemClickListener((parent, view, position, id) -> {
                String selection = (String) parent.getItemAtPosition(position);
                create_event_address.setText(selection);
            });

            create_event_address.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    String enteredText = create_event_address.getText().toString();
                    // Check if the enteredText matches any item in the adapter
                    boolean isMatch = false;
                    for (int i = 0; i < adapter.getCount(); i++) {
                        String item = adapter.getItem(i);
                        if (enteredText.equals(item)) {
                            isMatch = true;
                            break;
                        }
                    }
                    // If there is no match, clear the AutoCompleteTextView
                    if (!isMatch) {
                        create_event_address.setText("");
                    }
                }
            });

            create_event_address.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Perform search
                    if (!s.toString().isEmpty()) {
                        performSearch(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            uploadImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    galleryAccessPermissions();
                }
            });

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

                        Bitmap bitmap = uploadImageView.getDrawingCache();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                        createEvent = new Event(
                                "0",
                                eventName,
                                Home.currentUser,
                                eventAddress,
                                testP,
                                eventOrganisation, eventDescription, encodedImage);


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

    private void performSearch(String query) {
        MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
                .accessToken(Mapbox.getAccessToken())
                .query(query)
                .build();

        mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                if (response.body() != null) {
                    List<CarmenFeature> results = response.body().features();
                    List<String> addresses = new ArrayList<>();
                    for (CarmenFeature feature : results) {
                        addresses.add(feature.placeName());
                    }
                    // Update the adapter and the dropdown list.
                    runOnUiThread(() -> {
                        adapter.clear();
                        adapter.addAll(addresses);
                        adapter.notifyDataSetChanged();
                    });
                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private void AddingActivity(String image, String name, String description, String organisation, String address, String activity_start_time, String activity_end_time, Point center, ArrayList<Point> range) {

        Activity tmpActivity = new Activity( name,
                Home.currentUser,
                null,
                center,
                range,
                description,
                address,
                organisation,
                activity_start_time,
                activity_end_time,
                image);

        // adding activity to event
        createEvent.addEventActivity(tmpActivity);


        LayoutInflater inflater = LayoutInflater.from(this);

        // Inflate the card layout
        View cardView = inflater.inflate(R.layout.event_activity_item, activity_list, false);

        // Find views within the card and populate them
        ImageView activityImage = cardView.findViewById(R.id.activity_image);
        TextView activityName = cardView.findViewById(R.id.activity_name);
        TextView activityIndex = cardView.findViewById(R.id.activity_num);
        TextView removeBtn = cardView.findViewById(R.id.activity_remove_button);

        activityIndex.setText("0"+activityNum);
        activityNum += 1;

        activityName.setText(name);

        byte[] decodedImageBytes = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.length);
        activityImage.setImageBitmap(decodedBitmap);

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvent.removeEventActivity(tmpActivity);
                activity_list.removeView(cardView);
            }
        });


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

//                                byte[] decodedImageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
//                                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.length);
//                                uploadImageView.setImageBitmap(decodedBitmap);

                                Intent intent = result.getData();
                                String activity_image = intent.getStringExtra("activityImage");
                                String activity_name = intent.getStringExtra("activityName");
                                String activity_description = intent.getStringExtra("activityDescription");
                                String activity_organisation = intent.getStringExtra("activityOrganisation");
                                String activity_address = intent.getStringExtra("activityAddress");
                                String activity_start_time = intent.getStringExtra("activityStartTime");
                                String activity_end_time = intent.getStringExtra("activityEndTime");

                                LatLng activity_center = intent.getParcelableExtra("activityCenter");
                                ArrayList<LatLng> activity_range = intent.getParcelableArrayListExtra("activityRange");

                                Point activity_center_point = Point.fromLngLat(activity_center.getLongitude(), activity_center.getLatitude());

                                ArrayList<Point> activity_range_points = new ArrayList<>();
                                for(LatLng latlng: activity_range) {
                                    activity_range_points.add(Point.fromLngLat(latlng.getLongitude(), latlng.getLatitude()));
                                }

                                AddingActivity(activity_image, activity_name, activity_description, activity_organisation, activity_address, activity_start_time, activity_end_time, activity_center_point, activity_range_points);

                            }
                        }
                    }
            );

    // Define an ActivityResultLauncher
    private final ActivityResultLauncher<Intent> imageUploadResultLauncher =
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

                                Log.i("image","image uploaded");

                                Intent imageIntent = result.getData();
                                Uri imageUri = imageIntent.getData();
                                uploadImageView.setImageURI(imageUri);

//                                Bitmap bitmap = uploadImageView.getDrawingCache();
                                //ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                                byte[] imageBytes = baos.toByteArray();

                                // this will pass to the db
//                                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//
//                                byte[] decodedImageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
//                                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.length);
//                                uploadImageView.setImageBitmap(decodedBitmap);

                            }
                        }
                    }
            );


//    private void galleryAccessPermissions() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
//
//        } else {
//
//            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            imageUploadResultLauncher.launch(intent);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_PERMISSIONS) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, proceed with accessing gallery
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                imageUploadResultLauncher.launch(intent);
//            } else {
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                    // Permission denied without checking "Don't ask again"
//
//                    LayoutInflater inflater = getLayoutInflater();
//                    View layout = inflater.inflate(R.layout.customise_toast, null, false);
//
//                    TextView text = layout.findViewById(R.id.toast_text);
//                    text.setText("Permission denied!");
//
//                    Toast toast = new Toast(CreateEditEvent.this);
//                    toast.setView(layout);
//                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
//                    toast.setDuration(Toast.LENGTH_LONG);
//                    toast.show();
//
//                } else {
//                    // User checked "Don't ask again"
//
//                    LayoutInflater inflater = getLayoutInflater();
//                    View layout = inflater.inflate(R.layout.customise_toast, null, false);
//
//                    TextView text = layout.findViewById(R.id.toast_text);
//                    text.setText("Permission denied. Please enable it in app settings!");
//
//                    Toast toast = new Toast(CreateEditEvent.this);
//                    toast.setView(layout);
//                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
//                    toast.setDuration(Toast.LENGTH_LONG);
//                    toast.show();
//
//                    // Optional: If you want to open the app settings
//                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                            Uri.fromParts("package", getPackageName(), null));
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }
//            }
//        }
//    }
//


    private void galleryAccessPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user
                showRationaleDialog();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        } else {
            // Permission has already been granted
            openGallery();
        }
    }

    private void showRationaleDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Needed")
                .setMessage("This permission is needed to access your gallery for image selection.")
                .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(
                        CreateEditEvent.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imageUploadResultLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                openGallery();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Permission denied without checking "Don't ask again", show rationale again
                    showRationaleDialog();
                } else {
                    // User checked "Don't ask again", guide the user towards app settings
                    showAppSettingsDialog();
                }
            }
        }
    }

    private void showAppSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Denied")
                .setMessage("Please enable access to storage in the app settings.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    // Intent to open the app settings
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }




}

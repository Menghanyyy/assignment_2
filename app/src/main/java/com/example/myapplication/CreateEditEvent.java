package com.example.myapplication;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
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

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreateEditEvent extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 1001;

    // Views for Create, Edit, and Activity

    private View create_event_layout, edit_event_layout, activity_layout;
    private TextView create_event_name, create_event_description, create_event_organisation;
    private TextView edit_event_name, edit_event_description, edit_event_organisation, edit_event_address;
    private TextView event_activity_name, activity_event_confirm_button, activity_add_button;
    private Button edit_event_btn;
    private TextView create_event_btn;

    private ViewGroup activity_list;

    private ImageView uploadImageView;

    private GifImageView gifImageView;

    private AutoCompleteTextView create_event_address;

    private ArrayAdapter<String> adapter;

    // Runtime field

    private DatabaseManager databaseManager;

    private Event createEvent;

    private int activityNum = 1;

    // Passing event Id;
    private String eventId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.create_edit_event);

        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.event_create_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(),0);
                //InputMethodManager im = (InputMethodManager)
                //        getSystemService(INPUT_METHOD_SERVICE);
                //im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                //        0);
                //Toast.makeText(getApplicationContext(), "This is my Toast message!",
                //        Toast.LENGTH_LONG).show();
            }
        });

        gifImageView = findViewById(R.id.creating_animation_layout);

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


        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"Loading..."});
        create_event_address.setAdapter(adapter);

        create_event_address.setDropDownVerticalOffset(-3000);

        if(eventId != null){

            create_event_layout.setVisibility(View.GONE);
            edit_event_layout.setVisibility(View.GONE);
            activity_layout.setVisibility(View.GONE);
            gifImageView.setVisibility(View.VISIBLE);

            //Log.i("check id", "is not empty should be edit");

            databaseManager.getEventByID(Integer.parseInt(eventId), new DatabaseCallback<Event>() {
                @Override
                public void onSuccess(Event result) {

                    Log.i("get event by id", String.valueOf(result.getEventName()));

                    createEvent = result;

                    create_event_layout.setVisibility(View.GONE);
                    edit_event_layout.setVisibility(View.GONE);
                    activity_layout.setVisibility(View.VISIBLE);
                    gifImageView.setVisibility(View.GONE);


                    event_activity_name.setText(result.getEventName());

                    databaseManager.getAllActivities(result.getEventId(), new DatabaseCallback<ArrayList<Activity>>() {
                        @Override
                        public void onSuccess(ArrayList<Activity> result) {

                            Log.i("get activities", String.valueOf(result.get(0).getActivityLocation()));

                            //Add Activity
                            for(Activity a :  result) {
                                AddingActivityForDB(a.getImage(), a.getActivityName());
                            }

                        }

                        @Override
                        public void onError(String error) {
                            Log.println(Log.ASSERT, "Error get activities", error);
                        }
                    });

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

                            create_event_layout.setVisibility(View.GONE);
                            edit_event_layout.setVisibility(View.GONE);
                            activity_layout.setVisibility(View.GONE);
                            gifImageView.setVisibility(View.VISIBLE);

                            if(createEvent.getEventActivity().size() > 0) {

                                for(int i=0 ; i < createEvent.getEventActivity().size(); i++) {

                                    Activity activity = createEvent.getEventActivity().get(i);
                                    activity.setHostedEvent(createEvent);
                                    Log.i("activity adding to db", activity.toString());

                                    databaseManager.addActivity(activity, new DatabaseCallback<String>() {
                                        @Override
                                        public void onSuccess(String result) {

                                            //at the end because it looping
                                            if(createEvent.getEventActivity().indexOf(activity) + 1 >= createEvent.getEventActivity().size()) {
                                                finish();
                                            }

                                        }

                                        @Override
                                        public void onError(String error) {
                                            Log.println(Log.ASSERT, "Error adding activity", error);

                                            create_event_layout.setVisibility(View.GONE);
                                            edit_event_layout.setVisibility(View.GONE);
                                            activity_layout.setVisibility(View.VISIBLE);
                                            gifImageView.setVisibility(View.GONE);
                                        }
                                    });
                                }

                            } else {

                               finish();
                            }

                        }
                    });


                }

                @Override
                public void onError(String error) {
                    Log.println(Log.ASSERT, "Error Retrieving json", error);
                    finish();
                }
            });


        }
        else {

            //Log.i("check id", "is empty should be create");

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

                }

                @Override
                public void afterTextChanged(Editable s) {

                    // Perform search
                    if (s.toString().isEmpty() == false && s.length() >= 3) {
                        performSearch(s.toString());
                    }

                }
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
                        Log.i("getcurretnuser", MyApplication.getCurrentUser().getName());

                        Bitmap bitmap = uploadImageView.getDrawingCache();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                        createEvent = new Event(
                                "0",
                                eventName,
                                MyApplication.getCurrentUser(),
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

                                create_event_layout.setVisibility(View.GONE);
                                edit_event_layout.setVisibility(View.GONE);
                                activity_layout.setVisibility(View.GONE);
                                gifImageView.setVisibility(View.VISIBLE);

                                databaseManager.addEvent(createEvent, new DatabaseCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        try{
                                            Integer eventID = Integer.parseInt(result);
                                            createEvent.setEventId(eventID+"");

                                            if(createEvent.getEventActivity().size() > 0) {

                                                for(int i=0 ; i < createEvent.getEventActivity().size(); i++) {

                                                    Activity activity = createEvent.getEventActivity().get(i);
                                                    activity.setHostedEvent(createEvent);
                                                    Log.i("activity adding to db", activity.toString());

                                                    databaseManager.addActivity(activity, new DatabaseCallback<String>() {
                                                        @Override
                                                        public void onSuccess(String result) {

                                                            //at the end because it looping
                                                            if(createEvent.getEventActivity().indexOf(activity) + 1 >= createEvent.getEventActivity().size()) {
                                                                Intent i = new Intent(CreateEditEvent.this, Home.class);
                                                                startActivity(i);
                                                            }

                                                        }

                                                        @Override
                                                        public void onError(String error) {
                                                            Log.println(Log.ASSERT, "Error adding activity", error);

                                                            create_event_layout.setVisibility(View.GONE);
                                                            edit_event_layout.setVisibility(View.GONE);
                                                            activity_layout.setVisibility(View.VISIBLE);
                                                            gifImageView.setVisibility(View.GONE);
                                                        }
                                                    });
                                                }

                                            } else {

                                                Intent i = new Intent(CreateEditEvent.this, Home.class);
                                                startActivity(i);
                                            }

                                        }
                                        catch (Exception e){
                                            Log.i("Event bad string", result);
                                        }
                                    }

                                    @Override
                                    public void onError(String error) {

                                        Log.println(Log.ASSERT, "Error adding event:", error);

                                        create_event_layout.setVisibility(View.VISIBLE);
                                        edit_event_layout.setVisibility(View.GONE);
                                        activity_layout.setVisibility(View.GONE);
                                        gifImageView.setVisibility(View.GONE);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

                    // Update the dropdown list.

                    if(addresses.size() > 0) {
                        runOnUiThread(() -> {
                            adapter.clear();
                            adapter.addAll(addresses);
                            adapter.notifyDataSetChanged();
                        });


                    }

                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private void AddingActivity(String image, String name, String description, String organisation, String address, String activity_start_time, String activity_end_time, Point center, ArrayList<Point> range) {


        LayoutInflater inflater = LayoutInflater.from(this);

        // Inflate the card layout
        View cardView = inflater.inflate(R.layout.event_activity_item, activity_list, false);

        // Find views within the card and populate them
        ImageView activityImage = cardView.findViewById(R.id.activity_image);
        TextView activityName = cardView.findViewById(R.id.activity_name);
        TextView activityIndex = cardView.findViewById(R.id.activity_num);
        TextView removeBtn = cardView.findViewById(R.id.activity_remove_button);

        String activityImageString = "";

        Drawable drawable = null;
        Bitmap bitmap = null;

        if(image.isEmpty()) {

            activityImage.setImageResource(R.drawable.img_placeholder);

            drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.img_placeholder);

            if (drawable instanceof BitmapDrawable) {

                bitmap = ((BitmapDrawable) drawable).getBitmap();

            } else {

                if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                    bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
                } else {
                    bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                }
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
            }


        }
        else {
            Uri imageUri = Uri.parse(image);
            activityImage.setImageURI(imageUri);

           drawable = activityImage.getDrawable();

            if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();

            } else {

                if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {

                    bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

                } else {

                    // Use the drawable's dimensions
                    bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                }

                Canvas canvas = new Canvas(bitmap);

                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());

                drawable.draw(canvas);
            }

        }

        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            activityImageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }

        activityIndex.setText("0"+activityNum);
        activityNum += 1;

        activityName.setText(name);


        Activity tmpActivity = new Activity(name,
                MyApplication.getCurrentUser(),
                null,
                center,
                range,
                description,
                address+"",
                organisation+"",
                activity_start_time,
                activity_end_time,
                activityImageString);

        // adding activity to event
        Log.i("adding activity", tmpActivity.toString());
        createEvent.addEventActivity(tmpActivity);


        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvent.removeEventActivity(tmpActivity);
                activity_list.removeView(cardView);
            }
        });


        activity_list.addView(cardView);
    }

    private void AddingActivityForDB(String image, String name) {

        LayoutInflater inflater = LayoutInflater.from(this);

        // Inflate the card layout
        View cardView = inflater.inflate(R.layout.event_activity_item, activity_list, false);

        // Find views within the card and populate them
        ImageView activityImage = cardView.findViewById(R.id.activity_image);
        TextView activityName = cardView.findViewById(R.id.activity_name);
        TextView activityIndex = cardView.findViewById(R.id.activity_num);
        TextView removeBtn = cardView.findViewById(R.id.activity_remove_button);

        if(image.isEmpty()) {

            activityImage.setImageResource(R.drawable.img_placeholder);

        } else {
            byte[] decodedImageBytes = Base64.decode(image, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.length);
            activityImage.setImageBitmap(decodedBitmap);
        }

        activityIndex.setText("0"+activityNum);
        activityNum += 1;

        activityName.setText(name);

        removeBtn.setVisibility(View.GONE);

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

                                Intent imageIntent = result.getData();
                                Uri imageUri = imageIntent.getData();
                                uploadImageView.setImageURI(imageUri);


                            }
                        }
                    }
            );


    private void galleryAccessPermissions() {

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    showRationaleDialog();

                } else {

                    ActivityCompat.requestPermissions(this,
                            new String[]{
                                    Manifest.permission.READ_MEDIA_IMAGES
                            },
                            REQUEST_PERMISSIONS);
                }
            } else {

                // Permission has already been granted
                openGallery();
            }

        } else{

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    showRationaleDialog();

                } else {

                    ActivityCompat.requestPermissions(this,
                            new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                            },
                            REQUEST_PERMISSIONS);
                }
            } else {

                openGallery();
            }
        }
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
                openGallery();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showRationaleDialog();
                } else {
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


//    private void cameraAccessPermissions() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.CAMERA)) {
//                // Show an explanation to the user
//                showRationaleDialog();
//            } else {
//                // No explanation needed, we can request the permission.
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.CAMERA},
//                        REQUEST_CAMERA_PERMISSION);
//            }
//        } else {
//            // Permission has already been granted
//            openCamera();
//        }
//    }
//
//    private void openCamera() {
//        try {
//
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            imageUploadResultLauncher.launch(intent);
//
//        } catch (ActivityNotFoundException e) {
//            // display error state to the user
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_CAMERA_PERMISSION) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission was granted
//                openCamera();
//            } else {
//                // Permission was denied or request was cancelled
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
//                    // User has denied permission but not permanently
//                    showRationaleDialog();
//                } else {
//                    // User has denied permission permanently
//                    showAppSettingsDialog();
//                }
//            }
//        }
//    }
//
//    private void showRationaleDialog() {
//        new AlertDialog.Builder(this)
//                .setTitle("Permission Needed")
//                .setMessage("This permission is needed to use your camera for taking pictures.")
//                .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(
//                        CreateEditEvent.this,
//                        new String[]{Manifest.permission.CAMERA},
//                        REQUEST_CAMERA_PERMISSION))
//                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
//                .create()
//                .show();
//    }
//
//    private void showAppSettingsDialog() {
//        new AlertDialog.Builder(this)
//                .setTitle("Permission Denied")
//                .setMessage("Please enable access to the camera in the app settings.")
//                .setPositiveButton("Settings", (dialog, which) -> {
//                    // Intent to open the app settings
//                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                            Uri.fromParts("package", getPackageName(), null));
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                })
//                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
//                .create()
//                .show();
//    }
}

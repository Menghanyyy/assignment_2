package com.example.myapplication;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.component.*;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class that handle the Add and Remove activity of the event
 */
public class AddRemoveActivity extends AppCompatActivity {


    private static final int REQUEST_PERMISSIONS = 1001;

    private Calendar dateTimeCalendar;

    private Uri imageUri;


    private ImageView activity_image;
    private TextView activity_name;
    private TextView activity_description;
    private TextView activity_organisation;
    private AutoCompleteTextView activity_address;
    private TextView activity_start_time;
    private TextView activity_end_time;

    private TextView add_activity_button;

    private TextView confirm_activity_button;

    private ViewGroup registerMapCenterStatus;
    private ViewGroup registerMapRangeStatus;

    private LatLng activityCenter;
    private ArrayList<LatLng> activityRange;

    private ArrayAdapter<String> adapter;

    int timeStatusSelect = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);

        imageUri = null;

        dateTimeCalendar = Calendar.getInstance();

        activity_image = findViewById(R.id.activity_image);

        activity_name = findViewById(R.id.activity_name);
        activity_description = findViewById(R.id.activity_description);
        activity_organisation = findViewById(R.id.activity_organisation);
        activity_address = findViewById(R.id.activity_address);
        activity_start_time = findViewById(R.id.activity_start_time);
        activity_end_time = findViewById(R.id.activity_end_time);

        add_activity_button = findViewById(R.id.activity_register_map_button);
        confirm_activity_button = findViewById(R.id.activity_button);

        registerMapCenterStatus = findViewById(R.id.activity_map_center_register_layout);
        registerMapRangeStatus = findViewById(R.id.activity_map_range_register_layout);

        add_activity_button.setVisibility(View.VISIBLE);
        confirm_activity_button.setVisibility(View.GONE);
        registerMapCenterStatus.setVisibility(View.GONE);
        registerMapRangeStatus.setVisibility(View.GONE);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        activity_address.setAdapter(adapter);

        activity_address.setDropDownVerticalOffset(-3000);

        activity_address.setOnItemClickListener((parent, view, position, id) -> {
            String selection = (String) parent.getItemAtPosition(position);
            activity_address.setText(selection);
        });


        // Make sure that the address only be able to select from available option.
        activity_address.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String enteredText = activity_address.getText().toString();
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
                    activity_address.setText("");
                }
            }
        });

        activity_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                // Perform search
                if (s.toString().isEmpty() == false && s.length() >= 3) {

                    // getting the available address according to the suer input
                    performSearch(s.toString());
                }

            }
        });


        activity_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timeStatusSelect = 0;

                // calender register
                new DatePickerDialog(AddRemoveActivity.this, dateSetListener,
                        dateTimeCalendar.get(Calendar.YEAR),
                        dateTimeCalendar.get(Calendar.MONTH),
                        dateTimeCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        activity_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timeStatusSelect = 1;

                // calender register
                new DatePickerDialog(AddRemoveActivity.this, dateSetListener,
                        dateTimeCalendar.get(Calendar.YEAR),
                        dateTimeCalendar.get(Calendar.MONTH),
                        dateTimeCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        // open gallery to upload image
        activity_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryAccessPermissions();
            }
        });

        add_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String activityName = activity_name.getText().toString().trim();
                String activityDescription = activity_description.getText().toString().trim();
                String activityOrganisation = activity_organisation.getText().toString().trim();
                String activityAddress = activity_address.getText().toString().trim();

                if(activityName.isEmpty() || activityDescription.isEmpty() || activityOrganisation.isEmpty() || activityAddress.isEmpty()) {

                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.customise_toast, null, false);

                    TextView text = layout.findViewById(R.id.toast_text);
                    text.setText("Please fill out all the fields");

                    Toast toast = new Toast(AddRemoveActivity.this);
                    toast.setView(layout);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.show();

                }
                else {

                    // register activity on the map
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
                String activityStartTime = activity_start_time.getText().toString();
                String activityEndTime = activity_end_time.getText().toString();

                Intent intent = new Intent();
                if(imageUri == null) {
                    intent.putExtra("activityImage", "");
                } else {
                    intent.putExtra("activityImage", imageUri.toString());
                }
                intent.putExtra("activityName", activityName);
                intent.putExtra("activityDescription", activityDescription);
                intent.putExtra("activityOrganisation", activityOrganisation);
                intent.putExtra("activityAddress", activityAddress);
                intent.putExtra("activityStartTime", activityStartTime);
                intent.putExtra("activityEndTime", activityEndTime);
                intent.putExtra("activityCenter", activityCenter);
                intent.putParcelableArrayListExtra("activityRange", activityRange);

                setResult(RESULT_OK, intent);
                finish();
            }
        });

        findViewById(R.id.Root_activity_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(),0);
            }
        });

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

                                // Process the result once user registered the activity on the map

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

                                // upload the image from gallery to activity

                                Intent imageIntent = result.getData();
                                imageUri = imageIntent.getData();
                                activity_image.setImageURI(imageUri);


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
                        AddRemoveActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            // Set the Calendar new date chosen by user
            dateTimeCalendar.set(Calendar.YEAR, year);
            dateTimeCalendar.set(Calendar.MONTH, month);
            dateTimeCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Now that we have the date, show the time picker
            new TimePickerDialog(AddRemoveActivity.this, timeSetListener,
                    dateTimeCalendar.get(Calendar.HOUR_OF_DAY),
                    dateTimeCalendar.get(Calendar.MINUTE), true).show();
        }
    };

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Set the Calendar new time chosen by user
            dateTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateTimeCalendar.set(Calendar.MINUTE, minute);

            // Format the dateTimeCalendar to your liking and display it
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

            if(timeStatusSelect == 0) {
                activity_start_time.setText(dateFormat.format(dateTimeCalendar.getTime()));
            }
            else {
                activity_end_time.setText(dateFormat.format(dateTimeCalendar.getTime()));
            }

            // Use the dateTimeCalendar.getTime() as a Date object
            Date date = dateTimeCalendar.getTime();

        }
    };


}

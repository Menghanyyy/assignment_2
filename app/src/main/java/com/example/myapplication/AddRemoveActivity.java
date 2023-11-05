package com.example.myapplication;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.component.*;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddRemoveActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 1001;

    private Calendar dateTimeCalendar;

    ImageView activity_image;
    TextView activity_name;
    TextView activity_description;
    TextView activity_organisation;
    TextView activity_address;
    TextView activity_start_time;
    TextView activity_end_time;

    TextView add_activity_button;

    TextView confirm_activity_button;

    ViewGroup registerMapCenterStatus;
    ViewGroup registerMapRangeStatus;

    LatLng activityCenter;
    ArrayList<LatLng> activityRange;

    int timeStatusSelect = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity);

        dateTimeCalendar = Calendar.getInstance();

        activity_image = findViewById(R.id.activity_image);
        activity_image.setDrawingCacheEnabled(true);
        activity_image.buildDrawingCache();

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

        activity_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timeStatusSelect = 0;

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

                new DatePickerDialog(AddRemoveActivity.this, dateSetListener,
                        dateTimeCalendar.get(Calendar.YEAR),
                        dateTimeCalendar.get(Calendar.MONTH),
                        dateTimeCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



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

                Bitmap bitmap = activity_image.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.URL_SAFE);

                String activityName = activity_name.getText().toString().trim();
                String activityDescription = activity_description.getText().toString().trim();
                String activityOrganisation = activity_organisation.getText().toString().trim();
                String activityAddress = activity_address.getText().toString().trim();
                String activityStartTime = activity_start_time.getText().toString();
                String activityEndTime = activity_end_time.getText().toString();

                Intent intent = new Intent();
                intent.putExtra("activityImage", encodedImage);
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
                                activity_image.setImageURI(imageUri);

//                                Bitmap bitmap = uploadImageView.getDrawingCache();
//                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                                byte[] imageBytes = baos.toByteArray();
//                                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//
//                                byte[] decodedImageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
//                                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.length);
//                                uploadImageView.setImageBitmap(decodedBitmap);

                            }
                        }
                    }
            );


    private void galleryAccessPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);

        } else {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imageUploadResultLauncher.launch(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imageUploadResultLauncher.launch(intent);
        } else {
            // Permission denied. Inform the user.
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        }
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

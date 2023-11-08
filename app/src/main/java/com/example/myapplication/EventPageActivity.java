package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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

    ImageView copy_icon;
    ImageView iv_back;

    DatabaseManager databaseManager;
    TextView tv_gotomap;
    TextView invite;
    TextView link_view;

    ImageView iv_edit;


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

                if(result.getImage().isEmpty()) {
                    image.setImageResource(R.mipmap.aaaa);
                }else {
                    byte[] decodedImageBytes = Base64.decode(result.getImage(), Base64.DEFAULT);
                    Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.length);
                    image.setImageBitmap(decodedBitmap);
                }

                title.setText(result.getEventName());
                desc.setText(result.getDescription());
                if(result.getEventLocation().isEmpty()) {
                    location.setText("Melbourne");
                }
                else {
                    location.setText(result.getEventLocation());
                }
                organisation.setText(result.getOrganisationName());
            }

            @Override
            public void onError(String error) {
                Log.println(Log.ASSERT, "Error Retrieving json", error);
            }
        });

        invite = findViewById(R.id.invite);
        copy_icon = findViewById(R.id.link_copy_icon);
        link_view = findViewById(R.id.invite_link);
        tv_gotomap = findViewById(R.id.tv_gotomap);
        iv_edit = findViewById(R.id.eventEdit);


        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make the new TextView visible
                link_view.setVisibility(View.VISIBLE);
                copy_icon.setVisibility(View.VISIBLE);
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

        copy_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String link = link_view.getText().toString();

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                ClipData clip = ClipData.newPlainText("invite-link", link);

                clipboard.setPrimaryClip(clip);

                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.customise_toast, null, false);

                TextView text = layout.findViewById(R.id.toast_text);
                text.setText("Copied to clipboard");

                Toast toast = new Toast(EventPageActivity.this);
                toast.setView(layout);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.show();


            }
        });

        tv_gotomap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent  mapIntent = new Intent(EventPageActivity.this,MapActivity.class);
                mapIntent.putExtra("eventId", eventId);
                mapIntent.putExtra("bbox", pointsJson);

                activityResultLauncher.launch(mapIntent);

//                startActivity(mapIntent);

            }
        });


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
                MyApplication.getInstance().eventPagerTabIndex = 0;
                finish();
            }
        });

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

    @Override
    protected void onStart() {

        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();
        link_view.setVisibility(View.GONE);
        copy_icon.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        TextView desc = findViewById(R.id.eventDescription);
//        TextView textExpend = findViewById(R.id.eventDescriptionExpend);
//
//        textExpend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (desc.getMaxLines() == 5) {
//                    // If currently showing limited lines, show all
//                    desc.setMaxLines(Integer.MAX_VALUE);
//                    textExpend.setText("Read Less");
//                } else {
//                    // If currently showing all, revert back to limited lines
//                    desc.setMaxLines(5);
//                    textExpend.setText("Read More");
//                }
//            }
//        });
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {

                        /**
                         * Called when result is available
                         *
                         * @param result
                         */
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onActivityResult(ActivityResult result) {

                            if (result.getResultCode() == RESULT_OK) {

                                Log.i("Return", "return");
                            }
                        }
                    }
            );
}
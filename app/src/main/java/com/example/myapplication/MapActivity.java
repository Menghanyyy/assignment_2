package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.component.Activity;
import com.example.myapplication.component.Detect;
import com.example.myapplication.component.Event;
import com.example.myapplication.component.Features;
import com.example.myapplication.component.GeneralUser;
import com.example.myapplication.component.OnDetectResultListener;
import com.example.myapplication.component.Visit;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.*;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.plugins.annotation.CircleManager;
import com.mapbox.mapboxsdk.plugins.annotation.CircleOptions;
import com.mapbox.mapboxsdk.plugins.annotation.FillManager;
import com.mapbox.mapboxsdk.plugins.annotation.FillOptions;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.Source;

import com.example.myapplication.database.*;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.Manifest;

import javax.security.auth.login.LoginException;

import com.example.myapplication.component.Detect;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, OnDetectResultListener, MapboxMap.OnMapClickListener {


    private static final int REQUEST_CODE_LOCATION_PERMISSION = 101;

    private static final String ACTIVITY_FILL_LAYER_ID = "activity_fill_id";
    private static final String ACTIVITY_OUTLINE_LAYER_ID = "activity_outline_id";
    private static final String ACTIVITY_SOURCE_ID = "activity_source_id";

    private static final String MARKER_LAYER_ID = "activity-marker-layer-id";
    private static final String MARKER_SOURCE_ID = "activity-marker-source-id";
    private static final String MARKER_ICON_ID = "activity-marker-icon-id";

    private MapView mapView;
    private MapboxMap mapboxMap;

    private DatabaseManager databaseManager;

    private ArrayList<Activity> eventsActivities;
    private ArrayList<String> activitiesMarkerId;

    private boolean isLocationEnabled = false;
    private RecyclerView rvView;
    private MyAdapter rvAdapter;

    private LocationComponent locationComponent;
    private Handler locationHandler = new Handler();
    private Runnable locationRunnable;
    
    private Detect testDetect = new Detect(this);

    private ViewGroup popupLayout;
    private ArrayList<String> avoidPopUp;

    private String eventId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");

        eventsActivities = new ArrayList<>();
        activitiesMarkerId = new ArrayList<>();

        avoidPopUp = new ArrayList<>();

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));


        setContentView(R.layout.activity_map);

        popupLayout = findViewById(R.id.check_in_popup_layout);

        rvView = findViewById(R.id.rvView);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        this.databaseManager = new DatabaseManager(this);


    }

    /**
     * Called when the map is ready to be used.
     *
     * @param mapboxMap An instance of MapboxMap associated with the {@link MapFragment} or
     *                  {@link MapView} that defines the callback.
     */
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        MapActivity.this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/light-v11"),
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

//                        initSource(style);
//                        initLayers(style);


                        databaseManager.getAllActivities(eventId, new DatabaseCallback<ArrayList<Activity>>() {
                            @Override
                            public void onSuccess(ArrayList<Activity> result) {
                                Log.i("activity", result.size()+"");
                                eventsActivities = result;
                                drawPolygon_Geojson(style);
                                addMarker(style);

                                // handle get visit..

                            }

                            @Override
                            public void onError(String error) {
                                Log.println(Log.ASSERT, "Error get activities", error);
                            }
                        });


                        // Set initial map viewport and zoom
                        CameraPosition initialPosition = new CameraPosition.Builder()
                                .target(new LatLng(-37.7951, 144.9620))  // Set the latitude and longitude
                                .zoom(10)  // Set zoom level
                                .build();

                        // testing

                        // testing

                        mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(initialPosition));

                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments

                    }

                });

        // Enable zoom controls (+ and - buttons)
        mapboxMap.getUiSettings().setZoomGesturesEnabled(true);
        mapboxMap.addOnMapClickListener(this);

    }


    private Bitmap getBitmapFromDrawable(int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(this, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable || drawable instanceof GradientDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            throw new IllegalArgumentException("Unsupported drawable type");
        }
    }


    private void drawPolygon_Geojson(Style style) {

        for (Activity activity : eventsActivities)
        {
            // To remove the layer with ID "maine"
            Layer layer = style.getLayer(ACTIVITY_FILL_LAYER_ID + eventsActivities.indexOf(activity));
            if (layer != null) {
                style.removeLayer(layer);
            }

            // To remove the layer with ID "outline"
            Layer outlineLayer = style.getLayer(ACTIVITY_OUTLINE_LAYER_ID + eventsActivities.indexOf(activity));
            if (outlineLayer != null) {
                style.removeLayer(outlineLayer);
            }

            // To remove the source with ID "maine"
            Source source = style.getSource(ACTIVITY_SOURCE_ID + eventsActivities.indexOf(activity));
            if (source != null) {
                style.removeSource(source);
            }

        }

        for (Activity activity : eventsActivities)
        {

            // Create a Polygon
            Polygon polygon = Polygon.fromLngLats(Collections.singletonList(activity.getActivityRange()));
            Log.i("poly", polygon.toString()+"");

            // Create a GeoJsonSource
            GeoJsonSource geoJsonSource_2 = new GeoJsonSource(ACTIVITY_SOURCE_ID + eventsActivities.indexOf(activity), Feature.fromGeometry(polygon));

            // add geojson
            style.addSource(geoJsonSource_2);

            // Adding fill layer to the map
            FillLayer fillLayer = new FillLayer(ACTIVITY_FILL_LAYER_ID + eventsActivities.indexOf(activity), ACTIVITY_SOURCE_ID + eventsActivities.indexOf(activity));
            fillLayer.setProperties(
                    PropertyFactory.fillColor(Color.parseColor("#0080ff")), // blue color fill
                    PropertyFactory.fillOpacity(0.5f)
            );
            style.addLayer(fillLayer);

            // Adding outline layer to the map
            LineLayer lineLayer = new LineLayer(ACTIVITY_OUTLINE_LAYER_ID + eventsActivities.indexOf(activity), ACTIVITY_SOURCE_ID + eventsActivities.indexOf(activity));
            lineLayer.setProperties(
                    PropertyFactory.lineColor(Color.parseColor("#000000")), // black color line
                    PropertyFactory.lineWidth(3f)
            );
            style.addLayerAbove(lineLayer, ACTIVITY_FILL_LAYER_ID + eventsActivities.indexOf(activity)); // Make sure the outline layer is above the fill layer

        }


    }

    private void addMarker(Style loadedMapStyle) {


        for(Activity activity : eventsActivities) {

            // Remove the existing layer if it's present
            if (loadedMapStyle.getLayer(MARKER_LAYER_ID + eventsActivities.indexOf(activity)) != null) {
                loadedMapStyle.removeLayer(MARKER_LAYER_ID + eventsActivities.indexOf(activity));
            }

            // Remove the existing source if it's present
            if (loadedMapStyle.getSource(MARKER_SOURCE_ID + eventsActivities.indexOf(activity)) != null) {
                loadedMapStyle.removeSource(MARKER_SOURCE_ID + eventsActivities.indexOf(activity));
            }

            loadedMapStyle.addImage(MARKER_ICON_ID, getBitmapFromDrawable(R.drawable.location_pointer));

            GeoJsonSource geoJsonSource = new GeoJsonSource(MARKER_SOURCE_ID + eventsActivities.indexOf(activity));
            Point destinationPoint = activity.getActivityLocation();

            Feature feature = Feature.fromGeometry(destinationPoint);
            feature.addStringProperty("activityId", activity.getActivityId());

            geoJsonSource.setGeoJson(feature);
            loadedMapStyle.addSource(geoJsonSource);

            SymbolLayer destinationSymbolLayer = new SymbolLayer(MARKER_LAYER_ID + eventsActivities.indexOf(activity),
                    MARKER_SOURCE_ID + eventsActivities.indexOf(activity));

            activitiesMarkerId.add(MARKER_LAYER_ID + eventsActivities.indexOf(activity));

            destinationSymbolLayer.withProperties(
                    iconImage(MARKER_ICON_ID),
                    iconAllowOverlap(true),
                    iconIgnorePlacement(true)
            );

            // render above outline layer
            loadedMapStyle.addLayerAbove(destinationSymbolLayer, ACTIVITY_OUTLINE_LAYER_ID + eventsActivities.indexOf(activity));



        }

    }


    @SuppressWarnings( {"MissingPermission"})
    private void toggleUserLocation() {
        LocationComponent locationComponent = mapboxMap.getLocationComponent();
        if (isLocationEnabled) {
            locationComponent.setLocationComponentEnabled(false);
            isLocationEnabled = false;
            if (locationHandler != null && locationRunnable != null) {
                locationHandler.removeCallbacks(locationRunnable);
            }
        } else {
            enableLocationComponent(mapboxMap.getStyle());
            isLocationEnabled = true;
        }
    }


    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Get an instance of the component
            locationComponent = mapboxMap.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            startLocationChecker();



        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocationComponent(mapboxMap.getStyle());
            } else {
                Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void startLocationChecker() {
        locationRunnable = new Runnable() {
            @Override
            public void run() {
                if (locationComponent != null) {
                    Location userLocation = locationComponent.getLastKnownLocation();
                    if (userLocation != null) {
                        testDetect.nearActivities(userLocation.getLongitude(), userLocation.getLatitude());

                        // You now have the user's location, you can check and trigger pop-up or other actions.
                    }
                    locationHandler.postDelayed(this, 3000);  // Check again after 3 seconds
                }
            }
        };
        locationHandler.post(locationRunnable);
    }



    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();

        ImageButton toggleLocationButton = findViewById(R.id.location_toggle_button);
        toggleLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapboxMap != null && mapboxMap.getStyle() != null) {
                    toggleUserLocation();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        if (locationHandler != null && locationRunnable != null) {
            locationHandler.removeCallbacks(locationRunnable);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }



    // this function return 
    @Override
    public void onDetectResult(List<Features> featureList) {
        Log.i("featurelist", String.valueOf(featureList.size()));


        if(featureList.size() > 0) {


            for(Features f : featureList) {

                Activity tmpActivity = null;
                Log.i("activity Id",f.getActivityID()+ "");

                for (Activity activity : eventsActivities) {
                    if(Integer.parseInt(activity.getActivityId()) == f.getActivityID()) {
                        tmpActivity = activity;
                        break;
                    }
                }

                if(tmpActivity != null && avoidPopUp.contains(tmpActivity.getActivityId()) == false)
                {

                    LayoutInflater inflater = LayoutInflater.from(this);

                    // Inflate the card layout
                    View checkInCardView = inflater.inflate(R.layout.detected_activity_card, popupLayout, false);

                    // Find views within the card and populate them
                    TextView activityName = checkInCardView.findViewById(R.id.check_in_activity_name);
                    Button checkInBtn = checkInCardView.findViewById(R.id.activity_check_in_btn);
                    Button cancelCheckInBth = checkInCardView.findViewById(R.id.cancel_check_in_btn);

                    activityName.setText(tmpActivity.getActivityName());

                    Activity finalTmpActivity = tmpActivity;

                    cancelCheckInBth.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            avoidPopUp.add(finalTmpActivity.getActivityId());
                            popupLayout.removeView(checkInCardView);
                        }
                    });

                    checkInBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            popupLayout.removeView(checkInCardView);
                            Intent intent = new Intent(MapActivity.this, CheckIn.class);
                            intent.putExtra("activityId", finalTmpActivity.getActivityId());
                            activityResultLauncher.launch(intent);
                        }
                    });

                    popupLayout.addView(checkInCardView);

                }
                else {
                    Log.i("features", "No Matching Event");
                }
            }
        }


    }

    /**
     * Called when the user clicks on the map view.
     *
     * @param point The projected map coordinate the user clicked on.
     * @return True if this click should be consumed and not passed further to other listeners registered afterwards,
     * false otherwise.
     */
    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        PointF touchPoint = mapboxMap.getProjection().toScreenLocation(point);

        String[] markerLayerId = activitiesMarkerId.toArray(new String[0]);

        Log.i("cickedMarker", String.valueOf(markerLayerId));
        List<Feature> features = mapboxMap.queryRenderedFeatures(touchPoint, markerLayerId);

        if (!features.isEmpty()) {
            // A marker was clicked. You can retrieve its data from the feature.
            Feature clickedMarkerFeature = features.get(0);
            String activityId = clickedMarkerFeature.getStringProperty("activityId");

            for(Activity a : eventsActivities) {
                if(a.getActivityId().equals(activityId)) {
                    rvAdapter = new MyAdapter(a);
                    rvView.setAdapter(rvAdapter);
                    rvView.setVisibility(View.VISIBLE);
                    break;

                }
            }
            return true; // return true to indicate that we handled the click
        }
        return false; // return false to let the map handle the click as normal (e.g., pan or zoom)
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
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onActivityResult(ActivityResult result) {

                            if(result.getResultCode() == RESULT_OK) {

                                Intent intent = result.getData();
                                String checkedInActivityId = intent.getStringExtra("activityId");

                                Visit newVisit = new Visit(Home.currentUser.getUserId(), checkedInActivityId);

                                databaseManager.addVisit(newVisit, new DatabaseCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        avoidPopUp.add(checkedInActivityId);
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Log.println(Log.ASSERT, "Error adding visit", error);
                                    }
                                });



                            }
                        }
                    }
            );
}

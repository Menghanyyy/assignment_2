package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.component.Event;
import com.example.myapplication.component.GeneralUser;
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

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.Manifest;

import javax.security.auth.login.LoginException;


public class MapActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 101;

    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String MARKER_ICON_ID = "my-marker-icon";

    private MapView mapView;
    private MapboxMap mapboxMap;
    
    private FillManager fillManager;
    private CircleManager circleManager;
    private CircleManager markerManager;
    private List<LatLng> polygonVertices = new ArrayList<>();
    private List<LatLng> clickedPoints = new ArrayList<>();
    private long downTime;
    private static final long LONG_PRESS_TIME = 3000; // Set the time for a long press
    List<List<Point>> pointsList = new ArrayList<>();
    private SymbolManager symbolManager;
    private DatabaseManager databaseManager;

    private boolean isLocationEnabled = false;

    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.databaseManager = new DatabaseManager(this);

        databaseManager.getEventByID(82, new DatabaseCallback<Event>() {
            @Override
            public void onSuccess(Event result) {
                pointsList.add(result.getEventRange());
            }

            @Override
            public void onError(String error) {
                Log.println(Log.ASSERT, "Error Retrieving json", error);
            }
        });


        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_map);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.map);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                
                MapActivity.this.mapboxMap = mapboxMap;

                mapboxMap.setStyle(new Style.Builder().fromUri(Style.DARK)
                        .withImage(MARKER_ICON_ID,
                        getBitmapFromDrawable(R.drawable.baseline_location_on_24)),
                        new Style.OnStyleLoaded() {

                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        initSource(style);
                        initLayers(style);

                        circleManager = new CircleManager(mapView, mapboxMap, style);

                        drawPolygon_Geojson(style);

                        // Set initial map viewport and zoom
                        CameraPosition initialPosition = new CameraPosition.Builder()
                                .target(new LatLng(-37.80995133438894, 144.96871464972733))  // Set the latitude and longitude
                                .zoom(10)  // Set zoom level
                                .build();

                        mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(initialPosition));



                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments


                    }

                });

                // Enable zoom controls (+ and - buttons)
                mapboxMap.getUiSettings().setZoomGesturesEnabled(true);


            }
        });

    }


    private void initSource(@NonNull Style loadedMapStyle) {
        GeoJsonSource gj = new GeoJsonSource(ICON_SOURCE_ID);
        loadedMapStyle.addSource(gj);
    }

    private void initLayers(@NonNull Style loadedMapStyle) {

        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                iconImage(MARKER_ICON_ID),
                iconIgnorePlacement(true),
                iconAllowOverlap(true)));

    }


    @SuppressWarnings( {"MissingPermission"})
    private void toggleUserLocation() {
        LocationComponent locationComponent = mapboxMap.getLocationComponent();
        if (isLocationEnabled) {
            locationComponent.setLocationComponentEnabled(false);
            isLocationEnabled = false;
        } else {
            enableLocationComponent(mapboxMap.getStyle());
            isLocationEnabled = true;
        }
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

        Log.i("polygonpl", String.valueOf(pointsList));

        for (List<Point> p : pointsList)
        {
            // To remove the layer with ID "maine"
            Layer layer = style.getLayer("maine" + pointsList.indexOf(p));
            if (layer != null) {
                style.removeLayer(layer);
            }

            // To remove the layer with ID "outline"
            Layer outlineLayer = style.getLayer("outline"+ pointsList .indexOf(p));
            if (outlineLayer != null) {
                style.removeLayer(outlineLayer);
            }

            // To remove the source with ID "maine"
            Source source = style.getSource("maine"+ pointsList.indexOf(p));
            if (source != null) {
                style.removeSource(source);
            }

        }

        List<Point> points = new ArrayList<>();
        for (LatLng latLng : polygonVertices) {
            Log.i("in", latLng+"");
            points.add(Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude()));
        }

        pointsList.add(points);

        for (List<Point> p : pointsList)
        {

            // Create a Polygon
            Polygon polygon = Polygon.fromLngLats(Collections.singletonList(p));
            Log.i("poly", polygon.toString()+"");

            // Create a GeoJsonSource
            GeoJsonSource geoJsonSource_2 = new GeoJsonSource("maine" + pointsList.indexOf(p), Feature.fromGeometry(polygon));

            // add geojson
            style.addSource(geoJsonSource_2);

            // Adding fill layer to the map
            FillLayer fillLayer = new FillLayer("maine" + pointsList.indexOf(p), "maine" + pointsList.indexOf(p));
            fillLayer.setProperties(
                    PropertyFactory.fillColor(Color.parseColor("#0080ff")), // blue color fill
                    PropertyFactory.fillOpacity(0.5f)
            );
            style.addLayer(fillLayer);

            // Adding outline layer to the map
            LineLayer lineLayer = new LineLayer("outline" + pointsList.indexOf(p), "maine" + pointsList.indexOf(p));
            lineLayer.setProperties(
                    PropertyFactory.lineColor(Color.parseColor("#000000")), // black color line
                    PropertyFactory.lineWidth(3f)
            );
            style.addLayerAbove(lineLayer, "maine" + pointsList.indexOf(p)); // Make sure the outline layer is above the fill layer

            circleManager.deleteAll();

        }


    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
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


    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();

        Button toggleLocationButton = findViewById(R.id.location_toggle_button);
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

}

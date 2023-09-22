package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;


import android.graphics.Color;
import android.os.Bundle;

public class MapActivity extends AppCompatActivity{

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_map);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {

                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        // Set initial map viewport and zoom
                        CameraPosition initialPosition = new CameraPosition.Builder()
                                .target(new LatLng(-37.80995133438894, 144.96871464972733))  // Set the latitude and longitude
                                .zoom(10)  // Set zoom level
                                .build();

                        mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(initialPosition));

                        // Add a data source
                        GeoJsonSource geoJsonSource = new GeoJsonSource("source-id",
                                Feature.fromGeometry(Point.fromLngLat(144.96871464972733, -37.80995133438894)));
                        style.addSource(geoJsonSource);

                        // Add a layer to use the source
                        CircleLayer circleLayer = new CircleLayer("layer-id", "source-id");
                        circleLayer.withProperties(
                                PropertyFactory.circleRadius(10f),
                                PropertyFactory.circleColor(Color.argb(127, 255, 0, 0))
                        );
                        style.addLayer(circleLayer);

                        // Map is set up and the style has loaded. Now you can add data or make other map adjustments


                    }
                });

                // Enable zoom controls (+ and - buttons)
                mapboxMap.getUiSettings().setZoomGesturesEnabled(true);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
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

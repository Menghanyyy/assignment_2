package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.mapboxsdk.annotations.PolygonOptions;
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
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;


import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapActivity extends AppCompatActivity{

    private MapView mapView;
    private FillManager fillManager;
    private CircleManager circleManager;
    private List<LatLng> polygonVertices = new ArrayList<>();
    private List<LatLng> clickedPoints = new ArrayList<>();
    private long downTime;
    private static final long LONG_PRESS_TIME = 3000; // Set the time for a long press


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
                        circleManager = new CircleManager(mapView, mapboxMap, style);
                        fillManager = new FillManager(mapView, mapboxMap, style);

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

                        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                            @Override
                            public boolean onMapClick(@NonNull LatLng point) {
                                // Draw a circle at the touched position
                                CircleOptions circleOptions = new CircleOptions();
                                circleOptions.withLatLng(point);
                                circleManager.create(circleOptions);

                                handleMapClick(point);
                                return true;
                            }
                        });

                        mapboxMap.addOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                            @Override
                            public boolean onMapLongClick(@NonNull LatLng point) {
                                handleMapLongClick();
                                return true;
                            }
                        });


                        mapView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                switch (motionEvent.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        polygonVertices.clear(); // 清空点列表
                                        downTime = System.currentTimeMillis();
                                        break;

                                    case MotionEvent.ACTION_UP:
                                        if (System.currentTimeMillis() - downTime > LONG_PRESS_TIME) {
                                            // Handle long click
                                            handleMapLongClick();
                                        } else if(polygonVertices.size() <= 0) {

                                            LatLng point = mapboxMap.getProjection().fromScreenLocation(
                                                    new android.graphics.PointF(motionEvent.getX(), motionEvent.getY()));

                                            CircleOptions circleOptions = new CircleOptions();
                                            circleOptions.withLatLng(point);
                                            circleManager.create(circleOptions);

                                            handleMapClick(point);
                                        }
                                        break;

                                    case MotionEvent.ACTION_MOVE:
                                        LatLng touchedPoint = mapboxMap.getProjection().fromScreenLocation(
                                                new android.graphics.PointF(motionEvent.getX(), motionEvent.getY()));
                                        polygonVertices.add(touchedPoint); // 添加点到列表
                                        drawPolygon(); // 画多边形
                                        break;
                                }
                                return true;
                            }
                        });

                    }

                    private void handleMapClick(LatLng point) {
                        clickedPoints.add(point);
                        drawPolygon_click();
                    }

                    private void drawPolygon_click() {
//                        fillManager.deleteAll();
                        FillOptions fillOptions = new FillOptions()
                                .withLatLngs(Collections.singletonList(clickedPoints))
                                .withFillColor("rgba(255,0,0,0.5)"); // semi-transparent red fill
                        fillManager.create(fillOptions);
                    }


                    private void drawPolygon() {
                        fillManager.deleteAll();
                        FillOptions fillOptions = new FillOptions()
                                .withLatLngs(Collections.singletonList(polygonVertices))
                                .withFillColor("rgba(255,0,0,0.5)"); // 半透明红色填充
                        fillManager.create(fillOptions);
                    }

                    private void handleMapLongClick() {
                        fillManager.deleteAll(); // Clear the drawn polygons
                        circleManager.deleteAll();
                    }

                });


                // Enable zoom controls (+ and - buttons)
//                mapboxMap.getUiSettings().setZoomGesturesEnabled(true);
                mapboxMap.getUiSettings().setScrollGesturesEnabled(false);


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

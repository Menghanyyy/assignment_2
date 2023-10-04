package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
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
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.Source;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapActivity extends AppCompatActivity{

    private MapView mapView;
    private FillManager fillManager;
    private CircleManager circleManager;
    private CircleManager markerManager;
    private List<LatLng> polygonVertices = new ArrayList<>();
    private List<LatLng> clickedPoints = new ArrayList<>();
    private long downTime;
    private static final long LONG_PRESS_TIME = 3000; // Set the time for a long press
    List<List<Point>> pointsList = new ArrayList<>();
    private SymbolManager symbolManager;


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
                        markerManager = new CircleManager(mapView, mapboxMap, style);
                        fillManager = new FillManager(mapView, mapboxMap, style);
                        symbolManager = new SymbolManager(mapView, mapboxMap, style);

                        // Add an image to the style
                        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
                        style.addImage("my-marker-icon", icon);

                        // Set initial map viewport and zoom
                        CameraPosition initialPosition = new CameraPosition.Builder()
                                .target(new LatLng(-37.80995133438894, 144.96871464972733))  // Set the latitude and longitude
                                .zoom(10)  // Set zoom level
                                .build();

                        mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(initialPosition));

                        //Small Dot Point
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

                        //Drawing Shape
                        mapView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                switch (motionEvent.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        polygonVertices.clear(); // 清空点列表
                                        downTime = System.currentTimeMillis();
                                        break;

                                    case MotionEvent.ACTION_UP:
                                        view.performClick();
                                        if (System.currentTimeMillis() - downTime > LONG_PRESS_TIME) {
                                            // Handle long click
                                            handleMapLongClick();
                                        } else if(polygonVertices.size() <= 0) {
                                            //add Marker
                                            LatLng point = mapboxMap.getProjection().fromScreenLocation(
                                                    new android.graphics.PointF(motionEvent.getX(), motionEvent.getY()));

                                            clickedPoints.add(point);
                                            addMarker();

                                        } else {
                                            drawPolygon_Geojson(style);
                                        }
                                        break;

                                    case MotionEvent.ACTION_MOVE:
                                        LatLng touchedPoint = mapboxMap.getProjection().fromScreenLocation(
                                                new android.graphics.PointF(motionEvent.getX(), motionEvent.getY()));
                                        polygonVertices.add(touchedPoint); // 添加点到列表

                                        CircleOptions circleOptions = new CircleOptions();
                                        circleOptions.withLatLng(touchedPoint);
                                        circleOptions.withCircleColor("rgba(255,0,0,0.5)");
                                        circleManager.create(circleOptions);
                                        break;
                                }
                                return true;
                            }
                        });



                    }

                    private void addMarker() {

                        markerManager.deleteAll();
                        for(LatLng l : clickedPoints) {
                            CircleOptions circleOptions = new CircleOptions();
                            circleOptions.withLatLng(l);
                            circleOptions.withCircleColor("rgba(0,255,255,0.5)");
                            markerManager.create(circleOptions);
                        }

                    }





//                    private void handleMapClick(LatLng point) {
//                        clickedPoints.add(point);
//                        drawPolygon_click();
//                    }
//
//                    private void drawPolygon_click() {
////                        fillManager.deleteAll();
//                        FillOptions fillOptions = new FillOptions()
//                                .withLatLngs(Collections.singletonList(clickedPoints))
//                                .withFillColor("rgba(255,0,0,0.5)"); // semi-transparent red fill
//                        fillManager.create(fillOptions);
//                    }


//                    private void drawPolygon() {
//                        fillManager.deleteAll();
//                        FillOptions fillOptions = new FillOptions()
//                                .withLatLngs(Collections.singletonList(polygonVertices))
//                                .withFillColor("rgba(255,0,0,0.5)"); // 半透明红色填充
//                        fillManager.create(fillOptions);
//                    }

                    private void drawPolygon_Geojson(Style style) {

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
                            points.add(Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude()));
                        }

                        pointsList.add(points);

                        for (List<Point> p : pointsList)
                        {
                            // Create a Polygon
                            Polygon polygon = Polygon.fromLngLats(Collections.singletonList(p));

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

                    private void handleMapLongClick() {
                        fillManager.deleteAll(); // Clear the drawn polygons
                        circleManager.deleteAll();
                    }

                });


                // Enable zoom controls (+ and - buttons)
                //mapboxMap.getUiSettings().setZoomGesturesEnabled(true);
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

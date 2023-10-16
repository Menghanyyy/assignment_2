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


public class EditableMapActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 101;

    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String MARKER_ICON_ID = "my-marker-icon";

    private MapView mapView;
    private MapboxMap mapboxMap;

    private CircleManager circleManager;
    private List<LatLng> polygonVertices = new ArrayList<>();
    private LatLng clickedPoints = new LatLng(0, 0);
    private List<List<Point>> pointsList = new ArrayList<>();

    private long downTime;
    private static final long LONG_PRESS_TIME = 3000; // Set the time for a long press
    private DatabaseManager databaseManager;

    private boolean drawCircle = false;
    private boolean mapMove = true;
    private boolean addMarker = false;

    private View.OnTouchListener drawCircleTouchListener = null;
    private MapboxMap.OnMapClickListener onMapClick = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.editable_activity_map);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.map);

        mapView = (MapView) findViewById(R.id.mapView_2);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {

                EditableMapActivity.this.mapboxMap = mapboxMap;

                mapboxMap.setStyle(new Style.Builder().fromUri(Style.DARK)
                                .withImage(MARKER_ICON_ID,
                                getBitmapFromDrawable(R.drawable.baseline_location_on_24)),
                                new Style.OnStyleLoaded() {

                            @Override
                            public void onStyleLoaded(@NonNull Style style) {

                                initSource(style);
                                initLayers(style);

                                circleManager = new CircleManager(mapView, mapboxMap, style);


                                // Set initial map viewport and zoom
                                CameraPosition initialPosition = new CameraPosition.Builder()
                                        .target(new LatLng(-37.80995133438894, 144.96871464972733))  // Set the latitude and longitude
                                        .zoom(10)  // Set zoom level
                                        .build();

                                mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(initialPosition));


                                if(mapMove) {
                                    mapboxMap.getUiSettings().setScrollGesturesEnabled(true);

                                } else if (addMarker) {
                                    mapboxMap.getUiSettings().setScrollGesturesEnabled(false);
                                    mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                                        @Override
                                        public boolean onMapClick(@NonNull LatLng point) {

                                            clickedPoints = point;
                                            addMarker();

                                            return false;
                                        }
                                    });


                                }

                            }

                        });



            }
        });

    }

    private void handleMapLongClick() {
        circleManager.deleteAll();
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

    private void addMarker() {
        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                GeoJsonSource destinationIconGeoJsonSource = style.getSourceAs(ICON_SOURCE_ID);
                if (destinationIconGeoJsonSource != null) {
                    destinationIconGeoJsonSource.setGeoJson(Feature.fromGeometry(Point.fromLngLat(
                            clickedPoints.getLongitude(), clickedPoints.getLatitude())));
                }
            }
        });

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


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();

        Button drawCircleButton = findViewById(R.id.circle_btn);
        drawCircleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawCircle = !drawCircle;
                mapMove = false;
                addMarker = false;

                if(drawCircle) {
                    mapboxMap.getUiSettings().setScrollGesturesEnabled(false);
                    drawCircleTouchListener = new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            switch (motionEvent.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    polygonVertices.clear();
                                    downTime = System.currentTimeMillis();
                                    break;

                                case MotionEvent.ACTION_UP:
                                    view.performClick();
                                    if (System.currentTimeMillis() - downTime > LONG_PRESS_TIME) {
                                        // Handle long click
                                        handleMapLongClick();
                                    } else {
                                        drawPolygon_Geojson(mapboxMap.getStyle());
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
                    };

                    mapView.setOnTouchListener(drawCircleTouchListener);
                }
                else {
                    mapboxMap.getUiSettings().setScrollGesturesEnabled(true);
                    mapView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            return false;
                        }
                    });
                }
            }
        });

        Button addMarkerButton = findViewById(R.id.marker_btn);
        addMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawCircle = false;
                mapMove = false;
                addMarker = !addMarker;

                mapView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });

                if(addMarker) {
                    mapboxMap.getUiSettings().setScrollGesturesEnabled(false);
                    onMapClick = new MapboxMap.OnMapClickListener() {
                        @Override
                        public boolean onMapClick(@NonNull LatLng point) {
                            clickedPoints = point;
                            addMarker();
                            return false;
                        }
                    };

                    mapboxMap.addOnMapClickListener(onMapClick);

                } else {

                    mapboxMap.getUiSettings().setScrollGesturesEnabled(true);
                    mapboxMap.removeOnMapClickListener(onMapClick);

                    mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                        @Override
                        public boolean onMapClick(@NonNull LatLng point) {
                            return false;
                        }
                    });

                }

            }
        });


        Button moveMapButton = findViewById(R.id.move_btn);
        moveMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawCircle = false;
                mapMove = !mapMove;
                addMarker = false;

                mapboxMap.getUiSettings().setScrollGesturesEnabled(true);

                mapView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        return false;
                    }
                });

                mapboxMap.removeOnMapClickListener(onMapClick);

                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {
                        return false;
                    }
                });



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

package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.mapbox.geojson.Polygon;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;

import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.plugins.annotation.CircleManager;
import com.mapbox.mapboxsdk.plugins.annotation.CircleOptions;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.Source;

import com.example.myapplication.database.*;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Editable Map that allow user to register activity location and range **/

public class EditableMapActivity extends AppCompatActivity implements OnMapReadyCallback{

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

    private Button map_registered_btn;

    private TextView activity_name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.editable_activity_map);

        Intent intent = getIntent();
        String activityName = intent.getStringExtra("activityName");

        activity_name = findViewById(R.id.activity_name);
        activity_name.setText(activityName);

        mapView = (MapView) findViewById(R.id.mapView_2);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        map_registered_btn = findViewById(R.id.register_map_done_btn);

        map_registered_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if((clickedPoints.getLongitude() != 0 && clickedPoints.getLatitude() != 0) && pointsList.size() > 0) {

                    ArrayList<LatLng> activityRange = new ArrayList<>();
                    for(Point point : pointsList.get(0)) {
                        activityRange.add(new LatLng(point.latitude(), point.longitude()));
                    }

                    ArrayList<Point> pp = new ArrayList<>(pointsList.get(0));

                    Intent intent = new Intent();
                    intent.putExtra("activityCenter", clickedPoints);
                    intent.putParcelableArrayListExtra("activityRange", activityRange);
                    setResult(RESULT_OK, intent);
                    finish();

                }
                else {

                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.customise_toast, null, false);

                    TextView text = layout.findViewById(R.id.toast_text);
                    text.setText("Please add marker and range!");

                    Toast toast = new Toast(EditableMapActivity.this);
                    toast.setView(layout);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.show();

                    // Open setting permission issue
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }

            }
        });

    }

    /**
     * Called when the map is ready to be used.
     *
     * @param mapboxMap An instance of MapboxMap associated with the
     *                  {@link MapView} that defines the callback.
     */
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        EditableMapActivity.this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/streets-v11"),
            new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {

                    circleManager = new CircleManager(mapView, mapboxMap, style);

                    // Set initial map viewport and zoom
                    CameraPosition initialPosition = new CameraPosition.Builder()
                            .target(new LatLng(-37.80995133438894, 144.96871464972733))  // Set the latitude and longitude
                            .zoom(10)  // Set zoom level
                            .build();

                    mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(initialPosition));

                }

            });


    }

    private void handleMapLongClick() {
        circleManager.deleteAll();
    }


    private void addMarker(Style loadedMapStyle) {

        // Remove the existing layer if it's present
        if (loadedMapStyle.getLayer(ICON_LAYER_ID) != null) {
            loadedMapStyle.removeLayer(ICON_LAYER_ID);
        }

        // Remove the existing source if it's present
        if (loadedMapStyle.getSource(ICON_SOURCE_ID) != null) {
            loadedMapStyle.removeSource(ICON_SOURCE_ID);
        }

        loadedMapStyle.addImage(MARKER_ICON_ID, getBitmapFromDrawable(R.drawable.location_pointer));

        GeoJsonSource geoJsonSource = new GeoJsonSource(ICON_SOURCE_ID);
        Point destinationPoint = Point.fromLngLat(clickedPoints.getLongitude(), clickedPoints.getLatitude());

        geoJsonSource.setGeoJson(Feature.fromGeometry(destinationPoint));
        loadedMapStyle.addSource(geoJsonSource);

        SymbolLayer destinationSymbolLayer = new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID);
        destinationSymbolLayer.withProperties(
                iconImage(MARKER_ICON_ID),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );

        // To remove the layer with ID "maine"
        Layer layer = loadedMapStyle.getLayer("maine0");
        if (layer != null) {
            loadedMapStyle.addLayerAbove(destinationSymbolLayer, "maine0");
        }
        else {
            loadedMapStyle.addLayer(destinationSymbolLayer);
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

        pointsList.clear();
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

            if (style.getLayer(ICON_LAYER_ID) != null) {
                style.addLayerAbove(fillLayer, ICON_LAYER_ID);

            }
            else {
                style.addLayer(fillLayer);
            }


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

        ImageButton drawCircleButton = findViewById(R.id.circle_btn);
        drawCircleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawCircle = !drawCircle;
                mapMove = false;
                addMarker = false;

                if(drawCircle) {

                    mapboxMap.getUiSettings().setScrollGesturesEnabled(false);

                    // allow user to draw polygon on map
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

        // Allow user to add marker to the map
        ImageButton  addMarkerButton = findViewById(R.id.marker_btn);
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
                            addMarker(mapboxMap.getStyle());
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


        // Allow user to move and zoom teh map
        ImageButton  moveMapButton = findViewById(R.id.move_btn);
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

package com.example.myapplication.component;


import android.util.Log;

import com.mapbox.api.tilequery.MapboxTilequery;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.gson.JsonObject;


public class Detect {
    private String tilesetId;
//    private double longitude;
//    private double latitude;
    private int radiusInMeters;
    private String geoJsonGeometryString;
    private boolean dedupe;
    private String singleOrListOfMapLayerIds;

    private List<Features> featureList;

    private OnDetectResultListener resultListener;



    public Detect(OnDetectResultListener listener) {
        this.resultListener = listener;
        this.tilesetId = "adrianteo1121.clnd78cu6336p2no1j6lxfh3e-4w0gm";
        this.singleOrListOfMapLayerIds = "activities";
        this.radiusInMeters = 25;
        this.dedupe = true;
        this.geoJsonGeometryString = "polygon";
    }

    // dont know if JSONObject would work or not
    public void nearActivities(double longitude, double latitude) {
        featureList = new ArrayList<>();

        //do i need to create a new call everytime?
        MapboxTilequery tilequery = MapboxTilequery.builder()
//                .accessToken(String.valueOf(R.string.mapbox_access_token))
                .accessToken("sk.eyJ1IjoiYWRyaWFudGVvMTEyMSIsImEiOiJjbG1uZXU3bzQwMmRtMmtwMmQ3cWV5d2M2In0.9ddhigLDMQFkY_Inz6f_Vw")
                .tilesetIds(tilesetId)
                .query(Point.fromLngLat(longitude, latitude))
                .radius(radiusInMeters)
                .geometry(geoJsonGeometryString)
                .dedupe(dedupe)
                .layers(singleOrListOfMapLayerIds)
                .build();

        tilequery.enqueueCall(new Callback<FeatureCollection>() {
            public void onResponse(Call<FeatureCollection> call, Response<FeatureCollection> response)
            {
//                                List<Feature> featureList = response.body().features();
//                Log.i("rFeedback", response.body().features().toString());
//                String responseString = response.body().features().toString();

                // Loop through the features
                for (Feature feature : response.body().features()) {
                     JsonObject properties = feature.properties();
                    int activityID = properties.get("activityID").getAsInt();
                    int eventID = properties.get("eventID").getAsInt();
//                    int visited = properties.get("visited").getAsInt();
                    double distance = (properties.get("tilequery").getAsJsonObject().get("distance")).getAsDouble();
                    Log.i("distance", String.valueOf(activityID));
                    Features newFeature = new Features(distance, activityID, eventID, 0);
                    featureList.add(newFeature);
                }
                resultListener.onDetectResult(featureList);
            }

            public void onFailure(Call<FeatureCollection> call, Throwable throwable) {

                Log.i("Request failed: %s", throwable.getMessage());

            }
        });
    }


    public List<Features> getFeatureList() {
        return featureList;
    }

    // Getter and Setter for 'tilesetId'
    public String getTilesetId() {
        return tilesetId;
    }

    public void setTilesetId(String tilesetId) {
        this.tilesetId = tilesetId;
    }

    // Getter and Setter for 'radiusInMeters'
    public int getRadiusInMeters() {
        return radiusInMeters;
    }

    public void setRadiusInMeters(int radiusInMeters) {
        this.radiusInMeters = radiusInMeters;
    }

    // Getter and Setter for 'geoJsonGeometryString'
    public String getGeoJsonGeometryString() {
        return geoJsonGeometryString;
    }

    public void setGeoJsonGeometryString(String geoJsonGeometryString) {
        this.geoJsonGeometryString = geoJsonGeometryString;
    }

    // Getter and Setter for 'dedupe'
    public boolean isDedupe() {
        return dedupe;
    }

    public void setDedupe(boolean dedupe) {
        this.dedupe = dedupe;
    }

    // Getter and Setter for 'singleOrListOfMapLayerIds'
    public String getSingleOrListOfMapLayerIds() {
        return singleOrListOfMapLayerIds;
    }

    public void setSingleOrListOfMapLayerIds(String singleOrListOfMapLayerIds) {
        this.singleOrListOfMapLayerIds = singleOrListOfMapLayerIds;
    }


}

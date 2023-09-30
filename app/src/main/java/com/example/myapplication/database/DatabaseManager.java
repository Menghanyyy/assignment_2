package com.example.myapplication.database;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.myapplication.component.Activity;
import com.example.myapplication.component.Event;
import com.example.myapplication.component.User;
import com.example.myapplication.component.Visit;
import com.example.myapplication.location.GPSLocation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Will implement the java interface
public class DatabaseManager implements DatabaseInterface {

    private RequestQueue requestQueue;
    private Context context;
    private ClassCodes classCodes;

    JSONObjectParsing objectParser = new JSONObjectParsing();

    private static String baseUrl = "http://192.168.0.247:8080";

    public DatabaseManager(Context context) {
        // Initialize the Volley RequestQueue
        requestQueue = Volley.newRequestQueue(context);
        this.context = context;
    }

    private <T> void sendJsonObjectRequest(
            int method,
            String urlExtension,
            JSONObject jsonRequest,
            DatabaseCallback<T> callback,
            ClassCode messageClassCode
    ) {
        String url = baseUrl + urlExtension;

        // If jsonRequest is not null, convert it to a string and append as query parameter
        if (jsonRequest != null) {
            try {
                String jsonString = URLEncoder.encode(jsonRequest.toString(), "utf-8");
                url += "?data=" + jsonString;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            method,
            url,
            jsonRequest,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response != null) {
                            if (response.get("status").equals("success")) {
                                T parsedResponse = parseSuccess(
                                    response.get("message"),
                                    messageClassCode
                                );
                                if (parsedResponse != null){
                                    callback.onSuccess(parsedResponse);
                                } else{
                                    callback.onError("Could not parse response.");
                                }
                            } else {
                                callback.onError((String) response.get("message"));
                            }
                        } else{
                            callback.onError("Received empty response");
                        }
                    } catch (JSONException e) {
                        callback.onError("Bad Json: " + e.getMessage());
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Handle the error response
                    String errorMessage = "VolleyError adding event: " + error.getMessage();
                    callback.onError(errorMessage);
                }
            }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException | JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };
        // Add the request to the Volley request queue
        requestQueue.add(jsonObjectRequest);
    }

    private <T> T parseSuccess(Object message, ClassCode classCode){
        switch (classCode.getCode()) {
            case ClassCodes.STRING_CLASS_VALUE:
                return (T) String.valueOf(message);
            case ClassCodes.INT_CLASS_VALUE:
                return (T) (Integer) message;
            case ClassCodes.EVENT_CLASS_VALUE:
                return (T) objectParser.parseEvent((JSONObject) message);
            case ClassCodes.ACTIVITY_CLASS_VALUE:
                return null;
            case ClassCodes.USER_CLASS_VALUE:
                return null;
            case ClassCodes.VISIT_CLASS_VALUE:
                return null;
            case ClassCodes.ACTIVITY_ARRAYLIST_CLASS_VALUE:
                return null;
            case ClassCodes.USER_ARRAYLIST_CLASS_VALUE:
                return null;
            case ClassCodes.VISIT_ARRAYLIST_CLASS_VALUE:
                return null;
            case ClassCodes.EVENT_ARRAYLIST_CLASS_VALUE:
                return (T) objectParser.parseEvents((JSONArray) message);
            default:
                return null;
        }
    }

    @Override
    public void addEvent(Event event, final DatabaseCallback<String> callback) {
        sendJsonObjectRequest(
            Request.Method.POST,
            "/events/addEvent",
            JSONObjectParsing.unpackEvent(event),
            callback,
            ClassCodes.STRING_CLASS
        );
    }

    @Override
    public void getEventByID(int eventID, DatabaseCallback<Event> callback) {
        sendJsonObjectRequest(
                Request.Method.GET,
                "/events/getByID/" + Integer.valueOf(eventID),
                null,
                callback,
                ClassCodes.EVENT_CLASS
        );
    }

    @Override
    public void getAllEvents(DatabaseCallback<ArrayList<Event>> callback){
        sendJsonObjectRequest(
                Request.Method.GET,
                "/events/getAll",
                null,
                callback,
                ClassCodes.EVENT_ARRAYLIST_CLASS
        );
    }

    @Override
    public void joinEvent(User user, Event event, DatabaseCallback<String> callback) {
        sendJsonObjectRequest(
                Request.Method.POST,
                "/events/joinEvent",
                objectParser.buildUserEventObject(event, user),
                callback,
                ClassCodes.STRING_CLASS
        );
    }

    @Override
    public void getUsersAtEvent(Event event, DatabaseCallback<ArrayList<User>> callback) {

    }

    @Override
    public void getJoinedEvents(User user, DatabaseCallback<ArrayList<Event>> callback) {

    }

    @Override
    public void addActivity(Activity activity, DatabaseCallback<String> callback) {

    }

    @Override
    public void getActivityByID(int activityID, DatabaseCallback<Activity> callback) {

    }

    @Override
    public void getAllActivities(Event event, DatabaseCallback<ArrayList<Activity>> callback) {

    }

    @Override
    public void addVisit(Visit visit, DatabaseCallback<String> callback) {

    }

    @Override
    public void getVisitByID(int userID, int activityID, DatabaseCallback<Visit> callback) {

    }

    @Override
    public void visitCountForUser(int userID, DatabaseCallback<Integer> callback) {
        sendJsonObjectRequest(
                Request.Method.GET,
                "/activities/visitCountForUser/" + Integer.toString(userID),
                null,
                callback,
                ClassCodes.INT_CLASS
        );
    }

    @Override
    public void visitCountForUserAtEvent(User user, Event event, DatabaseCallback<Integer> callback) {
        sendJsonObjectRequest(
                Request.Method.GET,
                "/activities/visitCountForUserAtEvent",
                objectParser.buildUserEventObject(event, user),
                callback,
                ClassCodes.INT_CLASS
        );
    }

    @Override
    public void visitCountAtActivity(int activityID, DatabaseCallback<Integer> callback) {
        sendJsonObjectRequest(
                Request.Method.GET,
                "/activities/visitCountAtActivity/" + Integer.toString(activityID),
                null,
                callback,
                ClassCodes.INT_CLASS
        );
    }

    @Override
    public void addUser(User user, DatabaseCallback<String> callback) {

    }

    @Override
    public void getUserByID(int userID, DatabaseCallback<User> callback) {

    }

    @Override
    public void getAllUsers(DatabaseCallback<ArrayList<User>> callback) {

    }

    @Override
    public void verifyPassword(String password, User user, DatabaseCallback<String> callback) {

    }

    @Override
    public void activitiesAtLocation(GPSLocation location, Event event, DatabaseCallback<ArrayList<Activity>> callback) {

    }
}

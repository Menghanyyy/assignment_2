package com.example.myapplication.database;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.example.myapplication.component.Activity;
import com.example.myapplication.component.Event;
import com.example.myapplication.component.User;
import com.example.myapplication.component.Visit;
import com.example.myapplication.location.GPSLocation;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Will implement the java interface
public class DatabaseManager implements DatabaseInterface {

    private RequestQueue requestQueue;
    private Context context;

    JSONObjectParsing objectParser = new JSONObjectParsing();
    private static String baseUrl = "http://comp90018.us.to:8080";

    public DatabaseManager(Context context) {
        // Initialize the Volley RequestQueue
        requestQueue = Volley.newRequestQueue(context);
        this.context = context;
    }

    @Override
    public void addEvent(Event event, final DatabaseCallback<Boolean> callback) {
        String url = baseUrl + "/events/addEvent";

        // Create a JSON object from the Event object using your JSON serialization method
        JSONObject eventJson = JSONObjectParsing.unpackEvent(event);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                eventJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.get("status") == "success"){
                                callback.onSuccess(true);
                            } else{
                                callback.onError((String) response.get("message"));
                            }
                        } catch (JSONException e) {
                            callback.onError(e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error response
                        String errorMessage = "Error adding event: " + error.getMessage();
                        callback.onError(errorMessage);
                    }
                });

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this.context).add(jsonObjectRequest);
    }

    @Override
    public void getEventByID(int eventID, DatabaseCallback<Event> callback) {
        String url = baseUrl + "/events/getByID/" + Integer.valueOf(eventID);

        // Create a RequestQueue if it's not already initialized
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

        // Create a JsonRequest for the GET request
        JsonRequest<JSONObject> jsonRequest = new JsonRequest<JSONObject>(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            Event event = null;
                            event = objectParser.parseEvent(response, eventID);
                            callback.onSuccess(event);
                        } else {
                            callback.onError("Received nothing from DB");
                        }
                    }
                },
                e -> callback.onError(e.getMessage())) {
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

        // Add the JsonRequest to the RequestQueue
        requestQueue.add(jsonRequest);
    }

    @Override
    public void getAllEvents(DatabaseCallback<ArrayList<Event>> callback) {
        String url = baseUrl + "/events/getAll";

        // Create a RequestQueue if it's not already initialized
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

        // Create a JsonRequest for the GET request
        JsonRequest<JSONArray> jsonRequest = new JsonRequest<JSONArray>(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response != null) {
                            ArrayList<Event> events = objectParser.parseEvents(response);
                            callback.onSuccess(events);
                        } else {
                            callback.onError("Received nothing from DB");
                        }
                    }
                },
                e -> callback.onError(e.getMessage())) {
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                    return Response.success(new JSONArray(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException | JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };

        // Add the JsonRequest to the RequestQueue
        requestQueue.add(jsonRequest);
    }

    @Override
    public void joinEvent(User user, Event event, DatabaseCallback<Integer> callback) {
        String url = baseUrl + "/events/getAll";
    }

    @Override
    public void getUsersAtEvent(Event event, DatabaseCallback<Integer> callback) {

    }

    @Override
    public void getJoinedEvents(User user, DatabaseCallback<Integer> callback) {

    }

    @Override
    public void addActivity(Activity activity, DatabaseCallback<Integer> callback) {

    }

    @Override
    public void getActivityByID(int activityID, DatabaseCallback<Integer> callback) {

    }

    @Override
    public void getAllActivities(Event event, DatabaseCallback<Integer> callback) {

    }

    @Override
    public void addVisit(Visit visit, DatabaseCallback<Integer> callback) {

    }

    @Override
    public void getVisitByID(int userID, int activityID, DatabaseCallback<Integer> callback) {

    }

    @Override
    public void visitCountForUser(int userID, DatabaseCallback<Integer> callback) {
        String url = baseUrl + "/activities/visitCountForUser/" + Integer.toString(userID);

        // Create a RequestQueue if it's not already initialized
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }

        // Create a StringRequest for the GET request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    if (response != null) {
                        try {
                            int result = Integer.parseInt(response);
                            callback.onSuccess(result);
                        } catch (NumberFormatException e) {
                            callback.onError("Did not receive integer from DB");
                        }
                    } else {
                        callback.onError("Received nothing from DB");
                    }
                },
                e -> {
                    callback.onError(e.getMessage());
                });

        // Add the request to the RequestQueue
        requestQueue.add(stringRequest);
    }

    @Override
    public void visitCountForUserAtEvent(int userID, int eventID, DatabaseCallback<Integer> callback) {

    }

    @Override
    public void visitCountAtActivity(int activityID, DatabaseCallback<Integer> callback) {

    }

    @Override
    public void addUser(User user, DatabaseCallback<Integer> callback) {

    }

    @Override
    public void getUserByID(int userID, DatabaseCallback<Integer> callback) {

    }

    @Override
    public void getAllUsers(DatabaseCallback<Integer> callback) {

    }

    @Override
    public void verifyPassword(String password, User user, DatabaseCallback<Integer> callback) {

    }

    @Override
    public void activitiesAtLocation(GPSLocation location, Event event, DatabaseCallback<Integer> callback) {

    }
}

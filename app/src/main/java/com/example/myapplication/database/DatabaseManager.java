package com.example.myapplication.database;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.example.myapplication.component.Activity;
import com.example.myapplication.component.Event;
import com.example.myapplication.component.GeneralUser;
import com.example.myapplication.component.User;
import com.example.myapplication.component.Visit;
import com.example.myapplication.location.GPSLocation;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
    public void addEvent(Event event, DatabaseCallback<Integer> callback) {

    }

    @Override
    public void getEventByID(int eventID, DatabaseCallback<Integer> callback) {

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
                            ArrayList<Event> events = parseEvents(response);
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

    private ArrayList<Event> parseEvents(JSONArray jsonArray) {
        ArrayList<Event> events = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonEvent = jsonArray.getJSONObject(i);
                Event event = objectParser.parseEvent(jsonEvent);
                if (event != null) {
                    events.add(event);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return events;
    }

    @Override
    public void joinEvent(User user, Event event, DatabaseCallback<Integer> callback) {

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

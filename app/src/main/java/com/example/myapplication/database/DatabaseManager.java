package com.example.myapplication.database;

import android.content.Context;

import com.example.myapplication.component.Activity;
import com.example.myapplication.component.Event;
import com.example.myapplication.component.User;
import com.example.myapplication.component.Visit;
import com.example.myapplication.location.GPSLocation;

import java.util.ArrayList;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

// Will implement the java interface
public class DatabaseManager implements DatabaseInterface {

    private RequestQueue requestQueue;
    private Context context;
    private static String baseUrl = "http://comp90018.us.to:8080";

    public DatabaseManager(Context context) {
        // Initialize the Volley RequestQueue
        requestQueue = Volley.newRequestQueue(context);
        this.context = context;
    }

    public void dummy(final DatabaseCallback<Integer> callback) {

        String url = baseUrl + "/activities/visitCountForUser/1";

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
    public void addEvent(Event event, DatabaseCallback<Integer> callback) {

    }

    @Override
    public void getEventByID(int eventID, DatabaseCallback<Integer> callback) {

    }

    @Override
    public void getAllEvents(DatabaseCallback<Integer> callback) {

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

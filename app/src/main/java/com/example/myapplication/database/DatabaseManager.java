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
    public boolean addEvent(Event event, DatabaseCallback<Integer> callback) {
        return false;
    }

    @Override
    public Event getEventByID(int eventID, DatabaseCallback<Integer> callback) {
        return null;
    }

    @Override
    public ArrayList<Event> getAllEvents(DatabaseCallback<Integer> callback) {
        return null;
    }

    @Override
    public boolean joinEvent(User user, Event event, DatabaseCallback<Integer> callback) {
        return false;
    }

    @Override
    public ArrayList<User> getUsersAtEvent(Event event, DatabaseCallback<Integer> callback) {
        return null;
    }

    @Override
    public ArrayList<Event> getJoinedEvents(User user, DatabaseCallback<Integer> callback) {
        return null;
    }

    @Override
    public boolean addActivity(Activity activity, DatabaseCallback<Integer> callback) {
        return false;
    }

    @Override
    public Activity getActivityByID(int activityID, DatabaseCallback<Integer> callback) {
        return null;
    }

    @Override
    public ArrayList<Activity> getAllActivities(Event event, DatabaseCallback<Integer> callback) {
        return null;
    }

    @Override
    public boolean addVisit(Visit visit, DatabaseCallback<Integer> callback) {
        return false;
    }

    @Override
    public Visit getVisitByID(int userID, int activityID, DatabaseCallback<Integer> callback) {
        return null;
    }

    @Override
    public int visitCountForUser(int userID, DatabaseCallback<Integer> callback) {
        return 0;
    }

    @Override
    public int visitCountForUserAtEvent(int userID, int eventID, DatabaseCallback<Integer> callback) {
        return 0;
    }

    @Override
    public int visitCountAtActivity(int activityID, DatabaseCallback<Integer> callback) {
        return 0;
    }

    @Override
    public boolean addUser(User user, DatabaseCallback<Integer> callback) {
        return false;
    }

    @Override
    public User getUserByID(int userID, DatabaseCallback<Integer> callback) {
        return null;
    }

    @Override
    public ArrayList<User> getAllUsers(DatabaseCallback<Integer> callback) {
        return null;
    }

    @Override
    public boolean verifyPassword(String password, User user, DatabaseCallback<Integer> callback) {
        return false;
    }

    @Override
    public ArrayList<Activity> activitiesAtLocation(GPSLocation location, Event event, DatabaseCallback<Integer> callback) {
        return null;
    }
}

package com.example.myapplication.database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.myapplication.component.Activity;
import com.example.myapplication.component.Event;
import com.example.myapplication.component.GeneralUser;
import com.example.myapplication.component.OrganisationUser;
import com.example.myapplication.component.User;
import com.example.myapplication.component.Visit;
import com.example.myapplication.location.GPSLocation;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

// Will implement the java interface
public class DatabaseManager implements DatabaseInterface {

    private RequestQueue requestQueue;
    private Context context;

    String baseUrl = "http://comp90018.us.to:8080";

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
    public boolean addEvent(Event event) {
        return false;
    }

    @Override
    public Event getEventByID(int eventID) {
        return null;
    }

    @Override
    public ArrayList<Event> getAllEvents() {
        return null;
    }

    @Override
    public boolean joinEvent(User user, Event event) {
        return false;
    }

    @Override
    public ArrayList<User> getUsersAtEvent(Event event) {
        return null;
    }

    @Override
    public ArrayList<Event> getJoinedEvents(User user) {
        return null;
    }

    @Override
    public boolean addActivity(Activity activity) {
        return false;
    }

    @Override
    public Activity getActivityByID(int activityID) {
        return null;
    }

    @Override
    public ArrayList<Activity> getAllActivities(Event event) {
        return null;
    }

    @Override
    public boolean addVisit(Visit visit) {
        return false;
    }

    @Override
    public Visit getVisitByID(int userID, int activityID) {
        return null;
    }

    @Override
    public int visitCountForUser(int userID) {
        return 0;
    }

    @Override
    public int visitCountAtActivity(int activityID) {
        return 0;
    }

    @Override
    public int visitCountForUserAtEvent(int userID, int eventID) {
        return 0;
    }

    @Override
    public boolean addUser(User user) {
        return false;
    }

    @Override
    public User getUserByID(int userID) {
        return null;
    }

    @Override
    public ArrayList<User> getAllUsers() {
        return null;
    }

    @Override
    public boolean verifyPassword(String password, User user) {
        return false;
    }

    @Override
    public ArrayList<Activity> activitiesAtLocation(GPSLocation location, Event event) {
        return null;
    }
}

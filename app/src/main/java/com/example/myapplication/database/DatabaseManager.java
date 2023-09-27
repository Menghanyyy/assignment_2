package com.example.myapplication.database;

import com.example.myapplication.component.Activity;
import com.example.myapplication.component.Event;
import com.example.myapplication.component.GeneralUser;
import com.example.myapplication.component.OrganisationUser;
import com.example.myapplication.component.User;
import com.example.myapplication.component.Visit;
import com.example.myapplication.location.GPSLocation;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

// Will implement the java interface
public class DatabaseManager implements DatabaseInterface {

    HttpURLConnection urlConnection = null;
    String baseUrl = "http://comp90018.us.to:8080";

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
        try {
            URL url = new URL(baseUrl + "/events/addEvent");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */ );
            urlConnection.setConnectTimeout(15000 /* milliseconds */ );
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();

            String jsonString = sb.toString();
            System.out.println("JSON: " + jsonString);

            System.out.println(new JSONObject(jsonString));

            return null;

        } catch (Exception e) {
            return null;
        }
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

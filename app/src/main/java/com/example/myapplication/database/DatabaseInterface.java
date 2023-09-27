package com.example.myapplication.database;

import com.example.myapplication.component.*;
import com.example.myapplication.location.GPSLocation;

import java.util.ArrayList;

public interface DatabaseInterface {

    ///////////////// Event Methods /////////////////

    public boolean addEvent(Event event, final DatabaseCallback<Integer> callback);

    public Event getEventByID(int eventID, final DatabaseCallback<Integer> callback);

    public ArrayList<Event> getAllEvents(final DatabaseCallback<Integer> callback);

    // Users signing up to attend an event
    public boolean joinEvent(User user, Event event, final DatabaseCallback<Integer> callback);

    // Returns all users currently signed up for an event
    public ArrayList<User> getUsersAtEvent(Event event, final DatabaseCallback<Integer> callback);

    // Returns all events an individual has signed up for
    public ArrayList<Event> getJoinedEvents(User user, final DatabaseCallback<Integer> callback);



    ///////////////// Activity Methods /////////////////

    public boolean addActivity(Activity activity, final DatabaseCallback<Integer> callback);

    public Activity getActivityByID(int activityID, final DatabaseCallback<Integer> callback);

    public ArrayList<Activity> getAllActivities(Event event, final DatabaseCallback<Integer> callback);

    // Visit (Visit class includes user/event details)
    public boolean addVisit(Visit visit, final DatabaseCallback<Integer> callback);

    public Visit getVisitByID(int userID, int activityID, final DatabaseCallback<Integer> callback);

    // Gives the total visits for a specific user
    public int visitCountForUser(int userID, final DatabaseCallback<Integer> callback);

    // Gives the total visits at an event for a specific user
    public int visitCountForUserAtEvent(int userID, int eventID, final DatabaseCallback<Integer> callback);

    // Gives the total visits for a specific activity
    public int visitCountAtActivity(int activityID, final DatabaseCallback<Integer> callback);



    ///////////////// User Methods /////////////////

    public boolean addUser(User user, final DatabaseCallback<Integer> callback);

    public User getUserByID(int userID, final DatabaseCallback<Integer> callback);

    public ArrayList<User> getAllUsers(final DatabaseCallback<Integer> callback);

    public boolean verifyPassword(String password, User user, final DatabaseCallback<Integer> callback);



    ///////////////// Location Methods /////////////////

    // Gives all the activities (in a given event) that overlap a point on the map
    public ArrayList<Activity> activitiesAtLocation(GPSLocation location, Event event, final DatabaseCallback<Integer> callback);

}

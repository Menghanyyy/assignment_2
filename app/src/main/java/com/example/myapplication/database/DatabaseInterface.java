package com.example.myapplication.database;

import com.example.myapplication.component.*;
import com.example.myapplication.location.GPSLocation;

import java.util.ArrayList;

public interface DatabaseInterface {

    ///////////////// Event Methods /////////////////

    public void addEvent(Event event, final DatabaseCallback<Boolean> callback);

    public void getEventByID(int eventID, final DatabaseCallback<Event> callback);

    public void getAllEvents(final DatabaseCallback<ArrayList<Event>> callback);

    // Users signing up to attend an event
    public void joinEvent(User user, Event event, final DatabaseCallback<Integer> callback);

    // Returns all users currently signed up for an event
    public void getUsersAtEvent(Event event, final DatabaseCallback<Integer> callback);

    // Returns all events an individual has signed up for
    public void getJoinedEvents(User user, final DatabaseCallback<Integer> callback);



    ///////////////// Activity Methods /////////////////

    public void addActivity(Activity activity, final DatabaseCallback<Integer> callback);

    public void getActivityByID(int activityID, final DatabaseCallback<Integer> callback);

    public void getAllActivities(Event event, final DatabaseCallback<Integer> callback);

    // Visit (Visit class includes user/event details)
    public void addVisit(Visit visit, final DatabaseCallback<Integer> callback);

    public void getVisitByID(int userID, int activityID, final DatabaseCallback<Integer> callback);

    // Gives the total visits for a specific user
    public void visitCountForUser(int userID, final DatabaseCallback<Integer> callback);

    // Gives the total visits at an event for a specific user
    public void visitCountForUserAtEvent(int userID, int eventID, final DatabaseCallback<Integer> callback);

    // Gives the total visits for a specific activity
    public void visitCountAtActivity(int activityID, final DatabaseCallback<Integer> callback);



    ///////////////// User Methods /////////////////

    public void addUser(User user, final DatabaseCallback<Integer> callback);

    public void getUserByID(int userID, final DatabaseCallback<Integer> callback);

    public void getAllUsers(final DatabaseCallback<Integer> callback);

    public void verifyPassword(String password, User user, final DatabaseCallback<Integer> callback);



    ///////////////// Location Methods /////////////////

    // Gives all the activities (in a given event) that overlap a point on the map
    public void activitiesAtLocation(GPSLocation location, Event event, final DatabaseCallback<Integer> callback);

}

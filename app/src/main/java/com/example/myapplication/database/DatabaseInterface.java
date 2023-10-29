package com.example.myapplication.database;

import com.example.myapplication.component.*;
import com.example.myapplication.location.GPSLocation;

import java.util.ArrayList;

public interface DatabaseInterface {

    ///////////////// Event Methods /////////////////

    public void addEvent(Event event, final DatabaseCallback<String> callback);

    public void getEventByID(int eventID, final DatabaseCallback<Event> callback);

    void getEventLinkByID(int eventID, DatabaseCallback<String> callback);

    public void getAllEvents(final DatabaseCallback<ArrayList<Event>> callback);

    // Users signing up to attend an event
    public void joinEvent(User user, Event event, final DatabaseCallback<String> callback);

    // Returns all users currently signed up for an event
    public void getUsersAtEvent(Event event, final DatabaseCallback<ArrayList<User>> callback);

    // Returns all events an individual has signed up for
    public void getJoinedEvents(User user, final DatabaseCallback<ArrayList<Event>> callback);



    ///////////////// Activity Methods /////////////////

    void getCreatedEvents(User user, DatabaseCallback<ArrayList<Event>> callback);

    public void addActivity(Activity activity, final DatabaseCallback<String> callback);

    public void getActivityByID(int activityID, final DatabaseCallback<Activity> callback);

    public void getAllActivities(String eventId, final DatabaseCallback<ArrayList<Activity>> callback);

    // Visit (Visit class includes user/event details)
    public void addVisit(Visit visit, final DatabaseCallback<String> callback);

    public void getVisitByID(int userID, int activityID, final DatabaseCallback<Visit> callback);

    // Gives the total visits for a specific user
    public void visitCountForUser(int userID, final DatabaseCallback<Integer> callback);

    // Gives the total visits at an event for a specific user
    public void visitCountForUserAtEvent(User user, Event event, final DatabaseCallback<Integer> callback);

    // Gives the total visits for a specific activity
    public void visitCountAtActivity(int activityID, final DatabaseCallback<Integer> callback);



    ///////////////// User Methods /////////////////

    public void addUser(GeneralUser user, final DatabaseCallback<String> callback);

    public void getUserByID(int userID, final DatabaseCallback<GeneralUser> callback);

    public void getAllUsers(final DatabaseCallback<ArrayList<GeneralUser>> callback);

    public void verifyPassword(String password, String username, final DatabaseCallback<String> callback);



    ///////////////// Location Methods /////////////////

    // Gives all the activities (in a given event) that overlap a point on the map
    public void activitiesAtLocation(GPSLocation location, Event event, final DatabaseCallback<ArrayList<Activity>> callback);

}

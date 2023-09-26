package com.example.myapplication.database;

import com.example.myapplication.component.*;
import com.example.myapplication.location.GPSLocation;

import java.util.ArrayList;

public interface DatabaseInterface {

    ///////////////// Event Methods /////////////////

    public boolean addEvent(Event event);

    public Event getEventByID(int eventID);

    public ArrayList<Event> getAllEvents();

    // Users signing up to attend an event
    public boolean joinEvent(User user, Event event);

    // Returns all users currently signed up for an event
    public ArrayList<User> getUsersAtEvent(Event event);

    // Returns all events an individual has signed up for
    public ArrayList<Event> getJoinedEvents(User user);



    ///////////////// Activity Methods /////////////////

    public boolean addActivity(Activity activity);

    public Activity getActivityByID(int activityID);

    public ArrayList<Activity> getAllActivities(Event event);

    // Visit (Visit class includes user/event details)
    public boolean addVisit(Visit visit);

    public Visit getVisitByID(int userID, int activityID);

    // Gives the total visits for a specific user
    public int visitCountForUser(int userID);

    // Gives the total visits at an event for a specific user
    public int visitCountForUserAtEvent(int userID, int eventID);

    // Gives the total visits for a specific activity
    public int visitCountAtActivity(int activityID);



    ///////////////// User Methods /////////////////

    public boolean addUser(User user);

    public User getUserByID(int userID);

    public ArrayList<User> getAllUsers();

    public boolean verifyPassword(String password, User user);



    ///////////////// Location Methods /////////////////

    // Gives all the activities (in a given event) that overlap a point on the map
    public ArrayList<Activity> activitiesAtLocation(GPSLocation location, Event event);

}

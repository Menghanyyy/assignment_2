package com.example.myapplication.database;

import com.example.myapplication.component.Activity;
import com.example.myapplication.component.Event;
import com.example.myapplication.component.GeneralUser;
import com.example.myapplication.component.OrganisationUser;
import com.example.myapplication.component.User;
import com.example.myapplication.component.Visit;
import com.example.myapplication.location.GPSLocation;

import java.util.ArrayList;

// Will implement the java interface
public class DatabaseManager implements DatabaseInterface {

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
    public Activity getActivityByID(int eventID, int activityID) {
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
    public Visit getVisitByID(int userID, int activityID, int eventID) {
        return null;
    }

    @Override
    public int visitCountAtActivity(int eventID, int activityID) {
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
    public GeneralUser getUserByID(int userID) {
        return null;
    }

    @Override
    public ArrayList<GeneralUser> getAllUsers() {
        return null;
    }

    @Override
    public boolean verifyPassword(String password, User user) {
        return false;
    }

    @Override
    public ArrayList<Activity> eventsAtLocation(GPSLocation location, Event event) {
        return null;
    }
}

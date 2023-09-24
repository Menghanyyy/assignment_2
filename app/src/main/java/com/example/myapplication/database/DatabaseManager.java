package com.example.myapplication.database;

import com.example.myapplication.component.Activity;
import com.example.myapplication.component.Event;
import com.example.myapplication.component.GeneralUser;
import com.example.myapplication.component.OrganisationUser;
import com.example.myapplication.component.User;
import com.example.myapplication.component.Visit;

import java.util.ArrayList;

// Will implement the java interface
public class DatabaseManager implements DatabaseInterface {
    @Override
    public boolean addEvent(Event event) {
        return false;
    }

    @Override
    public Event getEventByID(int organisationID, int eventID) {
        return null;
    }

    @Override
    public ArrayList<Event> getAllEvents() {
        return null;
    }

    @Override
    public boolean addActivity(Activity activity) {
        return false;
    }

    @Override
    public Activity getActivityByID(int organisationID, int eventID, int activityID) {
        return null;
    }

    @Override
    public ArrayList<Activity> getAllActivities() {
        return null;
    }

    @Override
    public boolean addUser(GeneralUser user) {
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
    public boolean addCreator(OrganisationUser creator) {
        return false;
    }

    @Override
    public OrganisationUser getCreatorByID(int creatorID) {
        return null;
    }

    @Override
    public ArrayList<OrganisationUser> getAllCreators() {
        return null;
    }

    @Override
    public boolean addVisit(Visit visit) {
        return false;
    }

    @Override
    public Visit getVisitByID(int userID, int activityID, int eventID, int organisationID) {
        return null;
    }

    @Override
    public int visitCountAtActivity(int organisationID, int eventID, int activityID) {
        return 0;
    }

    @Override
    public int visitCountForUserAtEvent(int userID, int organisationID, int eventID) {
        return 0;
    }

    @Override
    public boolean joinEvent(GeneralUser user, Event event) {
        return false;
    }

    @Override
    public boolean quitEvent(GeneralUser user, Event event) {
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
}

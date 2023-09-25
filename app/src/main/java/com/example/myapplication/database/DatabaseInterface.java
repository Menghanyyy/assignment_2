package com.example.myapplication.database;

import com.example.myapplication.component.*;

import java.util.ArrayList;

public interface DatabaseInterface {

    // Event
    public boolean addEvent(Event event);

    public Event getEventByID(int organisationID, int eventID);

    public ArrayList<Event> getAllEvents();


    // Activity
    public boolean addActivity(Activity activity);

    public Activity getActivityByID(int organisationID, int eventID, int activityID);

    public ArrayList<Activity> getAllActivities();



    // User (General)
    public boolean addUser(GeneralUser user);

    public GeneralUser getUserByID(int userID);

    public ArrayList<GeneralUser> getAllUsers();


    // Creator (Organisation User)
    public boolean addCreator(OrganisationUser creator);

    public OrganisationUser getCreatorByID(int creatorID);

    public ArrayList<OrganisationUser> getAllCreators();


    // Visit
    public boolean addVisit(Visit visit);

    public Visit getVisitByID(int userID, int activityID, int eventID, int organisationID);

    // Gives the total visits for a specific activity
    public int visitCountAtActivity(int organisationID, int eventID, int activityID);

    // Gives the total visits at an event for a specific user
    public int visitCountForUserAtEvent(int userID, int organisationID, int eventID);


    // Join (Users signing up for events)
    public boolean joinEvent(GeneralUser user, Event event);

    public boolean quitEvent(GeneralUser user, Event event);

    // Returns all users currently signed up for an event
    public ArrayList<User> getUsersAtEvent(Event event);

    // Returns all events an individual has signed up for
    public ArrayList<Event> getJoinedEvents(User user);
}

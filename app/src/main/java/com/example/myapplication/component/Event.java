package com.example.myapplication.component;

import com.mapbox.geojson.Point;
import java.util.ArrayList;
import java.util.List;

public class Event {

    private String eventId;
    private String eventName;
    private User eventOrganiser;
    private Point eventLocation;
    private List<Point> eventRange;
    private List<Activity> eventActivity;
    private List<Visit> eventVisit;

    // Optional, refers to company/organisation running event
    private String organisationName;

    private String description;

    public Event(String EventID, String EventName, User EventOrganiser, Point EventLocation,
                 List<Point> EventRange, String organisationName, String description) {

        this.eventId = EventID;
        this.eventName = EventName;
        this.eventOrganiser = EventOrganiser;
        this.eventLocation = EventLocation;
        this.eventRange = EventRange;
        this.eventActivity = new ArrayList<Activity>();
        this.eventVisit = new ArrayList<Visit>();
        this.organisationName = organisationName;
        this.description = description;
    }

    public String getEventId() {
        return this.eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return this.eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public User getEventOrganiser() {
        return this.eventOrganiser;
    }

    public void setEventOrganiser(User eventOrganiser) {
        this.eventOrganiser = eventOrganiser;
    }

    public Point getEventLocation() {
        return this.eventLocation;
    }

    public void setEventLocation( Point eventLocation) {
        this.eventLocation = eventLocation;
    }

    public List<Point> getEventRange() {
        return this.eventRange;
    }

    public void setEventRange(List<Point> eventRange) {
        this.eventRange = eventRange;
    }

    public String getOrganisationName() { return this.organisationName; }

    public void setOrganisationName(String organisationName) { this.organisationName = organisationName; }

    public List<Activity> getEventActivity() {
        return this.eventActivity;
    }

    public boolean addEventActivity(Activity eventActivity) {

        // checking repeating if needed
        return this.eventActivity.add(eventActivity);

    }

    public List<Visit> getEventVisit() {
        return this.eventVisit;
    }

    public boolean addEventVisit(Visit visit) {

        // checking repeating if needed
        return this.eventVisit.add(visit);
    }

    @Override
    public String toString() {

        String result = this.eventName + " Organise By " + this.eventOrganiser;
        return result;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

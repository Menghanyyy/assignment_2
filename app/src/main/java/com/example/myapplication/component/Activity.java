package com.example.myapplication.component;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class Activity {

    private String activityId;
    private String activityName;
    private User activityOrganiser;
    private Event hostedEvent;
    private Point activityLocation;
    private List<Point> activityRange;
    private List<Visit> activityVisits;

    public Activity(String ActivityId, String ActivityName, User ActivityOrganiser, Event HostedEvent, Point ActivityLocation, List<Point> ActivityRange) {
        this.activityId = ActivityId;
        this.activityName = ActivityName;
        this.activityOrganiser = ActivityOrganiser;
        this.hostedEvent = HostedEvent;
        this.activityLocation = ActivityLocation;
        this.activityRange = ActivityRange;

        this.activityVisits = new ArrayList<Visit>();
    }

    public String getActivityId() {
        return this.activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return this.activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public User getActivityOrganiser() {
        return this.activityOrganiser;
    }

    public void setActivityOrganiser(User activityOrganiser) {
        this.activityOrganiser = activityOrganiser;
    }

    public Event getHostedEvent() {
        return this.hostedEvent;
    }

    public void setHostedEvent(Event hostedEvent) {
        this.hostedEvent = hostedEvent;
    }

    public Point getActivityLocation() {
        return this.activityLocation;
    }

    public void setActivityLocation(Point activityLocation) {
        this.activityLocation = activityLocation;
    }

    public List<Point> getActivityRange() {
        return this.activityRange;
    }

    public void setActivityRange(List<Point> activityRange) {
        this.activityRange = activityRange;
    }

    public List<Visit> getActivityVisits() {
        return this.activityVisits;
    }

    public boolean addActivityVisit (Visit visit) {

        return this.activityVisits.add(visit);
    }

    @Override
    public String toString() {
        String result = this.activityName + " Hosting In " + this.hostedEvent + " Organise By " + this.activityOrganiser;
        return result;
    }
}

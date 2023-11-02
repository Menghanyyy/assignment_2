package com.example.myapplication.component;

import com.mapbox.geojson.Point;

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

    private String description;

    private String locationName;

    private List<Point> bbox;

    private String startTime;
    private String endTime;
    private String image;

    private int creatorID;

    public Activity(String ActivityId, String ActivityName, User ActivityOrganiser,
                    Event HostedEvent, Point ActivityLocation, List<Point> ActivityRange,
                    String description, String locationName, List<Point> bbox,
                    String startTime, String endTime, String image, int CreatorID) {
        this.activityId = ActivityId;
        this.activityName = ActivityName;
        this.activityOrganiser = ActivityOrganiser;
        this.hostedEvent = HostedEvent;
        this.activityLocation = ActivityLocation;
        this.activityRange = ActivityRange;
        this.description = description;
        this.activityVisits = new ArrayList<Visit>();
        this.locationName = locationName;
        this.bbox = bbox;
        this.startTime = startTime;
        this.endTime = endTime;
        this.image = image;
        this.creatorID = CreatorID;
    }

    public Activity(String ActivityId, String ActivityName, User ActivityOrganiser,
                    Event HostedEvent, Point ActivityLocation, List<Point> ActivityRange,
                    String description, String locationName, List<Point> bbox,
                    String startTime, String endTime, String image) {
        this.activityId = ActivityId;
        this.activityName = ActivityName;
        this.activityOrganiser = ActivityOrganiser;
        this.hostedEvent = HostedEvent;
        this.activityLocation = ActivityLocation;
        this.activityRange = ActivityRange;
        this.description = description;
        this.activityVisits = new ArrayList<Visit>();
        this.locationName = locationName;
        this.bbox = bbox;
        this.startTime = startTime;
        this.endTime = endTime;
        this.image = image;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public List<Point> getBbox() {
        return bbox;
    }

    public void setBbox(List<Point> bbox) {
        this.bbox = bbox;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        String result = this.activityName + " Hosting In " + this.hostedEvent + " Organise By " + this.activityOrganiser;
        return result;
    }
}

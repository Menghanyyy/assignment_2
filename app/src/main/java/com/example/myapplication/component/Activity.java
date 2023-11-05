package com.example.myapplication.component;

import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.List;

public class Activity {

    private String activityId;
    private String activityName;
    private User activityCreator;
    private Event hostedEvent;
    private Point activityLocation;
    private List<Point> activityRange;
    private List<Visit> activityVisits;

    private String activityOrganisation;

    private String description;
    private String locationName;

    private List<Point> bbox;

    private String startTime;
    private String endTime;
    private String image;

    private int creatorID;


    // when creating
    public Activity(String ActivityName, User ActivityCreator,
                    Event HostedEvent, Point ActivityLocation, List<Point> ActivityRange,
                    String description, String LocationName, String ActivityOrganisation,
                    String StartTime, String EndTime, String Image) {

        this.activityId = "Unknown";
        this.activityName = ActivityName;
        this.activityCreator = ActivityCreator;
        this.hostedEvent = HostedEvent;
        this.activityLocation = ActivityLocation;
        this.activityRange = ActivityRange;
        this.description = description;
        this.activityVisits = new ArrayList<Visit>();
        this.locationName = LocationName;
        this.activityOrganisation = ActivityOrganisation;
        this.bbox = new ArrayList<>();
        this.startTime = StartTime;
        this.endTime = EndTime;
        this.image = Image;
        this.creatorID = Integer.parseInt(ActivityCreator.getUserId());
    }

    // use to call back from api
    public Activity(String ActivityId, String ActivityName,
                    Point ActivityLocation, List<Point> ActivityRange,
                    String description, String locationName,
                    String startTime, String endTime, String image, int CreatorID) {

        this.activityId = ActivityId;
        this.activityName = ActivityName;
        this.activityCreator = null;
        this.hostedEvent = null;
        this.activityLocation = ActivityLocation;
        this.activityRange = ActivityRange;
        this.description = description;
        this.activityVisits = new ArrayList<Visit>();
        this.locationName = locationName;
        this.bbox = null;
        this.startTime = startTime;
        this.endTime = endTime;
        this.image = image;
        this.creatorID = CreatorID;
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

    public User getActivityCreator() {
        return this.activityCreator;
    }

    public void setActivityCreator(User activityCreator) {
        this.activityCreator = activityCreator;
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

    public String getActivityOrganisation() {
        return activityOrganisation;
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
        String result = this.activityName + " Hosting In " + this.hostedEvent + " Organise By " + this.activityCreator;
        return result;
    }
}

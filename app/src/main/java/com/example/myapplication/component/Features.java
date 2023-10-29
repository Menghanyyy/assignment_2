package com.example.myapplication.component;

import androidx.annotation.NonNull;

public class Features {
    private double distance;
    private int activityID;
    private int eventID;
    private int visited;

    public Features(double distance, int activityID, int eventID, int visited) {
        this.distance = distance;
        this.activityID = activityID;
        this.eventID = eventID;
        this.visited = visited;
    }

    public double getDistance() {
        return distance;
    }

    public int getActivityID() {
        return activityID;
    }

    public int getEventID() {
        return eventID;
    }

    public int getVisited() {
        return visited;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

    // Getters and setters for the class variables
    // ...
}

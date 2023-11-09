package com.example.myapplication.component;

import androidx.annotation.NonNull;

/** Features Object Class
 * Use to present the features that return from the mapbox api detection **/

public class Features {

    /** Class Field **/
    private double distance;
    private int activityID;
    private int eventID;
    private int visited;

    /** Constructors**/
    public Features(double distance, int activityID, int eventID, int visited) {
        this.distance = distance;
        this.activityID = activityID;
        this.eventID = eventID;
        this.visited = visited;
    }

    /** Getter and Setter **/

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

}

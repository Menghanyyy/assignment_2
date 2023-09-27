package com.latti31.springeventserver.objects;

public class Location {
    private final double longitude;
    private final double latitude;
    public Location(double latitude, double longitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public double getLongitude(){ return longitude; }
    public double getLatitude(){ return latitude; }
}

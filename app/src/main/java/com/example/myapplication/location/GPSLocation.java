package com.example.myapplication.location;

public class GPSLocation {
    private final double longitude;
    private final double latitude;
    public GPSLocation(double latitude, double longitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public double getLongitude(){ return longitude; }
    public double getLatitude(){ return latitude; }
}

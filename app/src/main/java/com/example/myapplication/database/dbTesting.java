package com.example.myapplication.database;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.myapplication.component.Activity;
import com.example.myapplication.component.Event;
import com.example.myapplication.component.GeneralUser;
import com.example.myapplication.component.User;
import com.example.myapplication.component.Visit;

import java.util.ArrayList;
import java.util.List;
import com.mapbox.geojson.Point;

import java.util.Random;

public class dbTesting {

    JSONObjectParsing jp = new JSONObjectParsing();


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void runTests(Context context){
        // Testing Database Connection
        DatabaseManager databaseManager = new DatabaseManager(context);

        GeneralUser testUser = new GeneralUser(
                "1",
                "zara",
                "zara.com",
                "Password"
        );


        List<Point> dummyRange = createCirclePolygon(144.9631, -37.8136, 10, 3);
        // Adding some sample points
//        dummyRange.add(new PointF(0, 0));
//        dummyRange.add(new PointF(1, 1));
//        dummyRange.add(new PointF(2, 2));
//        dummyRange.add(new PointF(3, 3));
//        dummyRange.add(new PointF(4, 4));

        Event testEvent = new Event(
                "4",
                "EVENT: " + Integer.toString(new Random().nextInt(10000000)),
                testUser,
                null,
                dummyRange,
                "Unimelb",
                "A big event"
        );

        Activity testActivity = new Activity(
                null,
                "Random activity",
                testUser,
                testEvent,
                null,
                dummyRange,
                "A great activity",
                "Melbourne",
                dummyRange,
                "2023-09-21T12:00:00Z",
                "2023-09-21T12:00:00Z",
                "/9j/4AAQSkZJRgABAQEAAAAAAAD/4QBYRXhpZgAATU0AKgAAAAgAAkAAAAMAAAABAAEAQAAEAA"
                );

        Visit testVisit = new Visit(
                "1",
                "1",
                null,
                null
        );

        // Event Tests
        if (true) {
            databaseManager.addEvent(testEvent, new DatabaseCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.i("On success", result);
                }

                @Override
                public void onError(String error) {
                    Log.println(Log.ASSERT, "Error adding event:", error);
                }
            });

            databaseManager.getEventByID(1, new DatabaseCallback<Event>() {
                @Override
                public void onSuccess(Event result) {
                    Log.i("get event by id", String.valueOf(result.getEventName()));
                }

                @Override
                public void onError(String error) {
                    Log.println(Log.ASSERT, "Error Retrieving json", error);
                }
            });

            databaseManager.getAllEvents(new DatabaseCallback<ArrayList<Event>>() {
                @Override
                public void onSuccess(ArrayList<Event> result) {
                    Log.i("get all events", String.valueOf(result.size()));
                }

                @Override
                public void onError(String error) {
                    Log.println(Log.ASSERT, "Error getting count", error);
                }
            });

            databaseManager.joinEvent(testUser, testEvent, new DatabaseCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.i("join event success", result.toString());
                }

                @Override
                public void onError(String error) {
                    Log.println(Log.ASSERT, "Error joining", error);
                }
            });

            databaseManager.getUsersAtEvent(testEvent, new DatabaseCallback<ArrayList<User>>() {
                @Override
                public void onSuccess(ArrayList<User> result) {
                    Log.i("get Users at event", result.get(0).getUserName());
                }

                @Override
                public void onError(String error) {
                    Log.println(Log.ASSERT, "Error with event users", error);
                }
            });

            databaseManager.getJoinedEvents(testUser, new DatabaseCallback<ArrayList<Event>>() {
                @Override
                public void onSuccess(ArrayList<Event> result) {
                    Log.i("get joined events", result.get(0).getEventName());
                }

                @Override
                public void onError(String error) {
                    Log.println(Log.ASSERT, "Error joined events", error);
                }
            });
        }

        // User Tests
        if (true){
            databaseManager.addUser(testUser, new DatabaseCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.i("add User", result);
                }

                @Override
                public void onError(String error) {
                    Log.println(Log.ASSERT, "Error adding user", error);
                }
            });

            databaseManager.getUserByID(1, new DatabaseCallback<GeneralUser>() {
                @Override
                public void onSuccess(GeneralUser result) {
                    Log.i("get User by ID", result.getUserId());
                }

                @Override
                public void onError(String error) {
                    Log.println(Log.ASSERT, "Error getting user", error);
                }
            });

            databaseManager.getAllUsers(new DatabaseCallback<ArrayList<GeneralUser>>() {
                @Override
                public void onSuccess(ArrayList<GeneralUser> result) {
                    Log.i("get all users", Integer.toString(result.size()));
                }

                @Override
                public void onError(String error) {
                    Log.println(Log.ASSERT, "Error getting users", error);
                }
            });

            databaseManager.verifyPassword("secret2", testUser, new DatabaseCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.i("verify password", result);
                }

                @Override
                public void onError(String error) {
                    Log.println(Log.ASSERT, "error verifying", error);
                }
            });
        }

        // Activity Tests
        if (true) {

            databaseManager.addActivity(testActivity, new DatabaseCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.i("Add activity", result);
                }

                @Override
                public void onError(String error) {
                    Log.println(Log.ASSERT, "Error adding activity", error);
                }
            });

            databaseManager.getActivityByID(1, new DatabaseCallback<Activity>() {
                @Override
                public void onSuccess(Activity result) {
                    Log.i("get activity by id", String.valueOf(result.getActivityRange()));
                }

                @Override
                public void onError(String error) {
                    Log.println(Log.ASSERT, "Error getting activity", error);
                }
            });

            databaseManager.getAllActivities(testEvent, new DatabaseCallback<ArrayList<Activity>>() {
                @Override
                public void onSuccess(ArrayList<Activity> result) {
                    Log.i("get activities", String.valueOf(result.size()));
                }

                @Override
                public void onError(String error) {
                    Log.println(Log.ASSERT, "Error get activities", error);
                }
            });

            databaseManager.addVisit(testVisit, new DatabaseCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.i("Add visit", result);
                }

                @Override
                public void onError(String error) {
                    Log.println(Log.ASSERT, "Error adding visit", error);
                }
            });

            databaseManager.getVisitByID(1, 1, new DatabaseCallback<Visit>() {
                @Override
                public void onSuccess(Visit result) {
                    Log.i("get visit by id", String.valueOf(result.getVisitingTime()));
                }

                @Override
                public void onError(String error) {
                    Log.println(Log.ASSERT, "Error getting visit", error);
                }
            });

            databaseManager.visitCountForUser(1, new DatabaseCallback<Integer>() {
                @Override
                public void onSuccess(Integer result) {
                    Log.i("Visit Count for user 1", String.valueOf(result));
                }

                @Override
                public void onError(String error) {
                    Log.println(Log.ASSERT, "Error getting count", error);
                }
            });

            databaseManager.visitCountForUserAtEvent(testUser, testEvent, new DatabaseCallback<Integer>() {
                @Override
                public void onSuccess(Integer result) {
                    Log.i("user at event count:", result.toString());
                }

                @Override
                public void onError(String error) {
                    Log.println(Log.ASSERT, "Error finding count", error);
                }
            });

            databaseManager.visitCountAtActivity(1, new DatabaseCallback<Integer>() {
                @Override
                public void onSuccess(Integer result) {
                    Log.i("Visit count act 1", result.toString());
                }

                @Override
                public void onError(String error) {
                    Log.println(Log.ASSERT, "Error getting act count", error);
                }
            });
        }

        // Location Tests
        if (false){
            System.out.println("Location tests");
        }
    }

    public static List<Point> createCirclePolygon(double lonCenter, double latCenter, double radiusKm, int numVertices) {
        List<Point> circlePoints = new ArrayList<>();

        // Convert radius from kilometers to degrees
        double radiusInDegrees = radiusKm / 111.32;  // Rough conversion factor; might be slightly inaccurate near poles

        double angleStep = 2 * Math.PI / numVertices;

        for (int i = 0; i < numVertices; i++) {
            double theta = i * angleStep;

            double dLat = radiusInDegrees * Math.sin(theta);
            double dLon = radiusInDegrees * Math.cos(theta) / Math.cos(Math.toRadians(latCenter));

            double lat = Math.round((latCenter + dLat) * 100.0) / 100.0;
            double lon = Math.round((lonCenter + dLon) * 100.0) / 100.0;

            circlePoints.add(Point.fromLngLat(lon,lat));
        }

        // Close the circle by adding the first point at the end
        circlePoints.add(circlePoints.get(0));
        return circlePoints;
    }


}

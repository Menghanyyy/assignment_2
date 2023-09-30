package com.example.myapplication.database;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;

import com.example.myapplication.component.Event;
import com.example.myapplication.component.GeneralUser;
import com.example.myapplication.component.User;

import java.util.ArrayList;
import java.util.List;

import java.util.Random;

public class dbTesting {

    JSONObjectParsing jp = new JSONObjectParsing();

    public void runTests(Context context){
        // Testing Database Connection
        DatabaseManager databaseManager = new DatabaseManager(context);

        GeneralUser testUser = new GeneralUser(
                "1",
                "zara",
                "zara.com",
                "Password"
        );

        List<Point> dummyRange = new ArrayList<>();
        // Adding some sample points
        dummyRange.add(new Point(0, 0));
        dummyRange.add(new Point(1, 1));
        dummyRange.add(new Point(2, 2));
        dummyRange.add(new Point(3, 3));
        dummyRange.add(new Point(4, 4));

        Event testEvent = new Event(
                "2",
                "EVENT: " + Integer.toString(new Random().nextInt(10000000)),
                testUser,
                null,
                dummyRange,
                "Unimelb",
                "A big event"
        );

        // Event Tests
        if (false) {
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
        if (false){
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
        if (false) {

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
}

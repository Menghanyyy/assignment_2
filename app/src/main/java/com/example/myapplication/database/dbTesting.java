package com.example.myapplication.database;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;

import com.example.myapplication.component.Event;
import com.example.myapplication.component.GeneralUser;

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
                null,
                "EVENT: " + Integer.toString(new Random().nextInt(10000000)),
                testUser,
                null,
                dummyRange,
                "Unimelb",
                "A big event"
        );

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
    }
}

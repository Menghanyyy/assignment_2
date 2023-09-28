package com.example.myapplication.database;

import android.util.Log;

import com.example.myapplication.component.Event;

import java.util.ArrayList;

public class dbTesting {

    public void runTests(){
        // Testing Database Connection
        DatabaseManager databaseManager = new DatabaseManager(this);
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

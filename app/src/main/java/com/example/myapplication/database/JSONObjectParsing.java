package com.example.myapplication.database;

import com.example.myapplication.component.Event;
import com.example.myapplication.component.GeneralUser;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONObjectParsing {

    public Event parseEvent(JSONObject jsonEvent) {
        try {
            String eventId = jsonEvent.getString("eventID");
            String eventName = jsonEvent.getString("name");
            // Parse other event properties similarly

            // Create and return an Event object
            return new Event(
                    eventId,
                    eventName,
                    new GeneralUser("","","",""),
                    null,
                    null
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}

package com.example.myapplication.database;

import android.graphics.Point;

import com.example.myapplication.component.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONObjectParsing {

    public static List<Point> extractPointsFromPolygon(String polygonString) {
        List<Point> pointList = new ArrayList<>();

        // Use regex to extract coordinates from the input string
        Pattern pattern = Pattern.compile("-?\\d+ -?\\d+");
        Matcher matcher = pattern.matcher(polygonString);

        while (matcher.find()) {
            String[] parts = matcher.group().split("\\s+");
            if (parts.length == 2) {
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                Point point = new Point(x, y);
                pointList.add(point);
            }
        }

        pointList.remove(pointList.size()-1);
        return pointList;
    }

    public ArrayList<Event> parseEvents(JSONArray jsonArray) {
        ArrayList<Event> events = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonEvent = jsonArray.getJSONObject(i);
                Event event = parseEvent(jsonEvent);
                if (event != null) {
                    events.add(event);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return events;
    }

    public Event parseEvent(JSONObject jsonEvent) {
        return parseEvent(jsonEvent, -1); // Call the overloaded method with a default value for eventID
    }

    public Event parseEvent(JSONObject jsonEvent, int eventID) {
        try {
            String eventId;
            if (eventID == -1){
               eventId = jsonEvent.getString("eventID");
            }
            else{
                eventId = String.valueOf(eventID);
            }
            String eventName = jsonEvent.getString("name");
            int creatorID = jsonEvent.getInt("creatorID");
            String organisationName = jsonEvent.getString("organisationName");

            String bboxString = jsonEvent.getString("bbox");
            List<Point> bbox = extractPointsFromPolygon(bboxString);

            // Create and return an Event object
            return new Event(
                    eventId,
                    eventName,
                    null,
                    null,
                    bbox,
                    organisationName
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}

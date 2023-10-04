package com.example.myapplication.database;

import android.graphics.Point;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.myapplication.component.Activity;
import com.example.myapplication.component.Event;
import com.example.myapplication.component.GeneralUser;
import com.example.myapplication.component.User;
import com.example.myapplication.component.Visit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONObjectParsing {

    public static String convertPoints(List<Point> points){
        StringBuilder polygonString = new StringBuilder("POLYGON((");

        if (points.size() > 0){
            points.add(points.get(0));
        } else{
            return "No points, cannot create SQL statement";
        }

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            polygonString.append(point.x).append(" ").append(point.y);

            if (i < points.size() - 1) {
                polygonString.append(", ");
            }
        }

        polygonString.append("))");

        return polygonString.toString();
    }

    public static JSONObject unpackEvent(Event event) {
        try {
            JSONObject jsonObject = new JSONObject();

            // Add individual fields to the JSON object
            jsonObject.put("bbox", convertPoints(event.getEventRange()));
            jsonObject.put("name", event.getEventName());
            jsonObject.put("organisationName", event.getOrganisationName());
            jsonObject.put("creatorID", event.getEventOrganiser().getUserId());
            jsonObject.put("description", event.getDescription());

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject unpackUser(GeneralUser user) {
        try {
            JSONObject jsonObject = new JSONObject();

            // Add individual fields to the JSON object
            jsonObject.put("name", user.getUserName());
            jsonObject.put("email", user.getUserEmail());
            jsonObject.put("userName", user.getUserName());
            jsonObject.put("password", user.getUserPin());

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject unpackVisit(Visit visit) {
        try {
            JSONObject jsonObject = new JSONObject();

            // Add individual fields to the JSON object
            jsonObject.put("activityID", visit.getVisitActivityId());
            jsonObject.put("userID", visit.getUserId());
            jsonObject.put("time", "2023-09-25T14:30:00Z"); //visit.getVisitingTime());

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject unpackActivity(Activity activity) {
        try {
            JSONObject jsonObject = new JSONObject();

            // Add individual fields to the JSON object
            jsonObject.put("centreLocation", "POINT(-45.62390335574153 -3.9551761173743847)");
            jsonObject.put("polygonLocation", convertPoints(activity.getActivityRange()));
            jsonObject.put("bbox", convertPoints(activity.getBbox()));
            jsonObject.put("description", activity.getDescription());
            jsonObject.put("startTime", activity.getStartTime());
            jsonObject.put("endTime", activity.getEndTime());
            jsonObject.put("name", activity.getActivityName());
            jsonObject.put("eventID", activity.getHostedEvent().getEventId());
            jsonObject.put("locationName", activity.getLocationName());
            jsonObject.put("backgroundPicture", activity.getImage());
            jsonObject.put("creatorID", activity.getActivityOrganiser().getUserId());

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject buildUserEventObject(Event event, User user) {
        try {
            JSONObject jsonObject = new JSONObject();

            // Add individual fields to the JSON object
            jsonObject.put("userID", user.getUserId());
            jsonObject.put("eventID", event.getEventId());

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

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
            Log.println(Log.ASSERT, "Error parsing JSON", e.getMessage());
        }
        return events;
    }

    public Event parseEvent(JSONObject jsonEvent) {
        try {
            String eventId = jsonEvent.getString("eventID");
            String eventName = jsonEvent.getString("name");
            int creatorID = jsonEvent.getInt("creatorID");
            String organisationName = jsonEvent.getString("organisationName");
            String description = jsonEvent.getString("description");

            String bboxString = jsonEvent.getString("bbox");
            List<Point> bbox = extractPointsFromPolygon(bboxString);

            // Create and return an Event object
            return new Event(
                    eventId,
                    eventName,
                    null,
                    null,
                    bbox,
                    organisationName,
                    description
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Activity> parseActivities(JSONArray jsonArray) {
        ArrayList<Activity> activities = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonActivity = jsonArray.getJSONObject(i);
                Activity activity = parseActivity(jsonActivity);
                if (activity != null) {
                    activities.add(activity);
                }
            }
        } catch (JSONException e) {
            Log.println(Log.ASSERT, "Error parsing JSON", e.getMessage());
        }
        return activities;
    }

    public Activity parseActivity(JSONObject jsonActivity) {
        try {
            String activityID = jsonActivity.getString("activityID");
            String activityName = jsonActivity.getString("name");
            String locationString = jsonActivity.getString("centreLocation");
            String polygonString = jsonActivity.getString("polygonLocation");
            List<Point> activityPolygon = null; //extractPointsFromPolygon(polygonString);
            String description = jsonActivity.getString("description");
            String locationName = jsonActivity.getString("locationName");
            String bboxString = jsonActivity.getString("bbox");
            List<Point> bbox = extractPointsFromPolygon(bboxString);
            String startTime = jsonActivity.getString("startTime");
            String endTime = jsonActivity.getString("endTime");
            String image = jsonActivity.getString("backgroundPicture");

            // Create and return an Event object
            return new Activity(
                    activityID,
                    "Random activity",
                    null,
                    null,
                    null,
                    activityPolygon,
                    description,
                    locationName,
                    bbox,
                    startTime,
                    endTime,
                    image
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public GeneralUser parseUser(JSONObject userObject) {
        try {
            int userID = userObject.getInt("userID");
            String name = userObject.getString("name");
            String email = userObject.getString("email");

            return new GeneralUser(
                    String.valueOf(userID),
                    name,
                    email,
                    null
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<GeneralUser> parseUsers(JSONArray jsonArray) {
        List<GeneralUser> users = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject userObject = jsonArray.getJSONObject(i);
                GeneralUser user = parseUser(userObject);
                users.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace(); // Handle JSON parsing errors here
        }

        return users;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Visit parseVisit(JSONObject visitObject) {
        try {
            String userID = visitObject.getString("userID");
            String activityID = visitObject.getString("activityID");
            String time = visitObject.getString("time");

            return new Visit(
                    userID,
                    activityID,
                    null,
                    null
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}

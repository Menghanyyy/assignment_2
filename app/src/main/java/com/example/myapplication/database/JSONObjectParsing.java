package com.example.myapplication.database;

import com.mapbox.geojson.Point;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.myapplication.component.Activity;
import com.example.myapplication.component.Event;
import com.example.myapplication.component.GeneralUser;
import com.example.myapplication.component.Visit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONObjectParsing {

    public static String convertPoint(Point point) {
        if (point == null) {
            return "No point provided";
        }

        return "POINT(" + point.longitude() + " " + point.latitude() + ")";
    }


    public static String convertPoints(List<Point> points){
        StringBuilder polygonString = new StringBuilder("POLYGON((");

        if (points.size() > 0){
            points.add(points.get(0));
        } else{
            return "No points, cannot create SQL statement";
        }

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            polygonString.append(point.longitude()).append(" ").append(point.latitude());

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
            jsonObject.put("locationName", event.getEventLocation());

            String imageString = event.getImage();
            imageString = imageString.replaceAll("\\n", "").
                    replaceAll("\\r", ""); // Remove newline characters

            jsonObject.put("backgroundPicture", imageString);

//            Log.i("Image length (event)", event.getImage());

            return jsonObject;
        } catch (JSONException e) {
            Log.i("Couldnt unpack event", e.getMessage());
        }
        return null;
    }

    public static JSONObject unpackUser(GeneralUser user) {
        try {
            JSONObject jsonObject = new JSONObject();

            // Add individual fields to the JSON object
            jsonObject.put("name", user.getName());
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

    private static String convertTime(String inputTime){
        String inputDateFormat = "yyyy-MM-dd HH:mm";
        String outputDateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";

        SimpleDateFormat inputSdf = new SimpleDateFormat(inputDateFormat);
        SimpleDateFormat outputSdf = new SimpleDateFormat(outputDateFormat);
        inputSdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        outputSdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date date = inputSdf.parse(inputTime);
            return outputSdf.format(date);
        } catch (ParseException e) {
            return "2000-01-01T00:00:00Z";
        }
    }

    public static JSONObject unpackActivity(Activity activity) {
        try {
            JSONObject jsonObject = new JSONObject();

            // Add individual fields to the JSON object
            jsonObject.put("centreLocation", convertPoint(activity.getActivityLocation()));
            jsonObject.put("polygonLocation", convertPoints(activity.getActivityRange()));
            jsonObject.put("description", activity.getDescription());

            String startString = activity.getStartTime();
            String endString = activity.getEndTime();

            Log.i("START", startString);
            Log.i("END", endString);

            jsonObject.put("startTime", convertTime(startString));
            jsonObject.put("endTime", convertTime(endString));

            jsonObject.put("name", activity.getActivityName());
            jsonObject.put("eventID", activity.getHostedEvent().getEventId());
            jsonObject.put("locationName", activity.getLocationName());

            String imageString = activity.getImage();
            imageString = imageString.replaceAll("\\n", "").
                    replaceAll("\\r", ""); // Remove newline characters

            jsonObject.put("backgroundPicture", imageString);
            jsonObject.put("creatorID", activity.getActivityCreator().getUserId());

            return jsonObject;
        } catch (JSONException e) {
            Log.i("Could not unpack act", e.getMessage());
        }
        return null;
    }

    public static JSONObject buildUserEventObject(String eventId, String userId) {
        try {
            JSONObject jsonObject = new JSONObject();

            // Add individual fields to the JSON object
            jsonObject.put("userID", userId);
            jsonObject.put("eventID", eventId);

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Point> extractPointsFromPolygon(String polygonString) {
        List<Point> pointList = new ArrayList<>();

        Pattern pattern = Pattern.compile("-?\\d+\\.?\\d* -?\\d+\\.?\\d*");
        Matcher matcher = pattern.matcher(polygonString);

        while (matcher.find()) {
            String[] parts = matcher.group().split("\\s+");
            if (parts.length == 2) {
                double x = Double.parseDouble(parts[0]);
                double y = Double.parseDouble(parts[1]);
                Point point = Point.fromLngLat(x, y);
                pointList.add(point);
            }
        }
        return pointList;
    }

    public static Point extractPoint(String pointString) {
        Pattern pattern = Pattern.compile("-?\\d+\\.?\\d* -?\\d+\\.?\\d*");
        Matcher matcher = pattern.matcher(pointString);

        if (matcher.find()) {
            String[] parts = matcher.group().split("\\s+");
            if (parts.length == 2) {
                double x = Double.parseDouble(parts[0]);
                double y = Double.parseDouble(parts[1]);
                return Point.fromLngLat(x, y);
            }
        }
        // Return null if the input format is not valid
        return null;
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
            String locationName = jsonEvent.getString("locationName");
            String image = jsonEvent.getString("backgroundPicture");

            if(image == null) {
                image = "";
            }

            String bboxString = jsonEvent.getString("bbox");
            //Log.i("bboxString", String.valueOf(bboxString));
            List<Point> bbox = extractPointsFromPolygon(bboxString);

            // Create and return an Event object
            return new Event(
                    eventId,
                    eventName,
                    null,
                    locationName,
                    bbox,
                    organisationName,
                    description,
                    image
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
            List<Point> activityPolygon = extractPointsFromPolygon(polygonString);
            Point centrePoint = extractPoint(locationString);
            String description = jsonActivity.getString("description");
            String locationName = jsonActivity.getString("locationName");
            int creatorID = jsonActivity.getInt("creatorID");

            String startTime = jsonActivity.getString("startTime");
            String endTime = jsonActivity.getString("endTime");
            String image = jsonActivity.getString("backgroundPicture");

            // Create and return an Event object
            return new Activity(
                    activityID,
                    activityName,
                    centrePoint,
                    activityPolygon,
                    description,
                    locationName,
                    startTime,
                    endTime,
                    image,
                    creatorID
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public GeneralUser parseUser(JSONObject userObject) {
        try {
            int userID = userObject.getInt("userID");
            String username = userObject.getString("userName");
            String name = userObject.getString("name");
            String email = userObject.getString("email");

            return new GeneralUser(
                    String.valueOf(userID),
                    username,
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

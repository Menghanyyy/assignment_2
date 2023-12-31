package com.example.myapplication.database;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.myapplication.component.Activity;
import com.example.myapplication.component.Event;
import com.example.myapplication.component.GeneralUser;
import com.example.myapplication.component.User;
import com.example.myapplication.component.Visit;
import com.example.myapplication.location.GPSLocation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Will implement the java interface
public class DatabaseManager implements DatabaseInterface {

    private RequestQueue requestQueue;
    private Context context;
    private ClassCodes classCodes;

    JSONObjectParsing objectParser = new JSONObjectParsing();

//    private static String baseUrl = "http://192.168.0.247:8080";
//    private static String baseUrl = "http://192.168.56.1:8080";
    private static String baseUrl = "http://comp90018.us.to:8080";

    public DatabaseManager(Context context) {
        // Initialize the Volley RequestQueue
        requestQueue = Volley.newRequestQueue(context);
        this.context = context;
    }

    private <T> void sendJsonObjectRequest(
            int method,
            String urlExtension,
            JSONObject jsonRequest,
            Object callback,
            ClassCode messageClassCode
    ) {

        String url = baseUrl + urlExtension;

        // If jsonRequest is not null, convert it to a string and append as query parameter
        if (jsonRequest != null && method == Request.Method.GET) {
            try {
                String jsonString = URLEncoder.encode(jsonRequest.toString(), "utf-8");
                url += "?data=" + jsonString;
            } catch (UnsupportedEncodingException e) {
                Log.println(Log.DEBUG, "couldnt encode JSON", e.getMessage());
            }
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
            method,
            url,
            jsonRequest,
            new Response.Listener<JSONObject>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response != null) {
                            if (response.get("status").equals("success")) {

                                // For arraylist Objects
                                if (messageClassCode.getIsArraylist()) {
                                    ArrayList<T> parsedResponseArraylist = parseSuccessArraylist(
                                            response.get("message"),
                                            messageClassCode
                                    );
                                    if (parsedResponseArraylist != null){
                                        ((DatabaseCallback<Object>) callback).onSuccess(
                                                (ArrayList<T>) parsedResponseArraylist
                                        );
                                    } else{
                                        ((DatabaseCallback<Object>) callback).onError(
                                                "Could not parse response 1."
                                        );
                                    }
                                }

                                // For non-arraylist objects
                                else{
                                    T parsedResponse = parseSuccess(
                                            response.get("message"),
                                            messageClassCode
                                    );
                                    if (parsedResponse != null){
                                        ((DatabaseCallback<Object>) callback).onSuccess(
                                                parsedResponse
                                        );
                                    } else{
                                        ((DatabaseCallback<Object>) callback).onError(
                                                "Could not parse response 2."
                                        );
                                    }
                                }
                            } else {
                                ((DatabaseCallback<Object>) callback).onError(
                                        (String) response.get("message")
                                );
                            }
                        } else{
                            ((DatabaseCallback<Object>) callback).onError(
                                    "Received empty response"
                            );
                        }
                    } catch (JSONException e) {
                        ((DatabaseCallback<Object>) callback).onError(
                                "Bad Json: " + e.getMessage()
                        );
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Handle the error response
                    String errorMessage = "VolleyError adding event: " + error.getMessage();
                    ((DatabaseCallback<Object>) callback).onError(errorMessage);
                }
            }) {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException | JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };
        // Add the request to the Volley request queue
        requestQueue.add(jsonObjectRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private <T> T parseSuccess(Object message, ClassCode classCode){
        switch (classCode.getCode()) {
            case ClassCodes.STRING_CLASS_VALUE:
                return (T) String.valueOf(message);
            case ClassCodes.INT_CLASS_VALUE:
                return (T) (Integer) message;
            case ClassCodes.EVENT_CLASS_VALUE:
                return (T) objectParser.parseEvent((JSONObject) message);
            case ClassCodes.ACTIVITY_CLASS_VALUE:
                return (T) objectParser.parseActivity((JSONObject) message);
            case ClassCodes.USER_CLASS_VALUE:
                return (T) objectParser.parseUser((JSONObject) message);
            case ClassCodes.VISIT_CLASS_VALUE:
                return (T) objectParser.parseVisit((JSONObject) message);
            default:
                return null;
        }
    }

    private <T> ArrayList<T> parseSuccessArraylist(Object message, ClassCode classCode){
        switch (classCode.getCode()) {
            case ClassCodes.ACTIVITY_ARRAYLIST_CLASS_VALUE:
                return (ArrayList<T>) objectParser.parseActivities((JSONArray) message);
            case ClassCodes.USER_ARRAYLIST_CLASS_VALUE:
                return (ArrayList<T>) objectParser.parseUsers((JSONArray) message);
            case ClassCodes.VISIT_ARRAYLIST_CLASS_VALUE:
                // Not needed yet
                return null;
            case ClassCodes.EVENT_ARRAYLIST_CLASS_VALUE:
                return (ArrayList<T>) objectParser.parseEvents((JSONArray) message);
            default:
                return null;
        }
    }

    @Override
    public void addEvent(Event event, final DatabaseCallback<String> callback) {

        Log.i("about to send event", JSONObjectParsing.unpackEvent(event).toString());

        sendJsonObjectRequest(
            Request.Method.POST,
            "/events/addEvent",
            JSONObjectParsing.unpackEvent(event),
            callback,
            ClassCodes.STRING_CLASS
        );
//        Log.i("json length (event)", Integer.toString(JSONObjectParsing.unpackEvent(event).toString().length()));

    }

    @Override
    public void getEventByID(int eventID, DatabaseCallback<Event> callback) {
        sendJsonObjectRequest(
                Request.Method.GET,
                "/events/getByID/" + Integer.valueOf(eventID),
                null,
                callback,
                ClassCodes.EVENT_CLASS
        );
    }

    @Override
    public void getEventByLink(String link, DatabaseCallback<Event> callback) {
        sendJsonObjectRequest(
                Request.Method.GET,
                "/events/getByLink/" + link,
                null,
                callback,
                ClassCodes.EVENT_CLASS
        );
    }

    @Override
    public void getEventLinkByID(int eventID, DatabaseCallback<String> callback) {
        sendJsonObjectRequest(
                Request.Method.GET,
                "/events/getEventLinkByID/" + Integer.valueOf(eventID),
                null,
                callback,
                ClassCodes.STRING_CLASS
        );
    }

    @Override
    public void getAllEvents(DatabaseCallback<ArrayList<Event>> callback){
        sendJsonObjectRequest(
                Request.Method.GET,
                "/events/getAll",
                null,
                callback,
                ClassCodes.EVENT_ARRAYLIST_CLASS
        );
    }

    @Override
    public void joinEvent(String userId, String eventId, DatabaseCallback<String> callback) {
        sendJsonObjectRequest(
                Request.Method.POST,
                "/events/joinEvent",
                objectParser.buildUserEventObject(eventId, userId),
                callback,
                ClassCodes.STRING_CLASS
        );
    }

    @Override
    public void getUsersAtEvent(Event event, DatabaseCallback<ArrayList<User>> callback) {
        sendJsonObjectRequest(
                Request.Method.GET,
                "/events/getUsersAtEvent/" + Integer.valueOf(event.getEventId()),
                null,
                callback,
                ClassCodes.USER_ARRAYLIST_CLASS
        );
    }

    @Override
    public void getJoinedEvents(String userId, DatabaseCallback<ArrayList<Event>> callback) {

        try{
            sendJsonObjectRequest(
                    Request.Method.GET,
                    "/events/getJoinedEvents/" + Integer.valueOf(userId),
                    null,
                    callback,
                    ClassCodes.EVENT_ARRAYLIST_CLASS
            );
        } catch (NumberFormatException e){
            callback.onError("user id not received: " + e.getMessage());
        }
    }

    @Override
    public void getCreatedEvents(String userId, DatabaseCallback<ArrayList<Event>> callback) {

        try{
            sendJsonObjectRequest(
                    Request.Method.GET,
                    "/events/getCreatedEvents/" + Integer.valueOf(userId),
                    null,
                    callback,
                    ClassCodes.EVENT_ARRAYLIST_CLASS
            );
        } catch (NumberFormatException e){
            callback.onError("user id not received: " + e.getMessage());
        }
    }

    @Override
    public void addActivity(Activity activity, DatabaseCallback<String> callback) {
        try {
            Log.i("about to send act", JSONObjectParsing.unpackActivity(activity).toString());

            sendJsonObjectRequest(
                    Request.Method.POST,
                    "/activities/addActivity",
                    JSONObjectParsing.unpackActivity(activity),
                    callback,
                    ClassCodes.STRING_CLASS
            );
        } catch (Exception e){
            Log.i("Caught outgoing ex", e.getMessage());

        }
    }

    @Override
    public void getActivityByID(int activityID, DatabaseCallback<Activity> callback) {
        sendJsonObjectRequest(
                Request.Method.GET,
                "/activities/getByID/" + Integer.toString(activityID),
                null,
                callback,
                ClassCodes.ACTIVITY_CLASS
        );
    }

    @Override
    public void getAllActivities(String eventId, DatabaseCallback<ArrayList<Activity>> callback) {
        sendJsonObjectRequest(
                Request.Method.GET,
                "/activities/getAll/" + eventId,
                null,
                callback,
                ClassCodes.ACTIVITY_ARRAYLIST_CLASS
        );
    }

    @Override
    public void addVisit(Visit visit, DatabaseCallback<String> callback) {
        sendJsonObjectRequest(
                Request.Method.POST,
                "/activities/addVisit",
                JSONObjectParsing.unpackVisit(visit),
                callback,
                ClassCodes.STRING_CLASS
        );
    }

    @Override
    public void getVisitByID(int userID, int activityID, DatabaseCallback<Visit> callback) {
        sendJsonObjectRequest(
                Request.Method.GET,
                "/activities/getVisitByID?userID=" + Integer.toString(userID) + "" +
                        "&activityID=" + Integer.toString(activityID),
                null,
                callback,
                ClassCodes.VISIT_CLASS
        );
    }

    @Override
    public void visitCountForUser(int userID, DatabaseCallback<Integer> callback) {
        sendJsonObjectRequest(
                Request.Method.GET,
                "/activities/visitCountForUser/" + Integer.toString(userID),
                null,
                callback,
                ClassCodes.INT_CLASS
        );
    }

    @Override
    public void visitCountForUserAtEvent(String userID, String eventID, DatabaseCallback<Integer> callback) {
        try{
            String url = "/activities/visitCountForUserAtEvent?userID="
                    + userID
                    + "&eventID="
                    + eventID;

            sendJsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    callback,
                    ClassCodes.INT_CLASS
            );
        } catch (NumberFormatException e){
            callback.onError("user or event id not received: " + e.getMessage());
        }
    }

    @Override
    public void visitCountAtActivity(int activityID, DatabaseCallback<Integer> callback) {
        sendJsonObjectRequest(
                Request.Method.GET,
                "/activities/visitCountAtActivity/" + Integer.toString(activityID),
                null,
                callback,
                ClassCodes.INT_CLASS
        );
    }

    @Override
    public void addUser(GeneralUser user, DatabaseCallback<String> callback) {
        sendJsonObjectRequest(
                Request.Method.POST,
                "/users/addUser",
                objectParser.unpackUser(user),
                callback,
                ClassCodes.STRING_CLASS
        );
    }

    @Override
    public void getUserByID(int userID, DatabaseCallback<GeneralUser> callback) {
        sendJsonObjectRequest(
                Request.Method.GET,
                "/users/getByID/" + Integer.toString(userID),
                null,
                callback,
                ClassCodes.USER_CLASS
        );
    }

    @Override
    public void getAllUsers(DatabaseCallback<ArrayList<GeneralUser>> callback) {
        sendJsonObjectRequest(
                Request.Method.GET,
                "/users/getAll",
                null,
                callback,
                ClassCodes.USER_ARRAYLIST_CLASS
        );
    }

    @Override
    public void verifyPassword(String password, String username, DatabaseCallback<String> callback) {
        try{
            String url = "/users/verifyPassword?username="
                    + username
                    + "&password=" + password;
            Log.i("sending url", url);

            sendJsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    callback,
                    ClassCodes.STRING_CLASS
            );
        } catch (NumberFormatException e){
            callback.onError("pw or user id not received: " + e.getMessage());
        }
    }

    @Override
    public void activitiesAtLocation(GPSLocation location, Event event, DatabaseCallback<ArrayList<Activity>> callback) {
        // NOT IMPLEMENTED YET
    }
}

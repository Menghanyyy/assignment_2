package com.latti31.springeventserver.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.latti31.springeventserver.objects.DatabaseChecker;
import com.latti31.springeventserver.objects.JSONResponseWrapper;
import com.latti31.springeventserver.objects.MapboxUploader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final JdbcTemplate jdbcTemplate;
    private final DatabaseChecker databaseChecker;

    private final JSONResponseWrapper jsonWrapper = new JSONResponseWrapper();

    private final ExecutorService executorService = Executors.newFixedThreadPool(10); // adjust the pool size as needed

    public static final int UPLOAD_ATTEMPTS_LIMIT = 25;

    public ActivityController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.databaseChecker = new DatabaseChecker(jdbcTemplate);
    }

    private void asyncRebalance(int eventID){
        CompletableFuture.runAsync(() -> {

            rebalanceEvent(eventID);

        }, executorService);

    }

    private void asyncUpdateMapbox(int activityID, int eventID, int visited, String polygonString){
        CompletableFuture.runAsync(() -> {
            try {

                MapboxUploader.makePutRequest(activityID, eventID, visited, polygonString);

                int attemptsRemaining = UPLOAD_ATTEMPTS_LIMIT;

                while (attemptsRemaining > 0){

                    try {
                        attemptsRemaining--;
                        Boolean result = MapboxUploader.makePostRequest();
                        if (result){
                            break;
                        }
                        Thread.sleep(1000); // Pauses for 1 second

                    } catch (InterruptedException e) {
                        // Something went wrong
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, executorService);

    }

    @GetMapping("/getByID/{activity_id}")
    public String getActivity(@PathVariable int activity_id) {
        String query = "SELECT " +
                "activityID, " +
                "ST_AsText(centreLocation) AS centreLocation, " +
                "ST_AsText(polygonLocation) AS polygonLocation, " +
                "description, " +
                "startTime, " +
                "endTime, " +
                "name, " +
                "eventID, " +
                "locationName," +
                "backgroundPicture, " +
                "creatorID " +
                "FROM Activity WHERE activityID = ?";

        try {
            List<Map<String, Object>> activities = jdbcTemplate.queryForList(query, activity_id);
            if (!activities.isEmpty()) {
                Map<String, Object> activity = activities.get(0);

                // Retrieve the BLOB data as a byte array
                byte[] backgroundPictureData = (byte[]) activity.get("backgroundPicture");

                // Convert the byte array to Base64 encoding
                String backgroundPictureBase64 = Base64.getEncoder().encodeToString(
                        backgroundPictureData
                );

                // Create a JSON object
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode jsonObject = objectMapper.createObjectNode();
                jsonObject.put("activityID", (Integer) activity.get("activityID"));
                jsonObject.put("centreLocation", (String) activity.get("centreLocation"));
                jsonObject.put("polygonLocation", (String) activity.get("polygonLocation"));
                jsonObject.put("description", (String) activity.get("description"));
                jsonObject.put("startTime", activity.get("startTime").toString());
                jsonObject.put("endTime", activity.get("endTime").toString());
                jsonObject.put("name", (String) activity.get("name"));
                jsonObject.put("eventID", (Integer) activity.get("eventID"));
                jsonObject.put("locationName", (String) activity.get("locationName"));
                jsonObject.put("backgroundPicture", backgroundPictureBase64);
                jsonObject.put("creatorID", (Integer) activity.get("creatorID"));

                // Convert JSON object to a JSON string
                return jsonWrapper.wrapJsonNode(true, jsonObject);
            } else {
                return jsonWrapper.wrapString(false, "Activity not found");
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if the activity with the specified ID doesn't exist
            return jsonWrapper.wrapString(false, "Error retrieving activity: " +
                    e.getMessage());
        }
    }

    @PostMapping("/addActivity")
    public String addActivity(@RequestBody String jsonText) {
        String query = "INSERT INTO Activity (" +
                "`centreLocation`, " +
                "`polygonLocation`, " +
                "`description`, " +
                "`startTime`, " +
                "`endTime`, " +
                "`name`, " +
                "`eventID`, " +
                "`locationName`, " +
                "`backgroundPicture`, " +
                "`creatorID`" +
                ") VALUES " +
                "(ST_PointFromText(?), ST_PolygonFromText(?)," +
                "?, ?, ?, ?, ?, ?, ?, ?)";

        // Parse the JSON string
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonText);

            // Extract activity properties from JSON
            String centreLocation = jsonNode.get("centreLocation").asText();
            String polygonLocation = jsonNode.get("polygonLocation").asText();
            String description = jsonNode.get("description").asText();
            String startTimeString = jsonNode.get("startTime").asText();
            String endTimeString = jsonNode.get("endTime").asText();
            String name = jsonNode.get("name").asText();
            int eventID = jsonNode.get("eventID").asInt();
            String locationName = jsonNode.get("locationName").asText();
            String backgroundPictureBase64 = jsonNode.get("backgroundPicture").asText();
            int creatorID = jsonNode.get("creatorID").asInt();

            // Convert Base64 string to byte array
            byte[] backgroundPictureData = Base64.getDecoder().decode(backgroundPictureBase64);

            // Parse the time strings into java.util.Date objects
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date startTime = dateFormat.parse(startTimeString);
            Date endTime = dateFormat.parse(endTimeString);

            // EventID
            if (databaseChecker.keyNotInDBInt(
                    "Event",
                    "eventID",
                    eventID
            )){
                return jsonWrapper.wrapString(false, ("Event ID " + Integer.toString(eventID) +
                        " does not exist in the database."));
            }

            // UserID
            if (databaseChecker.keyNotInDBInt(
                    "User",
                    "userID",
                    creatorID
            )){
                return jsonWrapper.wrapString(false, ("User ID " + Integer.toString(creatorID) +
                        " does not exist in the database."));
            }

            jdbcTemplate.update(
                    query,
                    centreLocation,
                    polygonLocation,
                    description,
                    startTime,
                    endTime,
                    name,
                    eventID,
                    locationName,
                    backgroundPictureData,
                    creatorID
            );

            try {
                int generatedID = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);

                // Assumes these functions will always work
                // Call the makePostRequest and makePutRequest asynchronously
                asyncRebalance(eventID);
                asyncUpdateMapbox(generatedID, eventID, 0, polygonLocation);

                return jsonWrapper.wrapString(true, Integer.toString(generatedID));

            } catch (Exception ex){
                return jsonWrapper.wrapString(false, "Error getting last insert ID (event)"
                        + ex.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return jsonWrapper.wrapString(false, "Error creating activity: " +
                    e.getMessage());
        }
    }

    private void rebalanceEvent(int eventID) {

        System.out.println("Re-balancing events");

        String query = "SELECT " +
                "ST_AsText(polygonLocation) AS polygonLocation " +
                "FROM Activity WHERE eventID = ?";

        try {
            List<Map<String, Object>> activities = jdbcTemplate.queryForList(query, eventID);

            double minX = 180;
            double minY = 180;
            double maxX = -180;
            double maxY = -180;

            for (Map<String, Object> activity : activities) {

                String polygonText = activity.get("polygonLocation").toString();

                Pattern pattern = Pattern.compile("-?\\d+\\.?\\d* -?\\d+\\.?\\d*");
                Matcher matcher = pattern.matcher(polygonText);

                while (matcher.find()) {
                    String[] parts = matcher.group().split("\\s+");
                    if (parts.length == 2) {
                        double x = Double.parseDouble(parts[0]);
                        double y = Double.parseDouble(parts[1]);

                        if (x < minX){
                            minX = x;
                        }
                        if (x > maxX){
                            maxX = x;
                        }
                        if (y < minY){
                            minY = y;
                        }
                        if (y > maxY){
                            maxY = y;
                        }
                    }
                }
            }

            // Default to the bounding box for all of Australia
            String newBBox = LocationController.getSQLBBOX(
                    minX,
                    maxX,
                    minY,
                    maxY
            );

            try {
                String updateQuery = "UPDATE Event SET bbox = ST_PolygonFromText(?) WHERE eventID = ?";
                jdbcTemplate.update(updateQuery, newBBox, eventID);
                System.out.println("Successfully balanced event!");

            } catch (Exception e){
                System.out.println("Could not insert new bbox into event table: " + e.getMessage());
            }

        } catch (Exception e) {
            // Handle exceptions, e.g., if there are no activities for the specified event
            System.out.println("Failed to re-balance");
        }
    }

    // Method to retrieve all activities
    @GetMapping("/getAll/{event_id}")
    public String getAll(@PathVariable int event_id) {
        String query = "SELECT " +
                "activityID, " +
                "ST_AsText(centreLocation) AS centreLocation, " +
                "ST_AsText(polygonLocation) AS polygonLocation, " +
                "description, " +
                "startTime, " +
                "endTime, " +
                "name, " +
                "eventID, " +
                "locationName," +
                "backgroundPicture, " +
                "creatorID " +
                "FROM Activity WHERE eventID = ?";

        try {
            List<Map<String, Object>> activities = jdbcTemplate.queryForList(query, event_id);
            List<ObjectNode> activityList = new ArrayList<>(); // List to store activity JSON objects

            for (Map<String, Object> activity : activities) {
                // Retrieve the BLOB data as a byte array
                byte[] backgroundPictureData = (byte[]) activity.get("backgroundPicture");

                // Convert the byte array to Base64 encoding
                String backgroundPictureBase64 = Base64.getEncoder().encodeToString(backgroundPictureData);

                // Create a JSON object for the current activity
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode jsonObject = objectMapper.createObjectNode();
                jsonObject.put("activityID", (Integer) activity.get("activityID"));
                jsonObject.put("centreLocation", (String) activity.get("centreLocation"));
                jsonObject.put("polygonLocation", (String) activity.get("polygonLocation"));
                jsonObject.put("description", (String) activity.get("description"));
                jsonObject.put("startTime", activity.get("startTime").toString());
                jsonObject.put("endTime", activity.get("endTime").toString());
                jsonObject.put("name", (String) activity.get("name"));
                jsonObject.put("eventID", (Integer) activity.get("eventID"));
                jsonObject.put("locationName", (String) activity.get("locationName"));
                jsonObject.put("backgroundPicture", backgroundPictureBase64);
                jsonObject.put("creatorID", (Integer) activity.get("creatorID"));

                // Add the current activity JSON object to the list
                activityList.add(jsonObject);
            }

            // Convert the list of activity JSON objects to a JSON array
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode jsonArray = objectMapper.valueToTree(activityList);

            // Convert JSON array to a JSON string
            return jsonWrapper.wrapJsonNode(true, jsonArray);
        } catch (Exception e) {
            // Handle exceptions, e.g., if there are no activities for the specified event
            return jsonWrapper.wrapString(false, "Error retrieving activities: " +
                    e.getMessage());
        }
    }

    @PostMapping("/addVisit")
    public String addVisit(@RequestBody String jsonText) {
        String query = "INSERT INTO Visit (" +
                "userID, " +
                "activityID, " +
                "time " +
                ") VALUES (?, ?, ?)";

        // Parse the JSON string
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonText);

            // Extract the 'name' property from the JSON
            int userID = jsonNode.get("userID").asInt();
            int activityID = jsonNode.get("activityID").asInt();
            String timeString = jsonNode.get("time").asText();

            // Parse the time string into a java.util.Date object
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date time = dateFormat.parse(timeString);

            // User ID
            if (databaseChecker.keyNotInDBInt(
                    "User",
                    "userID",
                    userID
            )){
                return jsonWrapper.wrapString(false, ("User ID " + Integer.toString(userID) +
                        " does not exist in the database."));
            }

            // Activity ID
            if (databaseChecker.keyNotInDBInt(
                    "Activity",
                    "activityID",
                    activityID
            )) {
                return jsonWrapper.wrapString(false, ("Activity ID " + Integer.toString(activityID) +
                        " does not exist in the database."));
            }

            // Combination check (fail if already in)
            if (!databaseChecker.keyNotInDBInt(
                    "Visit",
                    "activityID",
                    activityID,
                    "userID",
                    userID
            )) {
                return jsonWrapper.wrapString(false, "User " + userID +
                        " already visited activity " + activityID);
            }

            jdbcTemplate.update(query, userID, activityID, time);

            String newQuery = "SELECT " +
                    "ST_AsText(polygonLocation) AS polyLocation, " +
                    "eventID " +
                    "FROM Activity WHERE activityID = ?";

            try {
                List<Map<String, Object>> activities = jdbcTemplate.queryForList(newQuery, activityID);
                if (!activities.isEmpty()) {
                    Map<String, Object> activity = activities.get(0);

                    int eventID = (int) activity.get("eventID");
                    String polygonLocation = ((String) activity.get("polyLocation")).replace(",", ", ");

                    asyncUpdateMapbox(activityID, eventID, 1, polygonLocation);

                    return jsonWrapper.wrapString(true, "Visit added successfully.");
                } else{
                    return jsonWrapper.wrapString(false, "Visit not found.");
                }
            } catch (Exception ex){
                return jsonWrapper.wrapString(false, "Problem uploading put request" + ex.getMessage());
            }
        } catch (Exception e) {
            return jsonWrapper.wrapString(false, "Error creating visit: " +
                    e.getMessage());
        }
    }

    @GetMapping("/getVisitByID")
    public String getActivity(
              @RequestParam("userID") int userID,
              @RequestParam("activityID") int activityID
    ) {
        String query = "SELECT " +
                "activityID, " +
                "userID, " +
                "time " +
                "FROM Visit WHERE userID = ? AND activityID = ?";

        try {

            // User ID
            if (databaseChecker.keyNotInDBInt(
                    "User",
                    "userID",
                    userID
            )){
                return jsonWrapper.wrapString(false, ("User ID " + Integer.toString(userID) +
                        " does not exist in the database."));
            }

            // Activity ID
            if (databaseChecker.keyNotInDBInt(
                    "Activity",
                    "activityID",
                    activityID
            )){
                return jsonWrapper.wrapString(false, ("Activity ID " + Integer.toString(activityID) +
                        " does not exist in the database."));
            }

            List<Map<String, Object>> visits = jdbcTemplate.queryForList(query, userID, activityID);

            if (!visits.isEmpty()) {
                Map<String, Object> visit = visits.get(0);
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode jsonObject = objectMapper.createObjectNode();
                jsonObject.put("time", visit.get("time").toString());
                jsonObject.put("activityID", visit.get("activityID").toString());
                jsonObject.put("userID", visit.get("userID").toString());

                // Convert JSON object to a JSON string
                return jsonWrapper.wrapJsonNode(true, jsonObject);
            } else {
                return jsonWrapper.wrapString(false, "Visit not found");
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if the activity with the specified ID doesn't exist
            return jsonWrapper.wrapString(false, "Error retrieving visit: " +
                    e.getMessage());
        }
    }

    @GetMapping("/visitCountForUser/{user_id}")
    public String getUserVisitCount(@PathVariable int user_id) {
        String query = "SELECT COUNT(*) FROM Visit WHERE `userID` = ?";

        try {
            List<Map<String, Object>> visitCountList = jdbcTemplate.queryForList(query, user_id);

            if (!visitCountList.isEmpty()) {
                Map<String, Object> visitCountMap = visitCountList.get(0);
                int visitCount = ((Number) visitCountMap.get("COUNT(*)")).intValue();
                return jsonWrapper.wrapInt(true, visitCount);
            } else {
                return jsonWrapper.wrapString(false, "No visits found for the user.");
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if there's an issue with the query or database
            return jsonWrapper.wrapString(false, "Error retrieving visit count: " +
                    e.getMessage());
        }
    }

    @GetMapping("/visitCountForUserAtEvent")
    public String getUserVisitEventCount(
            @RequestParam("userID") int userID,
            @RequestParam("eventID") int eventID
    ) {
        System.out.println("Started");
        String query = "SELECT COUNT(*) " +
                "FROM Visit v " +
                "JOIN Activity a " +
                "ON a.activityID = v.activityID " +
                "WHERE `userID` = ? AND a.eventID = ?";

        try {

            List<Map<String, Object>> visitCountList = jdbcTemplate.queryForList(query, userID, eventID);

            if (!visitCountList.isEmpty()) {
                Map<String, Object> visitCountMap = visitCountList.get(0);
                int visitCount = ((Number) visitCountMap.get("COUNT(*)")).intValue();
                return jsonWrapper.wrapInt(true, visitCount);
            } else {
                return jsonWrapper.wrapString(false, "No visits found for the user.");
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if there's an issue with the query or database
            return jsonWrapper.wrapString(false, "Error retrieving visit count: " +
                    e.getMessage());
        }
    }

    // Get the count for a specific activity
    @GetMapping("/visitCountAtActivity/{activity_id}")
    public String getActivityVisitCount(@PathVariable int activity_id) {
        String query = "SELECT COUNT(*) FROM Visit WHERE `activityID` = ?";

        try {
            List<Map<String, Object>> visitCountList = jdbcTemplate.queryForList(query, activity_id);

            if (!visitCountList.isEmpty()) {
                Map<String, Object> visitCountMap = visitCountList.get(0);
                int visitCount = ((Number) visitCountMap.get("COUNT(*)")).intValue();
                return jsonWrapper.wrapInt(true, visitCount);
            } else {
                return jsonWrapper.wrapString(false, "No visits found for the activity.");
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if there's an issue with the query or database
            return jsonWrapper.wrapString(false, "Error retrieving visit count: " +
                    e.getMessage());
        }
    }
}

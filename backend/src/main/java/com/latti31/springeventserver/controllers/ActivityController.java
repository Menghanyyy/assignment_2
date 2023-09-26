package com.latti31.springeventserver.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final JdbcTemplate jdbcTemplate;

    public ActivityController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/getByID/{activity_id}")
    public String getActivity(@PathVariable int activity_id) {
        String query = "SELECT " +
                "activityID, " +
                "ST_AsText(centreLocation) AS centreLocation, " +
                "ST_AsText(polygonLocation) AS polygonLocation, " +
                "ST_AsText(bbox) AS bbox, " +
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
                String backgroundPictureBase64 = Base64.getEncoder().encodeToString(backgroundPictureData);

                // Create a JSON object
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode jsonObject = objectMapper.createObjectNode();
                jsonObject.put("activityID", (Integer) activity.get("activityID"));
                jsonObject.put("centreLocation", (String) activity.get("centreLocation"));
                jsonObject.put("polygonLocation", (String) activity.get("polygonLocation"));
                jsonObject.put("bbox", (String) activity.get("bbox"));
                jsonObject.put("description", (String) activity.get("description"));
                jsonObject.put("startTime", activity.get("startTime").toString());
                jsonObject.put("endTime", activity.get("endTime").toString());
                jsonObject.put("name", (String) activity.get("name"));
                jsonObject.put("eventID", (Integer) activity.get("eventID"));
                jsonObject.put("locationName", (String) activity.get("locationName"));
                jsonObject.put("backgroundPicture", backgroundPictureBase64);
                jsonObject.put("creatorID", (Integer) activity.get("creatorID"));

                // Convert JSON object to a JSON string
                return jsonObject.toString();
            } else {
                return "Activity not found";
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if the activity with the specified ID doesn't exist
            return "Error retrieving activity: " + e.getMessage();
        }
    }

    @PostMapping("/addActivity")
    public String addActivity(@RequestBody String jsonText) {
        String query = "INSERT INTO Activity (" +
                "`centreLocation`, " +
                "`polygonLocation`, " +
                "`bbox`, " +
                "`description`, " +
                "`startTime`, " +
                "`endTime`, " +
                "`name`, " +
                "`eventID`, " +
                "`locationName`, " +
                "`backgroundPicture`, " +
                "`creatorID`" +
                ") VALUES " +
                "(ST_PointFromText(?), ST_POLYGONFROMTEXT(?), ST_PolygonFromText(?)," +
                "?, ?, ?, ?, ?, ?, ?, ?)";

        // Parse the JSON string
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonText);

            // Extract activity properties from JSON
            String centreLocation = jsonNode.get("centreLocation").asText();
            String polygonLocation = jsonNode.get("polygonLocation").asText();
            String bbox = jsonNode.get("bbox").asText();
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

            jdbcTemplate.update(
                    query,
                    centreLocation,
                    polygonLocation,
                    bbox,
                    description,
                    startTime,
                    endTime,
                    name,
                    eventID,
                    locationName,
                    backgroundPictureData,
                    creatorID
            );
            return "Activity added successfully.";
        } catch (Exception e) {
            return "Error creating activity: " + e.getMessage();
        }
    }

    // Method to retrieve all activities
    @GetMapping("/getAll/{event_id}")
    public String getAll(@PathVariable int event_id) {
        String query = "SELECT " +
                "activityID, " +
                "ST_AsText(centreLocation) AS centreLocation, " +
                "ST_AsText(polygonLocation) AS polygonLocation, " +
                "ST_AsText(bbox) AS bbox, " +
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

            for (int i = 0; i < activities.size(); i++) {
                Map<String, Object> activity = activities.get(i);

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
                jsonObject.put("bbox", (String) activity.get("bbox"));
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
            return jsonArray.toString();
        } catch (Exception e) {
            // Handle exceptions, e.g., if there are no activities for the specified event
            return "Error retrieving activities: " + e.getMessage();
        }
    }

}

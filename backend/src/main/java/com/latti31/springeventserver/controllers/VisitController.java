package com.latti31.springeventserver.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.latti31.springeventserver.objects.DatabaseChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/visits")
public class VisitController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Get the count for a specific activity
    @GetMapping("/userCount/{user_id}")
    public String getUserVisitCount(@PathVariable int user_id) {
        String query = "SELECT COUNT(*) FROM Visit WHERE `User Account_userID` = ?";

        try {
            int visitCount = jdbcTemplate.queryForObject(query, Integer.class, user_id);
            return Integer.toString(visitCount);
        } catch (Exception e) {
            // Handle exceptions, e.g., if there's an issue with the query or database
            return "Error retrieving visit count: " + e.getMessage();
        }
    }

    // Get the count for a specific activity
    @GetMapping("/activityCount/{activity_id}")
    public String getActivityVisitCount(@PathVariable int activity_id) {
        String query = "SELECT COUNT(*) FROM Visit WHERE `Activity_activityID` = ?";

        try {
            int visitCount = jdbcTemplate.queryForObject(query, Integer.class, activity_id);
            return Integer.toString(visitCount);
        } catch (Exception e) {
            // Handle exceptions, e.g., if there's an issue with the query or database
            return "Error retrieving visit count: " + e.getMessage();
        }
    }

    @PostMapping("/add")
    public String addVisit(@RequestBody String jsonText) {
        String query = "INSERT INTO Visit (`" +
                "User Account_userID`, Activity_activityID, " +
                "Activity_Event_eventID, Activity_Event_Organisation_idOrganisation, " +
                "time) VALUES (?, ?, ?, ?, ?)";

        // Parse the JSON string
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonText);

            // Extract the 'name' property from the JSON
            int userID = jsonNode.get("userID").asInt();
            int activityID = jsonNode.get("activityID").asInt();
            int eventID = jsonNode.get("eventID").asInt();
            int organisationID = jsonNode.get("organisationID").asInt();
            String timeString = jsonNode.get("time").asText(); // Assuming time is provided as a string

            // Parse the time string into a java.util.Date object
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date time = dateFormat.parse(timeString);

            // Database Checking
            DatabaseChecker databaseChecks = new DatabaseChecker(jdbcTemplate);

            // User ID
            if (databaseChecks.keyNotInDB(
                    "User Account",
                    "userID",
                    userID
            )){
                return ("User ID " + Integer.toString(userID) + " does not exist in the database.");
            }

            // Event ID
            if (databaseChecks.keyNotInDB(
                    "Event",
                    "eventID",
                    eventID
            )){
                return ("Event ID " + Integer.toString(eventID) + " does not exist in the database.");
            }

            // Activity ID
            if (databaseChecks.keyNotInDB(
                    "Activity",
                    "activityID",
                    activityID
            )){
                return ("Activity ID " + Integer.toString(activityID) + " does not exist in the database.");
            }

            // Organisation ID
            if (databaseChecks.keyNotInDB(
                    "Organisation",
                    "idOrganisation",
                    organisationID
            )){
                return ("Organisation ID " + Integer.toString(organisationID) + " does not exist in the database.");
            }

            jdbcTemplate.update(query, userID, activityID, eventID, organisationID, time);
            return "Visit added successfully.";
        } catch (Exception e) {
            return "Error creating visit: " + e.getMessage();
        }
    }
}

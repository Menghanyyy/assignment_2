package com.latti31.springeventserver.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.latti31.springeventserver.objects.DatabaseChecker;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
public class EventController {

    private final JdbcTemplate jdbcTemplate;
    private final DatabaseChecker databaseChecker;

    public EventController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.databaseChecker = new DatabaseChecker(jdbcTemplate);
    }

    @GetMapping("/getByID/{id}")
    public String getEvent(@PathVariable int id) {
        String query = "SELECT " +
                "name, " +
                "ST_AsText(bbox) AS bbox, " +
                "organisationName, " +
                "creatorID, " +
                "description " +
                "FROM Event WHERE eventID = ?";
        try {
            List<Map<String, Object>> events = jdbcTemplate.queryForList(query, id);
            if (!events.isEmpty()) {
                Map<String, Object> event = events.get(0);
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(event);
            } else {
                return "Event not found";
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if the event with the specified ID doesn't exist
            return "Error retrieving event: " + e.getMessage();
        }
    }

    @PostMapping("/addEvent")
    public String createEvent(@RequestBody String jsonText) {
        String query = "INSERT INTO Event (" +
                "bbox, " +
                "name, " +
                "organisationName, " +
                "creatorID, " +
                "description) VALUES (ST_PolygonFromText(?), ?, ?, ?, ?)";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonText);

            // Extract values from JSON
            String bbox = jsonNode.get("bbox").asText();
            String name = jsonNode.get("name").asText();
            String organisationName = jsonNode.get("organisationName").asText();
            String description = jsonNode.get("description").asText();

            // Ensure creator exists in database
            int creatorID = jsonNode.get("creatorID").asInt();
            if (databaseChecker.keyNotInDB(
                    "User",
                    "userID",
                    creatorID
                    )) {
                return "User with ID " + creatorID + " does not exist in db.";
            }

            // Insert values into the database
            jdbcTemplate.update(
                    query,
                    bbox,
                    name,
                    organisationName,
                    creatorID,
                    description
            );

            return "Event created successfully.";
        } catch (Exception e) {
            // Handle exceptions, e.g., if the event creation fails
            return "Error creating event: " + e.getMessage();
        }
    }


    // Additional method to retrieve all events
    @GetMapping("/getAll")
    public String getAll() {
        String query = "SELECT " +
                "name, " +
                "ST_AsText(bbox) AS bbox, " +
                "organisationName, " +
                "creatorID, " +
                "description " +
                "FROM Event";

        try {
            List<Map<String, Object>> events = jdbcTemplate.queryForList(query);
            if (!events.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(events);
            } else {
                return "No users found";
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if there's an issue with the database query
            return "Error retrieving users: " + e.getMessage();
        }
    }

    // Helper method to check if a user has already joined an event
    private boolean userAlreadyJoinedEvent(int userID, int eventID) {
        String query = "SELECT COUNT(*) FROM `Joined Events` WHERE userID = ? AND eventID = ?";

        // Execute the SQL query and retrieve the result
        List<Map<String, Object>> result = jdbcTemplate.queryForList(query, userID, eventID);

        // Check if the result is not empty and the count is greater than 0
        return !result.isEmpty() && ((Number) result.get(0).get("COUNT(*)")).intValue() > 0;
    }

    @PostMapping("/joinEvent")
    public String joinEvent(@RequestBody String jsonText) {
        String query = "INSERT INTO `Joined Events` (" +
                "userID, eventID) VALUES (?, ?)";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonText);

            // Extract values from JSON
            int userID = jsonNode.get("userID").asInt();
            int eventID = jsonNode.get("eventID").asInt();

            // Ensure user exists in database
            if (databaseChecker.keyNotInDB(
                    "User",
                    "userID",
                    userID
            )) {
                return "User with ID " + userID + " does not exist in db.";
            }

            // Ensure event exists in database
            if (databaseChecker.keyNotInDB(
                    "Event",
                    "eventID",
                    eventID
            )) {
                return "Event with ID " + eventID + " does not exist in db.";
            }

            if (userAlreadyJoinedEvent(userID, eventID)){
                return "User already joined this event.";
            }

            // Insert values into the database
            jdbcTemplate.update(
                    query,
                    userID,
                    eventID
            );

            return "User " + userID + " joined event " + eventID + " successfully.";
        } catch (Exception e) {
            // Handle exceptions, e.g., if the event creation fails
            return "Error joining event: " + e.getMessage();
        }
    }
}

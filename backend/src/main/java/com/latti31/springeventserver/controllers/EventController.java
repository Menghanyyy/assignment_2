package com.latti31.springeventserver.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.latti31.springeventserver.objects.DatabaseChecker;
import com.latti31.springeventserver.objects.JSONResponseWrapper;
import com.latti31.springeventserver.objects.RandomStringGenerator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
public class EventController {

    public static int LINK_LENGTH = 15;

    private final JdbcTemplate jdbcTemplate;
    private final DatabaseChecker databaseChecker;
    private final JSONResponseWrapper jsonWrapper = new JSONResponseWrapper();
    private final RandomStringGenerator randomStringGenerator;

    public EventController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.databaseChecker = new DatabaseChecker(jdbcTemplate);
        this.randomStringGenerator = new RandomStringGenerator();
    }

    @GetMapping("/getByID/{id}")
    public String getEvent(@PathVariable int id) {
        String query = "SELECT " +
                "eventID, " +
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
                return jsonWrapper.wrapJsonNode(true, objectMapper.valueToTree(event));
            } else {
                return jsonWrapper.wrapString(false, "Event not found");
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if the event with the specified ID doesn't exist
            return jsonWrapper.wrapString(false, "Error retrieving event: " +
                    e.getMessage());
        }
    }

    @GetMapping("/getEventLinkByID/{id}")
    public String getEventLinkByID(@PathVariable int id) {
        String query = "SELECT " +
                "link " +
                "FROM Event WHERE eventID = ?";
        try {
            // Get actual password
            List<Map<String, Object>> events = jdbcTemplate.queryForList(query, id);
            if (events.size() == 1) {
                String link = (String) events.get(0).get("link");
                return jsonWrapper.wrapString(true, link);
            }
            return jsonWrapper.wrapString(false, "Event Not Unique");
        } catch (Exception e) {
            // Handle exceptions, e.g., if the event with the specified ID doesn't exist
            return jsonWrapper.wrapString(false, "Error getting link: " +
                    e.getMessage());
        }
    }

    @PostMapping("/addEvent")
    public String createEvent(@RequestBody String jsonText) {
        String query = "INSERT INTO Event (" +
                "bbox, " +
                "name, " +
                "organisationName, " +
                "creatorID, " +
                "description, " +
                "link) VALUES (ST_PolygonFromText(?), ?, ?, ?, ?, ?)";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonText);

            // Default to the bounding box for all of Australia
            String bbox = LocationController.getSQLBBOX(
                    -43.6345972634,
                    -10.6681857235,
                    113.338953078,
                    153.569469029
            );

            // Extract values from JSON
            String name = jsonNode.get("name").asText();
            String organisationName = jsonNode.get("organisationName").asText();
            String description = jsonNode.get("description").asText();

            // Ensure creator exists in database
            int creatorID = jsonNode.get("creatorID").asInt();
            if (databaseChecker.keyNotInDBInt(
                    "User",
                    "userID",
                    creatorID
                    )) {
                return jsonWrapper.wrapString(false, "User with ID "
                        + creatorID + " does not exist in db.");
            }

            if (!databaseChecker.keyNotInDBString(
                    "Event",
                    "name",
                    name
            )) {
                return jsonWrapper.wrapString(false, "Event name already exists");
            }

            String link = randomStringGenerator.generateRandomString(LINK_LENGTH);

            // Insert values into the database
            jdbcTemplate.update(
                    query,
                    bbox,
                    name,
                    organisationName,
                    creatorID,
                    description,
                    link
            );

            try {
                int generatedEventID = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
                return jsonWrapper.wrapString(true, Integer.toString(generatedEventID));

            } catch (Exception ex){
                return jsonWrapper.wrapString(false, "Error getting last insert ID (event)"
                        + ex.getMessage());
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if the event creation fails
            return jsonWrapper.wrapString(false, "Error creating event: " + e.getMessage());
        }
    }

    // Additional method to retrieve all events
    @GetMapping("/getAll")
    public String getAll() {
        String query = "SELECT " +
                "eventID, " +
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
                return jsonWrapper.wrapJsonNode(true, objectMapper.valueToTree(events));
            } else {
                return jsonWrapper.wrapString(false, "No events found");
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if there's an issue with the database query
            return jsonWrapper.wrapString(false, "Error retrieving events: " + e.getMessage());
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

    // Allows user to join an event (sign up for)
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
            if (databaseChecker.keyNotInDBInt(
                    "User",
                    "userID",
                    userID
            )) {
                return jsonWrapper.wrapString(false, "User with ID " +
                        userID + " does not exist in db.");
            }

            // Ensure event exists in database
            if (databaseChecker.keyNotInDBInt(
                    "Event",
                    "eventID",
                    eventID
            )) {
                return jsonWrapper.wrapString(false, "Event with ID " +
                        eventID + " does not exist in db.");
            }

            if (userAlreadyJoinedEvent(userID, eventID)){
                return jsonWrapper.wrapString(false, "User already joined this event.");
            }

            // Insert values into the database
            jdbcTemplate.update(
                    query,
                    userID,
                    eventID
            );

            return jsonWrapper.wrapString(true, "User " +
                    userID + " joined event " + eventID + " successfully.");
        } catch (Exception e) {
            // Handle exceptions, e.g., if the event creation fails
            return jsonWrapper.wrapString(false, "Error joining event: " + e.getMessage());
        }
    }

    // Get all users who have joined a specific event
    @GetMapping("/getUsersAtEvent/{event_id}")
    public String getUsersAtEvent(@PathVariable int event_id) {
        String query = "SELECT " +
                "u.userID, " +
                "u.name, " +
                "email, " +
                "userName " +
                "FROM `User` u " +
                "JOIN `Joined Events` je ON u.userID = je.userID " +
                "WHERE je.eventID = ?";

        try {
            if (databaseChecker.keyNotInDBInt(
                    "Event",
                    "eventID",
                    event_id
            )) {
                return jsonWrapper.wrapString(false, "Event with ID " +
                        event_id + " not found.");
            }

            List<Map<String, Object>> events = jdbcTemplate.queryForList(query, event_id);
            if (!events.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                return jsonWrapper.wrapJsonNode(true, objectMapper.valueToTree(events));
            } else {
                return jsonWrapper.wrapString(false, "No users found");
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if there's an issue with the database query
            return jsonWrapper.wrapString(false, "Error retrieving users: " + e.getMessage());
        }
    }

    // Get all users who have joined a specific event
    @GetMapping("/getJoinedEvents/{user_id}")
    public String getJoinedEvents(@PathVariable int user_id) {
        String query = "SELECT " +
                "e.eventID, " +
                "name, " +
                "ST_AsText(bbox) AS bbox, " +
                "organisationName, " +
                "creatorID, " +
                "description " +
                "FROM Event e " +
                "JOIN `Joined Events` je ON e.eventID = je.eventID " +
                "WHERE je.userID = ?";

        try {
            if (databaseChecker.keyNotInDBInt(
                    "User",
                    "userID",
                    user_id
            )) {
                return jsonWrapper.wrapString(false, "User with ID " +
                        user_id + " not found.");
            }

            List<Map<String, Object>> events = jdbcTemplate.queryForList(query, user_id);
            if (!events.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                return jsonWrapper.wrapJsonNode(true, objectMapper.valueToTree(events));
            } else {
                return jsonWrapper.wrapString(false, "No events found");
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if there's an issue with the database query
            return jsonWrapper.wrapString(false, "Error retrieving events: " +
                    e.getMessage());
        }
    }

    // Get all events created by a specific user
    @GetMapping("/getCreatedEvents/{user_id}")
    public String getCreatedEvents(@PathVariable int user_id) {
        String query = "SELECT " +
                "eventID, " +
                "name, " +
                "ST_AsText(bbox) AS bbox, " +
                "organisationName, " +
                "creatorID, " +
                "description " +
                "FROM Event " +
                "WHERE creatorID = ?";

        try {
            if (databaseChecker.keyNotInDBInt(
                    "User",
                    "userID",
                    user_id
            )) {
                return jsonWrapper.wrapString(false, "User with ID " +
                        user_id + " not found.");
            }

            List<Map<String, Object>> events = jdbcTemplate.queryForList(query, user_id);
            if (!events.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                return jsonWrapper.wrapJsonNode(true, objectMapper.valueToTree(events));
            } else {
                return jsonWrapper.wrapString(false, "No events found");
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if there's an issue with the database query
            return jsonWrapper.wrapString(false, "Error retrieving events: " +
                    e.getMessage());
        }
    }
}
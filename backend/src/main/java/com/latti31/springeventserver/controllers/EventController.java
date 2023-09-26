package com.latti31.springeventserver.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
public class EventController {

    private final JdbcTemplate jdbcTemplate;

    public EventController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
            int creatorID = jsonNode.get("creatorID").asInt();
            String description = jsonNode.get("description").asText();

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
}

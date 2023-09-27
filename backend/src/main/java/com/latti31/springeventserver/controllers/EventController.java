package com.latti31.springeventserver.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/{id}")
    public String getEvent(@PathVariable int id) {
        String query = "SELECT eventID, name, ST_AsText(bbox) AS bbox, Organisation_idOrganisation FROM Event WHERE eventID = ?";
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


    @PostMapping("/create")
    public String createEvent(@RequestBody String jsonText) {
        String query = "INSERT INTO Event (bbox, name, Organisation_idOrganisation) VALUES (ST_PolygonFromText(?), ?, ?)";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonText);

            // Extract values from JSON
            String bbox = jsonNode.get("bbox").asText();
            String name = jsonNode.get("name").asText();
            int idOrganisation = jsonNode.get("Organisation_idOrganisation").asInt();

            // Insert values into the database
            jdbcTemplate.update(
                    query,
                    bbox,
                    name,
                    idOrganisation
            );

            return "Event created successfully.";
        } catch (Exception e) {
            // Handle exceptions, e.g., if the event creation fails
            return "Error creating event: " + e.getMessage();
        }
    }


    // Additional method to retrieve all events
//    @GetMapping("/all")
//    public List<String> getAllEvents() {
//        String query = "SELECT event_data FROM Event";
//        return jdbcTemplate.queryForList(query, String.class);
//    }
}

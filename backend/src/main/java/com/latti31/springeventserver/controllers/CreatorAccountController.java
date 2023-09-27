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
@RequestMapping("/creators")
public class CreatorAccountController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/{id}")
    public String getCreatorAccount(@PathVariable int id) {
        String query = "SELECT name, email FROM `Creator Account` WHERE creatorID = ?";
        try {
            List<Map<String, Object>> creators = jdbcTemplate.queryForList(query, id);
            if (!creators.isEmpty()) {
                Map<String, Object> creator = creators.get(0);
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(creator);
            } else {
                return "Creator not found";
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if the event with the specified ID doesn't exist
            return "Error retrieving creator: " + e.getMessage();
        }
    }


    @PostMapping("/add")
    public String addCreatorAccount(@RequestBody String jsonText) {
        String query = "INSERT INTO `Creator Account` (name, email) VALUES (?, ?)";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonText);

            // Extract values from JSON
            String name = jsonNode.get("name").asText();
            String email = jsonNode.get("email").asText();

            // Insert values into the database
            jdbcTemplate.update(
                    query,
                    name,
                    email
            );

            return "Creator Account added successfully.";
        } catch (Exception e) {
            return "Error Adding Creator Account: " + e.getMessage();
        }
    }


    // Additional method to retrieve all events
//    @GetMapping("/all")
//    public List<String> getAllEvents() {
//        String query = "SELECT event_data FROM Event";
//        return jdbcTemplate.queryForList(query, String.class);
//    }
}

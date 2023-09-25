package com.latti31.springeventserver.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Blob;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserController {

    private final JdbcTemplate jdbcTemplate;

    public UserController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("getByID/{user_id}")
    public String getUserByID(@PathVariable int user_id) {
        String query = "SELECT name, email FROM `User` WHERE userID = ?";

        try {
            List<Map<String, Object>> users = jdbcTemplate.queryForList(query, user_id);
            if (!users.isEmpty()) {
                Map<String, Object> user = users.get(0);
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(user);
            } else {
                return "User not found";
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if the event with the specified ID doesn't exist
            return "Error retrieving user: " + e.getMessage();
        }
    }

    @PostMapping("/addUser")
    public String addUser(@RequestBody String jsonText) {
        String query = "INSERT INTO `User` (email, name, userName, password) VALUES (?, ?, ?, ?)";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonText);

            // Extract values from JSON
            String email = jsonNode.get("email").asText();
            String name = jsonNode.get("name").asText();
            String userName = jsonNode.get("userName").asText();
            String password = jsonNode.get("password").asText();

            // Insert values into the database
            jdbcTemplate.update(query, email, name, userName, password);

            return "User added successfully.";
        } catch (Exception e) {
            // Handle exceptions, e.g., if the account creation fails
            return "Error Adding User: " + e.getMessage();
        }
    }


    // Additional method to retrieve all events
//    @GetMapping("/all")
//    public List<String> getAllEvents() {
//        String query = "SELECT event_data FROM Event";
//        return jdbcTemplate.queryForList(query, String.class);
//    }
}

package com.latti31.springeventserver.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.latti31.springeventserver.objects.DatabaseChecker;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final JdbcTemplate jdbcTemplate;
    private final DatabaseChecker databaseChecker;

    public UserController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.databaseChecker = new DatabaseChecker(jdbcTemplate);
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

    @GetMapping("/getAll")
    public String getAll() {
        String query = "SELECT name, email FROM `User`";

        try {
            List<Map<String, Object>> users = jdbcTemplate.queryForList(query);
            if (!users.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(users);
            } else {
                return "No users found";
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if there's an issue with the database query
            return "Error retrieving users: " + e.getMessage();
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

            if (!databaseChecker.keyNotInDBString(
                    "User",
                    "email",
                    email
            )) {
                return "email already in use";
            }

            if (!databaseChecker.keyNotInDBString(
                    "User",
                    "userName",
                    userName
            )) {
                return "Username already in use";
            }

            // Insert values into the database
            jdbcTemplate.update(query, email, name, userName, password);

            return "User added successfully.";
        } catch (Exception e) {
            // Handle exceptions, e.g., if the account creation fails
            return "Error Adding User: " + e.getMessage();
        }
    }

    @GetMapping("/verifyPassword")
    public String verifyPassword(@RequestBody String jsonText) {
        String query = "SELECT password FROM `User` WHERE userID = ?";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonText);

            // Extract values from JSON
            int userID = jsonNode.get("userID").asInt();
            String givenPassword = jsonNode.get("password").asText();

            // Get actual password
            List<Map<String, Object>> passwords = jdbcTemplate.queryForList(query, userID);
            if (passwords.size() == 1) {
                String correctPassword = (String) passwords.get(0).get("password");

                if (correctPassword.equals(givenPassword)) {
                    return "true";
                }

                return "false";
            }

            // Should be prevented by SQL regardless
            else if (passwords.size() > 1) {
                return "User not unique in system";
            } else {
                return "User not found";
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if the account creation fails
            return "Error Adding User: " + e.getMessage();
        }
    }
}

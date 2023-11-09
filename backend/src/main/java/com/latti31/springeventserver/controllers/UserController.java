package com.latti31.springeventserver.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.latti31.springeventserver.objects.DatabaseChecker;
import com.latti31.springeventserver.objects.JSONResponseWrapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final JdbcTemplate jdbcTemplate;
    private final DatabaseChecker databaseChecker;
    private final JSONResponseWrapper jsonWrapper = new JSONResponseWrapper();

    public UserController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.databaseChecker = new DatabaseChecker(jdbcTemplate);
    }

    @GetMapping("getByID/{user_id}")
    public String getUserByID(@PathVariable int user_id) {
        String query = "SELECT userID, name, email, userName FROM `User` WHERE userID = ?";

        try {
            List<Map<String, Object>> users = jdbcTemplate.queryForList(query, user_id);
            if (!users.isEmpty()) {
                Map<String, Object> user = users.get(0);
                ObjectMapper objectMapper = new ObjectMapper();
                return jsonWrapper.wrapJsonNode(true, objectMapper.valueToTree(user));
            } else {
                return jsonWrapper.wrapString(false, "User not found");
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if the event with the specified ID doesn't exist
            return jsonWrapper.wrapString(false, "Error retrieving user: " +
                    e.getMessage());
        }
    }

    @GetMapping("/getAll")
    public String getAll() {
        String query = "SELECT userID, name, email, userName FROM `User`";

        try {
            List<Map<String, Object>> users = jdbcTemplate.queryForList(query);
            if (!users.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                return jsonWrapper.wrapJsonNode(true, objectMapper.valueToTree(users));
            } else {
                return jsonWrapper.wrapString(false, "No users found");
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if there's an issue with the database query
            return jsonWrapper.wrapString(false, "Error retrieving users: " +
                    e.getMessage());
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
                return jsonWrapper.wrapString(false, "email already in use");
            }

            if (!databaseChecker.keyNotInDBString(
                    "User",
                    "userName",
                    userName
            )) {
                return jsonWrapper.wrapString(false, "Username already in use");
            }

            // Insert values into the database
            jdbcTemplate.update(query, email, name, userName, password);
            try {
                int generatedEventID = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
                return jsonWrapper.wrapString(true, Integer.toString(generatedEventID));

            } catch (Exception ex){
                return jsonWrapper.wrapString(false, "Error getting last insert ID (user)"
                        + ex.getMessage());
            }
        } catch (Exception e) {
            return jsonWrapper.wrapString(false, "Error Adding User: " +
                    e.getMessage());
        }
    }

    @GetMapping("/verifyPassword")
    public String verifyPassword(
            @RequestParam("username") String username,
            @RequestParam("password") String givenPassword) {
        String query = "SELECT password, userID FROM `User` WHERE username = ?";

        try {
            // Get actual password
            List<Map<String, Object>> passwords = jdbcTemplate.queryForList(query, username);
            if (passwords.size() == 1) {
                String correctPassword = (String) passwords.get(0).get("password");
                int userID = (Integer) passwords.get(0).get("userID");

                if (correctPassword.equals(givenPassword)) {
                    return jsonWrapper.wrapString(true, Integer.toString(userID));
                }

                return jsonWrapper.wrapString(false, "Password Incorrect");
            }

            // Should be prevented by SQL regardless
            else if (passwords.size() > 1) {
                return jsonWrapper.wrapString(false, "User not unique in system");
            } else {
                return jsonWrapper.wrapString(false, "Username not found");
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if the account creation fails
            return jsonWrapper.wrapString(false, "Error Verifying pw: " + e.getMessage());
        }
    }
}

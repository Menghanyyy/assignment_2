package com.latti31.springeventserver.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Blob;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/users")
public class UserAccountController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/{id}")
    public String getUserAccount(@PathVariable int id) {
        String query = "SELECT name, email, profilePicture FROM `User Account` WHERE userID = ?";
        try {
            List<Map<String, Object>> users = jdbcTemplate.queryForList(query, id);
            if (!users.isEmpty()) {
                Map<String, Object> user = users.get(0);

                // Retrieve the BLOB data as a byte array
                byte[] profilePictureData = (byte[]) user.get("profilePicture");

                // Convert the byte array to Base64 encoding
                String profilePictureBase64 = Base64.getEncoder().encodeToString(profilePictureData);

                // Create a JSON object
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode jsonObject = objectMapper.createObjectNode();
                jsonObject.put("name", (String) user.get("name"));
                jsonObject.put("email", (String) user.get("email"));
                jsonObject.put("profilePicture", profilePictureBase64);

                // Convert JSON object to a JSON string
                return jsonObject.toString();
            } else {
                return "User not found";
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., if the user with the specified ID doesn't exist
            return "Error retrieving user: " + e.getMessage();
        }
    }

    @PostMapping("/add")
    public String addUserAccount(@RequestBody String jsonText) {
        String query = "INSERT INTO `User Account` (name, email, profilePicture) VALUES (?, ?, ?)";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonText);

            // Extract values from JSON
            String name = jsonNode.get("name").asText();
            String email = jsonNode.get("email").asText();
            String imageBase64 = jsonNode.get("profilePic").asText();

            // Decode the Base64 image data
            byte[] decodedImage = Base64.getDecoder().decode(imageBase64);

            // Create a Blob object from the decoded image data
            Blob imageBlob = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection().createBlob();
            imageBlob.setBytes(1, decodedImage);

            // Insert values into the database
            jdbcTemplate.update(query, name, email, imageBlob);

            return "User Account added successfully.";
        } catch (Exception e) {
            // Handle exceptions, e.g., if the account creation fails
            return "Error Adding User Account: " + e.getMessage();
        }
    }


    // Additional method to retrieve all events
//    @GetMapping("/all")
//    public List<String> getAllEvents() {
//        String query = "SELECT event_data FROM Event";
//        return jdbcTemplate.queryForList(query, String.class);
//    }
}

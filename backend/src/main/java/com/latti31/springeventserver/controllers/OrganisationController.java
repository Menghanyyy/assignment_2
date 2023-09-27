package com.latti31.springeventserver.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/organisations")
public class OrganisationController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/{id}")
    public String getOrganisation(@PathVariable int id) {
        String query = "SELECT name FROM Organisation WHERE idOrganisation = ?";

        try {
            String name = jdbcTemplate.queryForObject(query, String.class, id);
            return "Organisation: " + name;
        } catch (Exception e) {
            // Handle exceptions, e.g., if the organisation with the specified ID doesn't exist
            return "Error retrieving organisation: " + e.getMessage();
        }
    }

    @PostMapping("/create")
    public String createOrganisation(@RequestBody String jsonText) {
        String query = "INSERT INTO Organisation (name) VALUES (?)";
        // Parse the JSON string
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonText);

            // Extract the 'name' property from the JSON
            String name = jsonNode.get("name").asText();
            jdbcTemplate.update(query, name);
            return "Organisation created successfully.";
        } catch (Exception e) {
            // Handle exceptions, e.g., if the organisation creation fails
            return "Error creating organisation: " + e.getMessage();
        }
    }
}

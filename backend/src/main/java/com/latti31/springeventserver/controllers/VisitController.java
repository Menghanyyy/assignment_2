package com.latti31.springeventserver.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.latti31.springeventserver.objects.DatabaseChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/visits")
public class VisitController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Get the count for a specific activity
    @GetMapping("/userCount/{user_id}")
    public String getUserVisitCount(@PathVariable int user_id) {
        String query = "SELECT COUNT(*) FROM Visit WHERE `User Account_userID` = ?";

        try {
            int visitCount = jdbcTemplate.queryForObject(query, Integer.class, user_id);
            return Integer.toString(visitCount);
        } catch (Exception e) {
            // Handle exceptions, e.g., if there's an issue with the query or database
            return "Error retrieving visit count: " + e.getMessage();
        }
    }

    // Get the count for a specific activity
    @GetMapping("/activityCount/{activity_id}")
    public String getActivityVisitCount(@PathVariable int activity_id) {
        String query = "SELECT COUNT(*) FROM Visit WHERE `Activity_activityID` = ?";

        try {
            int visitCount = jdbcTemplate.queryForObject(query, Integer.class, activity_id);
            return Integer.toString(visitCount);
        } catch (Exception e) {
            // Handle exceptions, e.g., if there's an issue with the query or database
            return "Error retrieving visit count: " + e.getMessage();
        }
    }


}

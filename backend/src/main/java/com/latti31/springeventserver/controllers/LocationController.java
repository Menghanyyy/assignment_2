package com.latti31.springeventserver.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.latti31.springeventserver.objects.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
@RequestMapping("/locations")
public class LocationController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LocationController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private boolean locationCollides(Location location, Location start, Location end) {

        // Check if both lines are vertical
        if (start.getLongitude() == end.getLongitude()) {
            // No collision if both lines are vertical
            return false;
        }

        // Calculate the slope of the line from start to end
        double slope = (end.getLatitude() - start.getLatitude()) / (end.getLongitude() - start.getLongitude());

        // Calculate the latitude of the intersection point
        double intersectionLatitude = start.getLatitude() + (location.getLongitude() - start.getLongitude()) * slope;

        return (
                location.getLatitude() <= intersectionLatitude
                && Math.min(start.getLongitude(), end.getLongitude()) < location.getLongitude()
                && Math.max(start.getLongitude(), end.getLongitude()) > location.getLongitude()
        );
    }
    private boolean inPolygon(Location location, ArrayList<Location> polygon){
        if (polygon.size() <= 2){
            return false;
        }

        boolean isInside = false;
        Location start = polygon.get(polygon.size() - 1);
        Location end = polygon.get(0);

        for (int i = 0; i < polygon.size(); i++){
            if (locationCollides(location, start, end)){
                isInside = !isInside;
            }

            if (i < polygon.size() - 1){
                // Update pointers
                start = polygon.get(i);
                end = polygon.get(i + 1);
            }
        }
        return isInside;
    }

    public static List<Location> convertWktPolygon(String wktPolygon) {
        List<Location> locations = new ArrayList<>();

        // Regular expression to match coordinates inside the polygon
        Pattern pattern = Pattern.compile("(-?\\d+\\.\\d+) (-?\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(wktPolygon);

        // Find and store coordinates
        while (matcher.find()) {
            double latitude = Double.parseDouble(matcher.group(2));
            double longitude = Double.parseDouble(matcher.group(1));
            locations.add(new Location(latitude, longitude));
        }

        return locations;
    }

    @GetMapping("/activitiesAtLocation")
    public String activitiesAtLocation(@RequestBody String jsonText) {

        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonText);

            // Extract values from JSON
            int eventID = jsonNode.get("eventID").asInt();
            double latitude = jsonNode.get("latitude").asDouble();
            double longitude = jsonNode.get("longitude").asDouble();

            String query = "SELECT " +
                    "activityID " +
                    "FROM Activity " +
                    "WHERE eventID = ? " +
                    "AND ST_Contains(polygonLocation, " +
                    "ST_GeomFromText('POINT(" + longitude + " " + latitude + ")'))";

            List<Map<String, Object>> activities = jdbcTemplate.queryForList(query, eventID);

            try {
                // Convert the ArrayList to a JSON string
                return objectMapper.writeValueAsString(activities);

            } catch (Exception e) {
                return "Error getting events: " + e.getMessage();
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}

package com.latti31.springeventserver.controllers;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;


@RestController
public class EventRESTController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EventRESTController(JdbcTemplate jdbcTemplate) {
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

    @GetMapping("/greet")
    public String greetMessage(){
        return "Hello Friend";
    }

    @GetMapping("/location/{param}")
    public String getDummyDataWithPathParam(@PathVariable String param) {

        // Dummy polygon (will come from database later)
        ArrayList<Location> poly = new ArrayList<>();
        poly.add(new Location(-37.84746047827345, 144.8671836223046));
        poly.add(new Location(-37.76695394333571, 144.97203533372885));
        poly.add(new Location(-37.8388813581512, 145.01629677792226));

        String[] splitParam = param.split(",");

        double lat = Double.parseDouble(splitParam[0]);
        double lon = Double.parseDouble(splitParam[1]);
        return "Your shape is inside: " + inPolygon(new Location(lat, lon), poly);
    }

    @GetMapping("/location")
    public String getDummyDataWithRequestParam(@RequestParam String param) {
        return "Received parameter: " + param;
    }

    @GetMapping("/test_push/{param}")
    public String greetMessage(@PathVariable String param) {
        // create the SQL insert statement
//        String query = "INSERT INTO test_table (test_attribute) VALUES (3)";
        String query = "INSERT INTO test_table_1 (attribute_1) VALUES (3)";

        try {
            jdbcTemplate.update(query);
            return "Hello Friend. Data inserted successfully.";
        } catch (Exception e) {
            // Handle exceptions
            return "Error inserting data: " + e.getMessage();
        }
    }
}

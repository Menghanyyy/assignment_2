package com.latti31.springeventserver.objects;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapboxUploader {

    private static String accessToken = "sk.eyJ1IjoiYWRyaWFudGVvMTEyMSIsImEiOiJjbG1uZXU3bzQwMmRtMmtwMmQ3cWV5d2M2In0.9ddhigLDMQFkY_Inz6f_Vw";

    public static boolean makePostRequest() {
        try {
            // API Endpoint and Access Token
            String apiUrl = "https://api.mapbox.com/uploads/v1/adrianteo1121?access_token=" + accessToken;

            // JSON Body
            String jsonBody = "{ \"tileset\": \"adrianteo1121.clnd78cu6336p2no1j6lxfh3e-4w0gm\", " +
                    "\"url\": \"mapbox://datasets/adrianteo1121/clnd78cu6336p2no1j6lxfh3e\", " +
                    "\"name\": \"activities\" }";

            // Create URL object
            URL url = new URL(apiUrl);

            // Open connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method to POST
            connection.setRequestMethod("POST");

            // Enable input/output streams
            connection.setDoOutput(true);

            // Set content type and content length for the request
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", String.valueOf(jsonBody.length()));

            // Write JSON data to the output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the HTTP response code
            int responseCode = connection.getResponseCode();
            Boolean success;

            // Handle the response based on the response code
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                // Success - handle the response as needed
                System.out.println("POST request successful!");
                success = true;
            } else {
                // Error - handle the error response
                System.out.println("POST request failed! Response Code: " + responseCode);
                success = false;
            }

            // Close the connection
            connection.disconnect();

            return success;

        } catch (Exception e) {
            // Handle exceptions (e.g., MalformedURLException, IOException)
            e.printStackTrace();
            return false;
        }
    }

    private static List<String> parsePolygonCoordinates(String polygonString) {
        // Remove "POLYGON(" and ")" from the input string
        String coordinatesString = polygonString.replace("POLYGON((", "").replace("))", "");

        // Split the coordinates using ","
        String[] coordinatePairs = coordinatesString.split(", ");

        // List to store formatted coordinates
        List<String> formattedCoordinates = new ArrayList<>();

        // Process each coordinate pair and format it as "[ latitude, longitude ]"
        for (String pair : coordinatePairs) {
            // Split the pair into latitude and longitude using " "
            String[] latLong = pair.split(" ");

            // Format latitude and longitude and add to the list
            String formattedCoordinate = "[ " + latLong[1] + ", " + latLong[0] + " ]";
            formattedCoordinates.add(formattedCoordinate);
        }

        return formattedCoordinates;
    }

    private static String formatCoordinates(List<String> coordinates) {
        StringBuilder formattedCoordinates = new StringBuilder("[");
        for (String coordinate : coordinates) {
            formattedCoordinates.append(coordinate).append(", ");
        }
        formattedCoordinates.delete(formattedCoordinates.length() - 2, formattedCoordinates.length());
        formattedCoordinates.append("]");
        return formattedCoordinates.toString();
    }

    public static void makePutRequest(int id, int eventID, int visited, String SQLString) {
        try {
            // PUT API Endpoint and Access Token
            String apiUrl = "https://api.mapbox.com/datasets/v1/adrianteo1121/clnd78cu6336p2no1j6lxfh3e/features/"
                    + id + "?access_token=" + accessToken;

            // Get formatted coordinates
            List<String> formattedCoordinates = parsePolygonCoordinates(SQLString);
            String coordinatesArray = formatCoordinates(formattedCoordinates);

            // JSON Body
            String jsonBody = "{ \"id\": \"" + id + "\", " +
                    "\"type\": \"Feature\", " +
                    "\"geometry\": { \"type\": \"Polygon\", " +
                    "\"coordinates\": [" + coordinatesArray + "] }, " +
                    "\"properties\": { \"activityID\": \"" + id + "\", " +
                    "\"visited\": \"" + visited + "\", " +
                    "\"eventID\": " + eventID + " } }";

            // Create URL object
            URL url = new URL(apiUrl);

            // Open connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method to PUT
            connection.setRequestMethod("PUT");

            // Enable input/output streams
            connection.setDoOutput(true);

            // Set content type and content length for the request
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", String.valueOf(jsonBody.length()));

            // Write JSON data to the output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the HTTP response code
            int responseCode = connection.getResponseCode();

            // Handle the response based on the response code
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Success - handle the response as needed
                System.out.println("PUT request successful!");
            } else {
                // Error - handle the error response
                System.out.println("PUT request failed! Response Code: " + responseCode);
            }

            // Close the connection
            connection.disconnect();

        } catch (Exception e) {
            // Handle exceptions (e.g., MalformedURLException, IOException)
            e.printStackTrace();
        }
    }
}

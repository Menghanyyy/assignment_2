package com.latti31.springeventserver.objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;
import java.util.Map;

public class JSONResponseWrapper {

    public static String ERROR_KEY = "error";
    public static String STATUS_KEY = "status";
    public static String SUCCESS_KEY = "success";
    public static String MESSAGE = "message";

    public String wrapJsonNode(boolean isSuccess, JsonNode jsonNode) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode rootNode = objectMapper.createObjectNode();

            if (isSuccess) {
                rootNode.put(STATUS_KEY, SUCCESS_KEY);
            } else {
                rootNode.put(STATUS_KEY, ERROR_KEY);
            }
            rootNode.set(MESSAGE, jsonNode);

            ObjectNode truncatedRootNode = truncateJsonNode(rootNode, 250); // Set the truncation length as needed

            System.out.println("Returning object: " + objectMapper.writeValueAsString(truncatedRootNode));

            return objectMapper.writeValueAsString(rootNode);
        } catch (Exception e) {
            return null;
        }
    }

    private ObjectNode truncateJsonNode(ObjectNode node, int maxLength) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode truncatedNode = objectMapper.createObjectNode();

        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String key = entry.getKey();
            JsonNode value = entry.getValue();

            String truncatedValue = value.toString();
            if (truncatedValue.length() > maxLength) {
                truncatedValue = truncatedValue.substring(0, maxLength) + "...";
            }

            truncatedNode.put(key, truncatedValue);
        }

        return truncatedNode;
    }

    public String wrapString(boolean isSuccess, String messageString){

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode rootNode = objectMapper.createObjectNode();

            if (isSuccess){
                rootNode.put(STATUS_KEY, SUCCESS_KEY);
            } else{
                rootNode.put(STATUS_KEY, ERROR_KEY);
            }
            rootNode.put(MESSAGE, messageString);

            System.out.println("Returning object: " + objectMapper.writeValueAsString(rootNode));
            return objectMapper.writeValueAsString(rootNode);
        } catch (Exception e) {
            return null;
        }
    }

    public String wrapInt(boolean isSuccess, int messageInt){

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode rootNode = objectMapper.createObjectNode();

            if (isSuccess){
                rootNode.put(STATUS_KEY, SUCCESS_KEY);
            } else{
                rootNode.put(STATUS_KEY, ERROR_KEY);
            }
            rootNode.put(MESSAGE, messageInt);

            System.out.println("Returning object: " + objectMapper.writeValueAsString(rootNode));
            return objectMapper.writeValueAsString(rootNode);
        } catch (Exception e) {
            return null;
        }
    }
}


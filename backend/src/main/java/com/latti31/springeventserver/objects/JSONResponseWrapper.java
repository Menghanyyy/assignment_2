package com.latti31.springeventserver.objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

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

            System.out.println("Returning object: " + objectMapper.writeValueAsString(rootNode));
            return objectMapper.writeValueAsString(rootNode);
        } catch (Exception e) {
            return null;
        }
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

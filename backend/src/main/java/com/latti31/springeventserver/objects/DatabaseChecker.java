package com.latti31.springeventserver.objects;

import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseChecker {

    private JdbcTemplate jdbcTemplate; // You need to inject the JdbcTemplate

    public DatabaseChecker(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean keyNotInDB(String tableName, String primaryKeyColumnName, int primaryKeyValue) {
        String surroundedTableName = "`" + tableName + "`";
        String query = "SELECT COUNT(*) FROM " + surroundedTableName + " WHERE " + primaryKeyColumnName + " = ?";

        try {
            // Execute the query and check if the count is greater than 0
            int count = jdbcTemplate.queryForObject(query, Integer.class, primaryKeyValue);
            return count <= 0;
        } catch (Exception e) {
            // Log or print the exception for debugging
            e.printStackTrace(); // You can replace this with your preferred logging mechanism
            return true;
        }
    }



}

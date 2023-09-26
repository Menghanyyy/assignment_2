package com.latti31.springeventserver.objects;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

public class DatabaseChecker {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseChecker(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean keyNotInDB(String tableName, String primaryKeyColumnName, int primaryKeyValue) {
        String surroundedTableName = "`" + tableName + "`";
        String query = "SELECT COUNT(*) FROM " + surroundedTableName +
                " WHERE " + primaryKeyColumnName + " = ?";

        try {
            // Execute the query and retrieve the result as a list of maps
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, primaryKeyValue);

            // Check if the result is empty or the count is not greater than 0
            return result.isEmpty() || ((Number) result.get(0).get("COUNT(*)")).intValue() <= 0;
        } catch (Exception e) {
            return true;
        }
    }



}

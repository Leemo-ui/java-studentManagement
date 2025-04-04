package com.studentmanagement.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitializer {
    public static void initializeDatabase() {
        try {
            // Read and execute the schema script
            executeScript("/schema.sql");
            
            // Read and execute the sample data script
            executeScript("/sample_data.sql");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
    
    private static void executeScript(String scriptPath) throws Exception {
        // Read the SQL script
        String sqlScript;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    DatabaseInitializer.class.getResourceAsStream(scriptPath)))) {
            sqlScript = reader.lines().collect(Collectors.joining("\n"));
        }

        // Split the script into individual statements
        String[] statements = sqlScript.split(";");

        // Execute each statement
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            for (String statement : statements) {
                if (!statement.trim().isEmpty()) {
                    stmt.execute(statement);
                }
            }
        }
    }
} 
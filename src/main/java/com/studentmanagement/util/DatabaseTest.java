package com.studentmanagement.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseTest {
    public static void main(String[] args) {
        try {
            // Test connection
            if (DatabaseConnection.testConnection()) {
                System.out.println("Database connection test successful!");
                
                // Test query
                try (Connection conn = DatabaseConnection.getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Student")) {
                    
                    if (rs.next()) {
                        System.out.println("Number of students in database: " + rs.getInt("count"));
                    }
                }
            } else {
                System.out.println("Database connection test failed!");
            }
        } catch (Exception e) {
            System.err.println("Error during database test: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
} 
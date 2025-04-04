package com.studentmanagement.dao;

import com.studentmanagement.model.Programme;
import com.studentmanagement.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public interface ProgrammeDAO extends FileBasedDAO<Programme, String> {
    
    public static void addProgramme(Programme programme) throws SQLException {
        String sql = "INSERT INTO programmes (code, name, department) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, programme.getCode());
            stmt.setString(2, programme.getName());
            stmt.setString(3, programme.getDepartment());
            stmt.executeUpdate();
        }
    }
    
    public static void updateProgramme(Programme programme) throws SQLException {
        String sql = "UPDATE programmes SET name = ?, department = ? WHERE code = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, programme.getName());
            stmt.setString(2, programme.getDepartment());
            stmt.setString(3, programme.getCode());
            stmt.executeUpdate();
        }
    }
    
    public static void deleteProgramme(String code) throws SQLException {
        String sql = "DELETE FROM programmes WHERE code = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            stmt.executeUpdate();
        }
    }
    
    public static Programme getProgramme(String code) throws SQLException {
        String sql = "SELECT code, name, department FROM programmes WHERE code = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Programme(
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("department")
                    );
                }
                return null;
            }
        }
    }
    
    public static List<Programme> getAllProgrammes() throws SQLException {
        List<Programme> programmes = new ArrayList<>();
        String sql = "SELECT code, name, department FROM programmes ORDER BY code";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                programmes.add(new Programme(
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("department")
                ));
            }
        }
        return programmes;
    }
} 
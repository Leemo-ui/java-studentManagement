package com.studentmanagement.dao;

import com.studentmanagement.model.Programme;
import com.studentmanagement.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgrammeDAOImpl implements ProgrammeDAO {

    @Override
    public Programme save(Programme programme) throws Exception {
        String sql = "INSERT INTO Programme (programme_id, name) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, programme.getCode());
            pstmt.setString(2, programme.getName());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating programme failed, no rows affected.");
            }
            
            return programme;
        }
    }

    @Override
    public Programme update(Programme programme) throws Exception {
        String sql = "UPDATE Programme SET name = ? WHERE programme_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, programme.getName());
            pstmt.setString(2, programme.getCode());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating programme failed, no rows affected.");
            }
            
            return programme;
        }
    }

    @Override
    public void delete(String code) throws Exception {
        String sql = "DELETE FROM Programme WHERE programme_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, code);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting programme failed, no rows affected.");
            }
        }
    }

    @Override
    public Programme findById(String code) throws Exception {
        String sql = "SELECT * FROM Programme WHERE programme_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, code);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Programme(
                        rs.getString("programme_id"),
                        rs.getString("name"),
                        "Department" // Default value since department is not in the Programme table
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<Programme> findAll() throws Exception {
        List<Programme> programmes = new ArrayList<>();
        String sql = "SELECT * FROM Programme";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                programmes.add(new Programme(
                    rs.getString("programme_id"),
                    rs.getString("name"),
                    "Department" // Default value since department is not in the Programme table
                ));
            }
        }
        return programmes;
    }
} 
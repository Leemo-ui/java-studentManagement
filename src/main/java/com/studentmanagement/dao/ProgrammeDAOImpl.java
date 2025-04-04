package com.studentmanagement.dao;

import com.studentmanagement.model.Programme;
import com.studentmanagement.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgrammeDAOImpl implements ProgrammeDAO {

    @Override
    public Programme save(Programme programme) throws Exception {
        String sql = "INSERT INTO programmes (code, name, department) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, programme.getCode());
            pstmt.setString(2, programme.getName());
            pstmt.setString(3, programme.getDepartment());
            pstmt.executeUpdate();
            
            return programme;
        }
    }

    @Override
    public Programme update(Programme programme) throws Exception {
        String sql = "UPDATE programmes SET name = ?, department = ? WHERE code = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, programme.getName());
            pstmt.setString(2, programme.getDepartment());
            pstmt.setString(3, programme.getCode());
            pstmt.executeUpdate();
            
            return programme;
        }
    }

    @Override
    public void delete(String code) throws Exception {
        String sql = "DELETE FROM programmes WHERE code = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, code);
            pstmt.executeUpdate();
        }
    }

    @Override
    public Programme findById(String code) throws Exception {
        String sql = "SELECT * FROM programmes WHERE code = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, code);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Programme(
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("department")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<Programme> findAll() throws Exception {
        List<Programme> programmes = new ArrayList<>();
        String sql = "SELECT * FROM programmes";
        
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
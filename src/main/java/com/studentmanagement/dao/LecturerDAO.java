package com.studentmanagement.dao;

import com.studentmanagement.model.Lecturer;
import com.studentmanagement.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LecturerDAO {
    
    public static int addLecturer(String name, String email) throws SQLException {
        String sqlPerson = "INSERT INTO persons (name, email, type) VALUES (?, ?, 'LECTURER')";
        String sqlLecturer = "INSERT INTO lecturers (person_id) VALUES (?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);
            
            try {
                // Insert into Person table
                PreparedStatement stmtPerson = conn.prepareStatement(sqlPerson, Statement.RETURN_GENERATED_KEYS);
                stmtPerson.setString(1, name);
                stmtPerson.setString(2, email);
                stmtPerson.executeUpdate();
                
                // Get the generated person ID
                int personId;
                try (ResultSet rs = stmtPerson.getGeneratedKeys()) {
                    if (rs.next()) {
                        personId = rs.getInt(1);
                    } else {
                        throw new SQLException("Failed to get generated person ID");
                    }
                }
                
                // Insert into Lecturer table
                PreparedStatement stmtLecturer = conn.prepareStatement(sqlLecturer);
                stmtLecturer.setInt(1, personId);
                stmtLecturer.executeUpdate();
                
                // Commit transaction
                conn.commit();
                return personId;
            } catch (SQLException e) {
                // Rollback transaction on error
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    
    public static void updateLecturer(Lecturer lecturer) throws SQLException {
        String sqlPerson = "UPDATE persons SET name = ?, email = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);
            
            try {
                // Update Person table
                PreparedStatement stmtPerson = conn.prepareStatement(sqlPerson);
                stmtPerson.setString(1, lecturer.getName());
                stmtPerson.setString(2, lecturer.getEmail());
                stmtPerson.setInt(3, lecturer.getId());
                stmtPerson.executeUpdate();
                
                // Commit transaction
                conn.commit();
            } catch (SQLException e) {
                // Rollback transaction on error
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    
    public static void deleteLecturer(int id) throws SQLException {
        // First delete from lecturer_course table (due to foreign key constraint)
        String sqlLecturerCourse = "DELETE FROM lecturer_course WHERE lecturer_id = ?";
        // Then delete from Lecturer table
        String sqlLecturer = "DELETE FROM lecturers WHERE person_id = ?";
        // Finally delete from Person table
        String sqlPerson = "DELETE FROM persons WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);
            
            try {
                // Delete from lecturer_course table first
                PreparedStatement stmtLecturerCourse = conn.prepareStatement(sqlLecturerCourse);
                stmtLecturerCourse.setInt(1, id);
                stmtLecturerCourse.executeUpdate();
                
                // Then delete from Lecturer table
                PreparedStatement stmtLecturer = conn.prepareStatement(sqlLecturer);
                stmtLecturer.setInt(1, id);
                stmtLecturer.executeUpdate();
                
                // Finally delete from Person table
                PreparedStatement stmtPerson = conn.prepareStatement(sqlPerson);
                stmtPerson.setInt(1, id);
                stmtPerson.executeUpdate();
                
                // Commit transaction
                conn.commit();
            } catch (SQLException e) {
                // Rollback transaction on error
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    
    public static Lecturer getLecturer(int id) throws SQLException {
        String sql = "SELECT p.id, p.name, p.email " +
                     "FROM persons p JOIN lecturers l ON p.id = l.person_id " +
                     "WHERE p.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Lecturer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email")
                    );
                }
                return null;
            }
        }
    }
    
    public static List<Lecturer> getAllLecturers() throws SQLException {
        List<Lecturer> lecturers = new ArrayList<>();
        String sql = "SELECT p.id, p.name, p.email " +
                     "FROM persons p JOIN lecturers l ON p.id = l.person_id " +
                     "ORDER BY p.name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                lecturers.add(new Lecturer(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email")
                ));
            }
        }
        return lecturers;
    }
    
    public static void updateLecturerCourses(int lecturerId, List<Integer> courseIds) throws SQLException {
        String deleteSql = "DELETE FROM lecturer_course WHERE lecturer_id = ?";
        String insertSql = "INSERT INTO lecturer_course (lecturer_id, course_id) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);
            
            try {
                // Delete existing course assignments
                PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                deleteStmt.setInt(1, lecturerId);
                deleteStmt.executeUpdate();
                
                // Insert new course assignments
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                for (int courseId : courseIds) {
                    insertStmt.setInt(1, lecturerId);
                    insertStmt.setInt(2, courseId);
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
                
                // Commit transaction
                conn.commit();
            } catch (SQLException e) {
                // Rollback transaction on error
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    
    public static int getCourseIdByName(String courseName) throws SQLException {
        String sql = "SELECT id FROM courses WHERE name = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, courseName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
                throw new SQLException("Course not found: " + courseName);
            }
        }
    }
}
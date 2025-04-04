package com.studentmanagement.dao;

import com.studentmanagement.model.Student;
import com.studentmanagement.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public interface StudentDAO extends FileBasedDAO<Student, Integer> {
    
    public static int addStudent(String name, String email, String programmeCode) throws SQLException {
        String sqlPerson = "INSERT INTO persons (name, email, type) VALUES (?, ?, 'STUDENT')";
        String sqlStudent = "INSERT INTO students (person_id, programme_id) VALUES (?, ?)";
        
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
                
                // Insert into Student table
                PreparedStatement stmtStudent = conn.prepareStatement(sqlStudent);
                stmtStudent.setInt(1, personId);
                stmtStudent.setString(2, programmeCode);
                stmtStudent.executeUpdate();
                
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
    
    public static void updateStudent(Student student) throws SQLException {
        String sqlPerson = "UPDATE persons SET name = ?, email = ? WHERE id = ?";
        String sqlStudent = "UPDATE students SET programme_id = ? WHERE person_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);
            
            try {
                // Update Person table
                PreparedStatement stmtPerson = conn.prepareStatement(sqlPerson);
                stmtPerson.setString(1, student.getName());
                stmtPerson.setString(2, student.getEmail());
                stmtPerson.setInt(3, student.getId());
                stmtPerson.executeUpdate();
                
                // Update Student table
                PreparedStatement stmtStudent = conn.prepareStatement(sqlStudent);
                stmtStudent.setString(1, student.getProgrammeCode());
                stmtStudent.setInt(2, student.getId());
                stmtStudent.executeUpdate();
                
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
    
    public static void deleteStudent(int id) throws SQLException {
        // First delete from Student table (due to foreign key constraint)
        String sqlStudent = "DELETE FROM students WHERE person_id = ?";
        // Then delete from Person table
        String sqlPerson = "DELETE FROM persons WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);
            
            try {
                // Delete from Student table first
                PreparedStatement stmtStudent = conn.prepareStatement(sqlStudent);
                stmtStudent.setInt(1, id);
                stmtStudent.executeUpdate();
                
                // Then delete from Person table
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
    
    public static Student getStudent(int id) throws SQLException {
        String sql = "SELECT s.*, p.name, p.email, pr.name as programme_name, pr.programme_id " +
                    "FROM students s " +
                    "JOIN persons p ON s.person_id = p.id " +
                    "LEFT JOIN Programme pr ON s.programme_id = pr.programme_id " +
                    "WHERE s.person_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                        rs.getInt("person_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("programme_id"),
                        rs.getString("programme_name")
                    );
                }
                return null;
            }
        }
    }
    
    public static List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, p.name, p.email, pr.name as programme_name, pr.programme_id " +
                    "FROM students s " +
                    "JOIN persons p ON s.person_id = p.id " +
                    "LEFT JOIN Programme pr ON s.programme_id = pr.programme_id " +
                    "ORDER BY p.name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                students.add(new Student(
                    rs.getInt("person_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("programme_id"),
                    rs.getString("programme_name")
                ));
            }
        }
        return students;
    }
    
    public static List<Student> getStudentsByProgramme(String programmeCode) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, p.name, p.email, pr.name as programme_name, pr.programme_id " +
                    "FROM students s " +
                    "JOIN persons p ON s.person_id = p.id " +
                    "LEFT JOIN Programme pr ON s.programme_id = pr.programme_id " +
                    "WHERE s.programme_id = ? " +
                    "ORDER BY p.name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, programmeCode);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(new Student(
                        rs.getInt("person_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("programme_id"),
                        rs.getString("programme_name")
                    ));
                }
            }
        }
        return students;
    }
} 
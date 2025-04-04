package com.studentmanagement.dao;

import com.studentmanagement.model.Student;
import com.studentmanagement.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAOImpl implements StudentDAO {

    @Override
    public Student save(Student student) throws Exception {
        String sqlPerson = "INSERT INTO persons (name, email, type) VALUES (?, ?, 'STUDENT')";
        String sqlStudent = "INSERT INTO students (person_id, programme_code) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);
            
            try {
                // Insert into Person table
                PreparedStatement stmtPerson = conn.prepareStatement(sqlPerson, Statement.RETURN_GENERATED_KEYS);
                stmtPerson.setString(1, student.getName());
                stmtPerson.setString(2, student.getEmail());
                stmtPerson.executeUpdate();
                
                // Get the generated person ID
                int personId;
                try (ResultSet rs = stmtPerson.getGeneratedKeys()) {
                    if (rs.next()) {
                        personId = rs.getInt(1);
                    } else {
                        throw new SQLException("Creating student failed, no ID obtained.");
                    }
                }
                
                // Insert into Student table
                PreparedStatement stmtStudent = conn.prepareStatement(sqlStudent);
                stmtStudent.setInt(1, personId);
                stmtStudent.setString(2, student.getProgrammeCode());
                stmtStudent.executeUpdate();
                
                // Commit transaction
                conn.commit();
                
                // Set the ID and return
                student.setId(personId);
                return student;
            } catch (SQLException e) {
                // Rollback transaction on error
                conn.rollback();
                throw e;
            } finally {
                // Reset auto-commit
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public Student update(Student student) throws Exception {
        String sqlPerson = "UPDATE persons SET name = ?, email = ? WHERE id = ?";
        String sqlStudent = "UPDATE students SET programme_code = ? WHERE person_id = ?";
        
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
                
                return student;
            } catch (SQLException e) {
                // Rollback transaction on error
                conn.rollback();
                throw e;
            } finally {
                // Reset auto-commit
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public void delete(Integer id) throws Exception {
        String sqlStudent = "DELETE FROM students WHERE person_id = ?";
        String sqlPerson = "DELETE FROM persons WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);
            
            try {
                // Delete from Student table first (due to foreign key constraint)
                PreparedStatement stmtStudent = conn.prepareStatement(sqlStudent);
                stmtStudent.setInt(1, id);
                stmtStudent.executeUpdate();
                
                // Delete from Person table
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
                // Reset auto-commit
                conn.setAutoCommit(true);
            }
        }
    }

    @Override
    public Student findById(Integer id) throws Exception {
        String sql = "SELECT s.*, p.name, p.email, pr.name as programme_name, pr.code as programme_code " +
                    "FROM students s " +
                    "JOIN persons p ON s.person_id = p.id " +
                    "LEFT JOIN programmes pr ON s.programme_code = pr.code " +
                    "WHERE s.person_id = ?";
                    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                        rs.getInt("person_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("programme_code"),
                        rs.getString("programme_name")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<Student> findAll() throws Exception {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, p.name, p.email, pr.name as programme_name, pr.code as programme_code " +
                    "FROM students s " +
                    "JOIN persons p ON s.person_id = p.id " +
                    "LEFT JOIN programmes pr ON s.programme_code = pr.code";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                students.add(new Student(
                    rs.getInt("person_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("programme_code"),
                    rs.getString("programme_name")
                ));
            }
        }
        return students;
    }
} 
package com.studentmanagement.dao;

import com.studentmanagement.model.Course;
import com.studentmanagement.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAOImpl implements CourseDAO {

    @Override
    public Course save(Course course) throws Exception {
        String sql = "INSERT INTO Course (course_id, name, credits) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, course.getCode());
            pstmt.setString(2, course.getName());
            pstmt.setInt(3, course.getCredits());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating course failed, no rows affected.");
            }
            
            return course;
        }
    }

    @Override
    public Course update(Course course) throws Exception {
        String sql = "UPDATE Course SET name = ?, credits = ? WHERE course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, course.getName());
            pstmt.setInt(2, course.getCredits());
            pstmt.setString(3, course.getCode());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating course failed, no rows affected.");
            }
            
            return course;
        }
    }

    @Override
    public void delete(Integer id) throws Exception {
        String sql = "DELETE FROM Course WHERE course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id.toString());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting course failed, no rows affected.");
            }
        }
    }

    @Override
    public Course findById(Integer id) throws Exception {
        String sql = "SELECT * FROM Course WHERE course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id.toString());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Course(
                        id,
                        rs.getString("course_id"),
                        rs.getString("name"),
                        rs.getInt("credits")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<Course> findAll() throws Exception {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM Course";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String courseId = rs.getString("course_id");
                courses.add(new Course(
                    0, // Using 0 as a placeholder since we don't need the numeric ID
                    courseId,
                    rs.getString("name"),
                    rs.getInt("credits")
                ));
            }
        }
        return courses;
    }
} 
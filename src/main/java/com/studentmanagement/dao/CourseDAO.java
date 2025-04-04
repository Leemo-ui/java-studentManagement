package com.studentmanagement.dao;

import com.studentmanagement.model.Course;
import com.studentmanagement.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public interface CourseDAO extends FileBasedDAO<Course, Integer> {
    public static int addCourse(String code, String name, int credits) throws SQLException {
        String sql = "INSERT INTO courses (code, name, credits) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, code);
            stmt.setString(2, name);
            stmt.setInt(3, credits);
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("Failed to get generated ID");
            }
        }
    }

    public static void updateCourse(Course course) throws SQLException {
        String sql = "UPDATE courses SET code = ?, name = ?, credits = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, course.getCode());
            stmt.setString(2, course.getName());
            stmt.setInt(3, course.getCredits());
            stmt.setInt(4, course.getId());
            stmt.executeUpdate();
        }
    }

    public static void deleteCourse(int id) throws SQLException {
        String sql = "DELETE FROM courses WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public static Course getCourse(int id) throws SQLException {
        String sql = "SELECT * FROM courses WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Course(
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getInt("credits")
                    );
                }
                return null;
            }
        }
    }

    public static List<Course> getAllCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                courses.add(new Course(
                    rs.getInt("id"),
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getInt("credits")
                ));
            }
        }
        return courses;
    }

    public static void assignLecturerToCourse(String lecturerId, String courseId) throws SQLException {
        String sql = "INSERT INTO Course_Lecturer (lecturer_id, course_id) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, lecturerId);
            pstmt.setString(2, courseId);
            
            pstmt.executeUpdate();
            System.out.println("Lecturer assigned to course successfully");
        }
    }

    public static void removeLecturerFromCourse(String lecturerId, String courseId) throws SQLException {
        String sql = "DELETE FROM Course_Lecturer WHERE lecturer_id = ? AND course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, lecturerId);
            pstmt.setString(2, courseId);
            
            pstmt.executeUpdate();
            System.out.println("Lecturer removed from course successfully");
        }
    }
} 
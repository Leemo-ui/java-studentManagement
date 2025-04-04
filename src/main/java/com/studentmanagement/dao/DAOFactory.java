package com.studentmanagement.dao;

public class DAOFactory {
    private static StorageType currentStorageType = StorageType.DATABASE;

    public enum StorageType {
        DATABASE,
        FILE
    }

    public static void setStorageType(StorageType type) {
        currentStorageType = type;
    }

    public static StorageType getStorageType() {
        return currentStorageType;
    }

    public static StudentDAO getStudentDAO() {
        return currentStorageType == StorageType.DATABASE ? 
            new StudentDAOImpl() : new FileBasedStudentDAO();
    }

    public static ProgrammeDAO getProgrammeDAO() {
        return currentStorageType == StorageType.DATABASE ? 
            new ProgrammeDAOImpl() : new FileBasedProgrammeDAO();
    }

    public static CourseDAO getCourseDAO() {
        return currentStorageType == StorageType.DATABASE ? 
            new CourseDAOImpl() : new FileBasedCourseDAO();
    }
} 
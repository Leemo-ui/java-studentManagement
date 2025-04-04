package com.studentmanagement.model;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;

public class Programme {
    private final IntegerProperty id;
    private final StringProperty code;
    private final StringProperty name;
    private final StringProperty department;
    private List<String> courseCodes;

    public Programme(int id, String code, String name, String department) {
        this.id = new SimpleIntegerProperty(id);
        this.code = new SimpleStringProperty(code);
        this.name = new SimpleStringProperty(name);
        this.department = new SimpleStringProperty(department);
        this.courseCodes = new ArrayList<>();
    }

    public Programme(String code, String name, String department) {
        this.id = new SimpleIntegerProperty(0);
        this.code = new SimpleStringProperty(code);
        this.name = new SimpleStringProperty(name);
        this.department = new SimpleStringProperty(department);
    }

    // Getters
    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getCode() {
        return code.get();
    }

    public StringProperty codeProperty() {
        return code;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getDepartment() {
        return department.get();
    }

    public StringProperty departmentProperty() {
        return department;
    }

    public List<String> getCourseCodes() {
        return courseCodes;
    }

    // Setters
    public void setId(int id) {
        this.id.set(id);
    }

    public void setCode(String code) {
        this.code.set(code);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setDepartment(String department) {
        this.department.set(department);
    }

    public void setCourseCodes(List<String> courseCodes) {
        this.courseCodes = courseCodes;
    }

    // Helper methods
    public void addCourse(String courseCode) {
        if (!courseCodes.contains(courseCode)) {
            courseCodes.add(courseCode);
        }
    }

    public void removeCourse(String courseCode) {
        courseCodes.remove(courseCode);
    }

    @Override
    public String toString() {
        return getCode() + " - " + getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Programme programme = (Programme) o;
        return code.get().equals(programme.code.get());
    }

    @Override
    public int hashCode() {
        return code.get().hashCode();
    }
} 
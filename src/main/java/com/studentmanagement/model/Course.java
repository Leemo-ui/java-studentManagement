package com.studentmanagement.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;

public class Course {
    private final IntegerProperty id;
    private final StringProperty code;
    private final StringProperty name;
    private final IntegerProperty credits;

    public Course(int id, String code, String name, int credits) {
        this.id = new SimpleIntegerProperty(id);
        this.code = new SimpleStringProperty(code);
        this.name = new SimpleStringProperty(name);
        this.credits = new SimpleIntegerProperty(credits);
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

    public int getCredits() {
        return credits.get();
    }

    public IntegerProperty creditsProperty() {
        return credits;
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

    public void setCredits(int credits) {
        this.credits.set(credits);
    }

    @Override
    public String toString() {
        return name.get() + " (" + code.get() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Course course = (Course) obj;
        return id.get() == course.id.get();
    }

    @Override
    public int hashCode() {
        return id.get();
    }
} 
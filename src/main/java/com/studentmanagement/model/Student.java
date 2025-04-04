package com.studentmanagement.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Student {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty email;
    private final StringProperty programmeCode;
    private final StringProperty programmeName;

    public Student(int id, String name, String email, String programmeCode, String programmeName) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.email = new SimpleStringProperty(email);
        this.programmeCode = new SimpleStringProperty(programmeCode);
        this.programmeName = new SimpleStringProperty(programmeName);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getProgrammeCode() {
        return programmeCode.get();
    }

    public StringProperty programmeCodeProperty() {
        return programmeCode;
    }

    public void setProgrammeCode(String programmeCode) {
        this.programmeCode.set(programmeCode);
    }

    public String getProgrammeName() {
        return programmeName.get();
    }

    public StringProperty programmeNameProperty() {
        return programmeName;
    }

    public void setProgrammeName(String programmeName) {
        this.programmeName.set(programmeName);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id.get() == student.id.get();
    }

    @Override
    public int hashCode() {
        return id.get();
    }
} 
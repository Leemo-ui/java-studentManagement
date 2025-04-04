package com.studentmanagement.dao;

import com.studentmanagement.model.Course;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileBasedCourseDAO implements CourseDAO {
    private static final String FILE_PATH = "data/courses.json";
    private final Gson gson;
    private List<Course> courses;
    private int nextId;

    public FileBasedCourseDAO() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.courses = new ArrayList<>();
        this.nextId = 1;
        loadCourses();
    }

    private void loadCourses() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                TypeToken<List<Course>> token = new TypeToken<List<Course>>() {};
                courses = gson.fromJson(reader, token.getType());
                if (courses != null && !courses.isEmpty()) {
                    nextId = courses.stream()
                            .mapToInt(Course::getId)
                            .max()
                            .getAsInt() + 1;
                }
            } catch (IOException e) {
                e.printStackTrace();
                courses = new ArrayList<>();
            }
        }
    }

    private void saveCourses() {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs(); // Create directories if they don't exist
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(courses, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Course save(Course course) throws Exception {
        if (courses.stream().anyMatch(c -> c.getCode().equals(course.getCode()))) {
            throw new Exception("Course with code " + course.getCode() + " already exists");
        }
        course.setId(nextId++);
        courses.add(course);
        saveCourses();
        return course;
    }

    @Override
    public Course update(Course course) throws Exception {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getId() == course.getId()) {
                courses.set(i, course);
                saveCourses();
                return course;
            }
        }
        throw new Exception("Course not found");
    }

    @Override
    public void delete(Integer id) throws Exception {
        courses = courses.stream()
                .filter(course -> course.getId() != id)
                .collect(Collectors.toList());
        saveCourses();
    }

    @Override
    public Course findById(Integer id) throws Exception {
        return courses.stream()
                .filter(course -> course.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Course> findAll() throws Exception {
        return new ArrayList<>(courses);
    }
} 
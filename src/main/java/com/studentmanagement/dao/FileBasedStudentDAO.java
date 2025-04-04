package com.studentmanagement.dao;

import com.studentmanagement.model.Student;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileBasedStudentDAO implements StudentDAO {
    private static final String FILE_PATH = "data/students.json";
    private final Gson gson;
    private List<Student> students;
    private int nextId;

    public FileBasedStudentDAO() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.students = new ArrayList<>();
        this.nextId = 1;
        loadStudents();
    }

    private void loadStudents() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (Reader reader = new FileReader(file)) {
                TypeToken<List<Student>> token = new TypeToken<List<Student>>() {};
                students = gson.fromJson(reader, token.getType());
                if (students != null && !students.isEmpty()) {
                    nextId = students.stream()
                            .mapToInt(Student::getId)
                            .max()
                            .getAsInt() + 1;
                }
            } catch (IOException e) {
                e.printStackTrace();
                students = new ArrayList<>();
            }
        }
    }

    private void saveStudents() {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs(); // Create directories if they don't exist
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(students, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Student save(Student student) throws Exception {
        student.setId(nextId++);
        students.add(student);
        saveStudents();
        return student;
    }

    @Override
    public Student update(Student student) throws Exception {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId() == student.getId()) {
                students.set(i, student);
                saveStudents();
                return student;
            }
        }
        throw new Exception("Student not found");
    }

    @Override
    public void delete(Integer id) throws Exception {
        students = students.stream()
                .filter(student -> student.getId() != id)
                .collect(Collectors.toList());
        saveStudents();
    }

    @Override
    public Student findById(Integer id) throws Exception {
        return students.stream()
                .filter(student -> student.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Student> findAll() throws Exception {
        return new ArrayList<>(students);
    }
} 
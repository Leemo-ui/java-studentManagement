package com.studentmanagement.controller;

import com.studentmanagement.dao.LecturerDAO;
import com.studentmanagement.model.Lecturer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class LecturersViewController {
    @FXML private TableView<Lecturer> lecturerTable;
    @FXML private TableColumn<Lecturer, Integer> idColumn;
    @FXML private TableColumn<Lecturer, String> nameColumn;
    @FXML private TableColumn<Lecturer, String> emailColumn;
    
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private ListView<String> coursesListView;
    @FXML private TextField searchField;

    private ObservableList<Lecturer> lecturers;
    private ObservableList<String> availableCourses;
    private FilteredList<Lecturer> filteredLecturers;

    @FXML
    private void initialize() {
        // Initialize the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        // Initialize the courses list view
        coursesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Initialize search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredLecturers.setPredicate(lecturer -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return lecturer.getName().toLowerCase().contains(lowerCaseFilter) ||
                       lecturer.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                       availableCourses.stream()
                           .anyMatch(course -> course.toLowerCase().contains(lowerCaseFilter));
            });
        });
        
        // Load lecturers and courses
        loadLecturers();
        loadAvailableCourses();
        
        // Add listener for selection changes
        lecturerTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> showLecturerDetails(newValue));
    }

    private void loadLecturers() {
        try {
            lecturers = FXCollections.observableArrayList(LecturerDAO.getAllLecturers());
            filteredLecturers = new FilteredList<>(lecturers, p -> true);
            lecturerTable.setItems(filteredLecturers);
        } catch (Exception e) {
            showError("Error loading lecturers", e.getMessage());
        }
    }

    private void loadAvailableCourses() {
        try {
            // For now, just add some sample courses
            availableCourses = FXCollections.observableArrayList(
                "Introduction to Programming",
                "Data Structures",
                "Database Systems",
                "Engineering Mathematics",
                "Mechanics",
                "Business Management",
                "Financial Accounting"
            );
            coursesListView.setItems(availableCourses);
        } catch (Exception e) {
            showError("Error loading courses", e.getMessage());
        }
    }
    
    private void showLecturerDetails(Lecturer lecturer) {
        if (lecturer != null) {
            nameField.setText(lecturer.getName());
            emailField.setText(lecturer.getEmail());
            
            // Clear previous selections
            coursesListView.getSelectionModel().clearSelection();
            
            // Get the lecturer's courses and select them in the list
            try {
                List<Integer> courseIds = new ArrayList<>(); // Get this from the database
                for (Integer courseId : courseIds) {
                    // Find the course name and select it
                    int courseIndex = courseId - 1; // Assuming IDs start from 1
                    if (courseIndex >= 0 && courseIndex < availableCourses.size()) {
                        coursesListView.getSelectionModel().select(courseIndex);
                    }
                }
            } catch (Exception e) {
                showError("Error loading lecturer's courses", e.getMessage());
            }
        } else {
            nameField.setText("");
            emailField.setText("");
            coursesListView.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void handleNewLecturer() {
        nameField.setText("");
        emailField.setText("");
        coursesListView.getSelectionModel().clearSelection();
        lecturerTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleEditLecturer() {
        Lecturer selectedLecturer = lecturerTable.getSelectionModel().getSelectedItem();
        if (selectedLecturer == null) {
            showError("No Selection", "Please select a lecturer to edit.");
            return;
        }
        
        if (!validateInput()) {
            return;
        }

        try {
            selectedLecturer.setName(nameField.getText());
            selectedLecturer.setEmail(emailField.getText());
            
            LecturerDAO.updateLecturer(selectedLecturer);
            
            // Update the lecturer's courses
            List<Integer> selectedCourseIds = new ArrayList<>();
            for (String courseName : coursesListView.getSelectionModel().getSelectedItems()) {
                selectedCourseIds.add(LecturerDAO.getCourseIdByName(courseName));
            }
            LecturerDAO.updateLecturerCourses(selectedLecturer.getId(), selectedCourseIds);
            
            loadLecturers();
            showLecturerDetails(selectedLecturer);
        } catch (Exception e) {
            showError("Error updating lecturer", e.getMessage());
        }
    }

    @FXML
    private void handleDeleteLecturer() {
        Lecturer selectedLecturer = lecturerTable.getSelectionModel().getSelectedItem();
        if (selectedLecturer == null) {
            showError("No Selection", "Please select a lecturer to delete.");
            return;
        }

            try {
                LecturerDAO.deleteLecturer(selectedLecturer.getId());
                lecturers.remove(selectedLecturer);
            handleNewLecturer();
        } catch (Exception e) {
                showError("Error deleting lecturer", e.getMessage());
        }
    }

    @FXML
    private void handleSaveLecturer() {
        if (!validateInput()) {
            return;
        }

        try {
            int id = LecturerDAO.addLecturer(
                nameField.getText(),
                emailField.getText()
            );
            
            Lecturer newLecturer = new Lecturer(
                id,
                nameField.getText(),
                emailField.getText()
            );

            // Save the lecturer's courses
            List<Integer> selectedCourseIds = new ArrayList<>();
            for (String courseName : coursesListView.getSelectionModel().getSelectedItems()) {
                selectedCourseIds.add(LecturerDAO.getCourseIdByName(courseName));
            }
            LecturerDAO.updateLecturerCourses(id, selectedCourseIds);
            
            lecturers.add(newLecturer);
            lecturerTable.getSelectionModel().select(newLecturer);
        } catch (Exception e) {
            showError("Error saving lecturer", e.getMessage());
        }
    }

    private boolean validateInput() {
        String errorMessage = "";
        
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errorMessage += "Name is required.\n";
        }
        if (emailField.getText() == null || emailField.getText().trim().isEmpty()) {
            errorMessage += "Email is required.\n";
        }
        
        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showError("Invalid Input", errorMessage);
            return false;
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 
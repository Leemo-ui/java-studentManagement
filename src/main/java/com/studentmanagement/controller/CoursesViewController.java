package com.studentmanagement.controller;

import com.studentmanagement.dao.DAOFactory;
import com.studentmanagement.dao.CourseDAO;
import com.studentmanagement.model.Course;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

import java.util.List;

public class CoursesViewController {
    @FXML private TableView<Course> coursesTable;
    @FXML private TableColumn<Course, Integer> idColumn;
    @FXML private TableColumn<Course, String> codeColumn;
    @FXML private TableColumn<Course, String> nameColumn;
    @FXML private TableColumn<Course, Integer> creditsColumn;
    @FXML private TextField searchField;

    private ObservableList<Course> courses;
    private FilteredList<Course> filteredCourses;
    private final CourseDAO courseDAO;

    public CoursesViewController() {
        this.courseDAO = DAOFactory.getCourseDAO();
    }

    @FXML
    private void initialize() {
        // Initialize the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));

        // Initialize search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredCourses.setPredicate(course -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return course.getCode().toLowerCase().contains(lowerCaseFilter) ||
                       course.getName().toLowerCase().contains(lowerCaseFilter) ||
                       String.valueOf(course.getCredits()).contains(lowerCaseFilter);
            });
        });

        // Load courses
        loadCourses();
    }

    private void loadCourses() {
        try {
            courses = FXCollections.observableArrayList(courseDAO.findAll());
            filteredCourses = new FilteredList<>(courses, p -> true);
            coursesTable.setItems(filteredCourses);
        } catch (Exception e) {
            showError("Error loading courses", e.getMessage());
        }
    }

    @FXML
    private void handleNewCourse() {
        showCourseDialog(null);
    }

    @FXML
    private void handleEditCourse() {
        Course selectedCourse = coursesTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showError("No Selection", "Please select a course to edit.");
            return;
        }
        showCourseDialog(selectedCourse);
    }

    private void showCourseDialog(Course course) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(course == null ? "New Course" : "Edit Course");
        dialog.setHeaderText(course == null ? "Create New Course" : "Edit Course Information");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField codeField = new TextField(course != null ? course.getCode() : "");
        TextField nameField = new TextField(course != null ? course.getName() : "");
        TextField creditsField = new TextField(course != null ? String.valueOf(course.getCredits()) : "");

        if (course != null) {
            codeField.setEditable(false);
        }

        grid.add(new Label("Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Credits:"), 0, 2);
        grid.add(creditsField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String code = codeField.getText().trim();
                String name = nameField.getText().trim();
                String creditsStr = creditsField.getText().trim();

                if (code.isEmpty() || name.isEmpty() || creditsStr.isEmpty()) {
                    showError("Validation Error", "All fields are required.");
                    return null;
                }

                try {
                    int credits = Integer.parseInt(creditsStr);
                    if (credits <= 0) {
                        showError("Validation Error", "Credits must be a positive number.");
                        return null;
                    }

                    if (course == null) {
                        Course newCourse = new Course(0, code, name, credits);
                        courseDAO.save(newCourse);
                    } else {
                        course.setName(name);
                        course.setCredits(credits);
                        courseDAO.update(course);
                    }
                    loadCourses();
                } catch (NumberFormatException e) {
                    showError("Validation Error", "Credits must be a number.");
                    return null;
                } catch (Exception e) {
                    showError("Error saving course", e.getMessage());
                    return null;
                }
            }
            return dialogButton;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleDeleteCourse() {
        Course selectedCourse = coursesTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showError("No Selection", "Please select a course to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Course");
        alert.setContentText("Are you sure you want to delete this course?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                courseDAO.delete(selectedCourse.getId());
                courses.remove(selectedCourse);
            } catch (Exception e) {
                showError("Error deleting course", e.getMessage());
            }
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
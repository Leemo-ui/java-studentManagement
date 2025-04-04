package com.studentmanagement.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainViewController {

    @FXML
    private StackPane mainContent;

    @FXML
    private VBox welcomeView;

    @FXML
    private void handleStudentsView() {
        loadView("/fxml/StudentsView.fxml");
    }

    @FXML
    private void handleLecturersView() {
        loadView("/fxml/LecturersView.fxml");
    }

    @FXML
    private void handleCoursesView() {
        loadView("/fxml/CoursesView.fxml");
    }

    @FXML
    private void handleProgrammesView() {
        loadView("/fxml/ProgrammesView.fxml");
    }

    @FXML
    private void handleExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Any unsaved changes will be lost.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            Stage stage = (Stage) mainContent.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Student Management System");
        alert.setContentText("Version 1.0\n\nA comprehensive system for managing students, lecturers, courses, and programmes.");
        alert.showAndWait();
    }

    private void loadView(String fxmlPath) {
        try {
            System.out.println("Loading view: " + fxmlPath);
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            System.out.println("View loaded successfully");
            mainContent.getChildren().clear();
            mainContent.getChildren().add(view);
            System.out.println("View added to main content");
        } catch (Exception e) {
            System.err.println("Error loading view: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load view");
            alert.setContentText("An error occurred while loading the requested view: " + e.getMessage());
            alert.showAndWait();
        }
    }
} 
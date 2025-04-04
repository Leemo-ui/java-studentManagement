package com.studentmanagement.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainMenuController {
    @FXML
    private void handleStudents() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/StudentsView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Students");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProgrammes() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/ProgrammesView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Programmes");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCourses() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/CoursesView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Courses");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSettings() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/SettingsView.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Settings");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 
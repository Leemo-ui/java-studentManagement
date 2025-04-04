package com.studentmanagement.controller;

import com.studentmanagement.dao.DAOFactory;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class SettingsViewController {
    @FXML private RadioButton databaseRadio;
    @FXML private RadioButton fileRadio;
    @FXML private ToggleGroup storageGroup;

    @FXML
    private void initialize() {
        // Set up the toggle group
        storageGroup = new ToggleGroup();
        databaseRadio.setToggleGroup(storageGroup);
        fileRadio.setToggleGroup(storageGroup);

        // Set the current storage type
        if (DAOFactory.getStorageType() == DAOFactory.StorageType.DATABASE) {
            databaseRadio.setSelected(true);
        } else {
            fileRadio.setSelected(true);
        }
    }

    @FXML
    private void handleSave() {
        // Update the storage type
        if (databaseRadio.isSelected()) {
            DAOFactory.setStorageType(DAOFactory.StorageType.DATABASE);
        } else {
            DAOFactory.setStorageType(DAOFactory.StorageType.FILE);
        }

        // Close the settings window
        Stage stage = (Stage) databaseRadio.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel() {
        // Close the settings window without saving
        Stage stage = (Stage) databaseRadio.getScene().getWindow();
        stage.close();
    }
} 
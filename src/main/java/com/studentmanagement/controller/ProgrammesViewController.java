package com.studentmanagement.controller;

import com.studentmanagement.dao.DAOFactory;
import com.studentmanagement.dao.ProgrammeDAO;
import com.studentmanagement.model.Programme;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.sql.SQLException;
import java.util.Optional;

public class ProgrammesViewController {
    @FXML private TableView<Programme> programmesTable;
    @FXML private TableColumn<Programme, String> codeColumn;
    @FXML private TableColumn<Programme, String> nameColumn;
    @FXML private TableColumn<Programme, String> departmentColumn;
    @FXML private TextField searchField;

    private ObservableList<Programme> programmes;
    private FilteredList<Programme> filteredProgrammes;
    private final ProgrammeDAO programmeDAO;

    public ProgrammesViewController() {
        this.programmeDAO = DAOFactory.getProgrammeDAO();
    }

    @FXML
    private void initialize() {
        // Initialize the table columns
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("department"));

        // Initialize search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredProgrammes.setPredicate(programme -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return programme.getCode().toLowerCase().contains(lowerCaseFilter) ||
                       programme.getName().toLowerCase().contains(lowerCaseFilter) ||
                       programme.getDepartment().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Load programmes
        loadProgrammes();
    }

    private void loadProgrammes() {
        try {
            programmes = FXCollections.observableArrayList(programmeDAO.findAll());
            filteredProgrammes = new FilteredList<>(programmes, p -> true);
            programmesTable.setItems(filteredProgrammes);
        } catch (Exception e) {
            showError("Error loading programmes", e.getMessage());
        }
    }

    @FXML
    private void handleNewProgramme() {
        showProgrammeDialog(null);
    }

    @FXML
    private void handleEditProgramme() {
        Programme selectedProgramme = programmesTable.getSelectionModel().getSelectedItem();
        if (selectedProgramme == null) {
            showError("No Selection", "Please select a programme to edit.");
            return;
        }
        showProgrammeDialog(selectedProgramme);
    }

    private void showProgrammeDialog(Programme programme) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(programme == null ? "New Programme" : "Edit Programme");
        dialog.setHeaderText(programme == null ? "Create New Programme" : "Edit Programme Information");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField codeField = new TextField(programme != null ? programme.getCode() : "");
        TextField nameField = new TextField(programme != null ? programme.getName() : "");
        TextField departmentField = new TextField(programme != null ? programme.getDepartment() : "");

        if (programme != null) {
            codeField.setEditable(false);
        }

        grid.add(new Label("Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Department:"), 0, 2);
        grid.add(departmentField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String code = codeField.getText().trim();
                String name = nameField.getText().trim();
                String department = departmentField.getText().trim();

                if (code.isEmpty() || name.isEmpty() || department.isEmpty()) {
                    showError("Validation Error", "All fields are required.");
                    return null;
                }

                try {
                    if (programme == null) {
                        Programme newProgramme = new Programme(code, name, department);
                        programmeDAO.save(newProgramme);
                    } else {
                        programme.setName(name);
                        programme.setDepartment(department);
                        programmeDAO.update(programme);
                    }
                    loadProgrammes();
                } catch (Exception e) {
                    showError("Error saving programme", e.getMessage());
                    return null;
                }
            }
            return dialogButton;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleDeleteProgramme() {
        Programme selectedProgramme = programmesTable.getSelectionModel().getSelectedItem();
        if (selectedProgramme == null) {
            showError("No Selection", "Please select a programme to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Programme");
        alert.setContentText("Are you sure you want to delete this programme?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                programmeDAO.delete(selectedProgramme.getCode());
                programmes.remove(selectedProgramme);
            } catch (Exception e) {
                showError("Error deleting programme", e.getMessage());
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
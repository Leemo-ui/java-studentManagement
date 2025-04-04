package com.studentmanagement.controller;

import com.studentmanagement.dao.DAOFactory;
import com.studentmanagement.dao.StudentDAO;
import com.studentmanagement.dao.ProgrammeDAO;
import com.studentmanagement.model.Student;
import com.studentmanagement.model.Programme;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.util.StringConverter;

import java.util.List;

public class StudentsViewController {
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, Integer> idColumn;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> emailColumn;
    @FXML private TableColumn<Student, String> programmeCodeColumn;
    @FXML private TableColumn<Student, String> programmeNameColumn;
    
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private ComboBox<Programme> programmeComboBox;
    @FXML private TextField searchField;

    private ObservableList<Student> students;
    private FilteredList<Student> filteredStudents;
    private ObservableList<Programme> programmes;
    private final StudentDAO studentDAO;
    private final ProgrammeDAO programmeDAO;

    public StudentsViewController() {
        this.studentDAO = DAOFactory.getStudentDAO();
        this.programmeDAO = DAOFactory.getProgrammeDAO();
    }

    @FXML
    private void initialize() {
        // Initialize the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        programmeCodeColumn.setCellValueFactory(new PropertyValueFactory<>("programmeCode"));
        programmeNameColumn.setCellValueFactory(new PropertyValueFactory<>("programmeName"));
        
        // Initialize programme ComboBox
        setupProgrammeComboBox();
        
        // Initialize search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredStudents.setPredicate(student -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return student.getName().toLowerCase().contains(lowerCaseFilter) ||
                       student.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                       student.getProgrammeCode().toLowerCase().contains(lowerCaseFilter) ||
                       student.getProgrammeName().toLowerCase().contains(lowerCaseFilter);
            });
        });
        
        // Load students
        loadStudents();
        
        // Add listener for selection changes
        studentTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> showStudentDetails(newValue));
    }

    private void setupProgrammeComboBox() {
        try {
            programmes = FXCollections.observableArrayList(programmeDAO.findAll());
            programmeComboBox.setItems(programmes);
            
            // Set up the converter to display programme name and code
            programmeComboBox.setConverter(new StringConverter<Programme>() {
                @Override
                public String toString(Programme programme) {
                    if (programme == null) {
                        return "";
                    }
                    return programme.getCode() + " - " + programme.getName();
                }
                
                @Override
                public Programme fromString(String string) {
                    if (string == null || string.isEmpty()) {
                        return null;
                    }
                    // Find the programme by code (assuming format is "code - name")
                    String code = string.split(" - ")[0];
                    return programmes.stream()
                            .filter(p -> p.getCode().equals(code))
                            .findFirst()
                            .orElse(null);
                }
            });
        } catch (Exception e) {
            showError("Error loading programmes", e.getMessage());
        }
    }

    private void loadStudents() {
        try {
            students = FXCollections.observableArrayList(studentDAO.findAll());
            filteredStudents = new FilteredList<>(students, p -> true);
            studentTable.setItems(filteredStudents);
        } catch (Exception e) {
            showError("Error loading students", e.getMessage());
        }
    }

    private void showStudentDetails(Student student) {
        if (student != null) {
            nameField.setText(student.getName());
            emailField.setText(student.getEmail());
            
            // Find and select the programme in the ComboBox
            Programme programme = programmes.stream()
                    .filter(p -> p.getCode().equals(student.getProgrammeCode()))
                    .findFirst()
                    .orElse(null);
            programmeComboBox.setValue(programme);
        } else {
            nameField.setText("");
            emailField.setText("");
            programmeComboBox.setValue(null);
        }
    }

    @FXML
    private void handleNewStudent() {
        nameField.setText("");
        emailField.setText("");
        programmeComboBox.setValue(null);
        studentTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleEditStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showError("No Selection", "Please select a student to edit.");
            return;
        }
        
        showStudentDialog(selectedStudent);
    }
    
    private void showStudentDialog(Student student) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Student");
        dialog.setHeaderText("Edit Student Information");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(student.getName());
        TextField emailField = new TextField(student.getEmail());
        
        // Create a ComboBox for programme selection
        ComboBox<Programme> programmeComboBox = new ComboBox<>(programmes);
        programmeComboBox.setConverter(new StringConverter<Programme>() {
            @Override
            public String toString(Programme programme) {
                if (programme == null) {
                    return "";
                }
                return programme.getCode() + " - " + programme.getName();
            }
            
            @Override
            public Programme fromString(String string) {
                if (string == null || string.isEmpty()) {
                    return null;
                }
                String code = string.split(" - ")[0];
                return programmes.stream()
                        .filter(p -> p.getCode().equals(code))
                        .findFirst()
                        .orElse(null);
            }
        });
        
        // Set the current programme
        Programme currentProgramme = programmes.stream()
                .filter(p -> p.getCode().equals(student.getProgrammeCode()))
                .findFirst()
                .orElse(null);
        programmeComboBox.setValue(currentProgramme);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Programme:"), 0, 2);
        grid.add(programmeComboBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                Programme selectedProgramme = programmeComboBox.getValue();

                if (name.isEmpty() || email.isEmpty() || selectedProgramme == null) {
                    showError("Validation Error", "All fields are required.");
                    return null;
                }

                try {
                    student.setName(name);
                    student.setEmail(email);
                    student.setProgrammeCode(selectedProgramme.getCode());
                    student.setProgrammeName(selectedProgramme.getName());
                    
                    studentDAO.update(student);
                    loadStudents();
                    
                    // Select the updated student in the table
                    studentTable.getSelectionModel().select(student);
                } catch (Exception e) {
                    showError("Error updating student", e.getMessage());
                    return null;
                }
            }
            return dialogButton;
        });

        dialog.showAndWait();
    }

    @FXML
    private void handleDeleteStudent() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showError("No Selection", "Please select a student to delete.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Student");
        alert.setContentText("Are you sure you want to delete " + selectedStudent.getName() + "?");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                studentDAO.delete(selectedStudent.getId());
                loadStudents();
            } catch (Exception e) {
                showError("Error deleting student", e.getMessage());
            }
        }
    }

    @FXML
    private void handleSaveStudent() {
        if (!validateInput()) {
            return;
        }
        
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            Programme selectedProgramme = programmeComboBox.getValue();
            
            Student student = new Student(0, name, email, selectedProgramme.getCode(), selectedProgramme.getName());
            studentDAO.save(student);
            
            loadStudents();
            
            // Clear the form
            handleNewStudent();
        } catch (Exception e) {
            showError("Error saving student", e.getMessage());
        }
    }

    private boolean validateInput() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        Programme selectedProgramme = programmeComboBox.getValue();
        
        if (name.isEmpty()) {
            showError("Validation Error", "Name is required.");
            return false;
        }
        
        if (email.isEmpty()) {
            showError("Validation Error", "Email is required.");
            return false;
        }
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Validation Error", "Please enter a valid email address.");
            return false;
        }
        
        if (selectedProgramme == null) {
            showError("Validation Error", "Please select a programme.");
            return false;
        }
        
        return true;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 

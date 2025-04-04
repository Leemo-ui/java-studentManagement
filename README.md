# Student Management System

A JavaFX-based student management system with MySQL database backend.

## Prerequisites

1. Java JDK 17 or later
2. MySQL Server
3. Maven

## Setup Instructions

1. **Set up the Database**
   ```bash
   # Login to MySQL
   mysql -u root -p
   
   # Enter your password when prompted
   
   # Copy and paste the contents of database_setup.sql
   ```

2. **Build the Application**
   ```bash
   # Navigate to the project directory
   cd studentmanagement_java
   
   # Clean and build the project
   mvn clean install
   ```

3. **Run the Application**
   ```bash
   # Run using Maven
   mvn javafx:run
   ```

## Troubleshooting

If you encounter any issues:

1. **Database Connection Issues**
   - Make sure MySQL server is running
   - Verify the database credentials in `DatabaseConnection.java`
   - Check if the database and tables are created properly

2. **Build Issues**
   - Make sure you have Java 17 or later installed
   - Verify Maven is properly installed
   - Check if all dependencies are downloaded

3. **Runtime Issues**
   - Check the console output for error messages
   - Verify all required files are in the correct locations

## Project Structure

```
studentmanagement_java/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── studentmanagement/
│       │           ├── controller/
│       │           ├── dao/
│       │           ├── model/
│       │           └── util/
│       └── resources/
│           └── fxml/
├── pom.xml
└── database_setup.sql
```

#!/bin/bash

echo "Setting up Student Management System..."

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Java is not installed. Installing Java 17..."
    sudo apt update
    sudo apt install -y openjdk-17-jdk
fi

# Check Java version
java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$java_version" -lt 17 ]; then
    echo "Java version must be 17 or later. Please install Java 17."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed. Installing Maven..."
    sudo apt update
    sudo apt install -y maven
fi

# Check if MySQL is installed
if ! command -v mysql &> /dev/null; then
    echo "MySQL is not installed. Installing MySQL..."
    sudo apt update
    sudo apt install -y mysql-server
fi

# Check if MySQL service is running
if ! systemctl is-active --quiet mysql; then
    echo "Starting MySQL service..."
    sudo systemctl start mysql
fi

# Reset MySQL root password
echo "Setting up MySQL root password..."
sudo mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'Arnold#254';"
sudo mysql -e "FLUSH PRIVILEGES;"

# Create MySQL user and database
echo "Creating MySQL user and database..."
mysql -u root -p"Arnold#254" -e "CREATE DATABASE IF NOT EXISTS student_management;"
mysql -u root -p"Arnold#254" -e "CREATE USER IF NOT EXISTS 'student_management'@'localhost' IDENTIFIED BY 'Arnold#254';"
mysql -u root -p"Arnold#254" -e "GRANT ALL PRIVILEGES ON student_management.* TO 'student_management'@'localhost';"
# Grant SUPER privilege to allow trigger creation with binary logging enabled
mysql -u root -p"Arnold#254" -e "GRANT SUPER ON *.* TO 'student_management'@'localhost';"
mysql -u root -p"Arnold#254" -e "FLUSH PRIVILEGES;"

# Update database connection settings
echo "Updating database connection settings..."
sed -i 's/private static final String USER = "student_management";/private static final String USER = "student_management";/' src/main/java/com/studentmanagement/util/DatabaseConnection.java
sed -i 's/private static final String PASSWORD = "Arnold#254";/private static final String PASSWORD = "Arnold#254";/' src/main/java/com/studentmanagement/util/DatabaseConnection.java

# Set up the database
echo "Setting up the database..."
mysql -u student_management -p"Arnold#254" student_management < database_setup.sql

# Clean and build the project
echo "Building the project..."
mvn clean install

# Run the application
echo "Starting the application..."
mvn javafx:run 
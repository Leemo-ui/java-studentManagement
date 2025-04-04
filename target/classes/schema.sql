-- Drop existing tables if they exist (in correct order due to foreign key constraints)
DROP TABLE IF EXISTS lecturer_course;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS lecturers;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS programmes;
DROP TABLE IF EXISTS persons;

-- Create tables
CREATE TABLE persons (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    UNIQUE KEY unique_email (email)
);

CREATE TABLE programmes (
    code VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL
);

CREATE TABLE students (
    person_id INT PRIMARY KEY,
    programme_code VARCHAR(20) NOT NULL,
    FOREIGN KEY (person_id) REFERENCES persons(id) ON DELETE CASCADE,
    FOREIGN KEY (programme_code) REFERENCES programmes(code)
);

CREATE TABLE lecturers (
    person_id INT PRIMARY KEY,
    FOREIGN KEY (person_id) REFERENCES persons(id) ON DELETE CASCADE
);

CREATE TABLE courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    credits INT NOT NULL,
    UNIQUE KEY unique_code (code)
);

CREATE TABLE lecturer_course (
    lecturer_id INT NOT NULL,
    course_id INT NOT NULL,
    PRIMARY KEY (lecturer_id, course_id),
    FOREIGN KEY (lecturer_id) REFERENCES lecturers(person_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
); 
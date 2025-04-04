-- Create the database
DROP DATABASE IF EXISTS student_management;
CREATE DATABASE student_management;
USE student_management;

-- Drop existing tables
DROP TABLE IF EXISTS Course_Student;
DROP TABLE IF EXISTS Course_Lecturer;
DROP TABLE IF EXISTS Course;
DROP TABLE IF EXISTS Programme;
DROP TABLE IF EXISTS Lecturer;
DROP TABLE IF EXISTS Student;
DROP TABLE IF EXISTS Person;

-- Create Person table
CREATE TABLE Person (
    person_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

-- Create Student table
CREATE TABLE Student (
    student_id VARCHAR(20) PRIMARY KEY,
    programme_id VARCHAR(20),
    FOREIGN KEY (student_id) REFERENCES Person(person_id)
);

-- Create Lecturer table
CREATE TABLE Lecturer (
    lecturer_id VARCHAR(20) PRIMARY KEY,
    FOREIGN KEY (lecturer_id) REFERENCES Person(person_id)
);

-- Create Programme table
CREATE TABLE Programme (
    programme_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    duration INT NOT NULL
);

-- Create Course table
CREATE TABLE Course (
    course_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    credits INT NOT NULL,
    max_students INT NOT NULL
);

-- Create Course_Lecturer junction table
CREATE TABLE Course_Lecturer (
    course_id VARCHAR(20),
    lecturer_id VARCHAR(20),
    PRIMARY KEY (course_id, lecturer_id),
    FOREIGN KEY (course_id) REFERENCES Course(course_id),
    FOREIGN KEY (lecturer_id) REFERENCES Lecturer(lecturer_id)
);

-- Create Course_Student junction table
CREATE TABLE Course_Student (
    course_id VARCHAR(20),
    student_id VARCHAR(20),
    enrollment_date DATE NOT NULL,
    PRIMARY KEY (course_id, student_id),
    FOREIGN KEY (course_id) REFERENCES Course(course_id),
    FOREIGN KEY (student_id) REFERENCES Student(student_id)
);

-- Add foreign key constraint to Student table
ALTER TABLE Student
ADD FOREIGN KEY (programme_id) REFERENCES Programme(programme_id);

-- Insert sample data
INSERT INTO Person (person_id, name, email) VALUES
('L001', 'John Smith', 'john.smith@university.edu'),
('L002', 'Jane Doe', 'jane.doe@university.edu'),
('S001', 'Alice Johnson', 'alice.johnson@university.edu'),
('S002', 'Bob Wilson', 'bob.wilson@university.edu');

INSERT INTO Lecturer (lecturer_id) VALUES
('L001'),
('L002');

INSERT INTO Programme (programme_id, name, description, duration) VALUES
('P001', 'Computer Science', 'Bachelor of Science in Computer Science', 4),
('P002', 'Information Technology', 'Bachelor of Science in Information Technology', 4);

INSERT INTO Student (student_id, programme_id) VALUES
('S001', 'P001'),
('S002', 'P002');

INSERT INTO Course (course_id, name, description, credits, max_students) VALUES
('C001', 'Introduction to Programming', 'Basic programming concepts and practices', 3, 30),
('C002', 'Database Systems', 'Design and implementation of database systems', 4, 25),
('C003', 'Web Development', 'Modern web development technologies and practices', 3, 35);

INSERT INTO Course_Lecturer (course_id, lecturer_id) VALUES
('C001', 'L001'),
('C002', 'L001'),
('C003', 'L002');

INSERT INTO Course_Student (course_id, student_id, enrollment_date) VALUES
('C001', 'S001', '2024-01-15'),
('C002', 'S001', '2024-01-15'),
('C003', 'S002', '2024-01-15');

-- Create indexes for better performance
CREATE INDEX idx_person_email ON Person(email);
CREATE INDEX idx_student_programme ON Student(programme_id);
CREATE INDEX idx_course_lecturer ON Course_Lecturer(course_id, lecturer_id);
CREATE INDEX idx_course_student ON Course_Student(course_id, student_id);

-- Create views for common queries
CREATE OR REPLACE VIEW student_details AS
SELECT 
    s.student_id,
    p.name,
    p.email,
    pr.name as programme_name,
    COUNT(cs.course_id) as enrolled_courses
FROM Student s
JOIN Person p ON s.student_id = p.person_id
JOIN Programme pr ON s.programme_id = pr.programme_id
LEFT JOIN Course_Student cs ON s.student_id = cs.student_id
GROUP BY s.student_id, p.name, p.email, pr.name;

CREATE OR REPLACE VIEW lecturer_courses AS
SELECT 
    l.lecturer_id,
    p.name as lecturer_name,
    COUNT(cl.course_id) as courses_teaching,
    GROUP_CONCAT(c.name) as course_names
FROM Lecturer l
JOIN Person p ON l.lecturer_id = p.person_id
LEFT JOIN Course_Lecturer cl ON l.lecturer_id = cl.lecturer_id
LEFT JOIN Course c ON cl.course_id = c.course_id
GROUP BY l.lecturer_id, p.name;

-- Create triggers for data integrity
DELIMITER //

-- Trigger to ensure a lecturer doesn't teach more than 2 courses
CREATE TRIGGER check_lecturer_courses
BEFORE INSERT ON Course_Lecturer
FOR EACH ROW
BEGIN
    DECLARE course_count INT;
    SELECT COUNT(*) INTO course_count
    FROM Course_Lecturer
    WHERE lecturer_id = NEW.lecturer_id;
    
    IF course_count >= 2 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'A lecturer cannot teach more than 2 courses';
    END IF;
END//

-- Trigger to ensure a student doesn't enroll in more than 3 courses
CREATE TRIGGER check_student_courses
BEFORE INSERT ON Course_Student
FOR EACH ROW
BEGIN
    DECLARE course_count INT;
    SELECT COUNT(*) INTO course_count
    FROM Course_Student
    WHERE student_id = NEW.student_id;
    
    IF course_count >= 3 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'A student cannot enroll in more than 3 courses';
    END IF;
END//

DELIMITER ; 
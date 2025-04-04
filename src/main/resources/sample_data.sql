-- Insert sample programmes
INSERT INTO programmes (code, name, department) VALUES
('CS101', 'Computer Science', 'School of Computing'),
('ENG101', 'Engineering', 'School of Engineering'),
('BUS101', 'Business Administration', 'School of Business');

-- Insert sample courses
INSERT INTO courses (code, name, credits) VALUES
('CS101', 'Introduction to Programming', 3),
('CS102', 'Data Structures', 4),
('CS103', 'Database Systems', 4),
('ENG101', 'Engineering Mathematics', 3),
('ENG102', 'Mechanics', 4),
('BUS101', 'Business Management', 3),
('BUS102', 'Financial Accounting', 4);

-- Insert sample persons (lecturers)
INSERT INTO persons (name, email, type) VALUES
('Dr. Smith', 'smith@university.edu', 'LECTURER'),
('Prof. Johnson', 'johnson@university.edu', 'LECTURER'),
('Dr. Williams', 'williams@university.edu', 'LECTURER');

-- Insert lecturers
INSERT INTO lecturers (person_id) VALUES
(1), (2), (3);

-- Insert sample persons (students)
INSERT INTO persons (name, email, type) VALUES
('John Doe', 'john.doe@student.edu', 'STUDENT'),
('Jane Smith', 'jane.smith@student.edu', 'STUDENT'),
('Mike Johnson', 'mike.johnson@student.edu', 'STUDENT'),
('Sarah Williams', 'sarah.williams@student.edu', 'STUDENT'),
('David Brown', 'david.brown@student.edu', 'STUDENT');

-- Insert students
INSERT INTO students (person_id, programme_code) VALUES
(4, 'CS101'),
(5, 'CS101'),
(6, 'ENG101'),
(7, 'BUS101'),
(8, 'BUS101');

-- Assign courses to lecturers
INSERT INTO lecturer_course (lecturer_id, course_id) VALUES
(1, 1), -- Dr. Smith teaches Introduction to Programming
(1, 2), -- Dr. Smith teaches Data Structures
(2, 3), -- Prof. Johnson teaches Database Systems
(2, 4), -- Prof. Johnson teaches Engineering Mathematics
(3, 5), -- Dr. Williams teaches Mechanics
(3, 6), -- Dr. Williams teaches Business Management
(3, 7); -- Dr. Williams teaches Financial Accounting 
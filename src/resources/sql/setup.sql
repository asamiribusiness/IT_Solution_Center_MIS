-- Create Database
CREATE DATABASE IF NOT EXISTS it_solution_center;
USE it_solution_center;

-- =============================================
-- 1. CORE TABLES
-- =============================================

-- Users/Employees table
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('admin', 'manager', 'employee', 'intern') DEFAULT 'employee',
    phone VARCHAR(20),
    address TEXT,
    hire_date DATE,
    salary DECIMAL(10, 2),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Clients table
CREATE TABLE IF NOT EXISTS clients (
    client_id INT PRIMARY KEY AUTO_INCREMENT,
    client_name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    address TEXT,
    client_type ENUM('individual', 'business', 'government') DEFAULT 'individual',
    registration_date DATE DEFAULT (CURRENT_DATE),
    status ENUM('active', 'inactive', 'suspended') DEFAULT 'active',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 2. COURSE MANAGEMENT
-- =============================================

-- Courses table
CREATE TABLE IF NOT EXISTS courses (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    course_code VARCHAR(20) UNIQUE NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    description TEXT,
    duration_hours INT NOT NULL,
    fee DECIMAL(10, 2) NOT NULL,
    category ENUM('programming', 'networking', 'database', 'web_dev', 'cybersecurity', 'other') DEFAULT 'programming',
    instructor_id INT,
    max_students INT DEFAULT 20,
    start_date DATE,
    end_date DATE,
    schedule VARCHAR(100),
    status ENUM('upcoming', 'ongoing', 'completed', 'cancelled') DEFAULT 'upcoming',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (instructor_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- Course enrollments
CREATE TABLE IF NOT EXISTS course_enrollments (
    enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    student_email VARCHAR(100),
    student_phone VARCHAR(20),
    enrollment_date DATE DEFAULT (CURRENT_DATE),
    fee_paid DECIMAL(10, 2) DEFAULT 0,
    total_fee DECIMAL(10, 2) NOT NULL,
    payment_status ENUM('pending', 'partial', 'paid', 'refunded') DEFAULT 'pending',
    attendance_percentage DECIMAL(5, 2) DEFAULT 0,
    certificate_issued BOOLEAN DEFAULT FALSE,
    notes TEXT,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    INDEX idx_course_student (course_id, student_email)
);

-- =============================================
-- 3. SUPPORT SERVICES
-- =============================================

-- Support tickets master table
CREATE TABLE IF NOT EXISTS support_tickets (
    ticket_id INT PRIMARY KEY AUTO_INCREMENT,
    ticket_number VARCHAR(20) UNIQUE NOT NULL,
    client_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    service_type ENUM('onsite', 'home', 'center', 'remote') NOT NULL,
    priority ENUM('low', 'medium', 'high', 'urgent') DEFAULT 'medium',
    assigned_to INT,
    status ENUM('open', 'in_progress', 'resolved', 'closed', 'cancelled') DEFAULT 'open',
    reported_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    scheduled_date DATETIME,
    completed_date DATETIME,
    location_address TEXT,
    estimated_hours DECIMAL(5, 2),
    actual_hours DECIMAL(5, 2),
    feedback_rating INT CHECK (feedback_rating >= 1 AND feedback_rating <= 5),
    feedback_comments TEXT,
    FOREIGN KEY (client_id) REFERENCES clients(client_id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_to) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_ticket_status (status, priority),
    INDEX idx_service_type (service_type)
);

-- Support ticket updates/logs
CREATE TABLE IF NOT EXISTS ticket_updates (
    update_id INT PRIMARY KEY AUTO_INCREMENT,
    ticket_id INT NOT NULL,
    user_id INT,
    update_text TEXT NOT NULL,
    update_type ENUM('status_change', 'comment', 'attachment', 'assignment') DEFAULT 'comment',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES support_tickets(ticket_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- =============================================
-- 4. DEVELOPMENT PROJECTS
-- =============================================

-- Development projects
CREATE TABLE IF NOT EXISTS development_projects (
    project_id INT PRIMARY KEY AUTO_INCREMENT,
    project_code VARCHAR(20) UNIQUE NOT NULL,
    project_name VARCHAR(100) NOT NULL,
    client_id INT NOT NULL,
    description TEXT,
    project_type ENUM('web_app', 'mobile_app', 'desktop', 'custom_software', 'consulting') DEFAULT 'web_app',
    start_date DATE,
    deadline DATE,
    budget DECIMAL(12, 2),
    manager_id INT,
    status ENUM('proposal', 'planned', 'in_progress', 'testing', 'completed', 'on_hold', 'cancelled') DEFAULT 'proposal',
    completion_percentage DECIMAL(5, 2) DEFAULT 0,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(client_id) ON DELETE CASCADE,
    FOREIGN KEY (manager_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- Project milestones
CREATE TABLE IF NOT EXISTS project_milestones (
    milestone_id INT PRIMARY KEY AUTO_INCREMENT,
    project_id INT NOT NULL,
    milestone_name VARCHAR(100) NOT NULL,
    description TEXT,
    due_date DATE,
    completed_date DATE,
    status ENUM('pending', 'in_progress', 'completed', 'delayed') DEFAULT 'pending',
    amount DECIMAL(10, 2),
    FOREIGN KEY (project_id) REFERENCES development_projects(project_id) ON DELETE CASCADE
);

-- Project team assignments
CREATE TABLE IF NOT EXISTS project_team (
    assignment_id INT PRIMARY KEY AUTO_INCREMENT,
    project_id INT NOT NULL,
    user_id INT NOT NULL,
    role VARCHAR(50),
    assigned_date DATE DEFAULT (CURRENT_DATE),
    hourly_rate DECIMAL(8, 2),
    hours_worked DECIMAL(8, 2) DEFAULT 0,
    FOREIGN KEY (project_id) REFERENCES development_projects(project_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY unique_project_user (project_id, user_id)
);

-- =============================================
-- 5. INTERN MANAGEMENT
-- =============================================

-- Intern applications
CREATE TABLE IF NOT EXISTS intern_applications (
    application_id INT PRIMARY KEY AUTO_INCREMENT,
    applicant_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    university VARCHAR(100),
    course VARCHAR(100),
    year_of_study VARCHAR(20),
    resume_path VARCHAR(255),
    applied_for ENUM('development', 'support', 'training', 'general') DEFAULT 'development',
    application_date DATE DEFAULT (CURRENT_DATE),
    status ENUM('received', 'reviewed', 'interviewed', 'accepted', 'rejected', 'hired') DEFAULT 'received',
    interview_date DATETIME,
    interview_notes TEXT,
    skills TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_app_status (status, applied_for)
);

-- Interns (once hired)
CREATE TABLE IF NOT EXISTS interns (
    intern_id INT PRIMARY KEY AUTO_INCREMENT,
    application_id INT UNIQUE,
    user_id INT UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE,
    department VARCHAR(50),
    supervisor_id INT,
    stipend DECIMAL(8, 2),
    performance_rating DECIMAL(3, 2),
    certificate_issued BOOLEAN DEFAULT FALSE,
    notes TEXT,
    FOREIGN KEY (application_id) REFERENCES intern_applications(application_id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (supervisor_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- =============================================
-- 6. FINANCIAL MANAGEMENT
-- =============================================

-- Income transactions
CREATE TABLE IF NOT EXISTS income_transactions (
    income_id INT PRIMARY KEY AUTO_INCREMENT,
    transaction_date DATE NOT NULL,
    reference_number VARCHAR(50) UNIQUE,
    source_type ENUM('course_fee', 'support_service', 'development_project', 'consulting', 'other') NOT NULL,
    source_id INT, -- Could reference course_id, ticket_id, project_id, etc.
    payer_name VARCHAR(100) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    payment_method ENUM('cash', 'bank_transfer', 'cheque', 'card', 'online') DEFAULT 'cash',
    description TEXT,
    received_by INT,
    status ENUM('pending', 'received', 'cancelled') DEFAULT 'received',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (received_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_income_date (transaction_date, source_type)
);

-- Expense transactions
CREATE TABLE IF NOT EXISTS expense_transactions (
    expense_id INT PRIMARY KEY AUTO_INCREMENT,
    transaction_date DATE NOT NULL,
    reference_number VARCHAR(50),
    category ENUM('salary', 'rent', 'utilities', 'equipment', 'software', 'marketing', 'travel', 'maintenance', 'other') NOT NULL,
    payee_name VARCHAR(100) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    payment_method ENUM('cash', 'bank_transfer', 'cheque', 'card') DEFAULT 'cash',
    description TEXT,
    approved_by INT,
    status ENUM('pending', 'approved', 'paid', 'cancelled') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (approved_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_expense_date (transaction_date, category)
);

-- =============================================
-- 7. INVENTORY & ASSETS
-- =============================================

-- Equipment/Assets
CREATE TABLE IF NOT EXISTS assets (
    asset_id INT PRIMARY KEY AUTO_INCREMENT,
    asset_tag VARCHAR(50) UNIQUE NOT NULL,
    asset_name VARCHAR(100) NOT NULL,
    category ENUM('computer', 'network', 'printer', 'software', 'furniture', 'other') DEFAULT 'computer',
    serial_number VARCHAR(100),
    purchase_date DATE,
    purchase_cost DECIMAL(10, 2),
    current_value DECIMAL(10, 2),
    status ENUM('available', 'in_use', 'maintenance', 'retired', 'lost') DEFAULT 'available',
    assigned_to INT,
    location VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assigned_to) REFERENCES users(user_id) ON DELETE SET NULL
);

-- =============================================
-- 8. REPORTS & ANALYTICS VIEWS
-- =============================================

-- Create a view for financial summary
CREATE OR REPLACE VIEW financial_summary AS
SELECT
    'Income' AS type,
    DATE_FORMAT(transaction_date, '%Y-%m') AS month,
    SUM(amount) AS total_amount,
    COUNT(*) AS transaction_count
FROM income_transactions
WHERE status = 'received'
GROUP BY DATE_FORMAT(transaction_date, '%Y-%m')
UNION ALL
SELECT
    'Expense' AS type,
    DATE_FORMAT(transaction_date, '%Y-%m') AS month,
    SUM(amount) AS total_amount,
    COUNT(*) AS transaction_count
FROM expense_transactions
WHERE status = 'paid'
GROUP BY DATE_FORMAT(transaction_date, '%Y-%m');

-- Create a view for support performance
CREATE OR REPLACE VIEW support_performance AS
SELECT
    u.user_id,
    u.full_name,
    COUNT(DISTINCT t.ticket_id) AS total_tickets,
    AVG(t.feedback_rating) AS avg_rating,
    SUM(t.actual_hours) AS total_hours,
    COUNT(DISTINCT CASE WHEN t.status = 'resolved' THEN t.ticket_id END) AS resolved_tickets
FROM users u
LEFT JOIN support_tickets t ON u.user_id = t.assigned_to
WHERE u.role IN ('employee', 'manager')
GROUP BY u.user_id, u.full_name;

-- =============================================
-- 9. DEFAULT DATA & INDEXES
-- =============================================

-- Insert default admin user (password: admin123 - change this in production!)
INSERT INTO users (username, password_hash, email, full_name, role)
VALUES ('admin', '$2y$10$YourHashedPasswordHere', 'admin@itsolution.com', 'Administrator', 'admin')
ON DUPLICATE KEY UPDATE username = username;

-- Create additional indexes for performance
CREATE INDEX idx_courses_status ON courses(status, start_date);
CREATE INDEX idx_tickets_assigned ON support_tickets(assigned_to, status);
CREATE INDEX idx_projects_status ON development_projects(status, deadline);
CREATE INDEX idx_income_source ON income_transactions(source_type, source_id);
CREATE INDEX idx_enrollments_payment ON course_enrollments(payment_status, enrollment_date);

-- =============================================
-- 10. STORED PROCEDURES (Optional but useful)
-- =============================================

DELIMITER $$

-- Procedure to calculate monthly revenue
CREATE PROCEDURE GetMonthlyRevenue(IN year INT)
BEGIN
    SELECT
        MONTH(transaction_date) AS month,
        SUM(amount) AS total_income,
        (SELECT SUM(amount) FROM expense_transactions
         WHERE YEAR(transaction_date) = year
         AND MONTH(transaction_date) = month
         AND status = 'paid') AS total_expense
    FROM income_transactions
    WHERE YEAR(transaction_date) = year
    AND status = 'received'
    GROUP BY MONTH(transaction_date)
    ORDER BY month;
END$$

-- Procedure to update project completion percentage
CREATE PROCEDURE UpdateProjectCompletion(IN p_project_id INT)
BEGIN
    DECLARE total_milestones INT;
    DECLARE completed_milestones INT;
    DECLARE completion_percentage DECIMAL(5,2);
   
    SELECT COUNT(*) INTO total_milestones
    FROM project_milestones
    WHERE project_id = p_project_id;
   
    SELECT COUNT(*) INTO completed_milestones
    FROM project_milestones
    WHERE project_id = p_project_id
    AND status = 'completed';
   
    IF total_milestones > 0 THEN
        SET completion_percentage = (completed_milestones / total_milestones) * 100;
    ELSE
        SET completion_percentage = 0;
    END IF;
   
    UPDATE development_projects
    SET completion_percentage = completion_percentage
    WHERE project_id = p_project_id;
END$$

DELIMITER ;

-- =============================================
-- DATABASE USERS AND PRIVILEGES
-- =============================================

-- Create application user (run these separately with admin privileges)
/*
CREATE USER 'itsolution_app'@'localhost' IDENTIFIED BY 'SecurePass123!';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON it_solution_center.* TO 'itsolution_app'@'localhost';
FLUSH PRIVILEGES;
*/
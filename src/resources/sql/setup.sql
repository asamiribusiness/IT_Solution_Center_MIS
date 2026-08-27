/*
 Navicat Premium Dump SQL

 Source Server         : newConnection
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : it_solution_center

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 26/08/2026 20:36:30
*/
-- ----------------------------
-- Create Database IT Solution Center
-- ----------------------------
-- Create Database
CREATE DATABASE IF NOT EXISTS it_solution_center;
USE it_solution_center;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for assets
-- ----------------------------
DROP TABLE IF EXISTS `assets`;
CREATE TABLE `assets`  (
  `asset_id` int NOT NULL AUTO_INCREMENT,
  `asset_tag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `asset_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `category` enum('IT Equipment','Office Equipment','Office Supply','Other') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'Office Supply',
  `serial_number` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `purchase_date` date NULL DEFAULT NULL,
  `purchase_cost` decimal(10, 2) NULL DEFAULT NULL,
  `current_value` decimal(10, 2) NULL DEFAULT NULL,
  `status` enum('Available','In_Use','Maintenance','Retired','Lost') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'Available',
  `assigned_to` int NULL DEFAULT NULL,
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`asset_id`) USING BTREE,
  UNIQUE INDEX `asset_tag`(`asset_tag` ASC) USING BTREE,
  INDEX `assigned_to`(`assigned_to` ASC) USING BTREE,
  CONSTRAINT `assets_ibfk_1` FOREIGN KEY (`assigned_to`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of assets
-- ----------------------------
INSERT INTO `assets` VALUES (3, 'IT-001', 'Computer', 'IT Equipment', '1055001', '2026-01-27', 20000.00, 20000.00, 'Available', 1, 'IT Office', NULL, '2026-02-01 05:33:36');
INSERT INTO `assets` VALUES (4, 'OE-001', 'Desk', 'Office Equipment', NULL, '2026-02-04', 4000.00, 4000.00, 'Available', 2, 'Finance Office', NULL, '2026-02-01 05:34:42');
INSERT INTO `assets` VALUES (5, 'OS-001', 'Shelf', 'Office Supply', NULL, '2026-01-28', 5000.00, 5000.00, 'In_Use', 4, 'Room', NULL, '2026-02-01 05:36:00');
INSERT INTO `assets` VALUES (6, 'IT-002', 'Printer', 'IT Equipment', '559563', '2026-02-03', 10000.00, 10000.00, 'Available', 3, 'IT Office', NULL, '2026-02-01 06:34:46');

-- ----------------------------
-- Table structure for attendance
-- ----------------------------
DROP TABLE IF EXISTS `attendance`;
CREATE TABLE `attendance`  (
  `attendance_id` int NOT NULL AUTO_INCREMENT,
  `enrollment_id` int NOT NULL,
  `course_id` int NOT NULL,
  `attendance_date` date NOT NULL,
  `status` enum('Present','Absent','Late','Excused') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'Present',
  `check_in_time` time NULL DEFAULT NULL,
  `remarks` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `recorded_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `record_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`attendance_id`) USING BTREE,
  UNIQUE INDEX `unique_attendance`(`enrollment_id` ASC, `course_id` ASC, `attendance_date` ASC) USING BTREE,
  INDEX `courseFK`(`course_id` ASC) USING BTREE,
  CONSTRAINT `attendance_ibfk_1` FOREIGN KEY (`enrollment_id`) REFERENCES `course_enrollments` (`enrollment_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `courseFK` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of attendance
-- ----------------------------

-- ----------------------------
-- Table structure for clients
-- ----------------------------
DROP TABLE IF EXISTS `clients`;
CREATE TABLE `clients`  (
  `client_id` int NOT NULL AUTO_INCREMENT,
  `client_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `contact_person` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `client_type` enum('Individual','Business','Government') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'Individual',
  `registration_date` date NULL DEFAULT (curdate()),
  `status` enum('Active','Inactive','Suspended') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'Active',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`client_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of clients
-- ----------------------------
INSERT INTO `clients` VALUES (1, 'Ahmad', 'Mahmood', 'ahmad@gmail.com', '078965787', 'Farah', 'Individual', '2026-01-18', 'Active', NULL, '2026-01-18 12:43:47');
INSERT INTO `clients` VALUES (2, 'Reza', 'Reza', 'reza@yahoo.com', '076787876', 'Herat', 'Business', '2026-01-27', 'Active', NULL, '2026-01-27 13:30:04');
INSERT INTO `clients` VALUES (3, 'Mahmood', 'Karim', 'Karim@gmail.com', '078998744', 'Farah', 'Individual', '2026-01-27', 'Active', NULL, '2026-01-27 13:30:39');
INSERT INTO `clients` VALUES (4, 'DoE', 'Ahmad', 'DoE@gov.af', '0799898345', 'Farah', 'Government', '2026-01-27', 'Active', NULL, '2026-01-27 13:31:09');
INSERT INTO `clients` VALUES (5, 'Alshafa Clinic', 'Saber Shah', 'alshafa@gmail.com', '0798894456', 'Farah', 'Business', '2026-01-27', 'Active', NULL, '2026-01-27 13:31:58');
INSERT INTO `clients` VALUES (6, 'Rahmat Tawar', 'Rahmat', 'rahmat@yahoo.com', '0789989789', 'Kabul', 'Business', '2026-01-27', 'Active', NULL, '2026-01-27 13:32:50');

-- ----------------------------
-- Table structure for course_enrollments
-- ----------------------------
DROP TABLE IF EXISTS `course_enrollments`;
CREATE TABLE `course_enrollments`  (
  `enrollment_id` int NOT NULL AUTO_INCREMENT,
  `course_id` int NOT NULL,
  `student_id` int NOT NULL,
  `enrollment_date` date NULL DEFAULT (curdate()),
  `fee_paid` decimal(10, 2) NULL DEFAULT 0.00,
  `total_fee` decimal(10, 2) NOT NULL,
  `payment_status` enum('Pending','Partial','Paid','Refunded') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'Pending',
  `attendance_percentage` decimal(5, 2) NULL DEFAULT 0.00,
  `status` enum('Active','Left','Graduated') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `certificate_issued` tinyint(1) NULL DEFAULT 0,
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`enrollment_id`) USING BTREE,
  INDEX `idx_course_student`(`course_id` ASC) USING BTREE,
  INDEX `idx_enrollments_payment`(`payment_status` ASC, `enrollment_date` ASC) USING BTREE,
  INDEX `student_id`(`student_id` ASC) USING BTREE,
  CONSTRAINT `course_enrollments_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `student_id` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_enrollments
-- ----------------------------

-- ----------------------------
-- Table structure for courses
-- ----------------------------
DROP TABLE IF EXISTS `courses`;
CREATE TABLE `courses`  (
  `course_id` int NOT NULL AUTO_INCREMENT,
  `course_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `duration_hours` int NOT NULL,
  `fee` decimal(10, 2) NOT NULL,
  `category` enum('Programming','Networking','Database','Web Development','Cyber Security','Internet & Mailing','Hardware','IT','ICT','MOUS','ICDL','Other') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'Programming',
  `instructor_id` int NULL DEFAULT NULL,
  `start_date` date NULL DEFAULT NULL,
  `end_date` date NULL DEFAULT NULL,
  `schedule` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` enum('Upcoming','Ongoing','Completed','Cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'Upcoming',
  PRIMARY KEY (`course_id`) USING BTREE,
  UNIQUE INDEX `course_code`(`course_code` ASC) USING BTREE,
  INDEX `instructor_id`(`instructor_id` ASC) USING BTREE,
  INDEX `idx_courses_status`(`status` ASC, `start_date` ASC) USING BTREE,
  CONSTRAINT `courses_ibfk_1` FOREIGN KEY (`instructor_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of courses
-- ----------------------------
INSERT INTO `courses` VALUES (1, 'PL01', 'C Progrmming', 'این کورس شامل.......', 36, 2500.00, 'Programming', 17, '2026-01-02', '2026-01-31', 'Evening 4:00', 'Ongoing');
INSERT INTO `courses` VALUES (2, 'PL02', 'C++', 'This is an ', 35, 3500.00, 'Programming', 29, '2026-01-02', '2026-02-18', 'Morning 8:00', 'Upcoming');
INSERT INTO `courses` VALUES (3, 'WD03', 'PHP', 'This is for adults', 20, 3000.00, 'Web Development', 18, '2026-01-07', '2026-01-29', 'Noon 12:00', 'Completed');
INSERT INTO `courses` VALUES (4, 'NW02', 'MCITP', 'this is called wonderfull', 44, 55.00, 'Networking', 15, '2026-01-08', '2022-11-01', 'Afternoon', 'Upcoming');
INSERT INTO `courses` VALUES (5, 'NW01', 'MCSC', 'this is a fantastic course', 33, 3500.00, 'Networking', 17, '2024-10-12', '2025-01-20', 'Noon 12:00', 'Upcoming');
INSERT INTO `courses` VALUES (6, 'PL03', 'OOP in Java', 'this is a course for adults', 33, 5500.00, 'Programming', 15, '2026-01-22', '2026-01-14', 'AfterNoon', 'Upcoming');
INSERT INTO `courses` VALUES (9, 'PL04', 'OOP in C#', 'this is oop programming', 35, 4500.00, 'Programming', 3, '2026-02-13', NULL, 'Morning 8:00', 'Upcoming');

-- ----------------------------
-- Table structure for employees
-- ----------------------------
DROP TABLE IF EXISTS `employees`;
CREATE TABLE `employees`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `department` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `gender` enum('Male','Female','Other') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `full_time` tinyint(1) NULL DEFAULT NULL,
  `benefits` json NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of employees
-- ----------------------------
INSERT INTO `employees` VALUES (1, 'John Doe', 'IT', 'Male', 1, '{\"health_insurance\": true}', '2026-01-09 20:57:52');
INSERT INTO `employees` VALUES (2, 'Jane Smith', 'HR', 'Female', 0, '{\"health_insurance\": false}', '2026-01-09 20:57:52');
INSERT INTO `employees` VALUES (3, 'Bob Wilson', 'Sales', 'Male', 1, '{\"health_insurance\": true}', '2026-01-09 20:57:54');

-- ----------------------------
-- Table structure for expense_transactions
-- ----------------------------
DROP TABLE IF EXISTS `expense_transactions`;
CREATE TABLE `expense_transactions`  (
  `expense_id` int NOT NULL AUTO_INCREMENT,
  `transaction_date` date NOT NULL,
  `reference_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `category` enum('Salary','Rent','Utilities','Equipment','Software','Marketing','Travel','Maintenance','Tax','Other') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `payee_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `amount` decimal(12, 2) NOT NULL,
  `payment_method` enum('Cash','Bank Transfer','Cheque','Card') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'Cash',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `approved_by` int NULL DEFAULT NULL,
  `status` enum('Pending','Approved','Paid','Cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'Pending',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`expense_id`) USING BTREE,
  INDEX `approved_by`(`approved_by` ASC) USING BTREE,
  INDEX `idx_expense_date`(`transaction_date` ASC, `category` ASC) USING BTREE,
  CONSTRAINT `expense_transactions_ibfk_1` FOREIGN KEY (`approved_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of expense_transactions
-- ----------------------------
INSERT INTO `expense_transactions` VALUES (1, '2026-02-12', '1020', 'Rent', 'Ahmad', 2000.00, 'Cash', 'January Month Office Rent', 1, 'Paid', '2026-02-03 18:52:04');

-- ----------------------------
-- Table structure for income_transactions
-- ----------------------------
DROP TABLE IF EXISTS `income_transactions`;
CREATE TABLE `income_transactions`  (
  `income_id` int NOT NULL AUTO_INCREMENT,
  `transaction_date` date NOT NULL,
  `reference_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `source_type` enum('Course Fee','Support Service','Development Project','Consulting','IT Solution','Other') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `source_id` int NULL DEFAULT NULL,
  `payer_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `amount` decimal(12, 2) NOT NULL,
  `payment_method` enum('Cash','Bank Transfer','Cheque','Card','Online') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'Cash',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `received_by` int NULL DEFAULT NULL,
  `status` enum('Pending','Received','Cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'Received',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`income_id`) USING BTREE,
  UNIQUE INDEX `reference_number`(`reference_number` ASC) USING BTREE,
  INDEX `received_by`(`received_by` ASC) USING BTREE,
  INDEX `idx_income_date`(`transaction_date` ASC, `source_type` ASC) USING BTREE,
  INDEX `idx_income_source`(`source_type` ASC) USING BTREE,
  CONSTRAINT `income_transactions_ibfk_1` FOREIGN KEY (`received_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of income_transactions
-- ----------------------------
INSERT INTO `income_transactions` VALUES (1, '2026-02-03', '12', 'Course Fee', 1, 'Ahmad', 2000.00, 'Cash', 'Programming Course Payment', 2, 'Received', '2026-02-03 08:05:45');
INSERT INTO `income_transactions` VALUES (2, '2026-02-18', '13', 'Course Fee', 2, 'Rahim', 10000.00, 'Cash', 'Web Development', 1, 'Received', '2026-02-04 16:53:57');
INSERT INTO `income_transactions` VALUES (3, '2026-02-05', '14', 'Course Fee', 1, 'Wali', 3000.00, 'Cash', 'Programming', 2, 'Received', '2026-02-04 16:55:12');

-- ----------------------------
-- Table structure for intern_applications
-- ----------------------------
DROP TABLE IF EXISTS `intern_applications`;
CREATE TABLE `intern_applications`  (
  `application_id` int NOT NULL AUTO_INCREMENT,
  `applicant_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `university` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `course` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `Qualification` enum('Vocational','High School','Institute','Bachelor','Master') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'Bachelor',
  `resume_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `applied_for` enum('Development','Support','Training','General') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'Development',
  `application_date` date NULL DEFAULT (curdate()),
  `status` enum('Received','Reviewed','Interviewed','Accepted','Rejected','Hired') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'Received',
  `interview_date` datetime NULL DEFAULT NULL,
  `interview_notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `skills` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`application_id`) USING BTREE,
  INDEX `idx_app_status`(`status` ASC, `applied_for` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of intern_applications
-- ----------------------------
INSERT INTO `intern_applications` VALUES (1, 'Ahmad Zia', 'saif@gmail.com', '0798814646', 'Gharjistan', 'IT', 'High School', NULL, 'Development', '2026-01-25', 'Received', '2026-01-30 05:47:51', NULL, 'Driving', '2026-01-25 05:48:14');
INSERT INTO `intern_applications` VALUES (2, 'Ali Reza', 'ahmadi@yahoo.com', '0789675434', 'kardan', 'Law', 'Bachelor', NULL, 'Support', '2026-01-26', 'Reviewed', '2026-01-13 09:17:26', NULL, 'Reporting', '2026-01-26 09:17:35');

-- ----------------------------
-- Table structure for interns
-- ----------------------------
DROP TABLE IF EXISTS `interns`;
CREATE TABLE `interns`  (
  `intern_id` int NOT NULL AUTO_INCREMENT,
  `application_id` int NULL DEFAULT NULL,
  `user_id` int NULL DEFAULT NULL,
  `start_date` date NOT NULL,
  `end_date` date NULL DEFAULT NULL,
  `department` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `supervisor_id` int NULL DEFAULT NULL,
  `stipend` decimal(8, 2) NULL DEFAULT NULL,
  `performance_rating` decimal(3, 2) NULL DEFAULT NULL,
  `certificate_issued` tinyint(1) NULL DEFAULT 0,
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  PRIMARY KEY (`intern_id`) USING BTREE,
  UNIQUE INDEX `application_id`(`application_id` ASC) USING BTREE,
  UNIQUE INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `supervisor_id`(`supervisor_id` ASC) USING BTREE,
  CONSTRAINT `interns_ibfk_1` FOREIGN KEY (`application_id`) REFERENCES `intern_applications` (`application_id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `interns_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `interns_ibfk_3` FOREIGN KEY (`supervisor_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of interns
-- ----------------------------
INSERT INTO `interns` VALUES (1, 1, 4, '2026-02-04', '2026-03-07', 'Development', 1, 2000.00, 9.00, 0, NULL);

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions`  (
  `permission_id` int NOT NULL AUTO_INCREMENT,
  `user_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `can_view` tinyint(1) NULL DEFAULT 0,
  `can_add` tinyint(1) NULL DEFAULT 0,
  `can_edit` tinyint(1) NULL DEFAULT 0,
  `can_delete` tinyint(1) NULL DEFAULT 0,
  `can_report` tinyint(1) NULL DEFAULT NULL,
  `can_export` tinyint(1) NULL DEFAULT 0,
  `can_manage_users` tinyint(1) NULL DEFAULT NULL,
  PRIMARY KEY (`permission_id`) USING BTREE,
  INDEX `idx_user_module`(`user_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permissions
-- ----------------------------
INSERT INTO `permissions` VALUES (1, 'Admin', 1, 1, 1, 1, 1, 1, 1);
INSERT INTO `permissions` VALUES (2, 'Manager', 1, 1, 1, 1, 1, 1, 0);
INSERT INTO `permissions` VALUES (3, 'Employee', 1, 1, 1, 0, 0, 0, 0);
INSERT INTO `permissions` VALUES (4, 'Instructor', 1, 1, 0, 0, 0, 0, 0);
INSERT INTO `permissions` VALUES (5, 'Intern', 1, 0, 0, 0, 0, 0, 0);

-- ----------------------------
-- Table structure for projects
-- ----------------------------
DROP TABLE IF EXISTS `projects`;
CREATE TABLE `projects`  (
  `project_id` int NOT NULL AUTO_INCREMENT,
  `project_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `project_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `client_id` int NULL DEFAULT NULL,
  `service_type` enum('Software Development','Troubleshooting','PC Assembly','Networking & Cabling','Training','Workshop','Consulting','Other') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `start_date` date NULL DEFAULT NULL,
  `end_date` date NULL DEFAULT NULL,
  `status` enum('Planning','Active','On Hold','Completed','Cancelled') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'Planning',
  `budget` decimal(12, 2) NULL DEFAULT NULL,
  `project_manager` int NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`project_id`) USING BTREE,
  UNIQUE INDEX `project_code`(`project_code` ASC) USING BTREE,
  INDEX `Client`(`client_id` ASC) USING BTREE,
  INDEX `PojectManager`(`project_manager` ASC) USING BTREE,
  CONSTRAINT `Client` FOREIGN KEY (`client_id`) REFERENCES `clients` (`client_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `PojectManager` FOREIGN KEY (`project_manager`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of projects
-- ----------------------------
INSERT INTO `projects` VALUES (1, 'SD001', 'Inventory Management System', 1, 'Software Development', '2026-01-25', '2026-02-04', 'Active', 3000.00, 1, NULL, '2026-01-24 09:57:17');
INSERT INTO `projects` VALUES (2, 'TS002', 'Network Infrastructure Troubleshooting', 2, 'Troubleshooting', '2026-01-07', '2026-01-29', 'On Hold', 5000.00, 1, NULL, '2026-01-24 09:57:17');
INSERT INTO `projects` VALUES (3, 'PA003', 'Office PC Assembly - 50 units', 3, 'PC Assembly', '2025-12-30', '2026-02-03', 'Completed', 10000.00, 2, NULL, '2026-01-24 09:57:17');
INSERT INTO `projects` VALUES (4, 'NC004', 'Office Network Cabling', 4, 'Networking & Cabling', '2026-02-04', '2026-02-07', 'Active', 2000.00, 3, NULL, '2026-01-24 09:57:17');
INSERT INTO `projects` VALUES (5, 'TR005', 'Java Programming Training', 5, 'Training', '2026-02-04', '2026-01-27', 'Planning', 30000.00, 2, NULL, '2026-01-24 09:57:17');
INSERT INTO `projects` VALUES (6, 'WS006', 'Cybersecurity Workshop', 6, 'Workshop', '2026-01-19', '2026-02-07', 'Cancelled', 12000.00, 4, NULL, '2026-01-24 09:57:17');
INSERT INTO `projects` VALUES (8, 'TR002', 'Java Programming Training', 1, 'Training', '2026-02-04', '2026-01-27', 'Planning', 210000.00, NULL, '', '2026-02-02 20:38:16');

-- ----------------------------
-- Table structure for students
-- ----------------------------
DROP TABLE IF EXISTS `students`;
CREATE TABLE `students`  (
  `student_id` int NOT NULL COMMENT 'YY-MM-NO',
  `full_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `father_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `image_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`student_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of students
-- ----------------------------
INSERT INTO `students` VALUES (260201, 'علی احمد', 'Rahim', 'Ahmadi@yahoo.com', '0799063252', 'student_images/260201_1770613701370.jpg');
INSERT INTO `students` VALUES (260202, 'Rahim', 'Ali', 'Rahim.kawa@yahoo.com', '0798765434', 'student_images/260202_1770613751194.jpg');
INSERT INTO `students` VALUES (260203, 'Ali', 'Wali', 'ali@gmail.co', '0799843754', 'student_images/260203_1770613789191.jpg');
INSERT INTO `students` VALUES (260204, 'Rahim', 'Mohammad', 'a@gmail.com', '0789748374', 'student_images/20200613_184346');
INSERT INTO `students` VALUES (260205, 'Karim', 'Sakhi', 'karim@yahoo.com', '0798744523', 'student_images/20200706_175020');
INSERT INTO `students` VALUES (260206, 'Wali', 'Jamal', 'adnan@yahoo.com', '0735498754', 'student_images/20210312_220220');
INSERT INTO `students` VALUES (260207, 'Ahmad Reza', 'Karim', 'ahmadi@gmail.com', '0789767655', 'student_images/20210312_220318');
INSERT INTO `students` VALUES (260210, 'Muska Amiri', 'Ahmad Shafiq', 'muska@yahoo.com', '0790987788', 'student_images/260210_1770613850218.png');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'SHA-256 produces 64 hex chars',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `full_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role` enum('Admin','Manager','Employee','Instructor','Intern') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'Employee',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `hire_date` date NULL DEFAULT NULL,
  `salary` decimal(10, 2) NULL DEFAULT NULL,
  `is_active` tinyint(1) NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'amiri', '$2a$10$ODkqv0No3STKYxbFNd.KLO2uyyFZqj695pI8wQoxFnK4./nBrhp3G', 'admin@itsolution.com', 'Ahmad Shafiq Amiri', 'Admin', '0799063252', 'Farah', '2026-01-01', 70000.00, 1, '2025-12-30 06:46:52');
INSERT INTO `users` VALUES (2, 'rahimi', '$2a$12$wOlKHrFyZUuE0Oh2HFHHbOXbQt7bJAV4Ek4A//RkbIJZuujo9MQs.', 'rahimi@gmail.com', 'Karimullah', 'Manager', '077898097', 'Kabul', '2026-01-08', 4000.00, 1, '2026-01-09 17:33:06');
INSERT INTO `users` VALUES (3, 'Karimi', '21232f297a57a5a743894a0e4a801fc3', 'karimi@gmail.com', 'Wahidullah', 'Instructor', '0765654322', 'Kabul', '2026-02-05', 3500.00, 1, '2026-01-09 17:34:17');
INSERT INTO `users` VALUES (4, 'Rahmani', '$2a$12$5s5QXbDPwvnyTz34RDs6kuAaIoxMEKjly.ZV8MfvDwQ3Y/EHuTMfq', 'rahmani@gmail.com', 'Rahman', 'Manager', '0798897867', 'Farah', '2026-01-02', 5000.00, 0, '2026-01-09 18:55:45');
INSERT INTO `users` VALUES (15, 'ahmadi', '$2a$10$L4tunpcahV.db0hhjbZIOOAQMBCMl5anhzIUZNl3wRCQbBNDgfYZq', 'ahmadi@gmail.com', 'Ahmad Reza', 'Instructor', '07898445599', 'Farah', '2026-01-08', 5000.00, 1, '2026-01-19 19:23:19');
INSERT INTO `users` VALUES (16, 'ali', '$2a$10$XK.owvLPQgKsFxVbVTLCo.aC44gb4Ixox2BIjIVlzDRxl1T.C26eW', 'ali@yahoo.com', 'Ali Ahmad', 'Employee', '0789977432', 'Kabul', '2026-01-01', 4000.00, 0, '2026-01-19 19:51:48');
INSERT INTO `users` VALUES (17, 'wali', '$2a$10$qrErTb.Ft7koYPczSqTTj.rJnHd26ZQDdL.1yqmRcKTRr23Lym0na', 'ahmad@yahoo.com', 'Ahmad Wail', 'Instructor', '0789534231', 'Herat', '2026-01-07', 5000.00, 1, '2026-01-19 19:54:02');
INSERT INTO `users` VALUES (18, 'akbari', '$2a$12$gMCPd6jIsVV4h/sVcuh00.YAYfrZKk2Z1qCoZpATjtNqfBVl/w4SW', 'akbari@yahoo.com', 'Assadullah Akbari', 'Instructor', '0798785533', 'Qandahar', '2026-01-08', 6000.00, 1, '2026-01-19 20:04:11');
INSERT INTO `users` VALUES (19, 'saifi', '$2a$10$.0QV21GNJGXa0wKQnY.fT.s0yzbEcPpIAQ7d5GweauEHFpbNQ0ZwG', 'karimzada@yahoo.com', 'Azita', 'Intern', '07987783423', 'Kabul', '2026-01-08', 6000.00, 1, '2026-01-20 21:01:18');
INSERT INTO `users` VALUES (20, 'Walizada', '$2a$10$uXkuSlfYkOrH.SRS26g3NO2IlNZ8WXkvxYshDgmkt43n2n84lt6xq', 'walizada@gmail.com', 'Ahmad Wali', 'Manager', '0789654322', 'Farah', '2026-02-02', 50000.00, 1, '2026-02-05 06:53:25');
INSERT INTO `users` VALUES (22, 'Samadi', '$2a$10$gsefXYUH/tmn318Oqlck5e1t76ZrvbdE5nq9RKyZI593/x4Ld3HN.', 'samadi@yahoo.com', 'Abdul Rashid', 'Manager', '0798994432', 'Herat', '2026-02-03', 5000.00, 1, '2026-02-06 08:22:07');
INSERT INTO `users` VALUES (29, 'aslam', '$2a$10$DFm4BYkSOTTsyLoi5.dBQOxnWhdDxhzvnVABAkTzP3E/aZgZ2Ck5G', 'dfsdfs@yahoo.co,', 'aslam jan', 'Instructor', '07988345213', 'Badghis', '2026-02-05', 5555.00, 1, '2026-02-06 09:16:08');

-- ----------------------------
-- View structure for financial_summary
-- ----------------------------
DROP VIEW IF EXISTS `financial_summary`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `financial_summary` AS select 'Income' AS `type`,date_format(`income_transactions`.`transaction_date`,'%Y-%m') AS `month`,sum(`income_transactions`.`amount`) AS `total_amount`,count(0) AS `transaction_count` from `income_transactions` where (`income_transactions`.`status` = 'received') group by date_format(`income_transactions`.`transaction_date`,'%Y-%m') union all select 'Expense' AS `type`,date_format(`expense_transactions`.`transaction_date`,'%Y-%m') AS `month`,sum(`expense_transactions`.`amount`) AS `total_amount`,count(0) AS `transaction_count` from `expense_transactions` where (`expense_transactions`.`status` = 'paid') group by date_format(`expense_transactions`.`transaction_date`,'%Y-%m');

-- ----------------------------
-- Procedure structure for GetMonthlyRevenue
-- ----------------------------
DROP PROCEDURE IF EXISTS `GetMonthlyRevenue`;
delimiter ;;
CREATE PROCEDURE `GetMonthlyRevenue`(IN year INT)
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
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for UpdateProjectCompletion
-- ----------------------------
DROP PROCEDURE IF EXISTS `UpdateProjectCompletion`;
delimiter ;;
CREATE PROCEDURE `UpdateProjectCompletion`(IN p_project_id INT)
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
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;

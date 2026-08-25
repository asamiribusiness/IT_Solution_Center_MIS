# IT Solution Center MIS

## Overview
A comprehensive Management Information System for IT Solution Center built with Java Swing and MySQL.

## Features
- User Management with Role-Based Access Control
- Course Management and Enrollment
- Technical Support Ticket System
- Software Development Project Management
- Intern Recruitment and Management
- Financial Management (Income/Expense Tracking)
- Asset/Inventory Management
- Reporting and Analytics

## Prerequisites
- Java JDK 8 or higher
- MySQL Server 8.0 or higher
- NetBeans IDE (for development)

## Installation

### 1. Database Setup
1. Install MySQL Server
2. Run the database script: `database_setup.sql`
3. Create database user with proper privileges

### 2. Application Setup
1. Clone or download the project
2. Update `config.properties` with your database credentials
3. Add required JAR files to `lib/` directory:
   - `mysql-connector-java-8.x.x.jar`
   - `jbcrypt-0.4.jar`
   - `jcalendar-1.4.jar`

### 3. Build and Run
**Using Ant:**
```bash
ant compile
ant jar
ant run# IT_Solution_Center_MIS

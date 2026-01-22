---
title: Pharmacy Inventory Management System
author: Alaa
date: 2025–2026
---

# Pharmacy Inventory Management System

**POO & Database Academic Project**

## Project Description

This is a **Pharmacy Inventory Management System** developed for a university course in **Object-Oriented Programming (POO)** and **Database Systems**.

### Main Features
- Manage **medicines** and their stock levels
- Handle **suppliers**
- Manage **customers**
- Record **sales**
- Manage **employees** (system users)
- Create and track **orders**
- Display basic **performance indicators**

## Technologies Used
- **Java** (JDK 24 or higher)
- **MySQL** (database)
- **JDBC** (for database connectivity)
- **Java Swing** (graphical user interface)
- **DAO** pattern (Data Access Object)
- **MVC**-inspired architecture
- **Eclipse IDE**

## How to Open the Project

### Option 1 – From ZIP archive (simpler but less recommended)
1. Extract the ZIP file
2. Open **Eclipse**
3. Menu: **File → Open Projects from File System…**
4. Select the extracted folder
5. Click **Finish**

> ⚠️ If you get build path or dependency errors, prefer **Option 2**

### Option 2 – From GitHub (recommended)


git clone https://github.com/alaa820/POO-BD-Project.git
cd POO-BD-Project
Then in Eclipse:

File → Import → General → Existing Projects into Workspace
Select the cloned folder → Finish
Switch to the correct branch if needed:

Bashgit checkout "final product"
# or
git switch "final product"
Repository: https://github.com/alaa820/POO-BD-Project
Recommended branch: final product
Database Setup (Very Important!)
1. Update connection settings
File:
src/util/DatabaseConnector.java
Change these lines to match your MySQL configuration:
Javaprivate static final String URL      = "jdbc:mysql://localhost:3306/pharmacy_db?useSSL=false&serverTimezone=UTC";
private static final String USER     = "root";
private static final String PASSWORD = "your_password_here";

Replace pharmacy_db with your actual database name
Update username and password accordingly

2. Add MySQL Connector/J to the project

Right-click project → Properties
Java Build Path → Libraries tab
Click Add External JARs…
Select mysql-connector-j-x.x.xx.jar
(download from: https://dev.mysql.com/downloads/connector/j/ if you don't have it)
Apply and Close

3. Create the database and tables

Open MySQL (Workbench / terminal / etc.)
Create the database:

SQLCREATE DATABASE pharmacy_db;

Run the SQL script provided in the project (usually in sql/, database/, or at the root)
Or make sure all required tables exist before launching the application

How to Run the Application

Make sure MySQL server is running
Database exists and connection settings are correct
In Eclipse: right-click the class containing main (usually Main.java)
Run As → Java Application

Learning Objectives
This project helps demonstrate:

Core Object-Oriented Programming concepts
DAO (Data Access Object) pattern implementation
JDBC database connectivity in Java
Full CRUD operations
Basic Swing GUI development
Simple MVC-like project structure
Separation between presentation, business logic, and data layers

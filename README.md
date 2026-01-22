# Pharmacy Inventory Management System
**POO & Database Project**

##  Project Description
This project is a **Pharmacy Inventory Management System** developed as part of a POO (Programmation Orientée Objet) and Database academic project.

It allows managing:
- Medicines and stock  
- Suppliers  
- Customers  
- Sales  
- Employees (users)  
- Orders and basic performance indicators  

The project is built using:
- Java (Swing)  
- MySQL  
- JDBC  
- DAO design pattern  
- MVC-style structure  

##  Technologies Used
- Java (JDK 24+)  
- MySQL  
- JDBC Connector  
- Eclipse IDE  

##  How to Open the Project

### Option 1: Using the ZIP file
1. Extract the folder.  
2. Open Eclipse.  
3. Go to:  
   `File → Open Projects from File System`  
4. Select the extracted project folder.  
5. Click **Finish**.  

>  If the ZIP project does not work correctly, use Option 2.  

### Option 2: Using GitHub (Recommended)
1. Clone the repository:  
   ```bash
   git clone https://github.com/alaa820/POO-BD-Project
   ```

2. Open Eclipse.  
3. Import the project:  
   - `File → Import → Existing Maven / Java Project`  
4. Make sure you switch to the **final product** branch.  

 **Repository link:** [https://github.com/alaa820/POO-BD-Project](https://github.com/alaa820/POO-BD-Project)  
**Branch:** `final product`

##  Database Configuration (IMPORTANT)
The project uses MySQL. You must update the database connection settings before running the project.

###  MySQL URL, Username, Password
Open the following file:  
```
src/util/DatabaseConnector.java
```

Modify these fields according to your MySQL configuration:  
```java
private static final String URL = "jdbc:mysql://localhost:3306/your_database_name";
private static final String USER = "your_username";
private static final String PASSWORD = "your_password";
```

###  JDBC Connector Configuration in Eclipse
You must ensure that the MySQL JDBC connector is correctly configured:

1. Right-click the project → **Properties**  
2. Go to **Java Build Path**  
3. Open the **Libraries** tab  
4. Add the MySQL Connector JAR (`mysql-connector-j.jar`)  
5. **Apply and Close**  

##  How to Run
- Run the **Main** class from Eclipse  
- Make sure:  
  - MySQL server is running  
  - Database tables are created  
  - Connection parameters are correct  


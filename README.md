# E-Commerce Management System

## Overview

The E-Commerce Management System is a Java Swing-based GUI desktop application designed to manage various aspects of an online store. It includes features for user authentication, product management, and dedicated dashboards for employees and customers. The system is built with a modular structure, applying core object oriented concepts, making it easy to extend and maintain.

This project was initially developed by a group of four as part of the "Object Oriented Programming 1 (JAVA)" course at American International University-Bangladesh (AIUB) for the Summer 2023-2024 Semester, but was later adopted for the "Introduction to Database" course's final term project by improving and expanding the feature sets. This project's key improvement over previous iterations is the switch to a relational database management system (RDBMS) using MySQL.

Please see [here](https://github.com/Emad006/E-CommerceManagementSystem) for the previous version of the project that used file-based data storage.

## Features

- User Authentication (Login/Logout)
- Product & User Management (Add, Update, Delete, Search)
- Persistent Carts (NEW)
- Saved Payment Information (NEW)
- Customer, Worker (NEW), and Admin Dashboards
- Role-based Access Control (RBAC)
- Database Integration (MySQL) (NEW)

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- MySQL Server
- Git (optional, for cloning the repository)

## Setup

Clone the repository (if using Git):
```sh
git clone https://github.com/Emad006/E-CommerceManagementSystemDB.git
cd E-CommerceManagementSystemDB
```
Or download the [ZIP File](https://github.com/Emad006/E-CommerceManagementSystemDB/archive/refs/heads/main.zip) and extract it.

## Database Setup
This section is yet to be written. Please check back later.

## Running the Application

### On UNIX (Linux/Mac)

1. Make sure the [`startApp.sh`](https://github.com/Emad006/E-CommerceManagementSystemDB/blob/main/startApp.sh) script has execute permissions:
    ```sh
    chmod +x startApp.sh
    ```

2. Run the [`startApp.sh`](https://github.com/Emad006/E-CommerceManagementSystemDB/blob/main/startApp.sh) script in the terminal:
    ```sh
    ./startApp.sh
    ```

### On Windows

1. Run the [`startApp.bat`](https://github.com/Emad006/E-CommerceManagementSystemDB/blob/main/startApp.bat) script by double-clicking it in the file explorer:

    ```bat
    startApp.bat
    ```
    If you get a security warning, click "More info" and then "Run anyway". I can assure you that the script is safe, but you can always check the contents before running it as it's just a simple command to run the Java application.

## Notes

- The application uses relative paths for loading assets and persistent data. Ensure that the directory structure is maintained.
- Please use the provided scripts to run the application as not doing so may cause issues with the relative paths (Assets not rendering). Additionally, the script creates a separate, organised directory for the bytecode.
- The [`database`](https://github.com/Emad006/E-CommerceManagementSystemDB/tree/main/database) directory contains all the data access objects (DAO) and the database connection class. The database connection class is used to establish a connection to the MySQL database.
- Adding products with images from the admin dashboard uses absolute paths, making the project less portable across different systems. Currently, all the sample products use relative path as they were directly modified in the database files, so shouldn't cause any issues.
- SuperAdmins cannot be added, deleted, or updated through the application. They must be manually added to the database. The default SuperAdmin credentials are:
    - Email: `sa@ecms.com`
    - Password: `superadmin123`

## Screenshots

![Login Page](https://github.com/Emad006/E-CommerceManagementSystem/blob/main/assets/front-end-screenshots/LoginPage.PNG)
![Registration Page](https://github.com/Emad006/E-CommerceManagementSystem/blob/main/assets/front-end-screenshots/RegistrationPage.PNG)
![Customer Dashboard](https://github.com/Emad006/E-CommerceManagementSystem/blob/main/assets/front-end-screenshots/CustomerDashboard.PNG)
![Cart](https://github.com/Emad006/E-CommerceManagementSystem/blob/main/assets/front-end-screenshots/Cart.PNG)
![Payment](https://github.com/Emad006/E-CommerceManagementSystem/blob/main/assets/front-end-screenshots/PaymentFrame.PNG)
![Admin Dashboard](https://github.com/Emad006/E-CommerceManagementSystem/blob/main/assets/front-end-screenshots/AdminDashboard.PNG)
![Admin Add User](https://github.com/Emad006/E-CommerceManagementSystem/blob/main/assets/front-end-screenshots/AdminAddUser.PNG)
![Admin List Products](https://github.com/Emad006/E-CommerceManagementSystem/blob/main/assets/front-end-screenshots/AdminListProducts.PNG)
![Admin Update Product](https://github.com/Emad006/E-CommerceManagementSystem/blob/main/assets/front-end-screenshots/AdminUpdateProduct.PNG)

## License

This project is licensed under the MIT License - see the [LICENSE File](https://github.com/Emad006/E-CommerceManagementSystem/blob/main/LICENSE) for details.
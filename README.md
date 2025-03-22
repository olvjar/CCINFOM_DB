# Computer Repair Shop Management System

A Java-based management system for computer repair shops. This application helps manage customers, technicians, repair appointments, and inventory.

## Features

- **Customer Management**
  - Add/Edit/Delete customer records
  - View customer repair history
  - Manage customer devices

- **Technician Management**
  - Manage technician profiles
  - Track technician assignments
  - Monitor performance

- **Appointment Management**
  - Schedule repair appointments
  - Track repair status
  - Manage payments

- **Inventory Management**
  - Track parts and supplies
  - Monitor stock levels
  - Record usage

- **Reporting**
  - Repair History Report
  - Customer Engagement Report
  - Inventory Usage Report
  - Revenue Report

## Prerequisites

- Java 11 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Setup

1. Clone the repository:
```bash
git clone https://github.com/yourusername/computer-repair-shop.git
cd computer-repair-shop
```


2. Configure database connection in `src/main/java/util/Constants.java`:
```java
public static final String DB_URL = "jdbc:mysql://localhost:3306/computerrepairshop";
public static final String DB_USER = "your_username";
public static final String DB_PASSWORD = "your_password";
```

3. Build the project:
```bash
mvn clean install
```

## Running the Application

Using Maven:
```bash
mvn clean compile exec:java
```


## Project Structure

```
src/main/java/
│
├── controller/   # logic and data access
│   ├── CustomerController.java     # Customer operations
│   ├── DeviceController.java       # Device operations
│   └── TechnicianController.java   # Technician operations
│
├── model/        # Data models
│   ├── entity/   # Domain objects
│   │   ├── Customer.java
│   │   ├── Device.java
│   │   ├── Technician.java
│   │   └── Product.java
│   │
│   └── service/  #  services
│       └── CustomerService.java
│
├── util/         # Utilities
│   ├── Constants.java              # Application constants
│   ├── DatabaseConnection.java     # Database connectivity
│   └── DatabaseInitializer.java    # Database setup
│
└── view/         # User interface
    ├── CustomerView.java           # Customer portal
    ├── TechnicianView.java        # Technician portal
    ├── LandingView.java           # Landing portal
    │
    ├── dialog/   # Popup dialogs
    │   ├── LoginDialog.java                # Authentication
    │   └── DeviceManagementDialog.java     # Device operations
    │
    ├── management/ # Management interfaces
    │   ├── CustomerManagementFrame.java
    │   └── TechnicianManagementFrame.java
    │
    └── panel/    # Reusable UI components
        ├── ModuleButtonsPanel.java  # Navigation
        └── ReportsPanel.java        # Report display

src/main/resources/
└── dbComputerRepairShop.sql       # Database schema and initial data
```

### Key Components

1. **Controllers (`controller/`)**
   - Handle business logic and data access
   - Validate input data
   - Manage database operations
   - Coordinate between views and models

2. **Models (`model/`)**
   - `entity/`: Domain objects representing business entities
   - `service/`: Business service layer

3. **Views (`view/`)**
   - User interface components
   - Dialog windows
   - Management frames
   - Reusable panels

4. **Utilities (`util/`)**
   - Database connection management
   - Constants and configurations
   - Database initialization


## Database Schema

The application uses MySQL with the following main tables:
- customers
- technicians
- appointments
- devices
- inventory
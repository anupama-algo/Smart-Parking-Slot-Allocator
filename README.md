# Smart Parking Slot Allocator

A Java-based Smart Parking Slot Allocation and Management System designed to streamline vehicle entry and exit, automate category-based parking allocation, monitor slot availability, calculate parking fees, and generate occupancy reports. The application provides both graphical and console interfaces backed by a shared MySQL database.

## Features

- **Parking Area Management:** Manage multiple parking areas with numbered slots and supported vehicle categories.
- **Automated Vehicle Entry:** Register vehicles and automatically allocate compatible available parking slots.
- **Duplicate Entry Prevention:** Prevent vehicles from having multiple active parking sessions.
- **Capacity Validation:** Reject entry requests when no compatible slot is available in the selected parking area.
- **Vehicle Exit Management:** Record exit times, calculate parking fees, and release occupied slots.
- **Dashboard:** Monitor total, occupied, and available parking slots.
- **Parking Availability:** View available parking slots across different parking areas.
- **Reports:** View occupancy details, active vehicles by category, area-wise availability, and daily collection.
- **Dual Interfaces:** Access the parking system through a Java Swing graphical interface or a menu-driven console interface.
- **Shared Database:** Both interfaces use the same MySQL database and DAO operations to maintain consistent parking records.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Core application development |
| Java Swing | Desktop graphical user interface |
| MySQL 8 | Relational database management |
| JDBC | Database connectivity and operations |
| Maven | Build and dependency management |
| MySQL Connector/J | MySQL JDBC driver |

## Application Interfaces

The application provides two independent ways to interact with the parking management system.

### 1. Graphical User Interface (GUI)

The Java Swing-based desktop interface provides:

- Dashboard with parking statistics.
- Parking area and slot management.
- Vehicle entry and exit operations.
- Parking availability monitoring.
- Occupancy and collection reports.

### 2. Console Interface

The menu-driven terminal interface provides:

- Vehicle entry through vehicle number and category selection.
- Parking location selection using numbered options.
- Automatic allocation of compatible parking slots.
- Vehicle exit processing and fee calculation.
- Parking availability and report viewing.

Both interfaces connect to the same MySQL database through the application's DAO layer. Therefore, operations performed through one interface are reflected in the other.

## Project Architecture

The application follows a layered architecture to separate responsibilities and maintain organized code.

- **Model:** Represents parking areas, parking slots, vehicles, vehicle categories, and parking sessions.
- **DAO (Data Access Object):** Handles database operations using JDBC.
- **Service:** Contains parking allocation logic and demonstrates collection-based allocation concepts.
- **UI:** Provides the Java Swing graphical interface.
- **Console:** Implements the menu-driven terminal interface.
- **Database:** Manages MySQL connectivity, persistent records, and database operations.

The graphical and console interfaces use shared DAO classes for database operations.

## Java Concepts Demonstrated

The project incorporates the following Java programming concepts:

- **Object-Oriented Programming:** Classes, objects, encapsulation, constructors, and modular design.
- **Collections Framework:** HashMap and Queue for managing parking allocation data.
- **Synchronization:** Synchronized methods for controlled access to shared in-memory allocation data.
- **JDBC:** Database connectivity and execution of SQL operations.
- **Transactions:** Transaction management to maintain consistency during parking entry and exit operations.
- **Java Streams:** Processing and working with collection data.

**Implementation Note:** HashMap, Queue, and synchronization are demonstrated in the in-memory parking allocation service. The primary GUI and console parking workflows use JDBC-based DAO operations to maintain persistent records in MySQL.

## Parking Fee Structure

The parking fee is calculated based on the duration of the parking session.

| Duration | Parking Fee |
|---|---:|
| First hour | ₹20 |
| Each additional hour or part thereof | ₹10 |

Partial hours are rounded up to the next billable hour.

**Example:**
- Up to 1 hour: ₹20
- More than 1 hour and up to 2 hours: ₹30
- More than 2 hours and up to 3 hours: ₹40

## Prerequisites

Ensure the following software is installed:

- JDK 21 or later
- Apache Maven
- MySQL Server 8.0 or later
- MySQL Workbench (recommended)

## Setup and Installation

### 1. Clone the Repository

```bash
git clone https://github.com/anupama-algo/Smart-Parking-Slot-Allocator.git
cd Smart-Parking-Slot-Allocator
```

### 2. Create the Database

1. Open MySQL Workbench.
2. Open `database/Smart_Parking_Slot_Allocator.sql`.
3. Execute the SQL script to create the required database, tables, parking areas, and initial parking slots.

### 3. Configure Database Credentials

Copy the example configuration file:

```powershell
Copy-Item src/main/resources/db.properties.example src/main/resources/db.properties
```

Open `src/main/resources/db.properties` and enter your own MySQL username, password, and database connection details as required.

**Important:** The `db.properties` file contains private database credentials and is excluded from Git using `.gitignore`. Never upload your actual credentials to GitHub.

### 4. Build the Application

Navigate to the project directory and run:

```bash
mvn clean compile
```

### 5. Run the Application

**Launch the Graphical Interface:**

```bash
mvn exec:java
```

**Launch the Console Interface:**

```powershell
.\park.bat
```

Alternatively, the console interface can be launched directly using Maven:

```powershell
mvn org.codehaus.mojo:exec-maven-plugin:3.5.1:java "-Dexec.mainClass=com.parking.ConsoleMain"
```

Both interfaces require a running MySQL server and valid database configuration.

## Testing

The application has been tested through database integration checks, report testing, and functional testing of both interfaces.

The following scenarios have been verified:

- Successful vehicle entry and automatic slot allocation.
- Successful vehicle exit and slot release.
- Duplicate active vehicle entry prevention.
- Rejection when no compatible parking slot is available.
- Invalid vehicle exit handling.
- Parking fee calculation, including partial-hour billing.
- Parking availability updates after vehicle entry and exit.
- Occupancy and daily collection report generation.
- Consistency of parking records across the GUI and console interfaces.

The Maven compilation and application integration checks completed successfully.

## Database Design

The application uses four primary relational database tables:

| Table | Purpose |
|---|---|
| `parking_areas` | Stores parking area information. |
| `parking_slots` | Maintains individual parking slots, categories, and availability status. |
| `vehicles` | Stores registered vehicle details. |
| `parking_sessions` | Records vehicle entry, exit, parking duration, and fee information. |

Foreign key relationships maintain data integrity between parking areas, slots, vehicles, and parking sessions. Database constraints help prevent duplicate active vehicle entries and simultaneous allocation of the same slot.

## Project Structure

```text
Smart-Parking-Slot-Allocator/
│
├── database/
│   ├── Smart_Parking_Slot_Allocator.sql
│   └── schema.sql
│
├── docs/
│   ├── diagrams/
│   │   ├── Architecture_Diagram.md
│   │   ├── Class_Diagram.md
│   │   ├── ER_Diagram.md
│   │   └── Vehicle_Entry_Exit_Workflow.md
│   ├── screenshots/
│   ├── Java_Concept_Mapping.md
│   ├── Project_Overview.md
│   └── Testing_Evidence.md
│
├── src/
│   └── main/
│       ├── java/com/parking/
│       │   ├── console/
│       │   │   └── ConsoleApp.java
│       │   ├── dao/
│       │   ├── database/
│       │   ├── model/
│       │   ├── service/
│       │   ├── ui/
│       │   ├── ConsoleMain.java
│       │   └── Main.java
│       │
│       └── resources/
│           └── db.properties.example
│
├── .gitignore
├── gui.bat
├── park.bat
├── pom.xml
└── README.md
```

## Future Scope

The project can be extended with additional capabilities, such as:

- Online parking slot reservation.
- QR-based vehicle entry and exit.
- Payment gateway integration.
- Advanced parking analytics.
- Role-based access for administrators and parking operators.

These are potential future enhancements and are not part of the current implementation.

## Author

**Anupama Sethi**

Computer Science and Engineering – Artificial Intelligence and Machine Learning  
Chitkara University, Punjab

---

*Smart Parking Slot Allocator — A structured approach to efficient parking space allocation and management.*
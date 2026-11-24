# Smart Parking Slot Allocator

A Java-based desktop application designed to simplify parking management through automated vehicle-slot allocation, parking session tracking, fee calculation, and occupancy reporting.

## Features

- **Parking Area Management:** Create parking areas with numbered slots and supported vehicle categories.
- **Automated Vehicle Entry:** Register vehicles and allocate compatible available parking slots.
- **Duplicate Entry Prevention:** Prevent vehicles from having multiple active parking sessions.
- **Capacity Validation:** Reject entry requests when no compatible slot is available in the selected parking area.
- **Vehicle Exit Management:** Record exit times, calculate parking fees, and release occupied slots.
- **Dashboard:** Monitor total, occupied, and available parking slots.
- **Reports:** View available slots by area, active vehicles by category, occupancy, and daily collection.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Core application development |
| Java Swing | Desktop graphical user interface |
| MySQL 8 | Relational database |
| JDBC | Database connectivity |
| Maven | Build and dependency management |
| MySQL Connector/J | MySQL JDBC driver |

## Project Architecture

The application follows a layered structure:

- **Model:** Represents parking areas, slots, vehicles, and parking sessions.
- **DAO:** Handles database operations.
- **Service:** Contains parking allocation logic.
- **UI:** Provides the desktop graphical interface.
- **Database:** Handles database connectivity and integration checks.

## Java Concepts Demonstrated

- Object-Oriented Programming (OOP)
- Collections: HashMap and Queue
- Synchronization
- JDBC and SQL transactions
- Java Streams

**Implementation note:** HashMap, Queue, and synchronization are demonstrated in the in-memory parking allocation service. The Queue is not integrated into the JDBC-based desktop workflow.

## Parking Fee Structure

| Duration | Fee |
|---|---:|
| First hour | ₹20 |
| Each additional hour or part thereof | ₹10 |

Partial hours are rounded up to the next billable hour.

## Prerequisites

- JDK 21 or later
- Apache Maven
- MySQL Server 8.0 or later
- MySQL Workbench (recommended)

## Setup and Installation

### 1. Clone the repository

```bash
git clone https://github.com/anupama-algo/Smart-Parking-Slot-Allocator.git
cd Smart-Parking-Slot-Allocator
```

### 2. Create the database

Open `database/Smart_Parking_Slot_Allocator.sql` in MySQL Workbench.

Execute the database creation and table setup statements to create the required database and tables.

### 3. Configure database credentials

Copy the example configuration file:

```powershell
Copy-Item src/main/resources/db.properties.example src/main/resources/db.properties
```

Open `src/main/resources/db.properties` and enter your own MySQL username and password.

**Important:** Never commit `db.properties`, as it contains private database credentials.

### 4. Build the application

```bash
mvn clean compile
```

### 5. Run the application

```bash
mvn exec:java
```

The Smart Parking Slot Allocator desktop application will launch.

## Testing

The application has been verified through database integration tests and desktop UI testing.

Tested scenarios include:

- Successful vehicle entry and automatic slot allocation.
- Successful vehicle exit and slot release.
- Duplicate active vehicle entry prevention.
- Rejection when no compatible slot is available in the selected parking area.
- Invalid vehicle exit handling.
- Parking fee calculation, including partial-hour billing.
- Occupancy and daily collection report generation.

The database integration checks and report tests completed successfully.

## Database Design

The application uses four primary tables:

- `parking_areas`
- `parking_slots`
- `vehicles`
- `parking_sessions`

Foreign keys maintain relationships between parking areas, slots, vehicles, and parking sessions. Database constraints help prevent duplicate active vehicle entries and simultaneous allocation of the same slot.

## Project Structure

```text
Smart-Parking-Slot-Allocator/
├── database/
│   └── Smart_Parking_Slot_Allocator.sql
├── docs/
│   ├── diagrams/
│   └── screenshots/
├── project-report/
├── src/
│   └── main/
│       ├── java/com/parking/
│       │   ├── dao/
│       │   ├── database/
│       │   ├── model/
│       │   ├── service/
│       │   └── ui/
│       └── resources/
│           ├── db.properties.example
│           └── db.properties (local configuration; not committed)
├── tests/
├── .gitignore
├── pom.xml
└── README.md
```

## Author

**Anupama Sethi**

CSE – Artificial Intelligence and Machine Learning  
Chitkara University, Punjab

---

*Smart Parking Slot Allocator — Making parking management simpler, structured, and more efficient.*

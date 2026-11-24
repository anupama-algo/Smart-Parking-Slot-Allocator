# SMART PARKING SLOT ALLOCATOR
## Project Overview

**Project Code:** JAVA-P027  
**Technology:** Java 21, Java Swing, MySQL 8.0, JDBC  
**Build Tool:** Apache Maven

---

## 1. Problem Statement

Managing parking facilities manually can lead to inefficient slot allocation, inaccurate occupancy records, duplicate vehicle entries, and difficulties in calculating parking charges. Parking operators may also struggle to identify available spaces, manage different vehicle categories, and generate accurate reports of parking activity and daily collections.

The Smart Parking Slot Allocator addresses these challenges by providing a desktop-based parking management system that automates vehicle entry, parking slot allocation, vehicle exit, fee calculation, and report generation.

The system maintains parking information in a MySQL database and provides a graphical interface through which parking operators can manage parking areas, monitor slot availability, and track parking sessions.

## 2. Proposed Solution

The proposed system is a Java-based desktop application designed to simplify parking operations within a parking facility containing multiple parking areas.

It allows an operator to register parking areas and their slots, process vehicle entries, allocate compatible available slots, record vehicle exits, calculate parking fees, and view parking reports.

The application uses Java Swing for its graphical interface, JDBC for database connectivity, and MySQL for persistent storage. Database transactions help maintain consistency during vehicle entry and exit operations.

## 3. Project Objectives

The main objectives of the Smart Parking Slot Allocator are:

1. To develop a desktop application for efficient parking management.
2. To allow operators to create and manage multiple parking areas.
3. To organize parking slots according to supported vehicle categories.
4. To automatically allocate an available compatible slot when a vehicle enters.
5. To prevent duplicate active parking sessions for the same vehicle.
6. To reject vehicle entry when no suitable slot is available in the selected parking area.
7. To record vehicle exit details and calculate parking fees according to parking duration.
8. To release occupied slots after successful vehicle exit.
9. To provide occupancy, available-slot, vehicle-category, and daily collection reports.
10. To demonstrate Java programming concepts, database connectivity, and object-oriented software design.

## 4. Project Scope

### 4.1 Included in the Project

The project covers the following functionality:

- **Parking Area Management:** Create and view parking areas.
- **Parking Slot Management:** Create slots with unique slot numbers and supported vehicle categories.
- **Vehicle Entry:** Register vehicle details and allocate a suitable available slot.
- **Duplicate Prevention:** Prevent a vehicle from having multiple active parking sessions.
- **Capacity Validation:** Reject entry when the selected area has no compatible available slot.
- **Vehicle Exit:** Record exit time, calculate parking duration and fee, and release the occupied slot.
- **Dashboard:** Display parking occupancy and slot availability.
- **Reports:** Generate occupancy, available-slot, active vehicle-category, and daily collection reports.
- **Database Management:** Store parking areas, slots, vehicles, and parking sessions in MySQL.

### 4.2 Supported Vehicle Categories

The current implementation supports:

- CAR
- BIKE

### 4.3 Parking Fee Structure

The application follows this fee structure:

| Parking Duration | Fee |
|---|---:|
| First hour | ₹20 |
| Every additional hour or part thereof | ₹10 |

Partial additional hours are rounded up to the next billable hour.

## 5. Intended Users

The system is primarily intended for:

- **Parking Operators:** Process vehicle entry and exit operations.
- **Parking Administrators:** Manage parking areas and slots.
- **Management Personnel:** Monitor occupancy and review parking collection reports.

The application is designed as an operator-facing desktop system rather than a public online parking-booking platform.

## 6. Functional Requirements

| ID | Requirement |
|---|---|
| FR-01 | The system shall allow creation of parking areas. |
| FR-02 | The system shall allow creation of parking slots within parking areas. |
| FR-03 | The system shall support CAR and BIKE categories. |
| FR-04 | The system shall allocate a suitable available slot during vehicle entry. |
| FR-05 | The system shall prevent duplicate active vehicle entries. |
| FR-06 | The system shall reject entry when no compatible slot is available in the selected area. |
| FR-07 | The system shall record vehicle exit details. |
| FR-08 | The system shall calculate parking duration and applicable fees. |
| FR-09 | The system shall release the slot after a successful vehicle exit. |
| FR-10 | The system shall display parking occupancy and availability. |
| FR-11 | The system shall generate parking and collection reports. |

## 7. Technology Scope

| Technology | Purpose |
|---|---|
| Java 21 | Core application development and business logic |
| Java Swing | Desktop graphical user interface |
| JDBC | Connectivity between Java and MySQL |
| MySQL 8.0 | Persistent storage and relational database management |
| Apache Maven | Dependency and project build management |
| MySQL Connector/J | JDBC driver for MySQL connectivity |

## 8. Java Concepts Demonstrated

The project incorporates the following course concepts:

- **Object-Oriented Programming:** Classes, objects, encapsulation, constructors, and relationships between application components.
- **HashMap:** Maintains active-session information in the allocation service.
- **Queue:** Demonstrates waiting-vehicle management in the allocation service.
- **Synchronization:** Uses synchronized service methods to coordinate allocation operations.
- **JDBC:** Executes database operations through Java.
- **Java Streams:** Processes and filters collections of parking slots.
- **Exception Handling:** Handles invalid operations and database errors.
- **SQL Transactions:** Maintains consistency during vehicle entry and exit operations.

The HashMap, Queue, and synchronization concepts are demonstrated in the in-memory allocation service. The desktop application's main database-backed entry and exit workflow uses JDBC and transactional DAO operations.

## 9. Database Scope

The application uses a relational MySQL database named `smart_parking_db`, consisting of four main tables:

1. `parking_areas` — stores parking area information.
2. `parking_slots` — stores slot details, vehicle categories, and availability status.
3. `vehicles` — stores registered vehicle information.
4. `parking_sessions` — stores vehicle entry, exit, duration, and parking fee records.

Foreign keys and database constraints maintain relationships and prevent conflicting active parking records.

## 10. Project Limitations

The current implementation is limited to the following:

- The application operates as a local desktop system.
- Only CAR and BIKE vehicle categories are supported.
- Parking allocation takes place when a vehicle entry is processed; advance online reservations are not supported.
- Online payments and payment gateway integration are not included.
- Mobile application access and cloud-based multi-facility management are outside the current scope.
- Automated number-plate recognition is not included.
- The waiting Queue is demonstrated in the in-memory allocation service and is not integrated into the database-backed desktop workflow.

## 11. Expected Outcome

The expected outcome is a functional Java desktop application that simplifies parking operations through automated slot allocation, reliable vehicle entry and exit processing, parking fee calculation, database-backed record management, and useful operational reports.

The project also demonstrates the practical application of Java object-oriented programming, data structures, synchronization, JDBC, SQL transactions, and Java Streams in a real-world management problem.

---

**Project:** Smart Parking Slot Allocator  
**Project Code:** JAVA-P027

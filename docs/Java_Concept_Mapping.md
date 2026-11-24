# JAVA COURSE CONCEPT MAPPING
## Smart Parking Slot Allocator

This document maps the Java programming concepts used in the Smart Parking Slot Allocator to their corresponding source-code implementations.

---

## 1. Object-Oriented Programming (OOP)

**Implementation:** `model/`, `dao/`, `service/`, and `ui/` packages.

The project follows an object-oriented structure by separating application data, database operations, business logic, and the graphical interface into different classes.

Examples:

- `Vehicle.java` represents a registered vehicle.
- `ParkingSlot.java` represents an individual parking slot.
- `ParkingArea.java` represents a parking area containing slots.
- `ParkingSession.java` represents a vehicle's parking session.
- DAO classes encapsulate database operations.
- `ParkingAllocationService.java` contains parking allocation logic.

**Concepts demonstrated:** Classes, objects, constructors, encapsulation, methods, and relationships between objects.

## 2. HashMap

**Implementation files:**
- `model/ParkingArea.java`
- `service/ParkingAllocationService.java`

The project uses HashMap to store and retrieve objects using keys.

Examples:

- `ParkingArea.java` maintains parking slots in a `HashMap`.
- `ParkingAllocationService.java` maintains active parking sessions using a HashMap keyed by vehicle number.

**Purpose:** Provides key-based access to parking slots and active-session information.

## 3. Queue

**Implementation file:** `service/ParkingAllocationService.java`

The project uses the Java `Queue` interface with a `LinkedList` implementation to maintain a waiting list of vehicles when no suitable slot is available in the in-memory allocation service.

The `offer()` method adds a vehicle to the waiting queue.

**Purpose:** Demonstrates FIFO (First-In, First-Out) waiting-vehicle management.

**Implementation note:** This Queue is part of the in-memory allocation service and is not integrated into the database-backed desktop workflow.

## 4. Synchronization

**Implementation file:** `service/ParkingAllocationService.java`

The allocation service uses synchronized methods, including:

- `allocateSlot()`
- `releaseSlot()`
- `getWaitingQueueSize()`
- `getActiveSessionCount()`

Synchronization ensures that only one thread at a time can execute these synchronized methods on the same service instance.

**Purpose:** Helps protect shared in-memory allocation data from concurrent access.

**Implementation note:** This synchronization applies to the in-memory service. Database-backed entry and exit operations use database transactions and row locking.

## 5. JDBC (Java Database Connectivity)

**Implementation files:**
- `database/DBConnection.java`
- `dao/ParkingAreaDAO.java`
- `dao/ParkingSlotDAO.java`
- `dao/VehicleDAO.java`
- `dao/ParkingSessionDAO.java`
- `dao/ParkingReportDAO.java`

JDBC connects the Java application to the MySQL database.

The project uses:

- `DriverManager.getConnection()` to establish database connections.
- `Connection` to manage database interactions.
- `PreparedStatement` to execute parameterized SQL statements.
- `ResultSet` to retrieve query results.
- Try-with-resources to close database resources automatically.

**Purpose:** Enables the Java application to store, retrieve, and update parking information in MySQL.

## 6. Java Streams

**Implementation file:** `model/ParkingArea.java`

The `getAvailableSlots()` method uses the Stream API to process the collection of parking slots.

It uses:

- `stream()` to create a stream from the slot collection.
- `filter()` to select available slots.
- Collection processing to produce the required result.

**Purpose:** Demonstrates collection filtering and declarative data processing using Java Streams.

## 7. SQL Transactions

**Implementation file:** `dao/ParkingSessionDAO.java`

The vehicle entry and exit operations use database transactions.

The implementation uses:

- `setAutoCommit(false)` to disable automatic transaction commits.
- `commit()` to confirm successful operations.
- `rollback()` to undo changes when an operation fails.

During vehicle entry, the application registers or retrieves the vehicle, allocates a suitable slot, updates its status, and creates a parking session within a transaction.

During vehicle exit, it records exit details, calculates the fee, and releases the occupied slot within a transaction.

**Purpose:** Maintains consistency across related database operations.

## 8. Database Row Locking

**Implementation file:** `dao/ParkingSessionDAO.java`

The entry and exit SQL operations use `SELECT ... FOR UPDATE` when retrieving records that need to be protected during transactional updates.

**Purpose:** Helps prevent conflicting concurrent operations on selected database records.

## 9. Exception Handling

**Implementation areas:**
- `dao/`
- `database/`
- `service/`

The project handles database errors and invalid parking operations through exception handling.

Examples include:

- Handling SQL exceptions during database operations.
- Rejecting vehicle entry when no suitable slot is available.
- Handling attempts to exit a vehicle without an active parking session.
- Rolling back database transactions when operations fail.

**Purpose:** Prevents failed operations from being treated as successful and supports controlled error handling.

---

## 10. Course Concept Summary

| Course Concept | Implementation Class / Package |
|---|---|
| Object-Oriented Programming | model, dao, service, ui |
| HashMap | ParkingArea, ParkingAllocationService |
| Queue | ParkingAllocationService |
| Synchronization | ParkingAllocationService |
| JDBC | DBConnection and DAO classes |
| Java Streams | ParkingArea |
| SQL Transactions | ParkingSessionDAO |
| Database Row Locking | ParkingSessionDAO |
| Exception Handling | DAO, database, and service operations |

## 11. Conclusion

The Smart Parking Slot Allocator demonstrates the practical application of Java programming concepts in a database-driven desktop application.

It combines object-oriented design, collection frameworks, synchronization, JDBC, Java Streams, exception handling, and SQL transaction management to implement parking operations in a structured manner.

The project connects classroom concepts with a practical software development use case.

---

**Project:** Smart Parking Slot Allocator  
**Project Code:** JAVA-P027

# Smart Parking Slot Allocator — System Architecture

```mermaid
flowchart TB
    U["User"]

    subgraph UI["Presentation Layer — Java Swing"]
        MF["MainFrame"]
        DA["DashboardPanel"]
        PA["ParkingAreaPanel"]
        VE["VehicleEntryPanel"]
        VX["VehicleExitPanel"]
        RP["ReportsPanel"]
    end

    subgraph SERVICE["In-Memory Service Layer"]
        PAS["ParkingAllocationService<br/>HashMap • Queue • Synchronization"]
    end

    subgraph DAO["Data Access Layer"]
        A["ParkingAreaDAO"]
        S["ParkingSlotDAO"]
        V["VehicleDAO"]
        PS["ParkingSessionDAO"]
        R["ParkingReportDAO"]
    end

    subgraph MODEL["Model Layer"]
        M["ParkingArea • ParkingSlot<br/>Vehicle • ParkingSession"]
        E["SlotStatus • VehicleCategory"]
    end

    subgraph DB["Database Layer"]
        JDBC["DBConnection — JDBC"]
        MYSQL[("MySQL 8.0<br/>smart_parking_db")]
        TEST["Database Integration Tests"]
    end

    U --> MF
    MF --> DA
    MF --> PA
    MF --> VE
    MF --> VX
    MF --> RP

    VE --> PS
    VX --> PS
    PA --> A
    PA --> S
    DA --> R
    RP --> R

    PAS -.-> M

    A --> M
    S --> M
    V --> M
    PS --> M
    R --> M

    A --> JDBC
    S --> JDBC
    V --> JDBC
    PS --> JDBC
    R --> JDBC

    JDBC --> MYSQL
    TEST --> JDBC
```

**Architecture note:** `ParkingAllocationService` is a separate in-memory demonstration of allocation concepts. The desktop application's database-backed operations use the DAO layer and JDBC.

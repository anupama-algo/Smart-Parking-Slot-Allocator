# Smart Parking Slot Allocator — Class Diagram

The following class diagram represents the six model classes and enumerations implemented in the project.

```mermaid
classDiagram
    class ParkingArea {
        -long areaId
        -String areaCode
        -String areaName
        -String location
        -Map~Integer, ParkingSlot~ slots
        +ParkingArea(String, String, String)
        +addSlot(ParkingSlot) void
        +getSlot(int) ParkingSlot
        +getAllSlots() Collection~ParkingSlot~
        +getTotalSlots() long
        +getAvailableSlots() long
        +getAreaId() long
        +setAreaId(long) void
        +getAreaCode() String
        +getAreaName() String
        +getLocation() String
        +toString() String
    }

    class ParkingSlot {
        -long slotId
        -long areaId
        -int slotNumber
        -VehicleCategory vehicleCategory
        -SlotStatus status
        +ParkingSlot(long, int, VehicleCategory)
        +getSlotId() long
        +setSlotId(long) void
        +getAreaId() long
        +getSlotNumber() int
        +getVehicleCategory() VehicleCategory
        +getStatus() SlotStatus
        +setStatus(SlotStatus) void
        +isAvailable() boolean
        +toString() String
    }

    class Vehicle {
        -long vehicleId
        -String vehicleNumber
        -VehicleCategory category
        +Vehicle(String, VehicleCategory)
        +getVehicleId() long
        +setVehicleId(long) void
        +getVehicleNumber() String
        +getCategory() VehicleCategory
        +toString() String
    }

    class ParkingSession {
        -long sessionId
        -Vehicle vehicle
        -ParkingSlot parkingSlot
        -LocalDateTime entryTime
        -LocalDateTime exitTime
        -long durationMinutes
        -double parkingFee
        -boolean active
        -static final double FIRST_HOUR_RATE = 20.0
        -static final double ADDITIONAL_HOUR_RATE = 10.0
        +ParkingSession(Vehicle, ParkingSlot)
        +completeSession() void
        +getSessionId() long
        +setSessionId(long) void
        +getVehicle() Vehicle
        +getParkingSlot() ParkingSlot
        +getEntryTime() LocalDateTime
        +getExitTime() LocalDateTime
        +getDurationMinutes() long
        +getParkingFee() double
        +isActive() boolean
    }

    class SlotStatus {
        <<enumeration>>
        AVAILABLE
        OCCUPIED
    }

    class VehicleCategory {
        <<enumeration>>
        CAR
        BIKE
    }

    ParkingArea "1" o-- "*" ParkingSlot : manages
    ParkingSession "many" --> "1" Vehicle : records
    ParkingSession "many" --> "1" ParkingSlot : occupies
    ParkingSlot --> SlotStatus : has status
    ParkingSlot --> VehicleCategory : supports
    Vehicle --> VehicleCategory : belongs to
```

## Class Relationship Notes

1. **ParkingArea and ParkingSlot:** A parking area maintains a collection of parking slots using a HashMap.
2. **ParkingSession and Vehicle:** Each parking session is associated with one vehicle.
3. **ParkingSession and ParkingSlot:** Each parking session occupies one parking slot.
4. **ParkingSlot and SlotStatus:** Each slot maintains its availability status.
5. **ParkingSlot and VehicleCategory:** Each slot supports a specified vehicle category.
6. **Vehicle and VehicleCategory:** Each vehicle belongs to a supported vehicle category.

## Implementation Notes

- `ParkingArea` uses a HashMap to maintain its parking slots.
- `ParkingSession` validates vehicle-slot category compatibility before creating a session.
- `ParkingSession` calculates parking duration and fees when a session is completed.
- Fee constants are declared as `static final`.
- `SlotStatus` and `VehicleCategory` are Java enumerations.

**Project:** Smart Parking Slot Allocator  
**Project Code:** JAVA-P027

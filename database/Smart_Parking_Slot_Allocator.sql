-- ============================================================
-- PROJECT      : SMART PARKING SLOT ALLOCATOR
-- DATABASE     : smart_parking_db
-- TECHNOLOGY   : MySQL 8.0
-- PURPOSE      : Database creation, structure and verification
-- ============================================================


-- ============================================================
-- SECTION 1: DATABASE CREATION AND SELECTION
-- ============================================================

CREATE DATABASE IF NOT EXISTS smart_parking_db;

USE smart_parking_db;

-- Verify the selected database.
SELECT DATABASE();


-- ============================================================
-- SECTION 2: TABLE CREATION
-- ============================================================


-- 2.1 PARKING AREAS
-- Stores information about individual parking areas.

CREATE TABLE IF NOT EXISTS parking_areas (
    area_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    area_code VARCHAR(20) NOT NULL UNIQUE,
    area_name VARCHAR(100) NOT NULL,
    location VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;


-- 2.2 PARKING SLOTS
-- Stores parking slot numbers, categories and availability.

CREATE TABLE IF NOT EXISTS parking_slots (
    slot_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    area_id BIGINT NOT NULL,
    slot_number INT NOT NULL,
    vehicle_category ENUM('CAR', 'BIKE') NOT NULL,
    slot_status ENUM('AVAILABLE', 'OCCUPIED')
        NOT NULL DEFAULT 'AVAILABLE',

    CONSTRAINT fk_slot_area
        FOREIGN KEY (area_id)
        REFERENCES parking_areas(area_id),

    CONSTRAINT uq_area_slot
        UNIQUE (area_id, slot_number)
) ENGINE=InnoDB;


-- 2.3 VEHICLES
-- Stores registered vehicle information.

CREATE TABLE IF NOT EXISTS vehicles (
    vehicle_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_number VARCHAR(20) NOT NULL UNIQUE,
    vehicle_category ENUM('CAR', 'BIKE') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;


-- 2.4 PARKING SESSIONS
-- Stores vehicle entry, exit, duration and parking fee details.

CREATE TABLE IF NOT EXISTS parking_sessions (
    session_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id BIGINT NOT NULL,
    slot_id BIGINT NOT NULL,
    entry_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    exit_time DATETIME NULL,
    duration_minutes BIGINT NULL,
    parking_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    -- Prevents multiple active sessions for the same vehicle.
    active_vehicle_id BIGINT
        GENERATED ALWAYS AS (
            CASE
                WHEN exit_time IS NULL THEN vehicle_id
                ELSE NULL
            END
        ) STORED,

    -- Prevents multiple active assignments to the same slot.
    active_slot_id BIGINT
        GENERATED ALWAYS AS (
            CASE
                WHEN exit_time IS NULL THEN slot_id
                ELSE NULL
            END
        ) STORED,

    CONSTRAINT fk_session_vehicle
        FOREIGN KEY (vehicle_id)
        REFERENCES vehicles(vehicle_id),

    CONSTRAINT fk_session_slot
        FOREIGN KEY (slot_id)
        REFERENCES parking_slots(slot_id),

    CONSTRAINT uq_active_vehicle
        UNIQUE (active_vehicle_id),

    CONSTRAINT uq_active_slot
        UNIQUE (active_slot_id),

    INDEX idx_session_entry_time (entry_time),
    INDEX idx_session_exit_time (exit_time)
) ENGINE=InnoDB;


-- ============================================================
-- SECTION 3: DATABASE STRUCTURE VERIFICATION
-- ============================================================

-- Display all database tables.

SHOW TABLES;

-- Inspect table structures.

DESCRIBE parking_areas;
DESCRIBE parking_slots;
DESCRIBE vehicles;
DESCRIBE parking_sessions;


-- ============================================================
-- SECTION 4: PARKING AREA AND SLOT VERIFICATION
-- ============================================================

-- 4.1 Overall parking occupancy.

SELECT
    COUNT(*) AS total_slots,
    SUM(slot_status = 'OCCUPIED') AS occupied_slots,
    SUM(slot_status = 'AVAILABLE') AS available_slots
FROM parking_slots;


-- 4.2 Parking slot count for each area.

SELECT
    pa.area_name,
    COUNT(ps.slot_id) AS total_slots
FROM parking_areas pa
LEFT JOIN parking_slots ps
    ON pa.area_id = ps.area_id
GROUP BY pa.area_id, pa.area_name;


-- 4.3 Complete parking slot inventory.

SELECT
    pa.area_name,
    ps.slot_id,
    ps.slot_number,
    ps.vehicle_category,
    ps.slot_status
FROM parking_slots ps
JOIN parking_areas pa
    ON ps.area_id = pa.area_id
ORDER BY pa.area_id, ps.slot_number;


-- ============================================================
-- SECTION 5: VEHICLE AND PARKING SESSION VERIFICATION
-- ============================================================

-- 5.1 Check active sessions for a particular vehicle.

SELECT
    v.vehicle_number,
    COUNT(ps.session_id) AS active_sessions
FROM parking_sessions ps
JOIN vehicles v
    ON ps.vehicle_id = v.vehicle_id
WHERE v.vehicle_number = 'PB10AB1234'
  AND ps.exit_time IS NULL
GROUP BY v.vehicle_number;


-- 5.2 Display recent parking session records.

SELECT
    v.vehicle_number,
    ps.entry_time,
    ps.exit_time,
    ps.duration_minutes,
    ps.parking_fee
FROM parking_sessions ps
JOIN vehicles v
    ON ps.vehicle_id = v.vehicle_id
ORDER BY ps.session_id DESC
LIMIT 10;


-- ============================================================
-- SECTION 6: PARKING FEE VERIFICATION
-- ============================================================

-- Display the parking session and fee details
-- for the BIKE billing test.

SELECT
    v.vehicle_number,
    ps.entry_time,
    ps.exit_time,
    ps.duration_minutes,
    ps.parking_fee
FROM parking_sessions ps
JOIN vehicles v
    ON ps.vehicle_id = v.vehicle_id
WHERE v.vehicle_number = 'PB10BIKE01'
ORDER BY ps.session_id DESC;


-- ============================================================
-- SECTION 7: TESTING OPERATION
-- IMPORTANT: THIS QUERY MODIFIES DATABASE RECORDS.
-- It was used to simulate a 61-minute parking duration.
-- DO NOT EXECUTE AGAIN as part of normal verification.
-- ============================================================

-- UPDATE parking_sessions ps
-- JOIN vehicles v
--     ON ps.vehicle_id = v.vehicle_id
-- SET ps.entry_time = NOW() - INTERVAL 61 MINUTE
-- WHERE v.vehicle_number = 'PB10BIKE01'
--   AND ps.exit_time IS NULL;


-- ============================================================
-- END OF SMART PARKING SLOT ALLOCATOR SQL SCRIPT
-- ============================================================

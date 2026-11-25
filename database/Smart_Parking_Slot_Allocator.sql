-- ============================================================
-- PROJECT      : SMART PARKING SLOT ALLOCATOR
-- DATABASE     : smart_parking_db
-- TECHNOLOGY   : MySQL 8.0
-- PURPOSE      : Database creation, table structure,
--                constraints and verification
-- ============================================================


-- ============================================================
-- SECTION 1: DATABASE CREATION AND SELECTION
-- ============================================================

CREATE DATABASE IF NOT EXISTS smart_parking_db;

USE smart_parking_db;

-- Verify selected database
SELECT DATABASE();


-- ============================================================
-- SECTION 2: TABLE CREATION
-- ============================================================


-- 2.1 PARKING AREAS
-- Stores individual parking area information.

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
-- Stores vehicle entry, exit, duration and fee details.

CREATE TABLE IF NOT EXISTS parking_sessions (
    session_id BIGINT AUTO_INCREMENT PRIMARY KEY,

    vehicle_id BIGINT NOT NULL,
    slot_id BIGINT NOT NULL,

    entry_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    exit_time DATETIME NULL,

    duration_minutes BIGINT NULL,
    parking_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,

    -- Prevent multiple active sessions for one vehicle.
    active_vehicle_id BIGINT
        GENERATED ALWAYS AS (
            CASE
                WHEN exit_time IS NULL THEN vehicle_id
                ELSE NULL
            END
        ) STORED,

    -- Prevent multiple active assignments to one slot.
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

-- Display all tables.

SHOW TABLES;

-- Inspect individual table structures.

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

    SUM(slot_status = 'OCCUPIED')
        AS occupied_slots,

    SUM(slot_status = 'AVAILABLE')
        AS available_slots

FROM parking_slots;


-- 4.2 Parking slot count for each area.

SELECT
    pa.area_name,
    COUNT(ps.slot_id) AS total_slots

FROM parking_areas pa

LEFT JOIN parking_slots ps
    ON pa.area_id = ps.area_id

GROUP BY
    pa.area_id,
    pa.area_name;


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

ORDER BY
    pa.area_id,
    ps.slot_number;


-- 4.4 Parking availability by area and vehicle category.

SELECT
    pa.area_name,
    s.vehicle_category,

    COUNT(*) AS total_slots,

    SUM(
        CASE
            WHEN s.slot_status = 'AVAILABLE' THEN 1
            ELSE 0
        END
    ) AS available_slots

FROM parking_areas pa

JOIN parking_slots s
    ON pa.area_id = s.area_id

GROUP BY
    pa.area_id,
    pa.area_name,
    s.vehicle_category

ORDER BY
    pa.area_id,
    s.vehicle_category;


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

WHERE
    v.vehicle_number = 'PB10AB1234'
    AND ps.exit_time IS NULL

GROUP BY
    v.vehicle_number;


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

ORDER BY
    ps.session_id DESC

LIMIT 10;


-- 5.3 Display all currently active parking sessions.

SELECT
    ps.session_id,
    v.vehicle_number,
    ps.slot_id,
    ps.entry_time,
    pa.area_name

FROM parking_sessions ps

JOIN vehicles v
    ON ps.vehicle_id = v.vehicle_id

JOIN parking_slots s
    ON ps.slot_id = s.slot_id

JOIN parking_areas pa
    ON s.area_id = pa.area_id

WHERE ps.exit_time IS NULL;


-- ============================================================
-- SECTION 6: PARKING FEE VERIFICATION
-- ============================================================

-- Display the BIKE billing test session details.

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

ORDER BY
    ps.session_id DESC;


-- ============================================================
-- SECTION 7: PARKING AREA HISTORY AND REPORTS
-- ============================================================


-- 7.1 Display parking areas.

SELECT *
FROM parking_areas;


-- 7.2 Display parking slots.

SELECT *
FROM parking_slots;


-- 7.3 Display parking session history for area ID 2.

SELECT
    ps.session_id,
    v.vehicle_number,
    ps.slot_id,
    ps.entry_time,
    ps.exit_time,
    ps.parking_fee

FROM parking_sessions ps

JOIN vehicles v
    ON ps.vehicle_id = v.vehicle_id

JOIN parking_slots s
    ON ps.slot_id = s.slot_id

WHERE s.area_id = 2

ORDER BY
    ps.entry_time;


-- 7.4 Summary report for area ID 2.

SELECT
    pa.area_id,
    pa.area_code,
    pa.area_name,

    COUNT(DISTINCT s.slot_id) AS total_slots,

    COUNT(DISTINCT ps.session_id) AS total_sessions

FROM parking_areas pa

LEFT JOIN parking_slots s
    ON pa.area_id = s.area_id

LEFT JOIN parking_sessions ps
    ON s.slot_id = ps.slot_id

WHERE pa.area_id = 2

GROUP BY
    pa.area_id,
    pa.area_code,
    pa.area_name;


-- ============================================================
-- SECTION 8: FOREIGN KEY VERIFICATION
-- ============================================================

SELECT
    TABLE_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME

FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE

WHERE
    TABLE_SCHEMA = 'smart_parking_db'
    AND REFERENCED_TABLE_NAME IS NOT NULL

ORDER BY
    TABLE_NAME,
    COLUMN_NAME;


-- ============================================================
-- SECTION 9: FINAL DATABASE VERIFICATION
-- ============================================================

-- Verify selected database.

SELECT DATABASE();


-- Display final parking area information.

SELECT
    area_id,
    area_code,
    area_name,
    location

FROM parking_areas

ORDER BY
    area_id;


-- ============================================================
-- SECTION 10: OPTIONAL TESTING OPERATION
-- IMPORTANT: DO NOT UNCOMMENT OR EXECUTE DURING NORMAL
-- DATABASE SETUP OR VERIFICATION.
-- This operation changes an existing parking session.
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


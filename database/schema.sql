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


package com.parking.dao;

import com.parking.database.DBConnection;
import com.parking.model.Vehicle;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;

public class ParkingSessionDAO {

    private static final BigDecimal FIRST_HOUR_RATE =
            new BigDecimal("20.00");

    private static final BigDecimal ADDITIONAL_HOUR_RATE =
            new BigDecimal("10.00");

    // Register vehicle and allocate a suitable parking slot.
    public long recordEntry(Vehicle vehicle, long areaId)
            throws SQLException {

        if (vehicle == null || areaId <= 0) {
            throw new IllegalArgumentException(
                    "Valid vehicle and parking area are required."
            );
        }

        String findSlotSql = """
                SELECT slot_id
                FROM parking_slots
                WHERE area_id = ?
                  AND vehicle_category = ?
                  AND slot_status = 'AVAILABLE'
                ORDER BY slot_number
                LIMIT 1
                FOR UPDATE
                """;

        String insertSessionSql = """
                INSERT INTO parking_sessions (vehicle_id, slot_id)
                VALUES (?, ?)
                """;

        String updateSlotSql = """
                UPDATE parking_slots
                SET slot_status = 'OCCUPIED'
                WHERE slot_id = ?
                  AND slot_status = 'AVAILABLE'
                """;

        try (Connection connection = DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                long vehicleId = saveVehicle(connection, vehicle);

                long slotId;

                try (PreparedStatement statement =
                             connection.prepareStatement(findSlotSql)) {

                    statement.setLong(1, areaId);
                    statement.setString(
                            2, vehicle.getCategory().name()
                    );

                    try (ResultSet result = statement.executeQuery()) {

                        if (!result.next()) {
                            throw new SQLException(
                                    "No suitable available parking slot."
                            );
                        }

                        slotId = result.getLong("slot_id");
                    }
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(updateSlotSql)) {

                    statement.setLong(1, slotId);

                    if (statement.executeUpdate() != 1) {
                        throw new SQLException(
                                "Parking slot is no longer available."
                        );
                    }
                }

                long sessionId;

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     insertSessionSql,
                                     Statement.RETURN_GENERATED_KEYS)) {

                    statement.setLong(1, vehicleId);
                    statement.setLong(2, slotId);

                    statement.executeUpdate();

                    try (ResultSet keys = statement.getGeneratedKeys()) {

                        if (!keys.next()) {
                            throw new SQLException(
                                    "Could not create parking session."
                            );
                        }

                        sessionId = keys.getLong(1);
                    }
                }

                connection.commit();

                vehicle.setVehicleId(vehicleId);

                return sessionId;

            } catch (SQLIntegrityConstraintViolationException e) {

                rollback(connection, e);

                throw new SQLException(
                        "Vehicle already has an active session, "
                        + "or a database constraint was violated.",
                        e
                );

            } catch (SQLException | RuntimeException e) {

                rollback(connection, e);
                throw e;
            }
        }
    }

    // Register a vehicle or retrieve its existing database ID.
    private long saveVehicle(
            Connection connection, Vehicle vehicle)
            throws SQLException {

        String sql = """
                INSERT INTO vehicles (vehicle_number, vehicle_category)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE
                vehicle_id = LAST_INSERT_ID(vehicle_id)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, vehicle.getVehicleNumber());
            statement.setString(2, vehicle.getCategory().name());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {

                if (!keys.next()) {
                    throw new SQLException(
                            "Could not register or retrieve vehicle."
                    );
                }

                long vehicleId = keys.getLong(1);

                String checkSql = """
                        SELECT vehicle_category
                        FROM vehicles
                        WHERE vehicle_id = ?
                        """;

                try (PreparedStatement check =
                             connection.prepareStatement(checkSql)) {

                    check.setLong(1, vehicleId);

                    try (ResultSet result = check.executeQuery()) {

                        if (!result.next() ||
                                !vehicle.getCategory().name().equals(
                                        result.getString("vehicle_category"))) {

                            throw new SQLException(
                                    "Vehicle number is already registered "
                                    + "with another category."
                            );
                        }
                    }
                }

                return vehicleId;
            }
        }
    }

    // Complete an active parking session and release its slot.
    public BigDecimal recordExit(String vehicleNumber)
            throws SQLException {

        if (vehicleNumber == null || vehicleNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Vehicle number is required."
            );
        }

        String normalizedNumber = vehicleNumber.trim()
                .toUpperCase()
                .replaceAll("\\s+", "");

        String findSessionSql = """
                SELECT ps.session_id, ps.slot_id, ps.entry_time
                FROM parking_sessions ps
                JOIN vehicles v
                    ON ps.vehicle_id = v.vehicle_id
                WHERE v.vehicle_number = ?
                  AND ps.exit_time IS NULL
                FOR UPDATE
                """;

        String updateSessionSql = """
                UPDATE parking_sessions
                SET exit_time = ?,
                    duration_minutes = ?,
                    parking_fee = ?
                WHERE session_id = ?
                  AND exit_time IS NULL
                """;

        String releaseSlotSql = """
                UPDATE parking_slots
                SET slot_status = 'AVAILABLE'
                WHERE slot_id = ?
                  AND slot_status = 'OCCUPIED'
                """;

        try (Connection connection = DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                long sessionId;
                long slotId;
                LocalDateTime entryTime;

                try (PreparedStatement statement =
                             connection.prepareStatement(findSessionSql)) {

                    statement.setString(1, normalizedNumber);

                    try (ResultSet result = statement.executeQuery()) {

                        if (!result.next()) {
                            throw new SQLException(
                                    "No active parking session found "
                                    + "for this vehicle."
                            );
                        }

                        sessionId = result.getLong("session_id");
                        slotId = result.getLong("slot_id");

                        entryTime = result.getTimestamp("entry_time")
                                .toLocalDateTime();
                    }
                }

                LocalDateTime exitTime = LocalDateTime.now();

                long elapsedSeconds = Math.max(
                        0,
                        Duration.between(entryTime, exitTime).getSeconds()
                );

                long durationMinutes = Math.max(
                        1, (elapsedSeconds + 59) / 60
                );

                long billedHours = Math.max(
                        1, (durationMinutes + 59) / 60
                );

                BigDecimal fee = FIRST_HOUR_RATE.add(
                        ADDITIONAL_HOUR_RATE.multiply(
                                BigDecimal.valueOf(billedHours - 1)
                        )
                ).setScale(2, RoundingMode.HALF_UP);

                try (PreparedStatement statement =
                             connection.prepareStatement(updateSessionSql)) {

                    statement.setTimestamp(
                            1, Timestamp.valueOf(exitTime)
                    );

                    statement.setLong(2, durationMinutes);
                    statement.setBigDecimal(3, fee);
                    statement.setLong(4, sessionId);

                    if (statement.executeUpdate() != 1) {
                        throw new SQLException(
                                "Could not complete parking session."
                        );
                    }
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(releaseSlotSql)) {

                    statement.setLong(1, slotId);

                    if (statement.executeUpdate() != 1) {
                        throw new SQLException(
                                "Could not release the parking slot."
                        );
                    }
                }

                connection.commit();

                return fee;

            } catch (SQLException | RuntimeException e) {

                rollback(connection, e);
                throw e;
            }
        }
    }

    // Safely roll back a failed transaction.
    private void rollback(Connection connection, Throwable originalError) {

        try {
            connection.rollback();
        } catch (SQLException rollbackError) {
            originalError.addSuppressed(rollbackError);
        }
    }
}

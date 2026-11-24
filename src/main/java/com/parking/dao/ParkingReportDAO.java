
package com.parking.dao;

import com.parking.database.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ParkingReportDAO {

    // 1. Overall parking occupancy report.
    public Map<String, Long> getOccupancyReport()
            throws SQLException {

        Map<String, Long> report = new LinkedHashMap<>();

        String sql = """
                SELECT
                    COUNT(*) AS total_slots,
                    COALESCE(SUM(
                        CASE WHEN slot_status = 'OCCUPIED'
                        THEN 1 ELSE 0 END
                    ), 0) AS occupied_slots,
                    COALESCE(SUM(
                        CASE WHEN slot_status = 'AVAILABLE'
                        THEN 1 ELSE 0 END
                    ), 0) AS available_slots
                FROM parking_slots
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            if (result.next()) {
                report.put("Total Slots",
                        result.getLong("total_slots"));

                report.put("Occupied Slots",
                        result.getLong("occupied_slots"));

                report.put("Available Slots",
                        result.getLong("available_slots"));
            }
        }

        return report;
    }

    // 2. Available slots in each parking area.
    public Map<String, Long> getAvailableSlotsByArea()
            throws SQLException {

        Map<String, Long> report = new LinkedHashMap<>();

        String sql = """
                SELECT
                    pa.area_name,
                    COUNT(ps.slot_id) AS available_slots
                FROM parking_areas pa
                LEFT JOIN parking_slots ps
                    ON pa.area_id = ps.area_id
                    AND ps.slot_status = 'AVAILABLE'
                GROUP BY pa.area_id, pa.area_name
                ORDER BY pa.area_name
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                report.put(
                        result.getString("area_name"),
                        result.getLong("available_slots")
                );
            }
        }

        return report;
    }

    // 3. Currently parked vehicles by category.
    public Map<String, Long> getActiveVehiclesByCategory()
            throws SQLException {

        Map<String, Long> report = new LinkedHashMap<>();

        String sql = """
                SELECT
                    v.vehicle_category,
                    COUNT(*) AS active_vehicles
                FROM parking_sessions ps
                JOIN vehicles v
                    ON ps.vehicle_id = v.vehicle_id
                WHERE ps.exit_time IS NULL
                GROUP BY v.vehicle_category
                ORDER BY v.vehicle_category
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                report.put(
                        result.getString("vehicle_category"),
                        result.getLong("active_vehicles")
                );
            }
        }

        return report;
    }

    // 4. Parking collection for a selected date.
    public BigDecimal getDailyCollection(LocalDate date)
            throws SQLException {

        String sql = """
                SELECT COALESCE(SUM(parking_fee), 0.00)
                    AS daily_collection
                FROM parking_sessions
                WHERE exit_time >= ?
                  AND exit_time < ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setTimestamp(
                    1,
                    java.sql.Timestamp.valueOf(date.atStartOfDay())
            );

            statement.setTimestamp(
                    2,
                    java.sql.Timestamp.valueOf(
                            date.plusDays(1).atStartOfDay()
                    )
            );

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getBigDecimal("daily_collection");
                }
            }
        }

        return BigDecimal.ZERO;
    }

    // 5. Detailed occupancy report for each parking area.
    public List<Map<String, Object>> getAreaWiseOccupancyReport()
            throws SQLException {

        List<Map<String, Object>> report = new ArrayList<>();

        String sql = """
                SELECT
                    pa.area_id,
                    pa.area_code,
                    pa.area_name,
                    pa.location,

                    COUNT(ps.slot_id) AS total_slots,

                    COALESCE(SUM(
                        CASE
                            WHEN ps.slot_status = 'OCCUPIED'
                            THEN 1 ELSE 0
                        END
                    ), 0) AS occupied_slots,

                    COALESCE(SUM(
                        CASE
                            WHEN ps.slot_status = 'AVAILABLE'
                            THEN 1 ELSE 0
                        END
                    ), 0) AS available_slots,

                    COALESCE(SUM(
                        CASE
                            WHEN ps.vehicle_category = 'CAR'
                            THEN 1 ELSE 0
                        END
                    ), 0) AS total_cars,

                    COALESCE(SUM(
                        CASE
                            WHEN ps.vehicle_category = 'CAR'
                            AND ps.slot_status = 'OCCUPIED'
                            THEN 1 ELSE 0
                        END
                    ), 0) AS occupied_cars,

                    COALESCE(SUM(
                        CASE
                            WHEN ps.vehicle_category = 'BIKE'
                            THEN 1 ELSE 0
                        END
                    ), 0) AS total_bikes,

                    COALESCE(SUM(
                        CASE
                            WHEN ps.vehicle_category = 'BIKE'
                            AND ps.slot_status = 'OCCUPIED'
                            THEN 1 ELSE 0
                        END
                    ), 0) AS occupied_bikes

                FROM parking_areas pa

                LEFT JOIN parking_slots ps
                    ON pa.area_id = ps.area_id

                GROUP BY
                    pa.area_id,
                    pa.area_code,
                    pa.area_name,
                    pa.location

                ORDER BY pa.area_id
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                Map<String, Object> area = new LinkedHashMap<>();

                area.put("areaId", result.getLong("area_id"));
                area.put("areaCode", result.getString("area_code"));
                area.put("areaName", result.getString("area_name"));
                area.put("location", result.getString("location"));

                area.put("totalSlots",
                        result.getLong("total_slots"));

                area.put("occupiedSlots",
                        result.getLong("occupied_slots"));

                area.put("availableSlots",
                        result.getLong("available_slots"));

                area.put("totalCars",
                        result.getLong("total_cars"));

                area.put("occupiedCars",
                        result.getLong("occupied_cars"));

                area.put("totalBikes",
                        result.getLong("total_bikes"));

                area.put("occupiedBikes",
                        result.getLong("occupied_bikes"));

                report.add(area);
            }
        }

        return report;
    }
}

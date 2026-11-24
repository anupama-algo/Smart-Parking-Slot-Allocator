package com.parking.dao;

import com.parking.database.DBConnection;
import com.parking.model.Vehicle;
import com.parking.model.VehicleCategory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class VehicleDAO {

    public long saveVehicle(Vehicle vehicle) throws SQLException {

        String insertSql = """
                INSERT INTO vehicles (vehicle_number, vehicle_category)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE
                vehicle_id = LAST_INSERT_ID(vehicle_id)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     insertSql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, vehicle.getVehicleNumber());
            statement.setString(2, vehicle.getCategory().name());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    long vehicleId = keys.getLong(1);
                    vehicle.setVehicleId(vehicleId);
                    return vehicleId;
                }
            }
        }

        throw new SQLException("Could not save or retrieve vehicle.");
    }

    public Vehicle findByNumber(String vehicleNumber)
            throws SQLException {

        String sql = """
                SELECT vehicle_id, vehicle_number, vehicle_category
                FROM vehicles
                WHERE vehicle_number = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1, vehicleNumber.trim()
                            .toUpperCase()
                            .replaceAll("\\s+", "")
            );

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    Vehicle vehicle = new Vehicle(
                            result.getString("vehicle_number"),
                            VehicleCategory.valueOf(
                                    result.getString("vehicle_category"))
                    );

                    vehicle.setVehicleId(
                            result.getLong("vehicle_id"));

                    return vehicle;
                }
            }
        }

        return null;
    }
}

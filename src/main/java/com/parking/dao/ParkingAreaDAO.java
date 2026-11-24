package com.parking.dao;

import com.parking.database.DBConnection;
import com.parking.model.ParkingArea;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ParkingAreaDAO {

    public long createArea(ParkingArea area) throws Exception {

        String sql = """
                INSERT INTO parking_areas
                (area_code, area_name, location)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, area.getAreaCode());
            statement.setString(2, area.getAreaName());
            statement.setString(3, area.getLocation());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    long areaId = keys.getLong(1);
                    area.setAreaId(areaId);
                    return areaId;
                }
            }
        }

        throw new Exception("Could not create parking area.");
    }

    public List<ParkingArea> getAllAreas() throws Exception {

        List<ParkingArea> areas = new ArrayList<>();

        String sql = """
                SELECT area_id, area_code, area_name, location
                FROM parking_areas
                ORDER BY area_id
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                ParkingArea area = new ParkingArea(
                        result.getString("area_code"),
                        result.getString("area_name"),
                        result.getString("location")
                );

                area.setAreaId(result.getLong("area_id"));
                areas.add(area);
            }
        }

        return areas;
    }
}

package com.parking.dao;

import com.parking.database.DBConnection;
import com.parking.model.ParkingSlot;
import com.parking.model.SlotStatus;
import com.parking.model.VehicleCategory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ParkingSlotDAO {

    public long createSlot(ParkingSlot slot) throws Exception {

        String sql = """
                INSERT INTO parking_slots
                (area_id, slot_number, vehicle_category, slot_status)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, slot.getAreaId());
            statement.setInt(2, slot.getSlotNumber());
            statement.setString(
                    3, slot.getVehicleCategory().name());
            statement.setString(
                    4, slot.getStatus().name());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    long slotId = keys.getLong(1);
                    slot.setSlotId(slotId);
                    return slotId;
                }
            }
        }

        throw new Exception("Could not create parking slot.");
    }

    public List<ParkingSlot> getSlotsByArea(long areaId)
            throws Exception {

        List<ParkingSlot> slots = new ArrayList<>();

        String sql = """
                SELECT slot_id, area_id, slot_number,
                       vehicle_category, slot_status
                FROM parking_slots
                WHERE area_id = ?
                ORDER BY slot_number
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, areaId);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    ParkingSlot slot = new ParkingSlot(
                            result.getLong("area_id"),
                            result.getInt("slot_number"),
                            VehicleCategory.valueOf(
                                    result.getString("vehicle_category"))
                    );

                    slot.setSlotId(result.getLong("slot_id"));

                    slot.setStatus(
                            SlotStatus.valueOf(
                                    result.getString("slot_status"))
                    );

                    slots.add(slot);
                }
            }
        }

        return slots;
    }
}

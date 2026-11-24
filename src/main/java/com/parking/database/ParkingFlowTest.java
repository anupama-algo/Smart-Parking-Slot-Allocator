package com.parking.database;

import com.parking.dao.ParkingAreaDAO;
import com.parking.dao.ParkingSlotDAO;
import com.parking.dao.ParkingSessionDAO;
import com.parking.model.ParkingArea;
import com.parking.model.ParkingSlot;
import com.parking.model.SlotStatus;
import com.parking.model.Vehicle;
import com.parking.model.VehicleCategory;

import java.math.BigDecimal;
import java.util.UUID;

public class ParkingFlowTest {

    public static void main(String[] args) {

        try {
            String uniqueId = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase();

            // 1. Create a test parking area.
            ParkingArea area = new ParkingArea(
                    "T" + uniqueId,
                    "Integration Test Area",
                    "Test Location"
            );

            ParkingAreaDAO areaDAO = new ParkingAreaDAO();
            long areaId = areaDAO.createArea(area);

            // 2. Add a car parking slot.
            ParkingSlot slot = new ParkingSlot(
                    areaId, 1, VehicleCategory.CAR
            );

            ParkingSlotDAO slotDAO = new ParkingSlotDAO();
            slotDAO.createSlot(slot);

            // 3. Register vehicle entry.
            Vehicle vehicle = new Vehicle(
                    "TEST" + uniqueId,
                    VehicleCategory.CAR
            );

            ParkingSessionDAO sessionDAO =
                    new ParkingSessionDAO();

            long sessionId = sessionDAO.recordEntry(
                    vehicle, areaId
            );

            System.out.println("VEHICLE ENTRY SUCCESSFUL");
            System.out.println("Session ID: " + sessionId);
            System.out.println(
                    "Vehicle: " + vehicle.getVehicleNumber()
            );

            // 4. Record vehicle exit.
            BigDecimal fee = sessionDAO.recordExit(
                    vehicle.getVehicleNumber()
            );

            System.out.println("\nVEHICLE EXIT SUCCESSFUL");
            System.out.println("Parking Fee: Rs. " + fee);

            // 5. Verify that the slot is available again.
            ParkingSlot savedSlot = slotDAO
                    .getSlotsByArea(areaId)
                    .get(0);

            if (savedSlot.getStatus() != SlotStatus.AVAILABLE) {
                throw new IllegalStateException(
                        "Slot was not released correctly."
                );
            }

            System.out.println("\nSLOT RELEASE VERIFIED");
            System.out.println("Slot Status: AVAILABLE");

            System.out.println(
                    "\nALL DATABASE INTEGRATION CHECKS PASSED!"
            );

        } catch (Exception e) {

            System.out.println(
                    "DATABASE INTEGRATION TEST FAILED!"
            );

            e.printStackTrace();
        }
    }
}

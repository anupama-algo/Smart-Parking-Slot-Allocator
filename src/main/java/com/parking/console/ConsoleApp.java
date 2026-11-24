package com.parking.console;

import com.parking.dao.ParkingAreaDAO;
import com.parking.dao.ParkingReportDAO;
import com.parking.dao.ParkingSessionDAO;
import com.parking.database.DBConnection;
import com.parking.model.ParkingArea;
import com.parking.model.Vehicle;
import com.parking.model.VehicleCategory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleApp {

    private final Scanner scanner = new Scanner(System.in);
    private final ParkingAreaDAO areaDAO = new ParkingAreaDAO();
    private final ParkingSessionDAO sessionDAO = new ParkingSessionDAO();
    private final ParkingReportDAO reportDAO = new ParkingReportDAO();

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public void run() {

        System.out.println("\n==============================================");
        System.out.println("       SMART PARKING SLOT ALLOCATOR");
        System.out.println("==============================================");

        try (Connection ignored = DBConnection.getConnection()) {
            System.out.println("[INFO] Database connection established.");
        } catch (SQLException e) {
            System.out.println("[ERROR] Database connection failed: "
                    + e.getMessage());
            return;
        }

        System.out.println("[READY] Console parking system started.");

        while (true) {

            System.out.println("\n============== MAIN MENU ==============");
            System.out.println("1. Vehicle Entry");
            System.out.println("2. Vehicle Exit");
            System.out.println("3. Check Parking Availability");
            System.out.println("4. View Parking Reports");
            System.out.println("5. Exit");
            System.out.println("=======================================");

            int choice = readNumber("Enter your choice: ");

            switch (choice) {
                case 1 -> vehicleEntry();
                case 2 -> vehicleExit();
                case 3 -> showAvailability();
                case 4 -> showReports();
                case 5 -> {
                    System.out.println("\nThank you for using "
                            + "Smart Parking Slot Allocator.");
                    System.out.println("[INFO] Console application closed.");
                    return;
                }
                default -> System.out.println(
                        "[ERROR] Invalid option. Please choose 1-5."
                );
            }
        }
    }

    private void vehicleEntry() {

        System.out.println("\n------------- VEHICLE ENTRY -------------");

        String vehicleNumber = readVehicleNumber();

        if (vehicleNumber == null) {
            return;
        }

        VehicleCategory category = chooseCategory();

        if (category == null) {
            return;
        }

        try {

            List<ParkingArea> areas = areaDAO.getAllAreas();

            if (areas.isEmpty()) {
                System.out.println("[ERROR] No parking areas available.");
                return;
            }

            System.out.println("\nSelect Parking Location:");

            for (int i = 0; i < areas.size(); i++) {
                ParkingArea area = areas.get(i);

                System.out.println((i + 1) + ". "
                        + area.getAreaName()
                        + " (" + area.getLocation() + ")");
            }

            System.out.println("0. Cancel");

            int selected = readNumber("Enter location choice: ");

            if (selected == 0) {
                System.out.println("[INFO] Entry cancelled.");
                return;
            }

            if (selected < 1 || selected > areas.size()) {
                System.out.println("[ERROR] Invalid location selection.");
                return;
            }

            ParkingArea selectedArea = areas.get(selected - 1);

            long available = getAvailableCount(
                    selectedArea.getAreaId(), category
            );

            if (available == 0) {
                System.out.println("\n[NOT AVAILABLE] No "
                        + category + " slots are available at "
                        + selectedArea.getAreaName() + ".");
                return;
            }

            System.out.println("\n[INFO] Checking parking availability...");
            System.out.println("[INFO] Available matching slots: " + available);

            Vehicle vehicle = new Vehicle(vehicleNumber, category);

            long sessionId = sessionDAO.recordEntry(
                    vehicle, selectedArea.getAreaId()
            );

            EntryDetails details = getEntryDetails(sessionId);

            System.out.println("\n=======================================");
            System.out.println("       VEHICLE ENTRY SUCCESSFUL");
            System.out.println("=======================================");
            System.out.println("Session ID     : " + sessionId);
            System.out.println("Vehicle Number : " + vehicleNumber);
            System.out.println("Vehicle Type   : " + category);
            System.out.println("Parking Area   : " + details.areaName);
            System.out.println("Location       : " + details.location);
            System.out.println("Allocated Slot : " + details.slotNumber);
            System.out.println("Entry Time     : "
                    + details.entryTime.format(TIME_FORMAT));
            System.out.println("=======================================");
            System.out.println("[SUCCESS] Parking slot allocated.");

        } catch (SQLException e) {
            System.out.println("\n[ENTRY FAILED] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        }
    }

    private void vehicleExit() {

        System.out.println("\n-------------- VEHICLE EXIT --------------");

        String vehicleNumber = readVehicleNumber();

        if (vehicleNumber == null) {
            return;
        }

        try {

            EntryDetails details = getActiveDetails(vehicleNumber);

            if (details == null) {
                System.out.println("[ERROR] No active parking session "
                        + "found for " + vehicleNumber + ".");
                return;
            }

            LocalDateTime exitTime = LocalDateTime.now();

            BigDecimal fee = sessionDAO.recordExit(vehicleNumber);

            long minutes = Math.max(
                    1, Duration.between(
                            details.entryTime, exitTime
                    ).toMinutes()
            );

            System.out.println("\n=======================================");
            System.out.println("       VEHICLE EXIT SUCCESSFUL");
            System.out.println("=======================================");
            System.out.println("Vehicle Number : " + vehicleNumber);
            System.out.println("Parking Area   : " + details.areaName);
            System.out.println("Released Slot  : " + details.slotNumber);
            System.out.println("Entry Time     : "
                    + details.entryTime.format(TIME_FORMAT));
            System.out.println("Exit Time      : "
                    + exitTime.format(TIME_FORMAT));
            System.out.println("Duration       : " + minutes + " minutes");
            System.out.println("Parking Fee    : Rs. " + fee.toPlainString());
            System.out.println("=======================================");
            System.out.println("[SUCCESS] Parking slot released.");

        } catch (SQLException e) {
            System.out.println("\n[EXIT FAILED] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        }
    }

    private VehicleCategory chooseCategory() {

        System.out.println("\nSelect Vehicle Type:");
        System.out.println("1. CAR");
        System.out.println("2. BIKE");
        System.out.println("0. Cancel");

        int choice = readNumber("Enter choice: ");

        return switch (choice) {
            case 1 -> VehicleCategory.CAR;
            case 2 -> VehicleCategory.BIKE;
            default -> {
                System.out.println("[INFO] Invalid selection or cancelled.");
                yield null;
            }
        };
    }

    private String readVehicleNumber() {

        System.out.print("Enter Vehicle Number: ");

        String number = scanner.nextLine()
                .trim()
                .toUpperCase()
                .replaceAll("\\s+", "");

        if (number.isBlank()) {
            System.out.println("[ERROR] Vehicle number cannot be empty.");
            return null;
        }

        return number;
    }

    private int readNumber(String message) {

        System.out.print(message);

        String value = scanner.nextLine().trim();

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private long getAvailableCount(
            long areaId, VehicleCategory category)
            throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM parking_slots
                WHERE area_id = ?
                  AND vehicle_category = ?
                  AND slot_status = 'AVAILABLE'
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, areaId);
            statement.setString(2, category.name());

            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getLong(1);
            }
        }
    }

    private EntryDetails getEntryDetails(long sessionId)
            throws SQLException {

        String sql = """
                SELECT pa.area_name, pa.location,
                       sl.slot_number, ps.entry_time
                FROM parking_sessions ps
                JOIN parking_slots sl ON ps.slot_id = sl.slot_id
                JOIN parking_areas pa ON sl.area_id = pa.area_id
                WHERE ps.session_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, sessionId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return new EntryDetails(
                            result.getString("area_name"),
                            result.getString("location"),
                            result.getString("slot_number"),
                            result.getTimestamp("entry_time")
                                    .toLocalDateTime()
                    );
                }
            }
        }

        throw new SQLException("Could not retrieve allocated slot details.");
    }

    private EntryDetails getActiveDetails(String vehicleNumber)
            throws SQLException {

        String sql = """
                SELECT pa.area_name, pa.location,
                       sl.slot_number, ps.entry_time
                FROM parking_sessions ps
                JOIN vehicles v ON ps.vehicle_id = v.vehicle_id
                JOIN parking_slots sl ON ps.slot_id = sl.slot_id
                JOIN parking_areas pa ON sl.area_id = pa.area_id
                WHERE v.vehicle_number = ?
                  AND ps.exit_time IS NULL
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, vehicleNumber);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return new EntryDetails(
                            result.getString("area_name"),
                            result.getString("location"),
                            result.getString("slot_number"),
                            result.getTimestamp("entry_time")
                                    .toLocalDateTime()
                    );
                }
            }
        }

        return null;
    }

    private void showAvailability() {

        System.out.println("\n--------- PARKING AVAILABILITY ---------");

        try {

            Map<String, Long> report =
                    reportDAO.getAvailableSlotsByArea();

            for (Map.Entry<String, Long> entry : report.entrySet()) {
                System.out.println(entry.getKey()
                        + " : " + entry.getValue()
                        + " available slots");
            }

            System.out.println("-----------------------------------------");

        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void showReports() {

        System.out.println("\n------------- PARKING REPORTS -------------");

        try {

            Map<String, Long> occupancy =
                    reportDAO.getOccupancyReport();

            System.out.println("\nOverall Occupancy:");

            for (Map.Entry<String, Long> entry : occupancy.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }

            System.out.println("\nActive Vehicles:");

            Map<String, Long> active =
                    reportDAO.getActiveVehiclesByCategory();

            for (Map.Entry<String, Long> entry : active.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }

            System.out.println("\nToday's Collection: Rs. "
                    + reportDAO.getDailyCollection(LocalDate.now()));

            System.out.println("-------------------------------------------");

        } catch (SQLException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private static class EntryDetails {

        private final String areaName;
        private final String location;
        private final String slotNumber;
        private final LocalDateTime entryTime;

        private EntryDetails(
                String areaName,
                String location,
                String slotNumber,
                LocalDateTime entryTime) {

            this.areaName = areaName;
            this.location = location;
            this.slotNumber = slotNumber;
            this.entryTime = entryTime;
        }
    }
}
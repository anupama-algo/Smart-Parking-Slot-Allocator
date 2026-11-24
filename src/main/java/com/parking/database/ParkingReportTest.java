package com.parking.database;

import com.parking.dao.ParkingReportDAO;

import java.time.LocalDate;

public class ParkingReportTest {

    public static void main(String[] args) {

        try {

            ParkingReportDAO reportDAO =
                    new ParkingReportDAO();

            System.out.println("====================================");
            System.out.println("       PARKING MANAGEMENT REPORTS");
            System.out.println("====================================");

            // 1. Occupancy report
            System.out.println("\n1. OVERALL PARKING OCCUPANCY");

            reportDAO.getOccupancyReport()
                    .forEach((key, value) ->
                            System.out.println(key + ": " + value)
                    );

            // 2. Available slots by area
            System.out.println("\n2. AVAILABLE SLOTS BY AREA");

            reportDAO.getAvailableSlotsByArea()
                    .forEach((key, value) ->
                            System.out.println(key + ": " + value)
                    );

            // 3. Active vehicles by category
            System.out.println("\n3. ACTIVE VEHICLES BY CATEGORY");

            var activeVehicles =
                    reportDAO.getActiveVehiclesByCategory();

            if (activeVehicles.isEmpty()) {
                System.out.println("No vehicles currently parked.");
            } else {
                activeVehicles.forEach((key, value) ->
                        System.out.println(key + ": " + value)
                );
            }

            // 4. Daily collection
            System.out.println("\n4. TODAY'S PARKING COLLECTION");

            System.out.println(
                    "Date: " + LocalDate.now()
            );

            System.out.println(
                    "Collection: Rs. " +
                    reportDAO.getDailyCollection(LocalDate.now())
            );

            System.out.println("\nALL REPORTS GENERATED SUCCESSFULLY!");

        } catch (Exception e) {

            System.out.println("REPORT GENERATION FAILED!");
            e.printStackTrace();
        }
    }
}

package com.parking.model;

public class Vehicle {

    private long vehicleId;
    private String vehicleNumber;
    private VehicleCategory category;

    public Vehicle(String vehicleNumber, VehicleCategory category) {

        if (vehicleNumber == null || vehicleNumber.isBlank()) {
            throw new IllegalArgumentException(
                "Vehicle number cannot be empty."
            );
        }

        if (category == null) {
            throw new IllegalArgumentException(
                "Vehicle category cannot be null."
            );
        }

        this.vehicleNumber = vehicleNumber.trim()
                .toUpperCase()
                .replaceAll("\\s+", "");

        this.category = category;
    }

    public long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(long vehicleId) {
        if (vehicleId <= 0) {
            throw new IllegalArgumentException(
                "Vehicle ID must be positive."
            );
        }
        this.vehicleId = vehicleId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public VehicleCategory getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleNumber='" + vehicleNumber + '\'' +
                ", category=" + category +
                '}';
    }
}

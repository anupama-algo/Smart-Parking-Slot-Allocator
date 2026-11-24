package com.parking.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class ParkingSession {

    private long sessionId;
    private final Vehicle vehicle;
    private final ParkingSlot parkingSlot;
    private final LocalDateTime entryTime;

    private LocalDateTime exitTime;
    private long durationMinutes;
    private double parkingFee;
    private boolean active;

    private static final double FIRST_HOUR_RATE = 20.0;
    private static final double ADDITIONAL_HOUR_RATE = 10.0;

    public ParkingSession(Vehicle vehicle, ParkingSlot parkingSlot) {

        if (vehicle == null || parkingSlot == null) {
            throw new IllegalArgumentException(
                "Vehicle and parking slot are required."
            );
        }

        if (vehicle.getCategory() != parkingSlot.getVehicleCategory()) {
            throw new IllegalArgumentException(
                "Vehicle category does not match parking slot."
            );
        }

        if (!parkingSlot.isAvailable()) {
            throw new IllegalStateException(
                "Parking slot is already occupied."
            );
        }

        this.vehicle = vehicle;
        this.parkingSlot = parkingSlot;
        this.entryTime = LocalDateTime.now();
        this.active = true;

        this.parkingSlot.setStatus(SlotStatus.OCCUPIED);
    }

    public void completeSession() {

        if (!active) {
            throw new IllegalStateException(
                "Parking session is already completed."
            );
        }

        this.exitTime = LocalDateTime.now();

        long elapsedSeconds = Duration.between(
                entryTime, exitTime
        ).getSeconds();

        this.durationMinutes = Math.max(
                1, (elapsedSeconds + 59) / 60
        );

        long billedHours = Math.max(
                1, (durationMinutes + 59) / 60
        );

        this.parkingFee = FIRST_HOUR_RATE
                + (billedHours - 1) * ADDITIONAL_HOUR_RATE;

        this.active = false;

        this.parkingSlot.setStatus(SlotStatus.AVAILABLE);
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        if (sessionId <= 0) {
            throw new IllegalArgumentException(
                "Invalid session ID."
            );
        }
        this.sessionId = sessionId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSlot getParkingSlot() {
        return parkingSlot;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public long getDurationMinutes() {
        return durationMinutes;
    }

    public double getParkingFee() {
        return parkingFee;
    }

    public boolean isActive() {
        return active;
    }
}
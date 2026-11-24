package com.parking.service;

import com.parking.model.ParkingArea;
import com.parking.model.ParkingSlot;
import com.parking.model.Vehicle;
import com.parking.model.SlotStatus;
import com.parking.model.ParkingSession;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class ParkingAllocationService {

    private final Map<String, ParkingSession> activeSessions;
    private final Queue<Vehicle> waitingQueue;

    public ParkingAllocationService() {
        activeSessions = new HashMap<>();
        waitingQueue = new LinkedList<>();
    }

    public synchronized ParkingSession allocateSlot(
            Vehicle vehicle, ParkingArea area) {

        if (vehicle == null || area == null) {
            throw new IllegalArgumentException(
                "Vehicle and parking area are required."
            );
        }

        String vehicleNumber = vehicle.getVehicleNumber();

        if (activeSessions.containsKey(vehicleNumber)) {
            throw new IllegalStateException(
                "Vehicle already has an active parking session."
            );
        }

        for (ParkingSlot slot : area.getAllSlots()) {

            if (slot.getVehicleCategory() == vehicle.getCategory()
                    && slot.getStatus() == SlotStatus.AVAILABLE) {

                ParkingSession session =
                        new ParkingSession(vehicle, slot);

                activeSessions.put(vehicleNumber, session);

                return session;
            }
        }

        waitingQueue.offer(vehicle);

        throw new IllegalStateException(
            "No suitable slot available. Vehicle added to waiting queue."
        );
    }

    public synchronized ParkingSession releaseSlot(
            String vehicleNumber) {

        if (vehicleNumber == null || vehicleNumber.isBlank()) {
            throw new IllegalArgumentException(
                "Vehicle number is required."
            );
        }

        String normalizedNumber = vehicleNumber.trim()
                .toUpperCase()
                .replaceAll("\\s+", "");

        ParkingSession session =
                activeSessions.remove(normalizedNumber);

        if (session == null) {
            throw new IllegalStateException(
                "No active parking session found for this vehicle."
            );
        }

        session.completeSession();

        return session;
    }

    public synchronized int getWaitingQueueSize() {
        return waitingQueue.size();
    }

    public synchronized int getActiveSessionCount() {
        return activeSessions.size();
    }
}

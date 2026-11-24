package com.parking.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ParkingArea {

    private long areaId;
    private String areaCode;
    private String areaName;
    private String location;

    private final Map<Integer, ParkingSlot> slots;

    public ParkingArea(String areaCode, String areaName, String location) {

        if (areaCode == null || areaCode.isBlank()
                || areaName == null || areaName.isBlank()) {
            throw new IllegalArgumentException(
                "Area code and area name are required."
            );
        }

        this.areaCode = areaCode.trim().toUpperCase();
        this.areaName = areaName.trim();
        this.location = location == null ? "" : location.trim();

        this.slots = new HashMap<>();
    }

    public void addSlot(ParkingSlot slot) {

        if (slot == null) {
            throw new IllegalArgumentException(
                "Parking slot cannot be null."
            );
        }

        if (slots.containsKey(slot.getSlotNumber())) {
            throw new IllegalArgumentException(
                "Slot number already exists in this area."
            );
        }

        slots.put(slot.getSlotNumber(), slot);
    }

    public ParkingSlot getSlot(int slotNumber) {
        return slots.get(slotNumber);
    }

    public Collection<ParkingSlot> getAllSlots() {
        return slots.values();
    }

    public long getTotalSlots() {
        return slots.size();
    }

    public long getAvailableSlots() {
        return slots.values()
                .stream()
                .filter(ParkingSlot::isAvailable)
                .count();
    }

    public long getAreaId() {
        return areaId;
    }

    public void setAreaId(long areaId) {
        if (areaId <= 0) {
            throw new IllegalArgumentException("Invalid area ID.");
        }
        this.areaId = areaId;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "ParkingArea{" +
                "areaCode='" + areaCode + '\'' +
                ", areaName='" + areaName + '\'' +
                ", totalSlots=" + getTotalSlots() +
                ", availableSlots=" + getAvailableSlots() +
                '}';
    }
}

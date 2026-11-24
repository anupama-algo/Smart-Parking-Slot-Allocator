package com.parking.model;

public class ParkingSlot {

    private long slotId;
    private long areaId;
    private int slotNumber;
    private VehicleCategory vehicleCategory;
    private SlotStatus status;

    public ParkingSlot(long areaId, int slotNumber,
                       VehicleCategory vehicleCategory) {

        if (areaId <= 0 || slotNumber <= 0) {
            throw new IllegalArgumentException(
                "Area ID and slot number must be positive."
            );
        }

        if (vehicleCategory == null) {
            throw new IllegalArgumentException(
                "Vehicle category cannot be null."
            );
        }

        this.areaId = areaId;
        this.slotNumber = slotNumber;
        this.vehicleCategory = vehicleCategory;
        this.status = SlotStatus.AVAILABLE;
    }

    public long getSlotId() {
        return slotId;
    }

    public void setSlotId(long slotId) {
        this.slotId = slotId;
    }

    public long getAreaId() {
        return areaId;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public VehicleCategory getVehicleCategory() {
        return vehicleCategory;
    }

    public SlotStatus getStatus() {
        return status;
    }

    public void setStatus(SlotStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(
                "Slot status cannot be null."
            );
        }

        this.status = status;
    }

    public boolean isAvailable() {
        return status == SlotStatus.AVAILABLE;
    }

    @Override
    public String toString() {
        return "ParkingSlot{" +
                "slotNumber=" + slotNumber +
                ", category=" + vehicleCategory +
                ", status=" + status +
                '}';
    }
}
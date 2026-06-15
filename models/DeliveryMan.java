package models;

import enums.VehicleType;
import interfaces.IAuthenticable;

public class DeliveryMan extends User implements IAuthenticable {
    private String licensePlate;
    private VehicleType vehicleType;
    private Location currentLocation;
    private boolean available;

    public DeliveryMan(String firstName, String secondName, String phone, String email, String password, String licensePlate, VehicleType vehicleType, Location location) {
        super(firstName, secondName, phone, email, password);
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.currentLocation = location;
        this.available = true;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean login(String email, String password) {
        return this.getEmail().equals(email) && this.getPassword().equals(password);
    }

    @Override
    public String toString() {
        return "DeliveryMan{"+"userId=" + getUserId() +
                ", name='" + getFirstName()+" "+getSecondName()+'\'' +
                ", phone='" + getPhone() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", vehicleType=" + vehicleType +
                ", available=" + available +
                ", wallet=" + getWallet() + '}';
    }
}

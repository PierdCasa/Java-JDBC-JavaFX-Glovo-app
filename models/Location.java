package models;

public class Location {
    private int locationId;
    private double x;
    private double y;
    private String address;

    public Location(double x, double y, String address) {
        this.x = x;
        this.y = y;
        this.address = address;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Distanta euclidiana intre doua locatii
    public double distanceTo(Location other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    @Override
    public String toString() {
        return address + " (x=" + String.format("%.1f", x) + ", y=" + String.format("%.1f", y) + ")";
    }
}

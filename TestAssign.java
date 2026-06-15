import database.*;
import models.*;
import enums.*;
import java.util.List;

public class TestAssign {
    public static void main(String[] args) {
        DatabaseSeeder.seedIfNeeded();
        
        System.out.println("All Delivery Men:");
        List<DeliveryMan> drivers = DatabaseService.getInstance().getUserDAO().findAllDeliveryMen();
        for (DeliveryMan dm : drivers) {
            System.out.println(" - " + dm.getFirstName() + " (Loc ID: " + 
                (dm.getCurrentLocation() != null ? dm.getCurrentLocation().getLocationId() : "null") + 
                ")");
        }
        
        Customer c = (Customer) DatabaseService.getInstance().getUserDAO().findByEmail("maria@email.com");
        System.out.println("Customer Loc: " + (c.getLocation() != null ? c.getLocation().getLocationId() : "null"));
        
        System.out.println("Executing CartView logic:");
        DeliveryMan bestMatch = null;
        double minDistance = Double.MAX_VALUE;

        for (DeliveryMan dm : drivers) {
            System.out.println("Checking driver: " + dm.getFirstName() + ", available=" + dm.isAvailable() + ", loc=" + dm.getCurrentLocation());
            if (dm.isAvailable()) {
                if (dm.getCurrentLocation() != null && c.getLocation() != null) {
                    double distance = dm.getCurrentLocation().distanceTo(c.getLocation());
                    System.out.println("Distance to driver: " + distance);
                    if (distance < minDistance) {
                        minDistance = distance;
                        bestMatch = dm;
                    }
                } else {
                    System.out.println("Missing location! Driver Loc: " + dm.getCurrentLocation() + ", Customer Loc: " + c.getLocation());
                }
            }
        }
        System.out.println("Best match: " + (bestMatch != null ? bestMatch.getFirstName() : "null"));
    }
}

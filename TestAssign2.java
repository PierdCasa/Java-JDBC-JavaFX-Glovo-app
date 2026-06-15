import database.*;
import models.*;
import enums.*;
import java.util.List;

public class TestAssign2 {
    public static void main(String[] args) {
        DatabaseSeeder.seedIfNeeded();
        
        Customer c = (Customer) DatabaseService.getInstance().getUserDAO().findByEmail("maria@email.com");
        System.out.println("Customer Loc: " + c.getLocation());
        
        DeliveryMan dm = (DeliveryMan) DatabaseService.getInstance().getUserDAO().findByEmail("andrei@email.com");
        System.out.println("Driver Loc: " + dm.getCurrentLocation());
        
        System.out.println("Distance: " + dm.getCurrentLocation().distanceTo(c.getLocation()));
    }
}

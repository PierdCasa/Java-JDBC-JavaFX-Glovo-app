import database.*;
import models.*;
import enums.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TestPlaceOrder {
    public static void main(String[] args) {
        DatabaseSeeder.seedIfNeeded();
        
        Customer c = (Customer) DatabaseService.getInstance().getUserDAO().findByEmail("maria@email.com");
        Restaurant r = DatabaseService.getInstance().getRestaurantDAO().findAll().get(0);
        
        Order cart = new Order(c, r, new ArrayList<>(), 10.0, 5.0);
        cart.setOrderPrice(50.0);
        cart.setStatus(OrderStatus.PENDING);
        cart.setOrderDate(LocalDateTime.now());
        
        java.util.List<models.DeliveryMan> allDrivers = DatabaseService.getInstance().getUserDAO().findAllDeliveryMen();
        models.DeliveryMan bestMatch = null;
        double minDistance = Double.MAX_VALUE;

        for (models.DeliveryMan dm : allDrivers) {
            if (dm.isAvailable()) {
                if (dm.getCurrentLocation() != null && c.getLocation() != null) {
                    double distance = dm.getCurrentLocation().distanceTo(c.getLocation());
                    if (distance < minDistance) {
                        minDistance = distance;
                        bestMatch = dm;
                    }
                }
            }
        }
        
        System.out.println("Best Match for new order: " + (bestMatch != null ? bestMatch.getFirstName() : "NULL"));

        if (bestMatch != null) {
            cart.setDeliveryMan(bestMatch);
        }

        DatabaseService.getInstance().getOrderDAO().save(cart);
        System.out.println("Saved new order. ID: " + cart.getOrderId());
        
        Order loaded = DatabaseService.getInstance().getOrderDAO().findById(cart.getOrderId());
        System.out.println("Loaded order DeliveryMan: " + (loaded.getDeliveryMan() != null ? loaded.getDeliveryMan().getFirstName() : "NULL"));
    }
}

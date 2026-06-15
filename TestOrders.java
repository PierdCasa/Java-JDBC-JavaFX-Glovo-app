import database.*;
import models.*;
import enums.*;
import java.util.List;

public class TestOrders {
    public static void main(String[] args) {
        DatabaseSeeder.seedIfNeeded();
        
        List<Order> orders = DatabaseService.getInstance().getOrderDAO().findAll();
        System.out.println("Total orders: " + orders.size());
        for (Order o : orders) {
            System.out.println("Order #" + o.getOrderId() + ", status=" + o.getStatus() + 
                               ", DeliveryMan=" + (o.getDeliveryMan() != null ? o.getDeliveryMan().getFirstName() : "NULL"));
        }
    }
}

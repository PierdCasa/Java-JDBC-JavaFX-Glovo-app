import database.*;
import java.sql.*;

public class TestDBOrders {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT order_id, customer_id, delivery_man_id, status FROM orders")) {
            System.out.println("ORDERS IN DB:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("order_id") + 
                                   ", Customer: " + rs.getInt("customer_id") + 
                                   ", DeliveryMan: " + rs.getInt("delivery_man_id") + 
                                   ", Status: " + rs.getString("status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

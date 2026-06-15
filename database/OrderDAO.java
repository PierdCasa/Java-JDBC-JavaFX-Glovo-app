package database;

import enums.OrderStatus;
import models.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO extends GenericDAO<Order> {

    private static OrderDAO instance;

    private OrderDAO() {}

    public static synchronized OrderDAO getInstance() {
        if (instance == null) instance = new OrderDAO();
        return instance;
    }

    @Override
    public Order findById(int id) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[OrderDAO] Eroare findById: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return null;
    }

    public List<Order> findByCustomerId(int customerId) {
        String sql = "SELECT * FROM orders WHERE customer_id = ?";
        List<Order> list = new ArrayList<>();
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, customerId);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[OrderDAO] Eroare findByCustomerId: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return list;
    }

    public List<Order> findByDeliveryManId(int deliveryManId) {
        String sql = "SELECT * FROM orders WHERE delivery_man_id = ?";
        List<Order> list = new ArrayList<>();
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, deliveryManId);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[OrderDAO] Eroare findByDeliveryManId: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return list;
    }

    @Override
    public List<Order> findAll() {
        String sql = "SELECT * FROM orders";
        List<Order> list = new ArrayList<>();
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[OrderDAO] Eroare findAll: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return list;
    }

    @Override
    public void save(Order order) {
        String sql = "INSERT INTO orders (customer_id, delivery_man_id, restaurant_id, order_price, delivery_fee, " +
                     "tip_amount, platform_commission, status, preparing_time, delivery_time, order_date, promo_code_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, order.getCustomer().getUserId());
            if (order.getDeliveryMan() != null) {
                ps.setInt(2, order.getDeliveryMan().getUserId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setInt(3, order.getRestaurant().getRestaurantId());
            ps.setDouble(4, order.getOrderPrice());
            ps.setDouble(5, order.getDeliveryFee());
            ps.setDouble(6, order.getTipAmount());
            ps.setDouble(7, order.getPlatformCommission());
            ps.setString(8, order.getStatus().name());
            ps.setInt(9, order.getPreparingTime());
            ps.setInt(10, order.getDeliveryTime());
            ps.setTimestamp(11, Timestamp.valueOf(order.getOrderDate()));
            if (order.getAppliedPromoCode() != null) {
                ps.setInt(12, order.getAppliedPromoCode().getPromoCodeId());
            } else {
                ps.setNull(12, Types.INTEGER);
            }
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) order.setOrderId(keys.getInt(1));

            // Salvam order items
            for (OrderItem item : order.getOrderItems()) {
                OrderItemDAO.getInstance().save(item, order.getOrderId());
            }
        } catch (SQLException e) {
            System.err.println("[OrderDAO] Eroare save: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void update(Order order) {
        String sql = "UPDATE orders SET delivery_man_id = ?, order_price = ?, delivery_fee = ?, tip_amount = ?, " +
                     "platform_commission = ?, status = ?, preparing_time = ?, delivery_time = ?, promo_code_id = ? WHERE order_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            if (order.getDeliveryMan() != null) {
                ps.setInt(1, order.getDeliveryMan().getUserId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setDouble(2, order.getOrderPrice());
            ps.setDouble(3, order.getDeliveryFee());
            ps.setDouble(4, order.getTipAmount());
            ps.setDouble(5, order.getPlatformCommission());
            ps.setString(6, order.getStatus().name());
            ps.setInt(7, order.getPreparingTime());
            ps.setInt(8, order.getDeliveryTime());
            if (order.getAppliedPromoCode() != null) {
                ps.setInt(9, order.getAppliedPromoCode().getPromoCodeId());
            } else {
                ps.setNull(9, Types.INTEGER);
            }
            ps.setInt(10, order.getOrderId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[OrderDAO] Eroare update: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void delete(int id) {
        // Order items se sterg automat via CASCADE
        String sql = "DELETE FROM orders WHERE order_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[OrderDAO] Eroare delete: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        int customerId = rs.getInt("customer_id");
        int restaurantId = rs.getInt("restaurant_id");
        Customer customer = (Customer) UserDAO.getInstance().findById(customerId);
        Restaurant restaurant = RestaurantDAO.getInstance().findById(restaurantId);

        // Cream comanda cu lista goala, apoi setam campurile manual
        Order order = new Order(customer, restaurant, new ArrayList<>(), rs.getDouble("delivery_fee"), rs.getDouble("tip_amount"));
        order.setOrderId(rs.getInt("order_id"));
        order.setOrderPrice(rs.getDouble("order_price"));
        order.setPlatformCommission(rs.getDouble("platform_commission"));
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        order.setPreparingTime(rs.getInt("preparing_time"));
        order.setDeliveryTime(rs.getInt("delivery_time"));
        order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());

        int deliveryManId = rs.getInt("delivery_man_id");
        if (deliveryManId > 0) {
            DeliveryMan dm = (DeliveryMan) UserDAO.getInstance().findById(deliveryManId);
            if (dm != null) order.setDeliveryMan(dm);
        }

        int promoCodeId = rs.getInt("promo_code_id");
        if (promoCodeId > 0) {
            // PromoCode se incarca dar nu se aplica din nou
        }

        return order;
    }
}

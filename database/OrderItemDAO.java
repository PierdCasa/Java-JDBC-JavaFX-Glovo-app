package database;

import models.Ingredient;
import models.OrderItem;
import models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO extends GenericDAO<OrderItem> {

    private static OrderItemDAO instance;

    private OrderItemDAO() {}

    public static synchronized OrderItemDAO getInstance() {
        if (instance == null) instance = new OrderItemDAO();
        return instance;
    }

    @Override
    public OrderItem findById(int id) {
        String sql = "SELECT * FROM order_items WHERE order_item_id = ?";
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[OrderItemDAO] Eroare findById: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return null;
    }

    public List<OrderItem> findByOrderId(int orderId) {
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        List<OrderItem> list = new ArrayList<>();
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, orderId);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[OrderItemDAO] Eroare findByOrderId: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return list;
    }

    @Override
    public List<OrderItem> findAll() {
        String sql = "SELECT * FROM order_items";
        List<OrderItem> list = new ArrayList<>();
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[OrderItemDAO] Eroare findAll: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return list;
    }

    @Override
    public void save(OrderItem item) {
        // Aceasta metoda nu se foloseste direct, folosim save(item, orderId)
        throw new UnsupportedOperationException("Folositi save(OrderItem, int orderId)");
    }

    public void save(OrderItem item, int orderId) {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, orderId);
            ps.setInt(2, item.getProduct().getProductId());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getPrice());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) item.setOrderItemId(keys.getInt(1));

            // Salvam personalizarile
            saveCustomizations(item);
        } catch (SQLException e) {
            System.err.println("[OrderItemDAO] Eroare save: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void update(OrderItem item) {
        String sql = "UPDATE order_items SET quantity = ?, price = ? WHERE order_item_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, item.getQuantity());
            ps.setDouble(2, item.getPrice());
            ps.setInt(3, item.getOrderItemId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[OrderItemDAO] Eroare update: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM order_items WHERE order_item_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[OrderItemDAO] Eroare delete: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    private void saveCustomizations(OrderItem item) {
        String sql = "INSERT INTO order_item_customizations (order_item_id, ingredient_id) VALUES (?, ?)";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            for (Ingredient ing : item.getSelectedCustomizations()) {
                if (ing.getIngredientId() == 0) IngredientDAO.getInstance().save(ing);
                ps.setInt(1, item.getOrderItemId());
                ps.setInt(2, ing.getIngredientId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            System.err.println("[OrderItemDAO] Eroare saveCustomizations: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    private OrderItem mapRow(ResultSet rs) throws SQLException {
        int productId = rs.getInt("product_id");
        Product product = ProductDAO.getInstance().findById(productId);

        // Incarcam personalizarile
        List<Ingredient> customizations = loadCustomizations(rs.getInt("order_item_id"));

        OrderItem item;
        if (!customizations.isEmpty()) {
            item = new OrderItem(product, rs.getInt("quantity"), customizations);
        } else {
            item = new OrderItem(product, rs.getInt("quantity"));
        }
        item.setOrderItemId(rs.getInt("order_item_id"));
        return item;
    }

    private List<Ingredient> loadCustomizations(int orderItemId) {
        List<Ingredient> list = new ArrayList<>();
        String sql = "SELECT i.* FROM order_item_customizations oic " +
                     "JOIN ingredients i ON oic.ingredient_id = i.ingredient_id WHERE oic.order_item_id = ?";
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, orderItemId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Ingredient ing = new Ingredient(rs.getString("name"), rs.getDouble("extra_price"), rs.getBoolean("locked"));
                ing.setIngredientId(rs.getInt("ingredient_id"));
                list.add(ing);
            }
        } catch (SQLException e) {
            System.err.println("[OrderItemDAO] Eroare loadCustomizations: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return list;
    }
}

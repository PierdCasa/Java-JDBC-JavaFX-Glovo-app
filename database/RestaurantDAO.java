package database;

import models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDAO extends GenericDAO<Restaurant> {

    private static RestaurantDAO instance;

    private RestaurantDAO() {}

    public static synchronized RestaurantDAO getInstance() {
        if (instance == null) instance = new RestaurantDAO();
        return instance;
    }

    @Override
    public Restaurant findById(int id) {
        String sql = "SELECT * FROM restaurants WHERE restaurant_id = ?";
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[RestaurantDAO] Eroare findById: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return null;
    }

    @Override
    public List<Restaurant> findAll() {
        String sql = "SELECT * FROM restaurants";
        List<Restaurant> list = new ArrayList<>();
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[RestaurantDAO] Eroare findAll: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return list;
    }

    @Override
    public void save(Restaurant restaurant) {
        String sql = "INSERT INTO restaurants (name, location, rating, wallet_id, location_coords_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = null;
        try {
            if (restaurant.getWallet().getWalletId() == 0) WalletDAO.getInstance().save(restaurant.getWallet());
            if (restaurant.getLocationCoords() != null && restaurant.getLocationCoords().getLocationId() == 0) {
                LocationDAO.getInstance().save(restaurant.getLocationCoords());
            }

            ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, restaurant.getName());
            ps.setString(2, restaurant.getLocation());
            ps.setDouble(3, restaurant.getRating());
            ps.setInt(4, restaurant.getWallet().getWalletId());
            if (restaurant.getLocationCoords() != null) {
                ps.setInt(5, restaurant.getLocationCoords().getLocationId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) restaurant.setRestaurantId(keys.getInt(1));

            // Salvam produsele asociate
            saveRestaurantProducts(restaurant);
        } catch (SQLException e) {
            System.err.println("[RestaurantDAO] Eroare save: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void update(Restaurant restaurant) {
        String sql = "UPDATE restaurants SET name = ?, location = ?, rating = ?, location_coords_id = ? WHERE restaurant_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, restaurant.getName());
            ps.setString(2, restaurant.getLocation());
            ps.setDouble(3, restaurant.getRating());
            if (restaurant.getLocationCoords() != null) {
                ps.setInt(4, restaurant.getLocationCoords().getLocationId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setInt(5, restaurant.getRestaurantId());
            ps.executeUpdate();

            // Actualizam produsele
            deleteRestaurantProducts(restaurant.getRestaurantId());
            saveRestaurantProducts(restaurant);
        } catch (SQLException e) {
            System.err.println("[RestaurantDAO] Eroare update: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void delete(int id) {
        deleteRestaurantProducts(id);
        String sql = "DELETE FROM restaurants WHERE restaurant_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[RestaurantDAO] Eroare delete: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    private void saveRestaurantProducts(Restaurant restaurant) {
        String sql = "INSERT INTO restaurant_products (restaurant_id, product_id) VALUES (?, ?)";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            for (Product p : restaurant.getProducts()) {
                ps.setInt(1, restaurant.getRestaurantId());
                ps.setInt(2, p.getProductId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            System.err.println("[RestaurantDAO] Eroare saveRestaurantProducts: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    private void deleteRestaurantProducts(int restaurantId) {
        String sql = "DELETE FROM restaurant_products WHERE restaurant_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, restaurantId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[RestaurantDAO] Eroare deleteRestaurantProducts: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    private Restaurant mapRow(ResultSet rs) throws SQLException {
        int locCoordsId = rs.getInt("location_coords_id");
        Location locCoords = locCoordsId > 0 ? LocationDAO.getInstance().findById(locCoordsId) : null;
        int walletId = rs.getInt("wallet_id");
        Wallet wallet = WalletDAO.getInstance().findById(walletId);

        Restaurant r = new Restaurant(rs.getString("name"), rs.getString("location"), rs.getDouble("rating"), locCoords);
        r.setRestaurantId(rs.getInt("restaurant_id"));
        if (wallet != null) r.setWallet(wallet);

        // Incarcam produsele asociate
        loadRestaurantProducts(r);
        loadRestaurantReviews(r);
        return r;
    }

    private void loadRestaurantReviews(Restaurant restaurant) {
        List<Review> reviews = ReviewDAO.getInstance().findByRestaurantId(restaurant.getRestaurantId());
        restaurant.setReviews(reviews);
    }

    private void loadRestaurantProducts(Restaurant restaurant) {
        String sql = "SELECT product_id FROM restaurant_products WHERE restaurant_id = ?";
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, restaurant.getRestaurantId());
            rs = ps.executeQuery();
            while (rs.next()) {
                Product p = ProductDAO.getInstance().findById(rs.getInt("product_id"));
                if (p != null) restaurant.addProduct(p);
            }
        } catch (SQLException e) {
            System.err.println("[RestaurantDAO] Eroare loadRestaurantProducts: " + e.getMessage());
        } finally { closeResources(rs, ps); }
    }
}

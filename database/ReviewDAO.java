package database;

import models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO extends GenericDAO<Review> {

    private static ReviewDAO instance;

    private ReviewDAO() {}

    public static synchronized ReviewDAO getInstance() {
        if (instance == null) instance = new ReviewDAO();
        return instance;
    }

    @Override
    public Review findById(int id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[ReviewDAO] Eroare findById: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return null;
    }

    public List<Review> findByRestaurantId(int restaurantId) {
        String sql = "SELECT * FROM reviews WHERE restaurant_id = ?";
        List<Review> list = new ArrayList<>();
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, restaurantId);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ReviewDAO] Eroare findByRestaurantId: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return list;
    }

    public Review findByOrderId(int orderId) {
        String sql = "SELECT * FROM reviews WHERE order_id = ?";
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, orderId);
            rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[ReviewDAO] Eroare findByOrderId: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return null;
    }

    @Override
    public List<Review> findAll() {
        String sql = "SELECT * FROM reviews";
        List<Review> list = new ArrayList<>();
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ReviewDAO] Eroare findAll: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return list;
    }

    @Override
    public void save(Review review) {
        String sql = "INSERT INTO reviews (customer_id, restaurant_id, grocery_store_id, order_id, rating, comment, date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, review.getCustomer().getUserId());
            // Determinam daca review-ul e pentru restaurant sau grocery store
            // Deoarece Review-ul actual nu stocheaza asta direct, setam restaurant_id pe baza comenzii
            if (review.getOrder() != null && review.getOrder().getRestaurant() != null) {
                ps.setInt(2, review.getOrder().getRestaurant().getRestaurantId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setNull(3, Types.INTEGER); // grocery_store_id
            if (review.getOrder() != null) {
                ps.setInt(4, review.getOrder().getOrderId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setInt(5, review.getRating());
            ps.setString(6, review.getComment());
            ps.setTimestamp(7, Timestamp.valueOf(review.getDate()));
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) review.setReviewId(keys.getInt(1));
        } catch (SQLException e) {
            System.err.println("[ReviewDAO] Eroare save: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void update(Review review) {
        String sql = "UPDATE reviews SET rating = ?, comment = ? WHERE review_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, review.getRating());
            ps.setString(2, review.getComment());
            ps.setInt(3, review.getReviewId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ReviewDAO] Eroare update: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ReviewDAO] Eroare delete: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    private Review mapRow(ResultSet rs) throws SQLException {
        int customerId = rs.getInt("customer_id");
        int orderId = rs.getInt("order_id");
        Customer customer = (Customer) UserDAO.getInstance().findById(customerId);
        
        // Breaking infinite recursion: Do not fetch the full Order here because OrderDAO fetches RestaurantDAO, which fetches ReviewDAO again!
        // We only need the order ID if anything, so we use a stub or null.
        Order stubOrder = null;
        if (orderId > 0) {
            stubOrder = new Order(customer, null, new ArrayList<>(), 0.0, 0.0);
            stubOrder.setOrderId(orderId);
        }

        Review review = new Review(customer, stubOrder, rs.getInt("rating"), rs.getString("comment"));
        review.setReviewId(rs.getInt("review_id"));
        review.setDate(rs.getTimestamp("date").toLocalDateTime());
        return review;
    }
}

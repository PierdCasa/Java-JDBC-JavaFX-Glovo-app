package database;

import models.Rating;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RatingDAO extends GenericDAO<Rating> {

    private static RatingDAO instance;

    private RatingDAO() {}

    public static synchronized RatingDAO getInstance() {
        if (instance == null) instance = new RatingDAO();
        return instance;
    }

    @Override
    public Rating findById(int id) {
        String sql = "SELECT * FROM ratings WHERE rating_id = ?";
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[RatingDAO] Eroare findById: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return null;
    }

    @Override
    public List<Rating> findAll() {
        String sql = "SELECT * FROM ratings";
        List<Rating> list = new ArrayList<>();
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[RatingDAO] Eroare findAll: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return list;
    }

    @Override
    public void save(Rating rating) {
        String sql = "INSERT INTO ratings (rating, comment) VALUES (?, ?)";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, rating.getRating());
            ps.setString(2, rating.getComment());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) rating.setRatingId(keys.getInt(1));
        } catch (SQLException e) {
            System.err.println("[RatingDAO] Eroare save: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void update(Rating rating) {
        String sql = "UPDATE ratings SET rating = ?, comment = ? WHERE rating_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, rating.getRating());
            ps.setString(2, rating.getComment());
            ps.setInt(3, rating.getRatingId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[RatingDAO] Eroare update: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM ratings WHERE rating_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[RatingDAO] Eroare delete: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    private Rating mapRow(ResultSet rs) throws SQLException {
        Rating r = new Rating(rs.getInt("rating"), rs.getString("comment"));
        r.setRatingId(rs.getInt("rating_id"));
        return r;
    }
}

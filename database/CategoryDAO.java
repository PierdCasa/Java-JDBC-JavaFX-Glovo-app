package database;

import models.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO extends GenericDAO<Category> {

    private static CategoryDAO instance;

    private CategoryDAO() {}

    public static synchronized CategoryDAO getInstance() {
        if (instance == null) {
            instance = new CategoryDAO();
        }
        return instance;
    }

    @Override
    public Category findById(int id) {
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[CategoryDAO] Eroare findById: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return null;
    }

    public Category findByName(String name) {
        String sql = "SELECT * FROM categories WHERE name = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[CategoryDAO] Eroare findByName: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return null;
    }

    @Override
    public List<Category> findAll() {
        String sql = "SELECT * FROM categories";
        List<Category> list = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[CategoryDAO] Eroare findAll: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return list;
    }

    @Override
    public void save(Category category) {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, category.getName());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) category.setCategoryId(keys.getInt(1));
        } catch (SQLException e) {
            System.err.println("[CategoryDAO] Eroare save: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void update(Category category) {
        String sql = "UPDATE categories SET name = ? WHERE category_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, category.getName());
            ps.setInt(2, category.getCategoryId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[CategoryDAO] Eroare update: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[CategoryDAO] Eroare delete: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    private Category mapRow(ResultSet rs) throws SQLException {
        Category cat = new Category(rs.getString("name"));
        cat.setCategoryId(rs.getInt("category_id"));
        return cat;
    }
}

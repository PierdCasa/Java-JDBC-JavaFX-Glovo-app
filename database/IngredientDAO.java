package database;

import models.Ingredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredientDAO extends GenericDAO<Ingredient> {

    private static IngredientDAO instance;

    private IngredientDAO() {}

    public static synchronized IngredientDAO getInstance() {
        if (instance == null) instance = new IngredientDAO();
        return instance;
    }

    @Override
    public Ingredient findById(int id) {
        String sql = "SELECT * FROM ingredients WHERE ingredient_id = ?";
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[IngredientDAO] Eroare findById: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return null;
    }

    @Override
    public List<Ingredient> findAll() {
        String sql = "SELECT * FROM ingredients";
        List<Ingredient> list = new ArrayList<>();
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[IngredientDAO] Eroare findAll: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return list;
    }

    @Override
    public void save(Ingredient ingredient) {
        String sql = "INSERT INTO ingredients (name, extra_price, locked) VALUES (?, ?, ?)";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, ingredient.getName());
            ps.setDouble(2, ingredient.getExtraPrice());
            ps.setBoolean(3, ingredient.isLocked());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) ingredient.setIngredientId(keys.getInt(1));
        } catch (SQLException e) {
            System.err.println("[IngredientDAO] Eroare save: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void update(Ingredient ingredient) {
        String sql = "UPDATE ingredients SET name = ?, extra_price = ?, locked = ? WHERE ingredient_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, ingredient.getName());
            ps.setDouble(2, ingredient.getExtraPrice());
            ps.setBoolean(3, ingredient.isLocked());
            ps.setInt(4, ingredient.getIngredientId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[IngredientDAO] Eroare update: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM ingredients WHERE ingredient_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[IngredientDAO] Eroare delete: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    private Ingredient mapRow(ResultSet rs) throws SQLException {
        Ingredient ing = new Ingredient(rs.getString("name"), rs.getDouble("extra_price"), rs.getBoolean("locked"));
        ing.setIngredientId(rs.getInt("ingredient_id"));
        return ing;
    }
}

package database;

import models.Category;
import models.Ingredient;
import models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO extends GenericDAO<Product> {

    private static ProductDAO instance;

    private ProductDAO() {}

    public static synchronized ProductDAO getInstance() {
        if (instance == null) instance = new ProductDAO();
        return instance;
    }

    @Override
    public Product findById(int id) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[ProductDAO] Eroare findById: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return null;
    }

    @Override
    public List<Product> findAll() {
        String sql = "SELECT * FROM products";
        List<Product> list = new ArrayList<>();
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[ProductDAO] Eroare findAll: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return list;
    }

    @Override
    public void save(Product product) {
        String sql = "INSERT INTO products (name, category_id, description, price) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, product.getName());
            if (product.getCategory() != null) {
                ps.setInt(2, product.getCategory().getCategoryId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, product.getDescription());
            ps.setDouble(4, product.getPrice());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) product.setProductId(keys.getInt(1));

            // Salvam ingredientele in tabela de legatura
            saveProductIngredients(product);
        } catch (SQLException e) {
            System.err.println("[ProductDAO] Eroare save: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void update(Product product) {
        String sql = "UPDATE products SET name = ?, category_id = ?, description = ?, price = ? WHERE product_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, product.getName());
            if (product.getCategory() != null) {
                ps.setInt(2, product.getCategory().getCategoryId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, product.getDescription());
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, product.getProductId());
            ps.executeUpdate();

            // Stergem si resalvam ingredientele
            deleteProductIngredients(product.getProductId());
            saveProductIngredients(product);
        } catch (SQLException e) {
            System.err.println("[ProductDAO] Eroare update: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void delete(int id) {
        deleteProductIngredients(id);
        String sql = "DELETE FROM products WHERE product_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ProductDAO] Eroare delete: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    private void saveProductIngredients(Product product) {
        String sql = "INSERT INTO product_ingredients (product_id, ingredient_id, is_locked) VALUES (?, ?, ?)";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            for (Ingredient ing : product.getLockedIngredients()) {
                if (ing.getIngredientId() == 0) IngredientDAO.getInstance().save(ing);
                ps.setInt(1, product.getProductId());
                ps.setInt(2, ing.getIngredientId());
                ps.setBoolean(3, true);
                ps.addBatch();
            }
            for (Ingredient ing : product.getOptionalIngredients()) {
                if (ing.getIngredientId() == 0) IngredientDAO.getInstance().save(ing);
                ps.setInt(1, product.getProductId());
                ps.setInt(2, ing.getIngredientId());
                ps.setBoolean(3, false);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            System.err.println("[ProductDAO] Eroare saveProductIngredients: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    private void deleteProductIngredients(int productId) {
        String sql = "DELETE FROM product_ingredients WHERE product_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ProductDAO] Eroare deleteProductIngredients: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        int categoryId = rs.getInt("category_id");
        Category category = categoryId > 0 ? CategoryDAO.getInstance().findById(categoryId) : null;

        Product product = new Product(rs.getString("name"), category, rs.getString("description"), rs.getDouble("price"));
        product.setProductId(rs.getInt("product_id"));

        // Incarcam ingredientele din tabela de legatura
        loadProductIngredients(product);
        return product;
    }

    private void loadProductIngredients(Product product) {
        String sql = "SELECT pi.is_locked, i.* FROM product_ingredients pi " +
                     "JOIN ingredients i ON pi.ingredient_id = i.ingredient_id " +
                     "WHERE pi.product_id = ?";
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, product.getProductId());
            rs = ps.executeQuery();
            while (rs.next()) {
                Ingredient ing = new Ingredient(rs.getString("name"), rs.getDouble("extra_price"), rs.getBoolean("locked"));
                ing.setIngredientId(rs.getInt("ingredient_id"));
                if (rs.getBoolean("is_locked")) {
                    product.addLockedIngredient(ing);
                } else {
                    product.addOptionalIngredient(ing);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ProductDAO] Eroare loadProductIngredients: " + e.getMessage());
        } finally { closeResources(rs, ps); }
    }
}

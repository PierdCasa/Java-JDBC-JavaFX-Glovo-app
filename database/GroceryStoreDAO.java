package database;

import models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GroceryStoreDAO extends GenericDAO<GroceryStore> {

    private static GroceryStoreDAO instance;

    private GroceryStoreDAO() {}

    public static synchronized GroceryStoreDAO getInstance() {
        if (instance == null) instance = new GroceryStoreDAO();
        return instance;
    }

    @Override
    public GroceryStore findById(int id) {
        String sql = "SELECT * FROM grocery_stores WHERE grocery_store_id = ?";
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[GroceryStoreDAO] Eroare findById: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return null;
    }

    @Override
    public List<GroceryStore> findAll() {
        String sql = "SELECT * FROM grocery_stores";
        List<GroceryStore> list = new ArrayList<>();
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[GroceryStoreDAO] Eroare findAll: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return list;
    }

    @Override
    public void save(GroceryStore store) {
        String sql = "INSERT INTO grocery_stores (name, location, rating, wallet_id, location_coords_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = null;
        try {
            if (store.getWallet().getWalletId() == 0) WalletDAO.getInstance().save(store.getWallet());
            if (store.getLocationCoords() != null && store.getLocationCoords().getLocationId() == 0) {
                LocationDAO.getInstance().save(store.getLocationCoords());
            }
            ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, store.getName());
            ps.setString(2, store.getLocation());
            ps.setDouble(3, store.getRating());
            ps.setInt(4, store.getWallet().getWalletId());
            if (store.getLocationCoords() != null) {
                ps.setInt(5, store.getLocationCoords().getLocationId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) store.setGroceryStoreId(keys.getInt(1));

            saveStoreProducts(store);
        } catch (SQLException e) {
            System.err.println("[GroceryStoreDAO] Eroare save: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void update(GroceryStore store) {
        String sql = "UPDATE grocery_stores SET name = ?, location = ?, rating = ?, location_coords_id = ? WHERE grocery_store_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, store.getName());
            ps.setString(2, store.getLocation());
            ps.setDouble(3, store.getRating());
            if (store.getLocationCoords() != null) {
                ps.setInt(4, store.getLocationCoords().getLocationId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setInt(5, store.getGroceryStoreId());
            ps.executeUpdate();

            deleteStoreProducts(store.getGroceryStoreId());
            saveStoreProducts(store);
        } catch (SQLException e) {
            System.err.println("[GroceryStoreDAO] Eroare update: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void delete(int id) {
        deleteStoreProducts(id);
        String sql = "DELETE FROM grocery_stores WHERE grocery_store_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[GroceryStoreDAO] Eroare delete: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    private void saveStoreProducts(GroceryStore store) {
        String sql = "INSERT INTO grocery_store_products (grocery_store_id, product_id) VALUES (?, ?)";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            for (Product p : store.getProducts()) {
                ps.setInt(1, store.getGroceryStoreId());
                ps.setInt(2, p.getProductId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            System.err.println("[GroceryStoreDAO] Eroare saveStoreProducts: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    private void deleteStoreProducts(int storeId) {
        String sql = "DELETE FROM grocery_store_products WHERE grocery_store_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, storeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[GroceryStoreDAO] Eroare deleteStoreProducts: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    private GroceryStore mapRow(ResultSet rs) throws SQLException {
        int locCoordsId = rs.getInt("location_coords_id");
        Location locCoords = locCoordsId > 0 ? LocationDAO.getInstance().findById(locCoordsId) : null;
        int walletId = rs.getInt("wallet_id");
        Wallet wallet = WalletDAO.getInstance().findById(walletId);

        GroceryStore store = new GroceryStore(rs.getString("name"), rs.getString("location"), rs.getDouble("rating"), locCoords);
        store.setGroceryStoreId(rs.getInt("grocery_store_id"));
        if (wallet != null) store.setWallet(wallet);

        loadStoreProducts(store);
        return store;
    }

    private void loadStoreProducts(GroceryStore store) {
        String sql = "SELECT product_id FROM grocery_store_products WHERE grocery_store_id = ?";
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, store.getGroceryStoreId());
            rs = ps.executeQuery();
            while (rs.next()) {
                Product p = ProductDAO.getInstance().findById(rs.getInt("product_id"));
                if (p != null) store.addProduct(p);
            }
        } catch (SQLException e) {
            System.err.println("[GroceryStoreDAO] Eroare loadStoreProducts: " + e.getMessage());
        } finally { closeResources(rs, ps); }
    }
}

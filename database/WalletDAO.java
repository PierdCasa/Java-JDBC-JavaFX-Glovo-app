package database;

import models.Wallet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WalletDAO extends GenericDAO<Wallet> {

    private static WalletDAO instance;

    private WalletDAO() {}

    public static synchronized WalletDAO getInstance() {
        if (instance == null) {
            instance = new WalletDAO();
        }
        return instance;
    }

    @Override
    public Wallet findById(int id) {
        String sql = "SELECT * FROM wallets WHERE wallet_id = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[WalletDAO] Eroare findById: " + e.getMessage());
        } finally {
            closeResources(rs, ps);
        }
        return null;
    }

    @Override
    public List<Wallet> findAll() {
        String sql = "SELECT * FROM wallets";
        List<Wallet> wallets = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                wallets.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[WalletDAO] Eroare findAll: " + e.getMessage());
        } finally {
            closeResources(rs, ps);
        }
        return wallets;
    }

    @Override
    public void save(Wallet wallet) {
        String sql = "INSERT INTO wallets (balance) VALUES (?)";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, wallet.getBalance());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                wallet.setWalletId(keys.getInt(1));
            }
            System.out.println("[WalletDAO] Wallet salvat cu ID: " + wallet.getWalletId());
        } catch (SQLException e) {
            System.err.println("[WalletDAO] Eroare save: " + e.getMessage());
        } finally {
            closeResources(null, ps);
        }
    }

    @Override
    public void update(Wallet wallet) {
        String sql = "UPDATE wallets SET balance = ? WHERE wallet_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setDouble(1, wallet.getBalance());
            ps.setInt(2, wallet.getWalletId());
            ps.executeUpdate();
            System.out.println("[WalletDAO] Wallet actualizat ID: " + wallet.getWalletId());
        } catch (SQLException e) {
            System.err.println("[WalletDAO] Eroare update: " + e.getMessage());
        } finally {
            closeResources(null, ps);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM wallets WHERE wallet_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("[WalletDAO] Wallet sters ID: " + id);
        } catch (SQLException e) {
            System.err.println("[WalletDAO] Eroare delete: " + e.getMessage());
        } finally {
            closeResources(null, ps);
        }
    }

    private Wallet mapRow(ResultSet rs) throws SQLException {
        Wallet wallet = new Wallet(rs.getDouble("balance"));
        wallet.setWalletId(rs.getInt("wallet_id"));
        return wallet;
    }
}

package database;

import enums.TransactionType;
import models.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO extends GenericDAO<Transaction> {

    private static TransactionDAO instance;

    private TransactionDAO() {}

    public static synchronized TransactionDAO getInstance() {
        if (instance == null) instance = new TransactionDAO();
        return instance;
    }

    @Override
    public Transaction findById(int id) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] Eroare findById: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return null;
    }

    public List<Transaction> findByWalletId(int walletId) {
        String sql = "SELECT * FROM transactions WHERE wallet_id = ?";
        List<Transaction> list = new ArrayList<>();
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, walletId);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] Eroare findByWalletId: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return list;
    }

    @Override
    public List<Transaction> findAll() {
        String sql = "SELECT * FROM transactions";
        List<Transaction> list = new ArrayList<>();
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] Eroare findAll: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return list;
    }

    public void save(Transaction transaction, int walletId) {
        String sql = "INSERT INTO transactions (wallet_id, type, amount, description, timestamp, related_order_id) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, walletId);
            ps.setString(2, transaction.getType().name());
            ps.setDouble(3, transaction.getAmount());
            ps.setString(4, transaction.getDescription());
            ps.setTimestamp(5, Timestamp.valueOf(transaction.getTimestamp()));
            if (transaction.getRelatedOrderId() > 0) {
                ps.setInt(6, transaction.getRelatedOrderId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) transaction.setTransactionId(keys.getInt(1));
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] Eroare save: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void save(Transaction transaction) {
        throw new UnsupportedOperationException("Folositi save(Transaction, int walletId)");
    }

    @Override
    public void update(Transaction transaction) {
        String sql = "UPDATE transactions SET type = ?, amount = ?, description = ? WHERE transaction_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, transaction.getType().name());
            ps.setDouble(2, transaction.getAmount());
            ps.setString(3, transaction.getDescription());
            ps.setInt(4, transaction.getTransactionId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] Eroare update: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM transactions WHERE transaction_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[TransactionDAO] Eroare delete: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        TransactionType type = TransactionType.valueOf(rs.getString("type"));
        Transaction t = new Transaction(type, rs.getDouble("amount"), rs.getString("description"), rs.getInt("related_order_id"));
        t.setTransactionId(rs.getInt("transaction_id"));
        t.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        return t;
    }
}

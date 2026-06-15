package database;

import models.PromoCode;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PromoCodeDAO extends GenericDAO<PromoCode> {

    private static PromoCodeDAO instance;

    private PromoCodeDAO() {}

    public static synchronized PromoCodeDAO getInstance() {
        if (instance == null) instance = new PromoCodeDAO();
        return instance;
    }

    @Override
    public PromoCode findById(int id) {
        String sql = "SELECT * FROM promo_codes WHERE promo_code_id = ?";
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[PromoCodeDAO] Eroare findById: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return null;
    }

    public PromoCode findByCode(String code) {
        String sql = "SELECT * FROM promo_codes WHERE code = ?";
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, code.toUpperCase());
            rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("[PromoCodeDAO] Eroare findByCode: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return null;
    }

    @Override
    public List<PromoCode> findAll() {
        String sql = "SELECT * FROM promo_codes";
        List<PromoCode> list = new ArrayList<>();
        PreparedStatement ps = null; ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[PromoCodeDAO] Eroare findAll: " + e.getMessage());
        } finally { closeResources(rs, ps); }
        return list;
    }

    @Override
    public void save(PromoCode promo) {
        String sql = "INSERT INTO promo_codes (code, discount_percent, free_delivery, active, max_uses, current_uses) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, promo.getCode());
            ps.setDouble(2, promo.getDiscountPercent());
            ps.setBoolean(3, promo.isFreeDelivery());
            ps.setBoolean(4, promo.isActive());
            ps.setInt(5, promo.getMaxUses());
            ps.setInt(6, promo.getCurrentUses());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) promo.setPromoCodeId(keys.getInt(1));
        } catch (SQLException e) {
            System.err.println("[PromoCodeDAO] Eroare save: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void update(PromoCode promo) {
        String sql = "UPDATE promo_codes SET code = ?, discount_percent = ?, free_delivery = ?, active = ?, max_uses = ?, current_uses = ? WHERE promo_code_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, promo.getCode());
            ps.setDouble(2, promo.getDiscountPercent());
            ps.setBoolean(3, promo.isFreeDelivery());
            ps.setBoolean(4, promo.isActive());
            ps.setInt(5, promo.getMaxUses());
            ps.setInt(6, promo.getCurrentUses());
            ps.setInt(7, promo.getPromoCodeId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[PromoCodeDAO] Eroare update: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM promo_codes WHERE promo_code_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[PromoCodeDAO] Eroare delete: " + e.getMessage());
        } finally { closeResources(null, ps); }
    }

    private PromoCode mapRow(ResultSet rs) throws SQLException {
        PromoCode promo = new PromoCode(rs.getString("code"), rs.getDouble("discount_percent"),
                rs.getBoolean("free_delivery"), rs.getInt("max_uses"));
        promo.setPromoCodeId(rs.getInt("promo_code_id"));
        promo.setCurrentUses(rs.getInt("current_uses"));
        promo.setActive(rs.getBoolean("active"));
        return promo;
    }
}

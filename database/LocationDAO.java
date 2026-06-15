package database;

import models.Location;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationDAO extends GenericDAO<Location> {

    private static LocationDAO instance;

    private LocationDAO() {}

    public static synchronized LocationDAO getInstance() {
        if (instance == null) {
            instance = new LocationDAO();
        }
        return instance;
    }

    @Override
    public Location findById(int id) {
        String sql = "SELECT * FROM locations WHERE location_id = ?";
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
            System.err.println("[LocationDAO] Eroare findById: " + e.getMessage());
        } finally {
            closeResources(rs, ps);
        }
        return null;
    }

    @Override
    public List<Location> findAll() {
        String sql = "SELECT * FROM locations";
        List<Location> locations = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                locations.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[LocationDAO] Eroare findAll: " + e.getMessage());
        } finally {
            closeResources(rs, ps);
        }
        return locations;
    }

    @Override
    public void save(Location location) {
        String sql = "INSERT INTO locations (x, y, address) VALUES (?, ?, ?)";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, location.getX());
            ps.setDouble(2, location.getY());
            ps.setString(3, location.getAddress());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                location.setLocationId(keys.getInt(1));
            }
            System.out.println("[LocationDAO] Locatie salvata cu ID: " + location.getLocationId());
        } catch (SQLException e) {
            System.err.println("[LocationDAO] Eroare save: " + e.getMessage());
        } finally {
            closeResources(null, ps);
        }
    }

    @Override
    public void update(Location location) {
        String sql = "UPDATE locations SET x = ?, y = ?, address = ? WHERE location_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setDouble(1, location.getX());
            ps.setDouble(2, location.getY());
            ps.setString(3, location.getAddress());
            ps.setInt(4, location.getLocationId());
            ps.executeUpdate();
            System.out.println("[LocationDAO] Locatie actualizata ID: " + location.getLocationId());
        } catch (SQLException e) {
            System.err.println("[LocationDAO] Eroare update: " + e.getMessage());
        } finally {
            closeResources(null, ps);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM locations WHERE location_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("[LocationDAO] Locatie stearsa ID: " + id);
        } catch (SQLException e) {
            System.err.println("[LocationDAO] Eroare delete: " + e.getMessage());
        } finally {
            closeResources(null, ps);
        }
    }

    private Location mapRow(ResultSet rs) throws SQLException {
        Location loc = new Location(
            rs.getDouble("x"),
            rs.getDouble("y"),
            rs.getString("address")
        );
        loc.setLocationId(rs.getInt("location_id"));
        return loc;
    }
}

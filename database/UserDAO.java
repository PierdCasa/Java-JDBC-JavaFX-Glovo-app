package database;

import enums.VehicleType;
import models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends GenericDAO<User> {

    private static UserDAO instance;

    private UserDAO() {}

    public static synchronized UserDAO getInstance() {
        if (instance == null) {
            instance = new UserDAO();
        }
        return instance;
    }

    @Override
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
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
            System.err.println("[UserDAO] Eroare findById: " + e.getMessage());
        } finally {
            closeResources(rs, ps);
        }
        return null;
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Eroare findByEmail: " + e.getMessage());
        } finally {
            closeResources(rs, ps);
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Eroare findAll: " + e.getMessage());
        } finally {
            closeResources(rs, ps);
        }
        return users;
    }

    public List<Customer> findAllCustomers() {
        String sql = "SELECT * FROM users WHERE role = 'CUSTOMER'";
        List<Customer> customers = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                customers.add((Customer) mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Eroare findAllCustomers: " + e.getMessage());
        } finally {
            closeResources(rs, ps);
        }
        return customers;
    }

    public List<DeliveryMan> findAllDeliveryMen() {
        String sql = "SELECT * FROM users WHERE role = 'DELIVERY_MAN'";
        List<DeliveryMan> deliveryMen = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                deliveryMen.add((DeliveryMan) mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Eroare findAllDeliveryMen: " + e.getMessage());
        } finally {
            closeResources(rs, ps);
        }
        return deliveryMen;
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (role, first_name, second_name, phone, email, password, wallet_id, " +
                     "delivery_address, location_id, loyalty_points, license_plate, vehicle_type, current_location_id, available) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = null;
        try {
            // Salvam wallet-ul mai intai
            if (user.getWallet().getWalletId() == 0) {
                WalletDAO.getInstance().save(user.getWallet());
            }

            ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Determinam rolul
            String role;
            if (user instanceof Customer) role = "CUSTOMER";
            else if (user instanceof DeliveryMan) role = "DELIVERY_MAN";
            else role = "ADMIN";

            ps.setString(1, role);
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getSecondName());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPassword());
            ps.setInt(7, user.getWallet().getWalletId());

            if (user instanceof Customer) {
                Customer c = (Customer) user;
                ps.setString(8, c.getDeliveryAddress());
                if (c.getLocation() != null) {
                    if (c.getLocation().getLocationId() == 0) {
                        LocationDAO.getInstance().save(c.getLocation());
                    }
                    ps.setInt(9, c.getLocation().getLocationId());
                } else {
                    ps.setNull(9, Types.INTEGER);
                }
                ps.setInt(10, c.getLoyaltyPoints());
                ps.setNull(11, Types.VARCHAR);
                ps.setNull(12, Types.VARCHAR);
                ps.setNull(13, Types.INTEGER);
                ps.setNull(14, Types.BOOLEAN);
            } else if (user instanceof DeliveryMan) {
                DeliveryMan d = (DeliveryMan) user;
                ps.setNull(8, Types.VARCHAR);
                ps.setNull(9, Types.INTEGER);
                ps.setInt(10, 0);
                ps.setString(11, d.getLicensePlate());
                ps.setString(12, d.getVehicleType().name());
                if (d.getCurrentLocation() != null) {
                    if (d.getCurrentLocation().getLocationId() == 0) {
                        LocationDAO.getInstance().save(d.getCurrentLocation());
                    }
                    ps.setInt(13, d.getCurrentLocation().getLocationId());
                } else {
                    ps.setNull(13, Types.INTEGER);
                }
                ps.setBoolean(14, d.isAvailable());
            } else {
                // Admin
                ps.setNull(8, Types.VARCHAR);
                ps.setNull(9, Types.INTEGER);
                ps.setInt(10, 0);
                ps.setNull(11, Types.VARCHAR);
                ps.setNull(12, Types.VARCHAR);
                ps.setNull(13, Types.INTEGER);
                ps.setNull(14, Types.BOOLEAN);
            }

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                user.setUserId(keys.getInt(1));
            }
            System.out.println("[UserDAO] Utilizator salvat cu ID: " + user.getUserId() + " (" + role + ")");
        } catch (SQLException e) {
            System.err.println("[UserDAO] Eroare save: " + e.getMessage());
        } finally {
            closeResources(null, ps);
        }
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE users SET first_name = ?, second_name = ?, phone = ?, email = ?, password = ?, " +
                     "delivery_address = ?, location_id = ?, loyalty_points = ?, license_plate = ?, " +
                     "vehicle_type = ?, current_location_id = ?, available = ? WHERE user_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getSecondName());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPassword());

            if (user instanceof Customer) {
                Customer c = (Customer) user;
                ps.setString(6, c.getDeliveryAddress());
                if (c.getLocation() != null) {
                    ps.setInt(7, c.getLocation().getLocationId());
                } else {
                    ps.setNull(7, Types.INTEGER);
                }
                ps.setInt(8, c.getLoyaltyPoints());
                ps.setNull(9, Types.VARCHAR);
                ps.setNull(10, Types.VARCHAR);
                ps.setNull(11, Types.INTEGER);
                ps.setNull(12, Types.BOOLEAN);
            } else if (user instanceof DeliveryMan) {
                DeliveryMan d = (DeliveryMan) user;
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.INTEGER);
                ps.setInt(8, 0);
                ps.setString(9, d.getLicensePlate());
                ps.setString(10, d.getVehicleType().name());
                if (d.getCurrentLocation() != null) {
                    ps.setInt(11, d.getCurrentLocation().getLocationId());
                } else {
                    ps.setNull(11, Types.INTEGER);
                }
                ps.setBoolean(12, d.isAvailable());
            } else {
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.INTEGER);
                ps.setInt(8, 0);
                ps.setNull(9, Types.VARCHAR);
                ps.setNull(10, Types.VARCHAR);
                ps.setNull(11, Types.INTEGER);
                ps.setNull(12, Types.BOOLEAN);
            }

            ps.setInt(13, user.getUserId());
            ps.executeUpdate();
            System.out.println("[UserDAO] Utilizator actualizat ID: " + user.getUserId());
        } catch (SQLException e) {
            System.err.println("[UserDAO] Eroare update: " + e.getMessage());
        } finally {
            closeResources(null, ps);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        PreparedStatement ps = null;
        try {
            ps = getConnection().prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("[UserDAO] Utilizator sters ID: " + id);
        } catch (SQLException e) {
            System.err.println("[UserDAO] Eroare delete: " + e.getMessage());
        } finally {
            closeResources(null, ps);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        String firstName = rs.getString("first_name");
        String secondName = rs.getString("second_name");
        String phone = rs.getString("phone");
        String email = rs.getString("email");
        String password = rs.getString("password");
        int walletId = rs.getInt("wallet_id");

        Wallet wallet = WalletDAO.getInstance().findById(walletId);

        User user;
        switch (role) {
            case "CUSTOMER":
                int locationId = rs.getInt("location_id");
                Location location = locationId > 0 ? LocationDAO.getInstance().findById(locationId) : null;
                String deliveryAddress = rs.getString("delivery_address");

                Customer customer = new Customer(firstName, secondName, phone, email, password, deliveryAddress, location);
                customer.setUserId(rs.getInt("user_id"));
                if (wallet != null) customer.setWallet(wallet);
                // loyalty_points - se incarca dar nu se seteaza direct (se adauga prin metode)
                user = customer;
                break;

            case "DELIVERY_MAN":
                String licensePlate = rs.getString("license_plate");
                String vehicleTypeStr = rs.getString("vehicle_type");
                VehicleType vehicleType = vehicleTypeStr != null ? VehicleType.valueOf(vehicleTypeStr) : VehicleType.BICYCLE;
                int currentLocId = rs.getInt("current_location_id");
                Location currentLoc = currentLocId > 0 ? LocationDAO.getInstance().findById(currentLocId) : null;

                DeliveryMan deliveryMan = new DeliveryMan(firstName, secondName, phone, email, password, licensePlate, vehicleType, currentLoc);
                deliveryMan.setUserId(rs.getInt("user_id"));
                deliveryMan.setAvailable(rs.getBoolean("available"));
                if (wallet != null) deliveryMan.setWallet(wallet);
                user = deliveryMan;
                break;

            default: // ADMIN
                Admin admin = new Admin(firstName, secondName, phone, email, password);
                admin.setUserId(rs.getInt("user_id"));
                if (wallet != null) admin.setWallet(wallet);
                user = admin;
                break;
        }
        return user;
    }
}

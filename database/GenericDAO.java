package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class GenericDAO<T> {

    protected Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public abstract T findById(int id);

    public abstract List<T> findAll();

    public abstract void save(T entity);

    public abstract void update(T entity);

    public abstract void delete(int id);

    protected void closeResources(ResultSet rs, PreparedStatement ps) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            System.err.println("[DAO] Eroare la inchidere ResultSet: " + e.getMessage());
        }
        try {
            if (ps != null) ps.close();
        } catch (SQLException e) {
            System.err.println("[DAO] Eroare la inchidere PreparedStatement: " + e.getMessage());
        }
    }
}

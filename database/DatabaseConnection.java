package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    //IMPORTANT edit the following according to yout MariaDB database
    private static final String URL = "jdbc:mariadb://localhost:3306/glovo_db";
    private static final String USER = "USERNAME";
    private static final String PASSWORD = "PASSWORD";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DB] Conexiune reusita la baza de date.");
        } catch (SQLException e) {
            System.err.println("[DB] Eroare la conectare: " + e.getMessage());
            throw new RuntimeException("Nu s-a putut conecta la baza de date.", e);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null || instance.isConnectionClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("[DB] Eroare la reconectare: " + e.getMessage());
            throw new RuntimeException("Nu s-a putut reconecta la baza de date.", e);
        }
        return connection;
    }

    private boolean isConnectionClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Conexiune inchisa.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Eroare la inchidere conexiune: " + e.getMessage());
        }
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author demi
 * Singleton-style JDBC connection manager.
 * All DAOs call DBConnection.getConnection() to get the shared connection.
 */
public class DBConnection {

    // Docker MySQL default
    private static final String URL = "jdbc:mysql://localhost:3306/bloodlink";
    private static final String USER = "root";
    private static final String PASSWORD = "my-secret-pw";

    // Single shared connection instance
    private static Connection connection = null;

    /**
     * Returns the shared database connection.
     * Creates it on first call; reuses it afterwards.
     * Automatically reconnects if the previous connection was closed.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load the MySQL JDBC driver explicitly
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connected successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Did you add the JAR to the project?");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed. Is your Docker container running?");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Closes the connection gracefully.
     * Call this when the application exits.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

public static void main(String[] args) {
    Connection conn = DBConnection.getConnection();
    System.out.println(conn != null ? "SUCCESS" : "FAILED");
}
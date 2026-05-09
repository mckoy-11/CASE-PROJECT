package com.wastely.database;

import java.sql.Connection;
import java.sql.SQLException;
import com.wastely.dao.AccountDao;
import com.wastely.service.AuthService;

/**
 * Manages database initialization and access for the application.
 * Provides centralized access to database connections and services.
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private AuthService authService;
    private Connection connection;
    
    private DatabaseManager() {
        try {
            // Initialize database connection
            this.connection = SQLConnection.getConnection();
            
            // Initialize auth service with database access
            AccountDao accountDao = new AccountDao(connection);
            this.authService = new AuthService(accountDao);
            
            System.out.println("Database connection initialized successfully");
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get the singleton instance of DatabaseManager.
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Get the AuthService for authentication operations.
     */
    public AuthService getAuthService() {
        return authService;
    }
    
    /**
     * Get a fresh database connection.
     */
    public Connection getConnection() throws SQLException {
        return SQLConnection.getConnection();
    }
    
    /**
     * Check if database is connected.
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Close the database connection.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}

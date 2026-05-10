package com.wastely;

import javax.swing.*;
import java.awt.*;
import com.wastely.database.DatabaseManager;
import com.wastely.models.AppSession;
import com.wastely.views.screens.LoginScreen;
import com.wastely.views.screens.SignupScreen;
import com.wastely.views.screens.WelcomePage;
import com.wastely.views.layouts.MenroLayout;
import com.wastely.views.layouts.BarangayLayout;

/**
 * Main application frame for WASTELY Waste Management System.
 * Manages navigation between different screens and layouts.
 * Initializes database connection on startup.
 */
public class WastelyApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private static final String WELCOME_PAGE = "WELCOME_PAGE";
    private static final String LOGIN_SCREEN = "LOGIN";
    private static final String SIGNUP_SCREEN = "SIGNUP";
    private static final String MENRO_LAYOUT = "MENRO";
    private static final String BARANGAY_LAYOUT = "BARANGAY";
    
    public WastelyApp() {
        // Initialize database connection early
        initializeDatabase();
        
        // Frame setup
        setTitle("WASTELY - Waste Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setLocationRelativeTo(null);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Use CardLayout for screen switching
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.WHITE);
        
        // Add screens
        cardPanel.add(new WelcomePage(this), WELCOME_PAGE);
        cardPanel.add(new LoginScreen(this), LOGIN_SCREEN);
        cardPanel.add(new SignupScreen(this), SIGNUP_SCREEN);
        cardPanel.add(new MenroLayout(this), MENRO_LAYOUT);
        cardPanel.add(new BarangayLayout(this), BARANGAY_LAYOUT);
        
        add(cardPanel);
        
        showWelcomePage();
    }
    
    /**
     * Initialize database connection and services.
     */
    private void initializeDatabase() {
        try {
            DatabaseManager dbManager = DatabaseManager.getInstance();
            if (dbManager.isConnected()) {
                System.out.println("✓ Database initialized successfully");
            } else {
                System.err.println("✗ Failed to initialize database connection");
                JOptionPane.showMessageDialog(null,
                        "Failed to connect to database.\nPlease check your database configuration and try again.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            System.err.println("Error during database initialization: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Database initialization error: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Show the login screen.
     */
    public void showWelcomePage() {
        AppSession.getInstance().logout();
        cardLayout.show(cardPanel, WELCOME_PAGE);
    }

    /**
     * Show the login screen.
     */
    public void showLoginScreen() {
        AppSession.getInstance().logout();
        cardLayout.show(cardPanel, LOGIN_SCREEN);
    }
    
    /**
     * Show the sign up screen.
     */
    public void showSignupScreen() {
        cardLayout.show(cardPanel, SIGNUP_SCREEN);
    }
    
    /**
     * Show MENRO admin dashboard.
     */
    public void showMenroLayout() {
        if (AppSession.getInstance().isMenroAdmin()) {
            // Recreate the layout to reset state
            cardPanel.remove(1);  // Remove old MENRO layout
            cardPanel.add(new MenroLayout(this), MENRO_LAYOUT);
            cardLayout.show(cardPanel, MENRO_LAYOUT);
        }
    }
    
    /**
     * Show Barangay admin dashboard.
     */
    public void showBarangayLayout() {
        if (AppSession.getInstance().isBarangayAdmin()) {
            // Recreate the layout to reset state
            cardPanel.remove(2);  // Remove old Barangay layout
            cardPanel.add(new BarangayLayout(this), BARANGAY_LAYOUT);
            cardLayout.show(cardPanel, BARANGAY_LAYOUT);
        }
    }
    
    /**
     * Entry point of the application.
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WastelyApp app = new WastelyApp();
            app.setVisible(true);
            
            // Add shutdown hook to cleanup database connection
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                DatabaseManager.getInstance().closeConnection();
                System.out.println("Application closed. Database connection closed.");
            }));
        });
    }
}

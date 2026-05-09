package com.wastely.views.layouts;

import java.awt.*;
import javax.swing.*;

import com.wastely.WastelyApp;
import com.wastely.assets.Loader;
import com.wastely.views.components.Sidebar;
import com.wastely.views.components.TopNav;
import com.wastely.views.pages.menro.*;

/**
 * Base layout for MENRO admin dashboard.
 * Provides sidebar, topnav, and main content area.
 */
public class MenroLayout extends JPanel {
    private WastelyApp app;
    private JPanel contentPanel;
    private Sidebar sidebar;
    private CardLayout contentCardLayout;
    
    public MenroLayout(WastelyApp app) {
        this.app = app;
        setupLayout();
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Sidebar
        sidebar = new Sidebar(this::handleNavigation);
        
        // Build menu for MENRO
        setupMenroMenu();
        
        add(sidebar, BorderLayout.WEST);
        
        // Top panel: TopNav + Content area
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(Color.WHITE);
        
        // TopNav
        TopNav topNav = new TopNav(() -> {
            app.showLoginScreen();
        });
        topSection.add(topNav, BorderLayout.NORTH);
        
        // Content area with CardLayout for switching pages
        contentCardLayout = new CardLayout();
        contentPanel = new JPanel(contentCardLayout);
        contentPanel.setBackground(new Color(240, 249, 245));  // Light green background
        
        // Add placeholder pages
        addContentPages();
        
        topSection.add(contentPanel, BorderLayout.CENTER);
        add(topSection, BorderLayout.CENTER);
    }
    
    private void setupMenroMenu() {
        sidebar.addMenuItem("home", Loader.loadIcon("house-white.png", 20),  "Home");
        sidebar.addMenuItem("schedule", Loader.loadIcon("calendar-white.png", 20), "Schedule");
        sidebar.addMenuItem("barangay", Loader.loadIcon("pin-house-white.png", 20), "Barangay");
        sidebar.addMenuItem("management" , Loader.loadIcon("user-cog-white.png", 20), "Management");
        sidebar.addMenuItem("service" , Loader.loadIcon("mail.png", 20), "Service Management");
        sidebar.addMenuItem("users", Loader.loadIcon("user-cog-white.png", 20), "Users");
        sidebar.addMenuItem("announcements", Loader.loadIcon("send-white.png", 20), "Announcements");
    }
    
    private void addContentPages() {
        // Add actual page implementations
        contentPanel.add(new MenroHomePage(), "home");
        contentPanel.add(new SchedulePage(), "schedule");
        contentPanel.add(new BarangayPage(), "barangay");
        contentPanel.add(new ManagementPage(), "management");
        contentPanel.add(new ServicePage(), "service");
        contentPanel.add(new UsersPage(), "users");
        contentPanel.add(new AnnouncementsPage(), "announcements");
        
        // Show home page by default
        contentCardLayout.show(contentPanel, "home");
    }
    
    private void handleNavigation(String pageId) {
        sidebar.setActiveMenuItem(pageId);
        contentCardLayout.show(contentPanel, pageId);
    }
}

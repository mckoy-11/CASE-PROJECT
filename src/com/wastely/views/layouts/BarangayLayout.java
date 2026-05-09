package com.wastely.views.layouts;

import javax.swing.*;
import java.awt.*;
import com.wastely.WastelyApp;
import com.wastely.assets.Loader;
import com.wastely.views.components.Sidebar;
import com.wastely.views.components.TopNav;
import com.wastely.views.pages.barangay.*;

/**
 * Base layout for Barangay admin dashboard.
 * Provides sidebar, topnav, and main content area.
 */
public class BarangayLayout extends JPanel {
    private WastelyApp app;
    private JPanel contentPanel;
    private Sidebar sidebar;
    private CardLayout contentCardLayout;
    
    public BarangayLayout(WastelyApp app) {
        this.app = app;
        setupLayout();
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Sidebar
        sidebar = new Sidebar(this::handleNavigation);
        
        // Build menu for Barangay
        setupBarangayMenu();
        
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
    
    private void setupBarangayMenu() {
        sidebar.addMenuItem("home", Loader.loadIcon(TOOL_TIP_TEXT_KEY, WIDTH), "Home");
        sidebar.addMenuItem("reports", Loader.loadIcon(TOOL_TIP_TEXT_KEY, WIDTH), "Reports");
        sidebar.addMenuItem("complaints", Loader.loadIcon(TOOL_TIP_TEXT_KEY, WIDTH), "Complaints");
        sidebar.addMenuItem("requests", Loader.loadIcon(TOOL_TIP_TEXT_KEY, WIDTH), "Requests");
        sidebar.addMenuItem("details", Loader.loadIcon(TOOL_TIP_TEXT_KEY, WIDTH), "Details");
    }
    
    private void addContentPages() {
        // Add actual page implementations
        contentPanel.add(new BarangayHomePage(), "home");
        contentPanel.add(new BarangayReportPage(), "reports");
        contentPanel.add(new BarangayComplaintPage(), "complaints");
        contentPanel.add(new BarangayRequestPage(), "requests");
        contentPanel.add(new BarangayDetailsPage(), "details");
        
        // Show home page by default
        contentCardLayout.show(contentPanel, "home");
    }
    
    private void handleNavigation(String pageId) {
        sidebar.setActiveMenuItem(pageId);
        contentCardLayout.show(contentPanel, pageId);
    }
}

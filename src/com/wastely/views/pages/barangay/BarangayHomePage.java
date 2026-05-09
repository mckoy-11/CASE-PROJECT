package com.wastely.views.pages.barangay;

import javax.swing.*;
import java.awt.*;
import com.wastely.utils.Colors;
import com.wastely.utils.Typography;
import com.wastely.views.components.Card;

/**
 * Barangay Home Dashboard Page
 * Displays barangay-specific overview and statistics.
 */
public class BarangayHomePage extends JPanel {
    
    public BarangayHomePage() {
        setupPage();
    }
    
    private void setupPage() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 249, 245));
        
        // Main content area
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(240, 249, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Page header
        JLabel headerLabel = new JLabel("Barangay Dashboard");
        headerLabel.setFont(Typography.HEADING_H1);
        headerLabel.setForeground(Colors.TEXT_PRIMARY);
        contentPanel.add(headerLabel);
        
        JLabel subtitleLabel = new JLabel("Welcome! Here's your barangay waste management overview.");
        subtitleLabel.setFont(Typography.LABEL);
        subtitleLabel.setForeground(Colors.TEXT_SECONDARY);
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(30));
        
        // KPI Stats Grid (4 cards for barangay)
        contentPanel.add(new JLabel("Barangay Metrics"));
        contentPanel.add(Box.createVerticalStrut(15));
        
        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 15, 15));
        statsGrid.setBackground(new Color(240, 249, 245));
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        statsGrid.add(Card.createStatCard("Total Requests", 245, "requests", "+5%", null));
        statsGrid.add(Card.createStatCard("Total Complaints", 42, "complaints", "-2%", null));
        statsGrid.add(Card.createStatCard("Pending Items", 18, "items", null, null));
        statsGrid.add(Card.createStatCard("Resolved This Month", 156, "items", "+12%", null));
        
        contentPanel.add(statsGrid);
        contentPanel.add(Box.createVerticalStrut(30));
        
        // Recent activity
        contentPanel.add(createActivityPanel());
        contentPanel.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(15);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createActivityPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colors.BORDER_GRAY, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        
        JLabel activityTitle = new JLabel("Recent Activity");
        activityTitle.setFont(Typography.HEADING_H3);
        activityTitle.setForeground(Colors.TEXT_PRIMARY);
        panel.add(activityTitle);
        panel.add(Box.createVerticalStrut(15));
        
        addActivityItem(panel, "📋", "New service request submitted", "2 hours ago");
        addActivityItem(panel, "⚠️", "Complaint resolved", "5 hours ago");
        addActivityItem(panel, "✅", "Collection completed", "1 day ago");
        addActivityItem(panel, "📧", "New announcement from MENRO", "2 days ago");
        
        return panel;
    }
    
    private void addActivityItem(JPanel panel, String icon, String description, String time) {
        JPanel item = new JPanel(new BorderLayout(10, 0));
        item.setBackground(Color.WHITE);
        item.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        item.add(iconLabel, BorderLayout.WEST);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(Typography.BODY_NORMAL);
        descLabel.setForeground(Colors.TEXT_PRIMARY);
        
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(Typography.SMALL_TEXT);
        timeLabel.setForeground(Colors.TEXT_SECONDARY);
        
        contentPanel.add(descLabel);
        contentPanel.add(timeLabel);
        
        item.add(contentPanel, BorderLayout.CENTER);
        panel.add(item);
    }
}

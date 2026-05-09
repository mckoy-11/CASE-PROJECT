package com.wastely.views.pages.barangay;

import javax.swing.*;
import java.awt.*;
import com.wastely.utils.Colors;
import com.wastely.utils.Typography;

/**
 * Barangay Details Page
 * Displays detailed information about the barangay.
 */
public class BarangayDetailsPage extends JPanel {
    
    public BarangayDetailsPage() {
        setupPage();
    }
    
    private void setupPage() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 249, 245));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(240, 249, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header
        JLabel titleLabel = new JLabel("Barangay Information");
        titleLabel.setFont(Typography.HEADING_H1);
        titleLabel.setForeground(Colors.TEXT_PRIMARY);
        contentPanel.add(titleLabel);
        
        contentPanel.add(Box.createVerticalStrut(30));
        
        // Details cards
        contentPanel.add(createInfoCard("Barangay Bagong Bayan"));
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createDetailCard("Basic Information", 
            new String[][]{
                {"Barangay Name:", "Bagong Bayan"},
                {"Administrator:", "Maria Cruz"},
                {"Contact Number:", "555-0001"},
                {"Email Address:", "bagongbayan@wastely.gov.ph"},
                {"Population:", "15,234"}
            }
        ));
        
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createDetailCard("Waste Management Information",
            new String[][]{
                {"Total Requests:", "245"},
                {"Pending Requests:", "18"},
                {"Total Complaints:", "42"},
                {"Resolved Complaints:", "35"},
                {"Last Collection:", "2026-05-06"}
            }
        ));
        
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createDetailCard("Service Schedule",
            new String[][]{
                {"Collection Days:", "Monday, Wednesday, Friday"},
                {"Collection Time:", "6:00 AM - 12:00 PM"},
                {"Assigned Team:", "Team Alpha"},
                {"Assigned Truck:", "TR-001 (Compactor)"},
                {"Next Collection:", "2026-05-08"}
            }
        ));
        
        contentPanel.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(15);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createInfoCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Colors.PRIMARY_GREEN);
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Typography.HEADING_H2);
        titleLabel.setForeground(Color.WHITE);
        
        JLabel iconLabel = new JLabel("🏘️");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 40));
        
        card.add(iconLabel, BorderLayout.WEST);
        card.add(titleLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createDetailCard(String title, String[][] details) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colors.BORDER_GRAY, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        
        JLabel cardTitle = new JLabel(title);
        cardTitle.setFont(Typography.HEADING_H3);
        cardTitle.setForeground(Colors.TEXT_PRIMARY);
        card.add(cardTitle);
        card.add(Box.createVerticalStrut(15));
        
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridLayout(details.length, 2, 20, 10));
        detailsPanel.setBackground(Color.WHITE);
        
        for (String[] row : details) {
            JLabel keyLabel = new JLabel(row[0]);
            keyLabel.setFont(Typography.LABEL_MEDIUM);
            keyLabel.setForeground(Colors.TEXT_SECONDARY);
            detailsPanel.add(keyLabel);
            
            JLabel valueLabel = new JLabel(row[1]);
            valueLabel.setFont(Typography.BODY_NORMAL);
            valueLabel.setForeground(Colors.TEXT_PRIMARY);
            detailsPanel.add(valueLabel);
        }
        
        card.add(detailsPanel);
        return card;
    }
}

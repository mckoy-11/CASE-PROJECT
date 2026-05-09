package com.wastely.views.pages.menro;

import javax.swing.*;
import java.awt.*;
import com.wastely.utils.Colors;
import com.wastely.utils.Typography;
import com.wastely.views.components.CustomButton;
import com.wastely.views.components.CustomTextField;

/**
 * Announcements Page for MENRO Dashboard
 * Displays and allows creation of public announcements.
 */
public class AnnouncementsPage extends JPanel {
    
    public AnnouncementsPage() {
        setupPage();
    }
    
    private void setupPage() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 249, 245));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 249, 245));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel("Announcements");
        titleLabel.setFont(Typography.HEADING_H1);
        titleLabel.setForeground(Colors.TEXT_PRIMARY);
        
        CustomButton addBtn = new CustomButton("+ New Announcement", CustomButton.ButtonStyle.PRIMARY);
        addBtn.addActionListener(e -> handleNewAnnouncement());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(addBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(240, 249, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 30, 30));
        
        // Sample announcements
        addAnnouncementCard(contentPanel, "System Maintenance Notice", 
            "The WASTELY system will undergo maintenance on May 10, 2026 from 10:00 PM to 2:00 AM. Please plan accordingly.",
            "2026-05-06", "Active");
        
        addAnnouncementCard(contentPanel, "New Collection Schedule",
            "New collection schedule for Q2 2026 has been released. Please check the Schedule page for details.",
            "2026-05-01", "Active");
        
        addAnnouncementCard(contentPanel, "Training Program",
            "Mandatory training for all personnel on new waste segregation guidelines will be held on May 15, 2026.",
            "2026-04-25", "Active");
        
        addAnnouncementCard(contentPanel, "Fleet Update",
            "5 new waste collection vehicles have been added to the fleet. Assignment details available in the Trucks section.",
            "2026-04-20", "Active");
        
        contentPanel.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(15);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void addAnnouncementCard(JPanel container, String title, String content, String date, String status) {

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER_GRAY, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // FIX: stable width behavior (prevents stretching issues)
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Typography.HEADING_H3);
        titleLabel.setForeground(Colors.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLabel);

        card.add(Box.createVerticalStrut(10));

        // Content (FIXED layout behavior)
        JTextArea contentArea = new JTextArea(content);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        contentArea.setOpaque(false);
        contentArea.setFont(Typography.BODY_NORMAL);
        contentArea.setForeground(Colors.TEXT_SECONDARY);
        contentArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setBorder(null);
        contentScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        contentScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        contentScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentScroll.setPreferredSize(new Dimension(0, 60));

        card.add(contentScroll);

        card.add(Box.createVerticalStrut(10));

        // Footer FIX: replace FlowLayout with BorderLayout (stable alignment)
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = new JLabel("📅 " + date);
        dateLabel.setFont(Typography.SMALL_TEXT);
        dateLabel.setForeground(Colors.TEXT_SECONDARY);

        JLabel statusLabel = new JLabel("Status: " + status);
        statusLabel.setFont(Typography.SMALL_MEDIUM);
        statusLabel.setForeground(Colors.STATUS_COMPLETED);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setBackground(Color.WHITE);
        left.add(dateLabel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setBackground(Color.WHITE);
        right.add(statusLabel);

        footerPanel.add(left, BorderLayout.WEST);
        footerPanel.add(right, BorderLayout.EAST);

        card.add(footerPanel);

        container.add(card);
        container.add(Box.createVerticalStrut(15));
    }
    
    private void handleNewAnnouncement() {
        JFrame dialog = new JFrame("Create Announcement");
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        JLabel titleFieldLabel = new JLabel("Title:");
        titleFieldLabel.setFont(Typography.LABEL_MEDIUM);
        titleFieldLabel.setForeground(Colors.TEXT_PRIMARY);
        panel.add(titleFieldLabel);
        panel.add(Box.createVerticalStrut(5));
        
        CustomTextField titleField = new CustomTextField("Announcement title...");
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.add(titleField);
        panel.add(Box.createVerticalStrut(15));
        
        JLabel contentFieldLabel = new JLabel("Content:");
        contentFieldLabel.setFont(Typography.LABEL_MEDIUM);
        contentFieldLabel.setForeground(Colors.TEXT_PRIMARY);
        panel.add(contentFieldLabel);
        panel.add(Box.createVerticalStrut(5));
        
        JTextArea contentArea = new JTextArea(5, 40);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setFont(Typography.BODY_NORMAL);
        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        panel.add(scrollPane);
        panel.add(Box.createVerticalStrut(20));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        CustomButton publishBtn = new CustomButton("Publish", CustomButton.ButtonStyle.PRIMARY);
        CustomButton cancelBtn = new CustomButton("Cancel", CustomButton.ButtonStyle.SECONDARY);
        
        cancelBtn.addActionListener(ev -> dialog.dispose());
        publishBtn.addActionListener(ev -> {
            JOptionPane.showMessageDialog(dialog, "Announcement published successfully!");
            dialog.dispose();
        });
        
        buttonPanel.add(publishBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel);
        
        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }
}

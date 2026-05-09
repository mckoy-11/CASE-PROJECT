package com.wastely.views.components;

import com.wastely.assets.Loader;
import javax.swing.*;
import java.awt.*;
import com.wastely.utils.Colors;
import com.wastely.utils.Typography;
import com.wastely.models.AppSession;

/**
 * Top navigation bar component.
 * Displays search, notifications, and user profile menu.
 */
public class TopNav extends JPanel {
    private Runnable logoutCallback;
    
    public TopNav(Runnable logoutCallback) {
        this.logoutCallback = logoutCallback;
        setupTopNav();
    }
    
    private void setupTopNav() {
        setLayout(new BorderLayout(20, 0));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(0, 80));  // h-20
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_GRAY));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_GRAY),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Left side: Search bar
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.CENTER);
        
        // Right side: Notifications and User menu
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        rightPanel.setBackground(Color.WHITE);
        
        // Notification icon
        JButton notifBtn = new JButton(Loader.loadIcon("bell.png", 30));
        notifBtn.setFont(new Font("Arial", Font.PLAIN, 20));
        notifBtn.setBackground(Color.WHITE);
        notifBtn.setForeground(Colors.TEXT_PRIMARY);
        notifBtn.setBorder(null);
        notifBtn.setFocusPainted(false);
        notifBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add notification badge (red dot)
        JPanel notifPanel = new JPanel(new BorderLayout());
        notifPanel.setBackground(Color.WHITE);
        notifPanel.add(notifBtn);
        
        rightPanel.add(notifPanel);
        
        // User menu
        JButton userBtn = new JButton();
        userBtn.setBackground(Color.WHITE);
        userBtn.setBorder(null);
        userBtn.setFocusPainted(false);
        userBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        String userName = AppSession.getInstance().getCurrentUser() != null ? 
            AppSession.getInstance().getCurrentUser().getFullName() : "User";
        userBtn.setIcon(Loader.loadIcon("account.png", 30));
        userBtn.setText(userName);
        userBtn.setFont(Typography.LABEL);
        userBtn.setForeground(Colors.TEXT_PRIMARY);
        
        // User dropdown menu
        JPopupMenu userMenu = new JPopupMenu();
        userMenu.setBackground(Color.WHITE);
        userMenu.setBorder(BorderFactory.createLineBorder(Colors.BORDER_GRAY, 1));
        
        JMenuItem changePasswordItem = new JMenuItem("Change Password");
        changePasswordItem.setFont(Typography.LABEL);
        changePasswordItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(TopNav.this, 
                "Change Password feature not yet implemented.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
        });
        userMenu.add(changePasswordItem);
        
        JMenuItem changeEmailItem = new JMenuItem("Change Email");
        changeEmailItem.setFont(Typography.LABEL);
        changeEmailItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(TopNav.this,
                "Change Email feature not yet implemented.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
        });
        userMenu.add(changeEmailItem);
        
        userMenu.addSeparator();
        
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setFont(Typography.LABEL);
        logoutItem.setForeground(Colors.BUTTON_DANGER);
        logoutItem.addActionListener(e -> {
            if (logoutCallback != null) {
                logoutCallback.run();
            }
        });
        userMenu.add(logoutItem);
        
        userBtn.addActionListener(e -> {
            userMenu.show(userBtn, 0, userBtn.getHeight());
        });
        
        rightPanel.add(userBtn);
        add(rightPanel, BorderLayout.EAST);
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);

        CustomTextField searchField = new CustomTextField("search.png", "Search...", new Dimension(600, 35));
        searchField.setCornerRadius(45);
        searchField.installClickOutsideToUnfocus(this);
        searchPanel.add(searchField, BorderLayout.CENTER);
        return searchPanel;
    }
}

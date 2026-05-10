package com.wastely.views.components;

import com.wastely.assets.Loader;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.wastely.utils.Colors;
import com.wastely.utils.ComponentStyle;
import com.wastely.utils.Typography;

/**
 * Sidebar navigation component.
 * Displays menu items and handles navigation.
 */
public class Sidebar extends JPanel {
    private JPanel menuPanel;
    private NavItemListener navItemListener;
    
    @FunctionalInterface
    public interface NavItemListener {
        void onNavItemClicked(String itemId);
    }
    
    public Sidebar(NavItemListener listener) {
        this.navItemListener = listener;
        setupSidebar();
    }
    
    private void setupSidebar() {
        setLayout(new BorderLayout());
        setBackground(Colors.SECONDARY_LIGHT_GREEN);
        setPreferredSize(new Dimension(256, 0));  // w-64
        
        // Header with logo
        JPanel headerPanel = ComponentStyle.createTransparentPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        
        JLabel logoLabel = new JLabel(Loader.loadIcon("leafv2.png", 30));
        logoLabel.setFont(new Font("Arial", Font.BOLD, 28));
        
        JLabel titleLabel = new JLabel("WASTELY");
        titleLabel.setFont(Typography.HEADING_H3);
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(logoLabel);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);
        
        // Menu items
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Colors.SECONDARY_LIGHT_GREEN);
        
        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Colors.SECONDARY_LIGHT_GREEN);
        scrollPane.getViewport().setBackground(Colors.SECONDARY_LIGHT_GREEN);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Add navigation menu items.
     * @param id
     * @param icon
     * @param label
     */
    public void addMenuItem(String id, ImageIcon icon, String label) {
        NavMenuItem menuItem = new NavMenuItem(id, icon, label, navItemListener);
        menuItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        menuPanel.add(menuItem);
        menuPanel.add(Box.createVerticalStrut(0));
    }
    
    /**
     * Clear all menu items.
     */
    public void clearMenu() {
        menuPanel.removeAll();
        menuPanel.revalidate();
        menuPanel.repaint();
    }
    
    /**
     * Set active menu item.
     * @param id
     */
    public void setActiveMenuItem(String id) {
        for (Component comp : menuPanel.getComponents()) {
            if (comp instanceof NavMenuItem) {
                NavMenuItem item = (NavMenuItem) comp;
                item.setActive(item.getId().equals(id));
            }
        }
    }
    
    /**
     * Individual navigation menu item.
     */
    private static class NavMenuItem extends JPanel {
        private final String id;
        private boolean isActive;
        private final JLabel labelComponent;
        
        public NavMenuItem(String id, ImageIcon icon, String label, NavItemListener listener) {
            this.id = id;
            this.isActive = false;
            
            setLayout(new BorderLayout());
            setBackground(Colors.SECONDARY_LIGHT_GREEN);
            
            labelComponent = new JLabel(label);
            labelComponent.setIcon(icon);
            labelComponent.setFont(Typography.LABEL_MEDIUM);
            labelComponent.setForeground(Color.WHITE);
            labelComponent.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
            
            add(labelComponent, BorderLayout.CENTER);
            
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    listener.onNavItemClicked(id);
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!isActive) {
                        setBackground(Colors.BACKGROUND_WHITE);  // Hover color
                        labelComponent.setForeground(Colors.SECONDARY_GREEN);
                        repaint();
                    }
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    if (!isActive) {
                        setBackground(Colors.SECONDARY_LIGHT_GREEN);
                        labelComponent.setForeground(Color.WHITE);
                        repaint();
                    }
                }
            });
        }
        
        public String getId() {
            return id;
        }
        
        public void setActive(boolean active) {
            this.isActive = active;
            if (active) {
                setBackground(Colors.BACKGROUND_WHITE);  // White background when selected
                labelComponent.setForeground(Colors.SECONDARY_GREEN);  // Green text for contrast
            } else {
                setBackground(Colors.SECONDARY_LIGHT_GREEN);
                labelComponent.setForeground(Color.WHITE);
            }
            repaint();
        }
    }
}

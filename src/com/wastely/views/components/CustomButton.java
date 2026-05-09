package com.wastely.views.components;

import javax.swing.*;
import java.awt.*;
import com.wastely.utils.Colors;
import com.wastely.utils.Typography;

/**
 * Reusable button component with consistent styling.
 */
public class CustomButton extends JButton {
    
    public enum ButtonStyle {
        PRIMARY,      // Green button for main actions
        SECONDARY,    // Gray button for secondary actions
        DANGER,       // Red button for destructive actions
        OUTLINE       // Outlined button
    }
    
    private ButtonStyle style;
    
    public CustomButton(String text, ButtonStyle style) {
        super(text);
        this.style = style;
        setupButton();
    }
    
    public CustomButton(String text) {
        this(text, ButtonStyle.PRIMARY);
    }
    
    private void setupButton() {
        setFont(Typography.BUTTON);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(true);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Set insets for padding
        setMargin(new Insets(10, 20, 10, 20));
        
        // Apply style
        applyStyle();
    }
    
    private void applyStyle() {
        switch (style) {
            case PRIMARY:
                setBackground(Colors.PRIMARY_GREEN);
                setForeground(Color.WHITE);
                setRolloverEnabled(true);
                break;
                
            case SECONDARY:
                setBackground(new Color(229, 231, 235));  // gray-200
                setForeground(Colors.TEXT_PRIMARY);
                break;
                
            case DANGER:
                setBackground(Colors.BUTTON_DANGER);
                setForeground(Color.WHITE);
                break;
                
            case OUTLINE:
                setBackground(Color.WHITE);
                setForeground(Colors.PRIMARY_GREEN);
                setBorder(BorderFactory.createLineBorder(Colors.BORDER_GRAY, 2));
                break;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw rounded background
        int radius = 10;
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        
        // Draw border if needed
        if (style == ButtonStyle.OUTLINE) {
            g2d.setColor(Colors.BORDER_GRAY);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }
        
        // Draw text
        super.paintComponent(g2d);
    }
}

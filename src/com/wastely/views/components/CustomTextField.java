package com.wastely.views.components;

import com.wastely.assets.Loader;
import com.wastely.utils.Colors;
import com.wastely.utils.Typography;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Production-ready CustomTextField
 *
 * Fixes:
 * - Stable placeholder system
 * - Correct text retrieval
 * - Safe click-outside handling
 * - Consistent icon rendering
 * - Reduced repaint overhead
 */
public class CustomTextField extends JTextField {

    private static final int DEFAULT_RADIUS = 10;
    private static final int BORDER_THICKNESS = 2;

    private String placeholder;
    private boolean showingPlaceholder;

    private Icon icon;
    private Dimension fixedSize;

    private boolean rounded = true;
    private int cornerRadius = DEFAULT_RADIUS;

    private AWTEventListener globalListener;

    private Color currentBorderColor = Colors.BORDER_GRAY;

    // ================= CONSTRUCTORS =================

    public CustomTextField(String placeholder) {
        this(null, placeholder);
    }

    public CustomTextField(String iconPath, String placeholder) {
        this.icon = iconPath != null ? Loader.loadIcon(iconPath, 18) : null;
        this.placeholder = placeholder == null ? "" : placeholder;
        init();
    }

    public CustomTextField(String iconPath, String placeholder, Dimension size) {
        this(iconPath, placeholder);
        this.fixedSize = size;
    }

    // ================= INIT =================

    private void init() {
        setFont(Typography.BODY_NORMAL);
        setForeground(Colors.TEXT_PRIMARY);
        setCaretColor(Colors.PRIMARY_GREEN);
        setOpaque(false);

        if (fixedSize != null) {
            setPreferredSize(fixedSize);
            setMinimumSize(fixedSize);
            setMaximumSize(fixedSize);
        }

        applyPlaceholder();
        setupFocusBehavior();
        updateBorder(false);
    }

    // ================= PLACEHOLDER =================

    private void applyPlaceholder() {
        if (!placeholder.isEmpty()) {
            showingPlaceholder = true;
            super.setText(placeholder);
            setForeground(Colors.DISABLED_TEXT);
        }
    }

    private void removePlaceholder() {
        if (showingPlaceholder) {
            super.setText("");
            showingPlaceholder = false;
            setForeground(Colors.TEXT_PRIMARY);
        }
    }

    public String getText() {
        return showingPlaceholder ? "" : super.getText().trim();
    }

    public String getActualText() {
        return getText();
    }

    @Override
    public void setText(String text) {
        if (text == null || text.isEmpty()) {
            applyPlaceholder();
        } else {
            showingPlaceholder = false;
            super.setText(text);
            setForeground(Colors.TEXT_PRIMARY);
        }
    }

    // ================= FOCUS =================

    private void setupFocusBehavior() {
        addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                removePlaceholder();
                updateBorder(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    applyPlaceholder();
                }
                updateBorder(false);
            }
        });
    }

    // ================= BORDER =================

    private void updateBorder(boolean focused) {
        currentBorderColor = focused ? Colors.PRIMARY_GREEN : Colors.BORDER_GRAY;

        setBorder(new RoundedBorder(
                currentBorderColor,
                rounded ? cornerRadius : 0,
                BORDER_THICKNESS
        ));
    }

    public void setRounded(boolean rounded) {
        this.rounded = rounded;
        updateBorder(hasFocus());
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = Math.max(0, radius);
        updateBorder(hasFocus());
    }

    // ================= ICON =================

    public void setIcon(Icon icon) {
        this.icon = icon;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (icon != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int iconY = (getHeight() - icon.getIconHeight()) / 2;
            icon.paintIcon(this, g2, 12, iconY);

            g2.dispose();
        }
    }

    @Override
    public Insets getInsets() {
        Insets base = super.getInsets();

        if (icon != null) {
            return new Insets(
                    base.top,
                    base.left + 28,
                    base.bottom,
                    base.right
            );
        }

        return base;
    }

    // ================= CLICK OUTSIDE =================

    public void installClickOutsideToUnfocus(JComponent root) {

        uninstallClickOutsideToUnfocus();

        globalListener = event -> {
            if (!(event instanceof MouseEvent)) return;

            MouseEvent e = (MouseEvent) event;
            if (e.getID() != MouseEvent.MOUSE_PRESSED) return;
            if (root == null || !root.isShowing()) return;

            Component source = e.getComponent();

            if (source instanceof JComboBox ||
                    SwingUtilities.getAncestorOfClass(JComboBox.class, source) != null) {
                return;
            }

            KeyboardFocusManager.getCurrentKeyboardFocusManager()
                    .clearGlobalFocusOwner();
        };

        Toolkit.getDefaultToolkit().addAWTEventListener(
                globalListener,
                AWTEvent.MOUSE_EVENT_MASK
        );
    }

    public void uninstallClickOutsideToUnfocus() {
        if (globalListener != null) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(globalListener);
            globalListener = null;
        }
    }

    @Override
    public void removeNotify() {
        uninstallClickOutsideToUnfocus();
        super.removeNotify();
    }

    // ================= BORDER CLASS =================

    public static class RoundedBorder extends AbstractBorder {

        private final Color color;
        private final int radius;
        private final int thickness;

        public RoundedBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));

            g2.drawRoundRect(x + 1, y + 1, w - 3, h - 3, radius, radius);

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(6, 6, 6, 6);
        }
    }
}
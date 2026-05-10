package com.wastely.utils;

import java.awt.*;
import javax.swing.*;

public class ComponentStyle {
    public static JPanel createTransparentPanel() {
        return createTransparentPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    }

    public static JPanel createTransparentPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }

    public static JLabel createCapsuleLabel(String text, Color background, Color foreground) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(background);
        label.setForeground(foreground);
        label.setFont(Typography.BUTTON.deriveFont(11f));
        label.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    public static JPanel createInfoItem(String label, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(Colors.TEXT_PRIMARY);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 13));

        panel.add(lbl);
        panel.add(Box.createVerticalStrut(3));
        panel.add(val);

        return panel;
    }

    public static JLabel createFormTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Typography.HEADING_H3.deriveFont(32f));
        label.setForeground(Colors.DISABLED_TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    public static JLabel createFormSubtitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Typography.HEADING_H4.deriveFont(14f));
        label.setForeground(Colors.DISABLED_TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    public static JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Typography.BUTTON.deriveFont(12f));
        label.setForeground(Colors.DISABLED_TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    public static JPanel createFieldGroup(String labelText, JComponent input) {
        JPanel group = createTransparentPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.setMaximumSize(new Dimension(300, 88));
        group.add(createFieldLabel(labelText));
        group.add(Box.createVerticalStrut(6));
        group.add(input);
        return group;
    }

    public static JPanel createSplitRow(JComponent left, JComponent right) {
        JPanel row = createTransparentPanel(new GridLayout(1, 2, 10, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(300, 88));
        row.add(left);
        row.add(right);
        return row;
    }
    
    public static JComponent wrap(JComponent c) {
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        c.setPreferredSize(new Dimension(Short.MAX_VALUE, c.getPreferredSize().height));
        c.setMinimumSize(new Dimension(0, c.getPreferredSize().height));

        return c;
    }
}

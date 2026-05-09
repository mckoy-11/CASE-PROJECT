package com.wastely.views.components;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.wastely.utils.Colors;
import com.wastely.utils.Typography;

public class Header extends JPanel {

    private final JLabel headerTitle;

    public Header(String title) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 15, 10, 15));
        setBackground(Colors.PRIMARY_GREEN);

        headerTitle = new JLabel(title);
        headerTitle.setForeground(Color.WHITE);
        headerTitle.setFont(Typography.HEADING_H3);

        add(headerTitle, BorderLayout.WEST);
    }

    public void setTitle(String title) {
        headerTitle.setText(title);
        revalidate();
        repaint();
    }
}
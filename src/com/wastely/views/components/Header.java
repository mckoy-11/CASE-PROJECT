package com.wastely.views.components;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.wastely.utils.Colors;
import com.wastely.utils.Typography;

public class Header extends JPanel {

    private final JLabel headerTitle;

    public Header(String title, boolean hasBG) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 15, 10, 15));

        headerTitle = new JLabel(title);
        headerTitle.setFont(Typography.HEADING_H3);
        
        if(hasBG) {
            setBackground(Colors.PRIMARY_GREEN);
            headerTitle.setForeground(Color.WHITE);
        } else {
            setOpaque(false);
            headerTitle.setForeground(Color.BLACK);
        }

        add(headerTitle, BorderLayout.WEST);
    }

    public void setTitle(String title) {
        headerTitle.setText(title);
        revalidate();
        repaint();
    }
}
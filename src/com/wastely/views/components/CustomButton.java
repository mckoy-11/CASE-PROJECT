package com.wastely.views.components;

import com.wastely.utils.Colors;
import com.wastely.utils.Typography;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomButton extends JButton {

    public enum ButtonStyle {
        PRIMARY,
        SECONDARY,
        DANGER,
        OUTLINE
    }

    private final ButtonStyle style;

    private Color bgDefault;
    private Color bgHover;

    private Color fgDefault;
    private Color fgHover;

    private boolean rounded = true;

    public CustomButton(String text) {
        this(text, ButtonStyle.PRIMARY);
    }

    public CustomButton(String text, ButtonStyle style) {
        this(text, style, true);
    }

    public CustomButton(String text, ButtonStyle style, boolean rounded) {
        super(text);
        this.style = style;
        this.rounded = rounded;

        initializeComponent();
        initializeStyle();
        initializeHoverEffect();
    }

    private void initializeComponent() {
        setFont(Typography.BUTTON);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(10, 20, 10, 20));
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void initializeStyle() {
        switch (style) {

            case PRIMARY:
                bgDefault = Colors.SECONDARY_LIGHT_GREEN;
                bgHover = Colors.SECONDARY_LIGHT_GREEN.darker();

                fgDefault = Color.WHITE;
                fgHover = Color.WHITE;

                break;

            case SECONDARY:
                bgDefault = Colors.BORDER_GRAY;
                bgHover = Colors.BORDER_GRAY.darker();

                fgDefault = Colors.TEXT_PRIMARY;
                fgHover = Colors.TEXT_PRIMARY;

                break;

            case DANGER:
                bgDefault = Colors.BUTTON_DANGER;
                bgHover = Colors.BUTTON_DANGER.darker();

                fgDefault = Color.WHITE;
                fgHover = Color.WHITE;

                break;

            case OUTLINE:
                bgDefault = Color.WHITE;
                bgHover = new Color(245, 245, 245);

                fgDefault = Colors.PRIMARY_GREEN;
                fgHover = Colors.PRIMARY_GREEN.darker();

                setBorder(new LineBorder(Colors.BORDER_GRAY, 2));

                break;
        }

        setForeground(fgDefault);
    }

    private void initializeHoverEffect() {
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                setForeground(fgHover);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setForeground(fgDefault);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int width = getWidth();
        int height = getHeight();

        int arc = rounded ? 18 : 0;

        Color backgroundColor = getModel().isRollover()
                ? bgHover
                : bgDefault;

        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, width, height, arc, arc);

        if (style == ButtonStyle.OUTLINE) {
            g2.setColor(Colors.BORDER_GRAY);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, width - 3, height - 3, arc, arc);
        }

        g2.dispose();

        super.paintComponent(graphics);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();

        return new Dimension(
                Math.max(size.width, 120),
                Math.max(size.height, 42)
        );
    }

    public void setRounded(boolean rounded) {
        this.rounded = rounded;
        repaint();
    }

    public boolean isRounded() {
        return rounded;
    }
}
package com.wastely.views.components;

import com.wastely.assets.Loader;
import com.wastely.utils.Colors;
import com.wastely.utils.Typography;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 * Reusable Container Card Panel
 * Acts as a flexible dashboard card shell.
 */
public class Card extends JPanel {

    private JLabel titleLabel;
    private JLabel valueLabel;
    private JLabel trendLabel;
    private JLabel iconLabel;

    private JPanel iconContainer;
    private JPanel contentPanel;

    public Card() {
        setup();
    }

    /**
     * Initializes empty card structure.
     */
    private void setup() {
        setLayout(new BorderLayout(12, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER_GRAY, 1),
                new EmptyBorder(16, 16, 16, 16)
        ));

        add(createIconContainer(), BorderLayout.WEST);
        add(createContent(), BorderLayout.CENTER);
    }

    /**
     * Icon container (dynamic content supported).
     */
    private JPanel createIconContainer() {
        iconContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Colors.PRIMARY_GREEN);
                g2.fill(new RoundRectangle2D.Float(
                        0, 0, getWidth(), getHeight(), 12, 12));

                g2.dispose();
                super.paintComponent(g);
            }
        };

        iconContainer.setOpaque(false);
        iconContainer.setBorder(new EmptyBorder(8, 12, 8, 12));

        iconLabel = new JLabel();
        iconContainer.add(iconLabel);

        return iconContainer;
    }

    /**
     * Main content panel.
     */
    private JPanel createContent() {
        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("Title");
        titleLabel.setFont(Typography.SMALL_MEDIUM);
        titleLabel.setForeground(Colors.TEXT_SECONDARY);

        valueLabel = new JLabel("0");
        valueLabel.setFont(Typography.HEADING_H2);
        valueLabel.setForeground(Colors.TEXT_PRIMARY);

        contentPanel.add(titleLabel);
        contentPanel.add(valueLabel);

        return contentPanel;
    }
    
    public static Card createStatCard(String title, int value, String unit, String trend, String iconPath) {

        Card card = new Card();

        card.setTitle(title);
        card.setValue(value);
        card.setUnit(unit);
        card.setTrend(trend);

        if (iconPath != null) {
            card.setIcon(iconPath);
        }

        // optional default styling fallback
        card.setIconBackground(new Color(230, 245, 235));

        return card;
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setValue(int value) {
        valueLabel.setText(value == 0 ? "" : String.valueOf(value));
    }

    public void setUnit(String unit) {
        valueLabel.setText(valueLabel.getText() + (unit != null ? " " + unit : ""));
    }

    public void setTrend(String trend) {
        if (trendLabel == null) {
            trendLabel = new JLabel();
            trendLabel.setFont(Typography.SMALL_MEDIUM);
            contentPanel.add(Box.createVerticalStrut(4));
            contentPanel.add(trendLabel);
        }
        trendLabel.setText(trend);
        trendLabel.setForeground(Colors.STATUS_COMPLETED);
    }

    public void setIcon(String iconPath) {
        iconLabel.setIcon(Loader.loadIcon(iconPath, 20));
    }

    public void setIconBackground(Color color) {
        iconContainer.setBackground(color);
        iconContainer.repaint();
    }
    
    public static JPanel createCard() {
        return new RoundedCardPanel(
                12, 12, 12, 12,
                2, 4,
                Color.WHITE,
                true
        );
    }

    /**
     * Transparent / non-opaque card version
     */
    public static JPanel createTransparentCard(Color color) {
        return new RoundedCardPanel(
                12, 12, 12, 12,
                2, 4,
                color,
                false
        );
    }

    private static final class RoundedCardPanel extends JPanel {

        private final int topLeft;
        private final int topRight;
        private final int bottomRight;
        private final int bottomLeft;

        private final int shadowSize;
        private final int shadowOpacity;

        private BufferedImage shadowCache;
        private int cachedWidth = -1;
        private int cachedHeight = -1;

        private RoundedCardPanel(
                int topLeft,
                int topRight,
                int bottomRight,
                int bottomLeft,
                int shadowSize,
                int shadowOpacity,
                Color background,
                boolean opaque
        ) {
            this.topLeft = topLeft;
            this.topRight = topRight;
            this.bottomRight = bottomRight;
            this.bottomLeft = bottomLeft;
            this.shadowSize = shadowSize;
            this.shadowOpacity = shadowOpacity;

            setOpaque(opaque);
            setBackground(background);

            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Colors.BORDER_GRAY, 1),
                    new EmptyBorder(16, 16, 16, 16)
            ));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            ensureShadowCache(width, height);

            if (shadowCache != null) {
                g2.drawImage(shadowCache, shadowSize, shadowSize, null);
            }

            g2.setColor(getBackground());
            g2.fill(createRoundedShape(
                    width - shadowSize * 2,
                    height - shadowSize * 2,
                    topLeft,
                    topRight,
                    bottomRight,
                    bottomLeft
            ));

            g2.dispose();
            super.paintComponent(graphics);
        }

        private void ensureShadowCache(int width, int height) {
            if (shadowSize <= 0) {
                shadowCache = null;
                return;
            }

            if (shadowCache != null
                    && cachedWidth == width
                    && cachedHeight == height) {
                return;
            }

            cachedWidth = width;
            cachedHeight = height;

            shadowCache = createShadowImage(
                    width - shadowSize * 2,
                    height - shadowSize * 2,
                    topLeft,
                    topRight,
                    bottomRight,
                    bottomLeft,
                    shadowSize,
                    Color.BLACK,
                    Math.min(1f, shadowOpacity / 255f)
            );
        }

        private static BufferedImage createShadowImage(
                int width,
                int height,
                int topLeft,
                int topRight,
                int bottomRight,
                int bottomLeft,
                int shadowSize,
                Color color,
                float opacity
        ) {
            BufferedImage image = new BufferedImage(
                    width + shadowSize * 2,
                    height + shadowSize * 2,
                    BufferedImage.TYPE_INT_ARGB
            );

            Graphics2D g2 = image.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(
                    color.getRed(),
                    color.getGreen(),
                    color.getBlue(),
                    Math.round(255 * opacity)
            ));

            g2.translate(shadowSize, shadowSize);
            g2.fill(createRoundedShape(width, height,
                    topLeft, topRight, bottomRight, bottomLeft));

            g2.dispose();
            return blurImage(image, shadowSize);
        }

        private static BufferedImage blurImage(BufferedImage image, int radius) {
            if (radius <= 0) return image;

            int size = radius * 2 + 1;
            float[] data = new float[size * size];
            float value = 1f / data.length;

            for (int i = 0; i < data.length; i++) {
                data[i] = value;
            }

            Kernel kernel = new Kernel(size, size, data);
            ConvolveOp blur = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
            return blur.filter(image, null);
        }

        public static Shape createRoundedShape(
                int width,
                int height,
                int topLeft,
                int topRight,
                int bottomRight,
                int bottomLeft
        ) {
            Path2D path = new Path2D.Double();

            path.moveTo(topLeft, 0);
            path.lineTo(width - topRight, 0);
            path.quadTo(width, 0, width, topRight);

            path.lineTo(width, height - bottomRight);
            path.quadTo(width, height, width - bottomRight, height);

            path.lineTo(bottomLeft, height);
            path.quadTo(0, height, 0, height - bottomLeft);

            path.lineTo(0, topLeft);
            path.quadTo(0, 0, topLeft, 0);

            path.closePath();
            return path;
        }
    }
}
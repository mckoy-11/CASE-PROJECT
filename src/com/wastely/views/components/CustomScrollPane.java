package com.wastely.views.components;

import com.wastely.utils.Colors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Custom scroll pane with:
 * - Modern rounded scrollbar UI
 * - Auto fade/hide scrollbar after inactivity
 * - Smooth fade animation
 * - Overlay-style scrollbar
 * - Custom scroll speed
 */
public class CustomScrollPane extends JScrollPane {

    private static final int SCROLLBAR_WIDTH = 10;

    private static final int SCROLL_SPEED = 24;

    private static final int FADE_DELAY = 3000;
    private static final int FADE_INTERVAL = 40;

    private static final float FADE_STEP = 0.08f;

    private final FadeScrollBar verticalScrollBar;
    private final FadeScrollBar horizontalScrollBar;

    private Timer fadeDelayTimer;

    /**
     * Creates custom scroll pane.
     */
    public CustomScrollPane() {
        this(null);
    }

    /**
     * Creates custom scroll pane with view.
     *
     * @param view viewport view
     */
    public CustomScrollPane(Component view) {

        super(view);

        setBorder(null);

        setOpaque(false);

        getViewport().setOpaque(false);

        verticalScrollBar = new FadeScrollBar(JScrollBar.VERTICAL);
        horizontalScrollBar = new FadeScrollBar(JScrollBar.HORIZONTAL);

        /*
         * Scroll speed configuration.
         */
        verticalScrollBar.setUnitIncrement(SCROLL_SPEED);
        horizontalScrollBar.setUnitIncrement(SCROLL_SPEED);

        verticalScrollBar.putClientProperty(
                "JScrollBar.fastWheelScrolling",
                Boolean.TRUE
        );

        horizontalScrollBar.putClientProperty(
                "JScrollBar.fastWheelScrolling",
                Boolean.TRUE
        );

        setVerticalScrollBar(verticalScrollBar);
        setHorizontalScrollBar(horizontalScrollBar);

        initializeBehavior();
    }

    /**
     * Sets scroll speed.
     *
     * @param speed scroll speed
     */
    public void setScrollSpeed(int speed) {

        verticalScrollBar.setUnitIncrement(speed);
        horizontalScrollBar.setUnitIncrement(speed);
    }

    /**
     * Initializes listeners and fade behavior.
     */
    private void initializeBehavior() {

        AdjustmentListener adjustmentListener =
                e -> showScrollbars();

        verticalScrollBar.addAdjustmentListener(adjustmentListener);
        horizontalScrollBar.addAdjustmentListener(adjustmentListener);

        MouseWheelListener wheelListener =
                e -> showScrollbars();

        addMouseWheelListener(wheelListener);

        MouseAdapter mouseAdapter = new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                showScrollbars();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                showScrollbars();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                showScrollbars();
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);

        initializeFadeTimer();
    }

    /**
     * Initializes fade delay timer.
     */
    private void initializeFadeTimer() {

        fadeDelayTimer = new Timer(FADE_DELAY, e -> {

            verticalScrollBar.fadeOut();
            horizontalScrollBar.fadeOut();
        });

        fadeDelayTimer.setRepeats(false);
    }

    /**
     * Shows scrollbars and resets fade timer.
     */
    private void showScrollbars() {

        verticalScrollBar.fadeIn();
        horizontalScrollBar.fadeIn();

        fadeDelayTimer.restart();
    }

    /**
     * Custom animated scrollbar.
     */
    private static class FadeScrollBar extends JScrollBar {

        private float alpha = 0f;

        private Timer fadeTimer;

        /**
         * Creates fade scrollbar.
         *
         * @param orientation scrollbar orientation
         */
        public FadeScrollBar(int orientation) {

            super(orientation);

            setOpaque(false);

            setPreferredSize(
                    orientation == JScrollBar.VERTICAL
                            ? new Dimension(SCROLLBAR_WIDTH, 0)
                            : new Dimension(0, SCROLLBAR_WIDTH)
            );

            setUI(new ModernScrollBarUI(this));

            setBorder(new EmptyBorder(2, 2, 2, 2));
        }

        /**
         * Fades scrollbar in.
         */
        public void fadeIn() {

            stopFadeTimer();

            fadeTimer = new Timer(FADE_INTERVAL, e -> {

                alpha += FADE_STEP;

                if (alpha >= 1f) {

                    alpha = 1f;

                    stopFadeTimer();
                }

                repaint();
            });

            fadeTimer.start();
        }

        /**
         * Fades scrollbar out.
         */
        public void fadeOut() {

            stopFadeTimer();

            fadeTimer = new Timer(FADE_INTERVAL, e -> {

                alpha -= FADE_STEP;

                if (alpha <= 0f) {

                    alpha = 0f;

                    stopFadeTimer();
                }

                repaint();
            });

            fadeTimer.start();
        }

        /**
         * Stops active fade timer.
         */
        private void stopFadeTimer() {

            if (fadeTimer != null && fadeTimer.isRunning()) {
                fadeTimer.stop();
            }
        }

        /**
         * Returns scrollbar alpha.
         *
         * @return alpha value
         */
        public float getAlpha() {
            return alpha;
        }
    }

    /**
     * Modern scrollbar UI implementation.
     */
    private static class ModernScrollBarUI
            extends BasicScrollBarUI {

        private final FadeScrollBar scrollBar;

        /**
         * Creates scrollbar UI.
         *
         * @param scrollBar target scrollbar
         */
        public ModernScrollBarUI(FadeScrollBar scrollBar) {
            this.scrollBar = scrollBar;
        }

        /**
         * Paints scrollbar thumb.
         *
         * @param g graphics object
         * @param c component
         * @param thumbBounds thumb bounds
         */
        @Override
        protected void paintThumb(
                Graphics g,
                JComponent c,
                Rectangle thumbBounds
        ) {

            if (thumbBounds.isEmpty()) {
                return;
            }

            Graphics2D g2d =
                    (Graphics2D) g.create();

            g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2d.setComposite(
                    AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER,
                            scrollBar.getAlpha()
                    )
            );

            int arc = 12;

            Shape thumbShape;

            if (scrollbar.getOrientation()
                    == JScrollBar.VERTICAL) {

                thumbShape = new RoundRectangle2D.Float(
                        thumbBounds.x + 2,
                        thumbBounds.y,
                        thumbBounds.width - 4,
                        thumbBounds.height,
                        arc,
                        arc
                );

            } else {

                thumbShape = new RoundRectangle2D.Float(
                        thumbBounds.x,
                        thumbBounds.y + 2,
                        thumbBounds.width,
                        thumbBounds.height - 4,
                        arc,
                        arc
                );
            }

            g2d.setColor(Colors.PRIMARY_GREEN);

            g2d.fill(thumbShape);

            g2d.dispose();
        }

        /**
         * Paints track.
         *
         * @param g graphics object
         * @param c component
         * @param trackBounds track bounds
         */
        @Override
        protected void paintTrack(
                Graphics g,
                JComponent c,
                Rectangle trackBounds
        ) {

            Graphics2D g2d =
                    (Graphics2D) g.create();

            g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2d.setComposite(
                    AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER,
                            scrollBar.getAlpha() * 0.15f
                    )
            );

            g2d.setColor(Colors.BORDER_GRAY);

            g2d.fillRoundRect(
                    trackBounds.x,
                    trackBounds.y,
                    trackBounds.width,
                    trackBounds.height,
                    10,
                    10
            );

            g2d.dispose();
        }

        /**
         * Removes decrease button.
         *
         * @param orientation orientation
         * @return empty button
         */
        @Override
        protected JButton createDecreaseButton(
                int orientation
        ) {
            return createZeroButton();
        }

        /**
         * Removes increase button.
         *
         * @param orientation orientation
         * @return empty button
         */
        @Override
        protected JButton createIncreaseButton(
                int orientation
        ) {
            return createZeroButton();
        }

        /**
         * Creates invisible scrollbar button.
         *
         * @return empty button
         */
        private JButton createZeroButton() {

            JButton button = new JButton();

            button.setOpaque(false);

            button.setFocusable(false);

            button.setBorder(null);

            button.setPreferredSize(
                    new Dimension(0, 0)
            );

            return button;
        }
    }
}
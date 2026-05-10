package com.wastely.views.screens;

import java.awt.*;
import javax.swing.*;

import com.wastely.WastelyApp;
import com.wastely.assets.Loader;
import com.wastely.utils.Colors;
import com.wastely.utils.ComponentStyle;
import com.wastely.utils.Typography;
import com.wastely.views.components.*;

public class WelcomePage extends JPanel {

    private WastelyApp app;

    /**
     * Welcome landing screen for Wastely application
     */
    public WelcomePage(WastelyApp app) {
        this.app = app;
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(600, 0));

        JPanel form = ComponentStyle.createTransparentPanel();
        form.setPreferredSize(new Dimension(500, 260));
        form.setLayout(new GridBagLayout());

        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel welcome = ComponentStyle.createFormTitle("Welcome Back");
        JLabel note = ComponentStyle.createFormSubtitle("Choose how you want to enter the system");

        JButton create = new CustomButton("Create Account", CustomButton.ButtonStyle.OUTLINE);
        create.addActionListener(e -> app.showSignupScreen());

        JButton login = new CustomButton("Log In", CustomButton.ButtonStyle.PRIMARY);
        login.addActionListener(e -> app.showLoginScreen());

        JPanel btnPanel = ComponentStyle.createTransparentPanel(new GridLayout(1, 2, 8, 0));
        btnPanel.add(create);
        btnPanel.add(login);

        JPanel stats = ComponentStyle.createTransparentPanel(new GridLayout(1, 2, 8, 0));
        stats.add(createStat("24/7", "Collection visibility"));
        stats.add(createStat("Live", "Request tracking"));

        welcome.setPreferredSize(new Dimension(500, 40));
        note.setPreferredSize(new Dimension(500, 30));

        stats.setPreferredSize(new Dimension(500, 90));
        btnPanel.setPreferredSize(new Dimension(500, 50));

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        form.add(welcome, gbc);

        gbc.gridy++;
        form.add(note, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(15, 0, 15, 0);
        form.add(stats, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 10, 0);
        form.add(btnPanel, gbc);

        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.gridx = 0;
        rightGbc.gridy = 0;
        rightGbc.weightx = 1;
        rightGbc.weighty = 1;
        rightGbc.anchor = GridBagConstraints.CENTER;

        rightPanel.add(form, rightGbc);

        add(createLeftPanel(), BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel createStat(String value, String label) {
        JPanel panel = Card.createTransparentCard(Colors.PRIMARY_LIGHT_GREEN);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel v = new JLabel(value);
        v.setFont(Typography.BODY_MEDIUM.deriveFont(18f));
        v.setForeground(Colors.TEXT_PRIMARY);

        JLabel l = new JLabel(label);
        l.setFont(Typography.BODY_NORMAL.deriveFont(12f));
        l.setForeground(Colors.DISABLED_TEXT);

        panel.add(v);
        panel.add(Box.createVerticalStrut(4));
        panel.add(l);

        return panel;
    }

    /**
     * Creates branding panel.
     *
     * @return left panel
     */
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                Graphics2D g2 =
                        (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                GradientPaint gradient =
                        new GradientPaint(
                                0,
                                0,
                                Colors.PRIMARY_LIGHT_GREEN,
                                0,
                                getHeight(),
                                Colors.PRIMARY_GREEN
                        );

                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());

                paintDecorations(g2);

                g2.dispose();
            }
        };

        panel.setPreferredSize(new Dimension(600, 0));
        panel.setLayout(new GridBagLayout());

        panel.add(createBrandCard());

        return panel;
    }

    /**
     * Paints background decorations.
     *
     * @param g2 graphics
     */
    private void paintDecorations(Graphics2D g2) {

        g2.setColor(new Color(255, 255, 255, 18));

        for (int x = 0; x < getWidth(); x += 40) {
            for (int y = 0; y < getHeight(); y += 40) {
                g2.fillOval(x, y, 3, 3);
            }
        }

        g2.setColor(new Color(255, 255, 255, 25));

        g2.fillOval(-80, 80, 200, 200);

        g2.fillOval(
                getWidth() - 120,
                getHeight() - 200,
                250,
                250
        );
    }

    /**
     * Creates branding card.
     *
     * @return branding card
     */
    private JPanel createBrandCard() {

        JPanel card = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 =
                        (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(new Color(255, 255, 255, 25));

                g2.fillRoundRect(
                        0,
                        0,
                        getWidth(),
                        getHeight(),
                        30,
                        30
                );

                g2.dispose();

                super.paintComponent(g);
            }
        };

        card.setOpaque(false);

        card.setBorder(
                BorderFactory.createEmptyBorder(
                        60,
                        40,
                        60,
                        40
                )
        );

        card.setLayout(
                new BoxLayout(card, BoxLayout.Y_AXIS)
        );

        JLabel logo =
                new JLabel(Loader.loadIcon("leafv2.png", 95));

        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("WASTELY");

        title.setFont(new Font("Arial", Font.BOLD, 52));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle =
                new JLabel("Waste Management System");

        subtitle.setFont(Typography.HEADING_H3);
        subtitle.setForeground(new Color(255, 255, 255, 210));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline =
                new JLabel("Clean • Smart • Sustainable");

        tagline.setFont(new Font("Arial", Font.PLAIN, 16));
        tagline.setForeground(new Color(255, 255, 255, 180));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(logo);
        card.add(Box.createVerticalStrut(20));
        card.add(title);
        card.add(Box.createVerticalStrut(10));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(14));
        card.add(tagline);
        card.add(Box.createVerticalGlue());

        return card;
    }
}
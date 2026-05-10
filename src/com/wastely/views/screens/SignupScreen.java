package com.wastely.views.screens;

import com.wastely.WastelyApp;
import com.wastely.assets.Loader;
import com.wastely.services.AuthService;
import com.wastely.utils.Colors;
import com.wastely.utils.Typography;
import com.wastely.views.components.CustomButton;
import com.wastely.views.components.CustomComboBox;
import com.wastely.views.components.CustomPasswordField;
import com.wastely.views.components.CustomTextField;

import javax.swing.*;
import java.awt.*;

/**
 * Signup screen for account registration.
 */
public class SignupScreen extends JPanel {

    private static final Dimension FIELD_SIZE =
            new Dimension(360, 48);

    private final WastelyApp app;

    private CustomTextField fullNameField;
    private CustomTextField ageField;
    private CustomComboBox<String> genderField;
    private CustomTextField emailField;
    private CustomComboBox<String> userTypeField;
    private CustomTextField barangayField;
    private CustomPasswordField passwordField;
    private CustomPasswordField confirmPasswordField;

    private JLabel errorLabel;

    /**
     * Creates signup screen.
     *
     * @param app application instance
     */
    public SignupScreen(WastelyApp app) {
        this.app = app;
        initializeUI();
    }

    /**
     * Initializes full UI.
     */
    private void initializeUI() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(createLeftPanel(), BorderLayout.WEST);
        add(createRightPanel(), BorderLayout.CENTER);
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

    /**
     * Creates form panel.
     *
     * @return right panel
     */
    private JPanel createRightPanel() {

        JPanel wrapper =
                new JPanel(new GridBagLayout());

        wrapper.setBackground(Color.WHITE);
        wrapper.setPreferredSize(new Dimension(600, 0));

        JPanel form =
                createSignupForm();

        wrapper.add(form);

        return wrapper;
    }

    /**
     * Creates signup form.
     *
     * @return form panel
     */
    private JPanel createSignupForm() {

        initializeFields();

        JPanel form =
                new JPanel(new GridBagLayout());

        form.setBackground(Color.WHITE);
        form.setPreferredSize(new Dimension(400, 700));
        form.setMaximumSize(new Dimension(400, 700));

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int row = 0;

        JLabel title =
                new JLabel("Create Account");

        title.setFont(Typography.HEADING_H1);
        title.setForeground(Colors.TEXT_PRIMARY);

        gbc.gridy = row++;
        form.add(title, gbc);

        JLabel subtitle =
                new JLabel("Fill in your information");

        subtitle.setFont(Typography.LABEL);
        subtitle.setForeground(Colors.TEXT_SECONDARY);

        gbc.gridy = row++;
        form.add(subtitle, gbc);

        gbc.gridy = row++;
        form.add(fullNameField, gbc);

        gbc.gridy = row++;
        form.add(createDualRow(ageField, genderField), gbc);

        gbc.gridy = row++;
        form.add(emailField, gbc);

        gbc.gridy = row++;
        form.add(userTypeField, gbc);

        gbc.gridy = row++;
        form.add(barangayField, gbc);

        gbc.gridy = row++;
        form.add(passwordField, gbc);

        gbc.gridy = row++;
        form.add(confirmPasswordField, gbc);

        errorLabel = new JLabel(" ");

        errorLabel.setFont(Typography.SMALL_TEXT);
        errorLabel.setForeground(Colors.BUTTON_DANGER);

        gbc.gridy = row++;
        form.add(errorLabel, gbc);

        CustomButton createButton =
                new CustomButton(
                        "Create Account",
                        CustomButton.ButtonStyle.PRIMARY
                );

        createButton.setPreferredSize(FIELD_SIZE);

        createButton.addActionListener(this::handleSignup);

        gbc.gridy = row++;
        form.add(createButton, gbc);

        gbc.gridy = row;
        form.add(createLoginPanel(), gbc);

        configureUserTypeBehavior(form);

        return form;
    }

    /**
     * Initializes fields.
     */
    private void initializeFields() {

        fullNameField =
                new CustomTextField("Enter full name");

        ageField =
                new CustomTextField("Age");

        genderField =
                new CustomComboBox<>(
                        new String[]{"Male", "Female"}
                );

        emailField =
                new CustomTextField("Enter email");

        userTypeField =
                new CustomComboBox<>(
                        new String[]{
                                "Barangay Admin",
                                "MENRO Admin"
                        }
                );

        barangayField =
                new CustomTextField("Enter your barangay");

        passwordField =
                new CustomPasswordField("Enter password");

        confirmPasswordField =
                new CustomPasswordField("Confirm password");

        applyFieldSizing(
                fullNameField,
                ageField,
                genderField,
                emailField,
                userTypeField,
                barangayField,
                passwordField,
                confirmPasswordField
        );
    }

    /**
     * Applies consistent field sizing.
     *
     * @param components fields
     */
    private void applyFieldSizing(JComponent... components) {

        for (JComponent component : components) {

            component.setPreferredSize(FIELD_SIZE);
            component.setMinimumSize(FIELD_SIZE);
            component.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 48)
            );
        }
    }

    /**
     * Creates dual-row panel.
     *
     * @param left  left component
     * @param right right component
     * @return row panel
     */
    private JPanel createDualRow(
            JComponent left,
            JComponent right
    ) {

        JPanel panel =
                new JPanel(new GridLayout(
                        1,
                        2,
                        12,
                        0
                ));

        panel.setOpaque(false);

        panel.add(left);
        panel.add(right);

        return panel;
    }

    /**
     * Creates login redirect panel.
     *
     * @return login panel
     */
    private JPanel createLoginPanel() {

        JPanel panel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                5,
                                0
                        )
                );

        panel.setOpaque(false);

        JLabel text =
                new JLabel("Already have an account?");

        text.setFont(Typography.LABEL);
        text.setForeground(Colors.TEXT_SECONDARY);

        JButton loginButton =
                new JButton("Login here");

        loginButton.setFont(Typography.LABEL);
        loginButton.setForeground(Colors.PRIMARY_GREEN);
        loginButton.setBorder(null);
        loginButton.setFocusPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        loginButton.addActionListener(
                e -> app.showLoginScreen()
        );

        panel.add(text);
        panel.add(loginButton);

        return panel;
    }

    /**
     * Handles barangay visibility.
     *
     * @param form form panel
     */
    private void configureUserTypeBehavior(JPanel form) {

        userTypeField.addActionListener(e -> {

            boolean visible =
                    "Barangay Admin".equals(
                            userTypeField.getSelectedItem()
                    );

            barangayField.setVisible(visible);

            form.revalidate();
            form.repaint();
        });

        barangayField.setVisible(true);
    }

    /**
     * Handles signup process.
     *
     * @param e action event
     */
    private void handleSignup(java.awt.event.ActionEvent e) {

        clearError();

        String validationError = validateForm();

        if (validationError != null) {
            showError(validationError);
            return;
        }

        SwingWorker<String, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected String doInBackground()
                            throws Exception {

                        String userType =
                                (String) userTypeField.getSelectedItem();

                        String barangay =
                                "Barangay Admin".equals(userType)
                                        ? (String) barangayField.getText()
                                        : null;

                        return AuthService.register(
                                fullNameField.getActualText(),
                                emailField.getActualText(),
                                passwordField.getActualPassword(),
                                Integer.parseInt(
                                        ageField.getActualText().isBlank()
                                                ? "0"
                                                : ageField.getActualText()
                                ),
                                (String) genderField.getSelectedItem(),
                                barangay
                        );
                    }

                    @Override
                    protected void done() {

                        try {

                            String result = get();

                            if (result == null
                                    || result.equalsIgnoreCase("success")) {

                                JOptionPane.showMessageDialog(
                                        SignupScreen.this,
                                        "Account created successfully!",
                                        "Success",
                                        JOptionPane.INFORMATION_MESSAGE
                                );

                                app.showLoginScreen();

                            } else {
                                showError(result);
                            }

                        } catch (Exception ex) {

                            ex.printStackTrace();

                            showError(
                                    "Registration failed. Please try again."
                            );
                        }
                    }
                };

        worker.execute();
    }

    /**
     * Validates form.
     *
     * @return validation error or null
     */
    private String validateForm() {

        String fullName =
                fullNameField.getActualText().trim();

        String email =
                emailField.getActualText().trim();

        String password =
                passwordField.getActualPassword();

        String confirmPassword =
                confirmPasswordField.getActualPassword();

        String ageText =
                ageField.getActualText().trim();

        String userType =
                (String) userTypeField.getSelectedItem();

        String barangay =
                barangayField.getActualText().trim();

        if (fullName.isEmpty()
                || email.isEmpty()
                || password.isEmpty()) {

            return "Please fill in all required fields.";
        }

        if (!AuthService.isValidEmail(email)) {
            return "Invalid email format.";
        }

        if (!AuthService.isValidPassword(password)) {
            return "Password must be at least 6 characters.";
        }

        if (!password.equals(confirmPassword)) {
            return "Passwords do not match.";
        }

        if (!ageText.isBlank()) {

            try {
                Integer.parseInt(ageText);
            } catch (NumberFormatException ex) {
                return "Invalid age format.";
            }
        }

        if ("Barangay Admin".equals(userType)
                && "Select Barangay".equals(barangay)) {

            return "Please select a barangay.";
        }

        if (!AuthService.isDatabaseConnected()) {
            return "Database connection failed.";
        }

        return null;
    }

    /**
     * Displays error.
     *
     * @param message error message
     */
    private void showError(String message) {
        errorLabel.setText(message);
    }

    /**
     * Clears error label.
     */
    private void clearError() {
        errorLabel.setText(" ");
    }
}
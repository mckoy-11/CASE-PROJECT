package com.wastely.views.screens;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import com.wastely.WastelyApp;
import com.wastely.assets.Loader;
import com.wastely.models.AppSession;
import com.wastely.models.User;
import com.wastely.services.AuthService;
import com.wastely.utils.Colors;
import com.wastely.utils.Typography;
import com.wastely.views.components.CustomButton;
import com.wastely.views.components.CustomPasswordField;
import com.wastely.views.components.CustomTextField;

public class LoginScreen extends JPanel {

    private WastelyApp app;
    private CustomTextField emailField;
    private CustomPasswordField passwordField;
    private JLabel errorLabel;

    public LoginScreen(WastelyApp app) {
        this.app = app;
        setupLoginScreen();
    }

    private void setupLoginScreen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, Colors.PRIMARY_LIGHT_GREEN,
                        0, getHeight(), Colors.PRIMARY_GREEN
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(new Color(255, 255, 255, 18));
                for (int i = 0; i < getWidth(); i += 40) {
                    for (int j = 0; j < getHeight(); j += 40) {
                        g2d.fillOval(i, j, 3, 3);
                    }
                }

                g2d.setColor(new Color(255, 255, 255, 25));
                g2d.fillOval(-80, 80, 200, 200);
                g2d.fillOval(getWidth() - 120, getHeight() - 200, 250, 250);
            }
        };

        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setPreferredSize(new Dimension(600, 0));

        JPanel brandCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(255, 255, 255, 25));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };

        brandCard.setOpaque(false);
        brandCard.setLayout(new BoxLayout(brandCard, BoxLayout.Y_AXIS));
        brandCard.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));

        JLabel logoLabel = new JLabel(Loader.loadIcon("leafv2.png", 95));
        logoLabel.setFont(new Font("Arial", Font.BOLD, 90));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("WASTELY");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 52));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Waste Management System");
        subtitleLabel.setFont(Typography.HEADING_H3);
        subtitleLabel.setForeground(new Color(255, 255, 255, 210));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("Clean • Smart • Sustainable");
        tagline.setFont(new Font("Arial", Font.PLAIN, 16));
        tagline.setForeground(new Color(255, 255, 255, 180));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        brandCard.add(Box.createVerticalGlue());
        brandCard.add(logoLabel);
        brandCard.add(Box.createVerticalStrut(20));
        brandCard.add(titleLabel);
        brandCard.add(Box.createVerticalStrut(10));
        brandCard.add(subtitleLabel);
        brandCard.add(Box.createVerticalStrut(15));
        brandCard.add(tagline);
        brandCard.add(Box.createVerticalGlue());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        leftPanel.add(brandCard, gbc);

        add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setMaximumSize(new Dimension(400, 500));

        JLabel loginTitle = new JLabel("Welcome Back");
        loginTitle.setFont(Typography.HEADING_H1);
        loginTitle.setForeground(Colors.TEXT_PRIMARY);
        loginTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel loginSubtitle = new JLabel("Sign in to your account");
        loginSubtitle.setFont(Typography.LABEL);
        loginSubtitle.setForeground(Colors.TEXT_SECONDARY);
        loginSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(loginTitle);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(loginSubtitle);
        formPanel.add(Box.createVerticalStrut(30));

        Dimension fieldSize = new Dimension(350, 50);

        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(Typography.LABEL_MEDIUM);
        emailLabel.setForeground(Colors.TEXT_PRIMARY);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(emailLabel);
        formPanel.add(Box.createVerticalStrut(5));

        emailField = new CustomTextField("example@gmail.com");
        emailField.setPreferredSize(fieldSize);
        emailField.setMaximumSize(fieldSize);
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(15));

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(Typography.LABEL_MEDIUM);
        passwordLabel.setForeground(Colors.TEXT_PRIMARY);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(5));

        passwordField = new CustomPasswordField("*********");
        passwordField.setPreferredSize(fieldSize);
        passwordField.setMaximumSize(fieldSize);
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(10));

        JPanel forgotPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        forgotPanel.setBackground(Color.WHITE);
        forgotPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        forgotPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton forgotPasswordBtn = new JButton("Forgot Password?");
        forgotPasswordBtn.setFont(Typography.LABEL);
        forgotPasswordBtn.setForeground(Colors.PRIMARY_GREEN);
        forgotPasswordBtn.setBorder(null);
        forgotPasswordBtn.setBackground(Color.WHITE);
        forgotPasswordBtn.setFocusPainted(false);
        forgotPasswordBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        forgotPasswordBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Password reset functionality not yet implemented.",
                        "Info", JOptionPane.INFORMATION_MESSAGE)
        );

        forgotPanel.add(forgotPasswordBtn);
        formPanel.add(forgotPanel);
        formPanel.add(Box.createVerticalStrut(15));

        errorLabel = new JLabel();
        errorLabel.setFont(Typography.SMALL_TEXT);
        errorLabel.setForeground(Colors.BUTTON_DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(errorLabel);
        formPanel.add(Box.createVerticalStrut(10));

        CustomButton loginBtn = new CustomButton("Sign In", CustomButton.ButtonStyle.PRIMARY);
        loginBtn.setPreferredSize(fieldSize);
        loginBtn.setMaximumSize(fieldSize);
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.addActionListener(this::handleLogin);

        formPanel.add(loginBtn);
        formPanel.add(Box.createVerticalStrut(15));

        JPanel signupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        signupPanel.setBackground(Color.WHITE);
        signupPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        signupPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel noAccountLabel = new JLabel("Don't have an account?");
        noAccountLabel.setFont(Typography.LABEL);
        noAccountLabel.setForeground(Colors.TEXT_SECONDARY);

        JButton signupBtn = new JButton("Sign Up");
        signupBtn.setFont(Typography.LABEL);
        signupBtn.setForeground(Colors.PRIMARY_GREEN);
        signupBtn.setBorder(null);
        signupBtn.setBackground(Color.WHITE);
        signupBtn.setFocusPainted(false);
        signupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        signupBtn.addActionListener(e -> {
            app.showSignupScreen();
        });

        signupPanel.add(noAccountLabel);
        signupPanel.add(signupBtn);

        formPanel.add(signupPanel);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 0;

        rightPanel.add(formPanel, gbc2);
        add(rightPanel, BorderLayout.CENTER);
    }

    private void handleLogin(ActionEvent e) {
        String email = emailField.getActualText();
        String password = passwordField.getActualPassword();

        errorLabel.setText("");

        // Validate inputs
        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Email and password are required.");
            return;
        }

        if (!AuthService.isValidEmail(email)) {
            errorLabel.setText("Invalid email format.");
            return;
        }

        // Check database connection
        if (!AuthService.isDatabaseConnected()) {
            errorLabel.setText("Database connection failed. Please check your connection.");
            return;
        }

        // Perform login in background thread to avoid UI blocking
        Thread loginThread = new Thread(() -> {
            try {
                User user = AuthService.login(email, password);

                SwingUtilities.invokeLater(() -> {
                    if (user == null) {
                        errorLabel.setText("Invalid email or password.");
                        return;
                    }

                    AppSession.getInstance().login(user);

                    if (user.isMenroAdmin()) {
                        app.showMenroLayout();
                    } else if (user.isBarangayAdmin()) {
                        app.showBarangayLayout();
                    } else {
                        errorLabel.setText("Unknown user role. Please contact administrator.");
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    System.err.println("Login error: " + ex.getMessage());
                    ex.printStackTrace();
                    errorLabel.setText("An error occurred during login. Please try again.");
                });
            } finally {
                clearLoginFields();
            }
        });
        
        loginThread.setDaemon(true);
        loginThread.start();
    }

    private void clearLoginFields() {
    emailField.setText("");
    passwordField.setText("");
}
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bloodlink.ui;

import bloodlink.dao.UserDAO;
import bloodlink.model.User;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author demi
 * 
 * Login screen for BloodLink.
 * All 4 roles log in through this single screen.
 * On success, the MainFrame is opened with the authenticated user.
 * 
 * Layout: GridBagLayout for a centered, form-style login card.
 * Components:
 *   - JLabel for the app title
 *   - JTextField for username
 *   - JPasswordField for password
 *   - JButton to submit
 *   - JLabel for error messages
 */
public class LoginScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel errorLabel;

    private UserDAO userDAO;

    public LoginScreen() {
        userDAO = new UserDAO();
        initComponents();
    }

    private void initComponents() {
        // ── Window settings ──────────────────────
        setTitle("BloodLink — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);

        // ── Main panel with padding ──────────────
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        mainPanel.setBackground(new Color(245, 245, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Spacing between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ── Row 0: App title ─────────────────────
        JLabel titleLabel = new JLabel("BloodLink", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(180, 30, 30));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // ── Row 1: Subtitle ──────────────────────
        JLabel subtitleLabel = new JLabel("Hospital & Blood Bank Coordination", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        mainPanel.add(subtitleLabel, gbc);

        // ── Row 2: Username label + field ────────
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Username:"), gbc);

        usernameField = new JTextField(15);
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // ── Row 3: Password label + field ────────
        gbc.gridy = 3;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // ── Row 4: Login button ──────────────────
        loginButton = new JButton("Log In");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setBackground(new Color(180, 30, 30));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        mainPanel.add(loginButton, gbc);

        // ── Row 5: Error message label ───────────
        errorLabel = new JLabel(" ", SwingConstants.CENTER);
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        gbc.gridy = 5;
        mainPanel.add(errorLabel, gbc);

        add(mainPanel);

        // ── Event handlers ───────────────────────
        loginButton.addActionListener(e -> handleLogin());

        // Allow pressing Enter in the password field to trigger login
        passwordField.addActionListener(e -> handleLogin());
    }

    /**
     * Validates input and attempts authentication.
     * On success: hides this window and opens the MainFrame.
     * On failure: shows an error message.
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Basic validation
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }

        // Attempt authentication via the DAO
        User user = userDAO.authenticate(username, password);

        if (user != null) {
            // Success — open the main application frame
            dispose(); // Close login window
            new MainFrame(user).setVisible(true);
        } else {
            // Failure — show error and clear password
            errorLabel.setText("Invalid username or password.");
            passwordField.setText("");
        }
    }
}

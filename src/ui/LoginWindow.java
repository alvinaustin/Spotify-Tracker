package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginWindow {

    public static void showLoginPage() {
        // ===== Frame setup =====
        JFrame loginFrame = new JFrame("Spotify Login");
        loginFrame.setSize(400, 450);
        loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        loginFrame.setLayout(new BorderLayout());
        loginFrame.setLocationRelativeTo(null);

        // Spotify colors
        Color spotifyDark = new Color(18, 18, 18);
        Color spotifyGreen = new Color(30, 215, 96);

        loginFrame.getContentPane().setBackground(spotifyDark);

        // ===== Title =====
        JLabel title = new JLabel("Login to Spotify Tracker", SwingConstants.CENTER);
        title.setForeground(spotifyGreen);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        loginFrame.add(title, BorderLayout.NORTH);

        // ===== Input Panel =====
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(spotifyDark);
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        JTextField usernameField = new JTextField(15);
        usernameField.setMaximumSize(new Dimension(250, 30));
        usernameField.setBackground(Color.BLACK);
        usernameField.setForeground(spotifyGreen);
        usernameField.setCaretColor(spotifyGreen);
        usernameField.setBorder(BorderFactory.createLineBorder(spotifyGreen, 1));

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setMaximumSize(new Dimension(250, 30));
        passwordField.setBackground(Color.BLACK);
        passwordField.setForeground(spotifyGreen);
        passwordField.setCaretColor(spotifyGreen);
        passwordField.setBorder(BorderFactory.createLineBorder(spotifyGreen, 1));

        // ===== Login Button =====
        JButton loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(Color.BLACK);
        loginButton.setForeground(spotifyGreen);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createLineBorder(spotifyGreen, 2));

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "Please enter both fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = db.DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT * FROM users WHERE username = ? AND password = ?")) {

                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(loginFrame, "✅ Login successful! Welcome, " + username + " 🎵");
                    loginFrame.dispose(); // Close login window
                   ui.DashboardWindow.showDashboard(username);
 // Open main window
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "❌ Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(loginFrame, "⚠️ Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ===== Register Button =====
        JButton registerButton = new JButton("Create Account");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setBackground(Color.BLACK);
        registerButton.setForeground(spotifyGreen);
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        registerButton.setBorder(BorderFactory.createLineBorder(spotifyGreen, 2));
        registerButton.setFocusPainted(false);

        registerButton.addActionListener(e -> {
            RegisterWindow.showRegisterPage(); // Open Register Window
        });

        // ===== Assemble Layout =====
        inputPanel.add(userLabel);
        inputPanel.add(usernameField);
        inputPanel.add(Box.createVerticalStrut(15));
        inputPanel.add(passLabel);
        inputPanel.add(passwordField);
        inputPanel.add(Box.createVerticalStrut(20));
        inputPanel.add(loginButton);
        inputPanel.add(Box.createVerticalStrut(15));
        inputPanel.add(registerButton);

        loginFrame.add(inputPanel, BorderLayout.CENTER);
        loginFrame.setVisible(true);
    }

    // For testing (you can run this file directly)
    public static void main(String[] args) {
        showLoginPage();
    }
}

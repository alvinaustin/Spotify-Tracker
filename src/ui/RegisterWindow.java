package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RegisterWindow {

    public static void showRegisterPage() {
        JFrame frame = new JFrame("Register New Account");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(18, 18, 18));
        frame.setLocationRelativeTo(null);

        Color spotifyGreen = new Color(30, 215, 96);

        JLabel title = new JLabel("Create a New Account", SwingConstants.CENTER);
        title.setForeground(spotifyGreen);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(new Color(18, 18, 18));
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Color.WHITE);
        JTextField usernameField = new JTextField(15);
        usernameField.setMaximumSize(new Dimension(250, 30));

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setMaximumSize(new Dimension(250, 30));

        JButton registerButton = new JButton("Register");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setBackground(Color.BLACK);
        registerButton.setForeground(spotifyGreen);
        registerButton.setBorder(BorderFactory.createLineBorder(spotifyGreen, 2));
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 16));

        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = db.DBConnection.getConnection()) {
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO users (username, password) VALUES (?, ?)"
                );
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(frame, "✅ Account created successfully!");
                frame.dispose(); // Close registration window
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "⚠️ Username already exists or DB error!", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        inputPanel.add(userLabel);
        inputPanel.add(usernameField);
        inputPanel.add(Box.createVerticalStrut(15));
        inputPanel.add(passLabel);
        inputPanel.add(passwordField);
        inputPanel.add(Box.createVerticalStrut(20));
        inputPanel.add(registerButton);

        frame.add(title, BorderLayout.NORTH);
        frame.add(inputPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}

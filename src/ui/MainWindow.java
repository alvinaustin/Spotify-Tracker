package ui;

import javax.swing.*;
import java.awt.*; // Needed for Color

public class MainWindow {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Spotify Tracker");
            frame.setSize(600, 400);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Set background color
            frame.getContentPane().setBackground(new Color(30, 215, 96)); // Spotify green

            // Add label
            JLabel label = new JLabel("Welcome to Spotify Tracker!", SwingConstants.CENTER);
            label.setForeground(Color.BLACK); // Make text readable on green background
            label.setFont(new Font("Arial", Font.BOLD, 24)); // Bigger, bold text
            frame.add(label);

            frame.setVisible(true);
        });
    }
}

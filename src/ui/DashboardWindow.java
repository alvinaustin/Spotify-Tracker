package ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class DashboardWindow {

    public static void showDashboard(String username) {
        JFrame frame = new JFrame("Spotify Tracker Dashboard");
        frame.setSize(950, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.setUndecorated(false);

        //  Spotify Colors 
        Color spotifyGreen = new Color(30, 215, 96);
        Color spotifyDark = new Color(18, 18, 18);
        Color spotifyGray = new Color(24, 24, 24);

        frame.getContentPane().setBackground(spotifyDark);

        // Sidebar Panel 
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(spotifyGray);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        //  Spotify Logo 
        JLabel logoLabel;
        try {
            ImageIcon logoIcon = new ImageIcon("assets/spotify-logo.png");
            Image img = logoIcon.getImage().getScaledInstance(140, 40, Image.SCALE_SMOOTH);
            logoLabel = new JLabel(new ImageIcon(img));
        } catch (Exception e) {
            logoLabel = new JLabel("Spotify Tracker", SwingConstants.CENTER);
            logoLabel.setForeground(spotifyGreen);
            logoLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        sidebar.add(logoLabel);

        //  Create Sidebar Buttons 
        JButton homeBtn = createSidebarButton("🏠  Home", spotifyGreen);
        JButton playlistBtn = createSidebarButton("🎵  Playlists", spotifyGreen);
        JButton profileBtn = createSidebarButton("👤  Profile", spotifyGreen);
        JButton logoutBtn = createSidebarButton("🚪  Logout", Color.RED);

        sidebar.add(homeBtn);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(playlistBtn);
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(profileBtn);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logoutBtn);

        //  Main Content Area 
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(spotifyDark);
        contentPanel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome, " + username + " 🎧", SwingConstants.CENTER);
        welcomeLabel.setForeground(spotifyGreen);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JTextArea infoArea = new JTextArea(
            "🎶 Your Spotify Tracker Dashboard 🎶\n\n" +
            "• View and manage your playlists\n" +
            "• Customize your preferences\n\n" +
            "Click options on the left sidebar to navigate!"
        );
        infoArea.setEditable(false);
        infoArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        infoArea.setForeground(Color.WHITE);
        infoArea.setBackground(spotifyDark);
        infoArea.setMargin(new Insets(20, 50, 20, 50));

        contentPanel.add(welcomeLabel, BorderLayout.NORTH);
        contentPanel.add(infoArea, BorderLayout.CENTER);

        //  Button Actions 
        homeBtn.addActionListener(e -> {
            contentPanel.removeAll();
            contentPanel.add(welcomeLabel, BorderLayout.NORTH);
            contentPanel.add(infoArea, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        playlistBtn.addActionListener(e -> {
            contentPanel.removeAll();
            contentPanel.add(new PlaylistPanel(username), BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        profileBtn.addActionListener(e -> {
            contentPanel.removeAll();
            contentPanel.add(new ProfilePanel(username), BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        logoutBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "You have been logged out.");
            frame.dispose();
            ui.LoginWindow.showLoginPage();
        });

        // ===== Add Panels to Frame =====
        frame.add(sidebar, BorderLayout.WEST);
        frame.add(contentPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    //  Method to Create Button 
    private static JButton createSidebarButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(160, 40));
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(Color.BLACK);
        btn.setForeground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(color, 2));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(25, 25, 25));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.BLACK);
            }
        });

        return btn;
    }

    //Testing
    public static void main(String[] args) {
        showDashboard("Alvin");
    }
}

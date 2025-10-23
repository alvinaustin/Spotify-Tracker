package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class PlaylistPanel extends JPanel {
    private String username;
    private DefaultListModel<String> playlistModel;
    private JList<String> playlistList;
    private JTextField nameField;
    private JTextArea descField;

    public PlaylistPanel(String username) {
        this.username = username;

        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 18));

        Color spotifyGreen = new Color(30, 215, 96);
        Color spotifyDark = new Color(18, 18, 18);

        // ===== Title =====
        JLabel title = new JLabel("Your Playlists 🎵", SwingConstants.CENTER);
        title.setForeground(spotifyGreen);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // ===== Playlist List =====
        playlistModel = new DefaultListModel<>();
        playlistList = new JList<>(playlistModel);
        playlistList.setBackground(spotifyDark);
        playlistList.setForeground(Color.WHITE);
        playlistList.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JScrollPane scrollPane = new JScrollPane(playlistList);
        add(scrollPane, BorderLayout.CENTER);

        // ===== Add/Delete Panel =====
        JPanel addPanel = new JPanel();
        addPanel.setLayout(new BoxLayout(addPanel, BoxLayout.Y_AXIS));
        addPanel.setBackground(spotifyDark);
        addPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        JLabel nameLabel = new JLabel("Playlist Name:");
        nameLabel.setForeground(Color.WHITE);
        nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(300, 30));

        JLabel descLabel = new JLabel("Description:");
        descLabel.setForeground(Color.WHITE);
        descField = new JTextArea(3, 20);
        descField.setLineWrap(true);
        descField.setWrapStyleWord(true);
        descField.setBackground(Color.BLACK);
        descField.setForeground(spotifyGreen);

        JButton addButton = new JButton("Add Playlist");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.setBackground(Color.BLACK);
        addButton.setForeground(spotifyGreen);
        addButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        addButton.setBorder(BorderFactory.createLineBorder(spotifyGreen, 2));

        JButton deleteButton = new JButton("🗑 Delete Playlist");
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteButton.setBackground(Color.BLACK);
        deleteButton.setForeground(Color.RED);
        deleteButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        deleteButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        addButton.addActionListener(e -> addPlaylist());
        deleteButton.addActionListener(e -> deletePlaylist());

        addPanel.add(nameLabel);
        addPanel.add(nameField);
        addPanel.add(Box.createVerticalStrut(10));
        addPanel.add(descLabel);
        addPanel.add(descField);
        addPanel.add(Box.createVerticalStrut(10));
        addPanel.add(addButton);
        addPanel.add(Box.createVerticalStrut(10));
        addPanel.add(deleteButton);

        add(addPanel, BorderLayout.SOUTH);

        loadPlaylists();

        // ===== Double-click playlist to open songs =====
        playlistList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    String selectedPlaylist = playlistList.getSelectedValue();
                    if (selectedPlaylist != null) {
                        try (Connection conn = db.DBConnection.getConnection();
                             PreparedStatement stmt = conn.prepareStatement(
                                     "SELECT id FROM playlists WHERE username = ? AND name = ?")) {
                            stmt.setString(1, username);
                            stmt.setString(2, selectedPlaylist);
                            ResultSet rs = stmt.executeQuery();

                            if (rs.next()) {
                                int playlistId = rs.getInt("id");
                                JFrame songsFrame = new JFrame("Songs — " + selectedPlaylist);
                                songsFrame.setSize(600, 400);
                                songsFrame.setLocationRelativeTo(null);
                                songsFrame.add(new SongPanel(playlistId, selectedPlaylist));
                                songsFrame.setVisible(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    // ===== Load playlists =====
    private void loadPlaylists() {
        playlistModel.clear();
        try (Connection conn = db.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT name FROM playlists WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                playlistModel.addElement(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== Add playlist =====
    private void addPlaylist() {
        String name = nameField.getText();
        String desc = descField.getText();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a playlist name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = db.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO playlists (username, name, description) VALUES (?, ?, ?)")) {
            stmt.setString(1, username);
            stmt.setString(2, name);
            stmt.setString(3, desc);
            stmt.executeUpdate();

            playlistModel.addElement(name);
            nameField.setText("");
            descField.setText("");
            JOptionPane.showMessageDialog(this, "✅ Playlist added successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "⚠️ Error adding playlist: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== Delete playlist =====
    private void deletePlaylist() {
        String selected = playlistList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a playlist to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete \"" + selected + "\"?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = db.DBConnection.getConnection()) {
            // Delete songs first (foreign key)
            PreparedStatement delSongs = conn.prepareStatement(
                    "DELETE FROM songs WHERE playlist_id IN (SELECT id FROM playlists WHERE username = ? AND name = ?)");
            delSongs.setString(1, username);
            delSongs.setString(2, selected);
            delSongs.executeUpdate();

            // Delete playlist
            PreparedStatement delPlaylist = conn.prepareStatement(
                    "DELETE FROM playlists WHERE username = ? AND name = ?");
            delPlaylist.setString(1, username);
            delPlaylist.setString(2, selected);
            delPlaylist.executeUpdate();

            playlistModel.removeElement(selected);
            JOptionPane.showMessageDialog(this, "🗑 Playlist deleted successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "⚠️ Error deleting playlist: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SongPanel extends JPanel {
    private int playlistId;
    private String playlistName;
    private DefaultListModel<String> songModel;
    private JList<String> songList;
    private JTextField titleField, artistField, durationField;

    public SongPanel(int playlistId, String playlistName) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;

        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 18));

        Color spotifyGreen = new Color(30, 215, 96);

        // ===== Title =====
        JLabel title = new JLabel("Songs in " + playlistName + " 🎵", SwingConstants.CENTER);
        title.setForeground(spotifyGreen);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // ===== Song List =====
        songModel = new DefaultListModel<>();
        songList = new JList<>(songModel);
        songList.setBackground(new Color(18, 18, 18));
        songList.setForeground(Color.WHITE);
        songList.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JScrollPane scrollPane = new JScrollPane(songList);
        add(scrollPane, BorderLayout.CENTER);

        // ===== Add Song Section =====
        JPanel addPanel = new JPanel();
        addPanel.setLayout(new BoxLayout(addPanel, BoxLayout.Y_AXIS));
        addPanel.setBackground(new Color(18, 18, 18));
        addPanel.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));

        JLabel nameLabel = new JLabel("Song Title:");
        nameLabel.setForeground(Color.WHITE);
        titleField = new JTextField();
        titleField.setMaximumSize(new Dimension(300, 30));

        JLabel artistLabel = new JLabel("Artist:");
        artistLabel.setForeground(Color.WHITE);
        artistField = new JTextField();
        artistField.setMaximumSize(new Dimension(300, 30));

        JLabel durationLabel = new JLabel("Duration (mm:ss):");
        durationLabel.setForeground(Color.WHITE);
        durationField = new JTextField();
        durationField.setMaximumSize(new Dimension(300, 30));

        JButton addButton = new JButton("➕ Add Song");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.setBackground(Color.BLACK);
        addButton.setForeground(spotifyGreen);
        addButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createLineBorder(spotifyGreen, 2));

        JButton deleteButton = new JButton("🗑 Delete Selected Song");
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteButton.setBackground(Color.BLACK);
        deleteButton.setForeground(Color.RED);
        deleteButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        addButton.addActionListener(e -> addSong());
        deleteButton.addActionListener(e -> deleteSong());

        addPanel.add(nameLabel);
        addPanel.add(titleField);
        addPanel.add(Box.createVerticalStrut(10));
        addPanel.add(artistLabel);
        addPanel.add(artistField);
        addPanel.add(Box.createVerticalStrut(10));
        addPanel.add(durationLabel);
        addPanel.add(durationField);
        addPanel.add(Box.createVerticalStrut(15));
        addPanel.add(addButton);
        addPanel.add(Box.createVerticalStrut(10));
        addPanel.add(deleteButton);

        add(addPanel, BorderLayout.SOUTH);

        // ===== Load songs initially =====
        loadSongs();
    }

    // ===== Load songs from DB =====
    private void loadSongs() {
        songModel.clear();
        try (Connection conn = db.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT title, artist, duration FROM songs WHERE playlist_id = ?")) {
            stmt.setInt(1, playlistId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String title = rs.getString("title");
                String artist = rs.getString("artist");
                String duration = rs.getString("duration");
                songModel.addElement("🎵 " + title + " — " + artist + " (" + duration + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== Add Song to DB =====
    private void addSong() {
        String title = titleField.getText();
        String artist = artistField.getText();
        String duration = durationField.getText();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a song title.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = db.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO songs (playlist_id, title, artist, duration) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, playlistId);
            stmt.setString(2, title);
            stmt.setString(3, artist);
            stmt.setString(4, duration);
            stmt.executeUpdate();

            songModel.addElement("🎵 " + title + " — " + artist + " (" + duration + ")");
            titleField.setText("");
            artistField.setText("");
            durationField.setText("");
            JOptionPane.showMessageDialog(this, "✅ Song added successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "⚠️ Error adding song: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== Delete Song from DB =====
    private void deleteSong() {
        String selected = songList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a song to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this song?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        String songTitle = selected.replaceAll("🎵 ", "").split(" — ")[0].trim();

        try (Connection conn = db.DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM songs WHERE playlist_id = ? AND title = ?")) {
            stmt.setInt(1, playlistId);
            stmt.setString(2, songTitle);
            stmt.executeUpdate();

            songModel.removeElement(selected);
            JOptionPane.showMessageDialog(this, "🗑 Song deleted successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "⚠️ Error deleting song: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

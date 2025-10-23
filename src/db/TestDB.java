package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDB {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "/src/spotify_users.db";

        try {
            // Force-load the SQLite driver
            Class.forName("org.sqlite.JDBC");

            try (Connection conn = DriverManager.getConnection(url)) {
                if (conn != null) {
                    System.out.println("✅ Connected to SQLite successfully!");
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("❌ SQLite JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ Connection failed!");
            e.printStackTrace();
        }
    }
}

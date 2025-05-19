package com.passwordmanager.dao;

import com.passwordmanager.model.PasswordEntry;
import com.passwordmanager.security.PasswordEncryptionService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PasswordEntryDAO {
    private Connection conn;

    public PasswordEntryDAO(Connection conn) {
        this.conn = conn;
    }

    public void createPasswordTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS passwords (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "user_id INTEGER," +
                     "entry_name TEXT NOT NULL," +
                     "username TEXT," +
                     "password TEXT NOT NULL," +
                     "FOREIGN KEY (user_id) REFERENCES users (id))";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        }
    }

    public List<PasswordEntry> getPasswordsByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM passwords WHERE user_id = ?";
        List<PasswordEntry> passwords = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                PasswordEntry password = new PasswordEntry(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("entry_name"),
                    rs.getString("username"),
                    rs.getString("password")
                );
                passwords.add(password);
            }
        }
        return passwords;
    }

    public void addPassword(int userId, String entryName, String username, String encryptedPassword) throws SQLException {
        String sql = "INSERT INTO passwords (user_id, entry_name, username, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, entryName);
            stmt.setString(3, username);
            stmt.setString(4, encryptedPassword);
            stmt.executeUpdate();
        }
    }

    public PasswordEntry getPassword(int id, int userId) throws SQLException {
        String sql = "SELECT * FROM passwords WHERE id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String encryptedPassword = rs.getString("password");
                String decryptedPassword = PasswordEncryptionService.getInstance().decryptPassword(encryptedPassword);
                PasswordEntry entry = new PasswordEntry(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("entry_name"),
                    rs.getString("username"),
                    decryptedPassword
                );
                return entry;
            }
        }
        return null;
    }

    public void updatePassword(int id, int userId, String entryName, String username, String encryptedPassword) throws SQLException {
        String sql = "UPDATE passwords SET entry_name = ?, username = ?, password = ? WHERE id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entryName);
            stmt.setString(2, username);
            stmt.setString(3, encryptedPassword);
            stmt.setInt(4, id);
            stmt.setInt(5, userId);
            stmt.executeUpdate();
        }
    }

    public void deletePassword(int id, int userId) throws SQLException {
        String sql = "DELETE FROM passwords WHERE id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
}

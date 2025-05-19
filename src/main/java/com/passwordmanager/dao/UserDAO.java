package com.passwordmanager.dao;

import com.passwordmanager.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserDAO {
    private Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    public void createUserTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "username TEXT UNIQUE NOT NULL," +
                     "password TEXT NOT NULL," +
                     "pin TEXT," +
                     "last_login TEXT," +
                     "last_pin_login TEXT)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        }
    }

    public boolean registerUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, BCrypt.hashpw(password, BCrypt.gensalt()));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                throw new SQLException("Username '" + username + "' already exists!");
            }
            throw e;
        }
    }

    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT id, password FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                int userId = rs.getInt("id");
                
                if (BCrypt.checkpw(password, storedPassword)) {
                    updateLastLogin(userId);
                    return new User(userId, username, storedPassword, null, LocalDateTime.now());
                }
            }
            return null;
        }
    }

    public User authenticateWithPin(String username, String pin) throws SQLException {
        String checkSql = "PRAGMA table_info(users)";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            ResultSet columns = checkStmt.executeQuery();
            boolean hasLastPinLogin = false;
            while (columns.next()) {
                if ("last_pin_login".equals(columns.getString("name"))) {
                    hasLastPinLogin = true;
                    break;
                }
            }

            if (!hasLastPinLogin) {
                String addColumnSql = "ALTER TABLE users ADD COLUMN last_pin_login TEXT";
                try (PreparedStatement addStmt = conn.prepareStatement(addColumnSql)) {
                    addStmt.execute();
                }
            }
        }

        String sql = "SELECT id, username, pin, last_pin_login FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("id");
                String storedPin = rs.getString("pin");
                String lastPinLoginStr = rs.getString("last_pin_login");
                
                String normalizedStoredPin = storedPin.trim();
                String normalizedEnteredPin = pin.trim();
                
                if (storedPin != null && normalizedStoredPin.equals(normalizedEnteredPin)) {
                    if (lastPinLoginStr == null) {
                        updateLastPinLogin(userId);
                        return new User(userId, username, null, pin, LocalDateTime.now());
                    }
                    
                    LocalDateTime lastPinLogin = LocalDateTime.parse(lastPinLoginStr);
                    
                    if (LocalDateTime.now().minusWeeks(1).isBefore(lastPinLogin)) {
                        updateLastPinLogin(userId);
                        return new User(userId, username, null, pin, LocalDateTime.now());
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
    }

    private void updateLastPinLogin(int userId) throws SQLException {
        String sql = "UPDATE users SET last_pin_login = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    public boolean verifyPin(int userId, String pin) throws SQLException {
        String sql = "SELECT pin FROM users WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("pin").equals(pin);
            }
        }
        return false;
    }

    public void setPin(int userId, String pin) throws SQLException {
        String normalizedPin = pin.trim();
        String sql = "UPDATE users SET pin = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, normalizedPin);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    public String getPin(int userId) throws SQLException {
        String sql = "SELECT pin FROM users WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("pin");
            }
        }
        return null;
    }

    public int getUserId(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }

    private void updateLastLogin(int userId) throws SQLException {
        String sql = "UPDATE users SET last_login = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
}

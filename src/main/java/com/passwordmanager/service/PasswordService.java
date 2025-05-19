package com.passwordmanager.service;

import com.passwordmanager.dao.PasswordEntryDAO;
import com.passwordmanager.model.PasswordEntry;
import com.passwordmanager.security.PasswordEncryptionService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PasswordService {
    private PasswordEntryDAO passwordDAO;
    private int currentUserId;

    public PasswordService(PasswordEntryDAO passwordDAO) {
        this.passwordDAO = passwordDAO;
    }

    public PasswordEncryptionService getEncryptionService() {
        return PasswordEncryptionService.getInstance();
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    public List<PasswordEntry> getPasswords() {
        try {
            List<PasswordEntry> entries = passwordDAO.getPasswordsByUserId(currentUserId);
            return entries;
        } catch (SQLException e) {
            System.err.println("Error fetching passwords: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean addPassword(String entryName, String username, String password) {
        try {
            String encryptedPassword = PasswordEncryptionService.getInstance().encryptPassword(password);
            passwordDAO.addPassword(currentUserId, entryName, username, encryptedPassword);
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding password: " + e.getMessage());
            return false;
        }
    }

    public boolean editPassword(int id, String entryName, String username, String password) {
        try {
            PasswordEntry entry = passwordDAO.getPassword(id, currentUserId);
            if (entry != null) {
                entry.setEntryName(entryName);
                entry.setUsername(username);
                String encryptedPassword = PasswordEncryptionService.getInstance().encryptPassword(password);
                entry.setEncryptedPassword(encryptedPassword);
                passwordDAO.updatePassword(id, entry.getUserId(), entry.getEntryName(), entry.getUsername(), entry.getEncryptedPassword());
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error editing password: " + e.getMessage());
            return false;
        }
    }

    public boolean deletePassword(int id) {
        try {
            passwordDAO.deletePassword(id, currentUserId);
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting password: " + e.getMessage());
            return false;
        }
    }

    public PasswordEntry getPassword(int id) {
        try {
            PasswordEntry entry = passwordDAO.getPassword(id, currentUserId);
            if (entry != null) {
                String decryptedPassword = PasswordEncryptionService.getInstance().decryptPassword(entry.getEncryptedPassword());
                entry.setPassword(decryptedPassword);
            }
            return entry;
        } catch (SQLException e) {
            System.err.println("Error fetching password: " + e.getMessage());
            return null;
        }
    }
}

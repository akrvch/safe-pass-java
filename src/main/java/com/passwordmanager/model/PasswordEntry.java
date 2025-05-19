package com.passwordmanager.model;

import com.passwordmanager.security.PasswordEncryptionService;

public class PasswordEntry {
    private int id;
    private int userId;
    private String entryName;
    private String username;
    private String encryptedPassword;

    public PasswordEntry(int id, int userId, String entryName, String username, String password) {
        this.id = id;
        this.userId = userId;
        this.entryName = entryName;
        this.username = username;
        this.encryptedPassword = PasswordEncryptionService.getInstance().encryptPassword(password);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getEntryName() { return entryName; }
    public void setEntryName(String entryName) { this.entryName = entryName; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public String getPassword() {
        return PasswordEncryptionService.getInstance().decryptPassword(encryptedPassword);
    }

    public void setPassword(String password) {
        setEncryptedPassword(PasswordEncryptionService.getInstance().encryptPassword(password));
    }


}

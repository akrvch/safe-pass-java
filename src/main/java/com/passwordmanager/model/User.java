package com.passwordmanager.model;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String username;
    private String password;
    private String pin;
    private LocalDateTime lastLogin;

    public User() {}

    public User(int id, String username, String password, String pin, LocalDateTime lastLogin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.pin = pin;
        this.lastLogin = lastLogin;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
}

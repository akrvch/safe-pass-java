package com.passwordmanager.service;

import com.passwordmanager.dao.UserDAO;
import com.passwordmanager.model.User;
import java.sql.SQLException;

public class AuthenticationService {
    private UserDAO userDAO;
    private User currentUser;

    public AuthenticationService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public boolean register(String username, String password) {
        try {
            return userDAO.registerUser(username, password);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public User login(String username, String password) {
        try {
            User user = userDAO.authenticate(username, password);
            if (user != null) {
                currentUser = user;
            }
            return user;
        } catch (SQLException e) {
            System.err.println("Login failed: " + e.getMessage());
            return null;
        }
    }

    public User loginWithPin(String username, String pin) {
        try {
            User user = userDAO.authenticateWithPin(username, pin);
            if (user != null) {
                currentUser = user;
            }
            return user;
        } catch (SQLException e) {
            System.err.println("PIN login failed: " + e.getMessage());
            return null;
        }
    }

    public boolean verifyPin(String pin) {
        if (currentUser == null) return false;
        try {
            return userDAO.verifyPin(currentUser.getId(), pin);
        } catch (SQLException e) {
            System.err.println("PIN verification failed: " + e.getMessage());
            return false;
        }
    }

    public void setPin(String pin) {
        if (currentUser == null) return;
        try {
            userDAO.setPin(currentUser.getId(), pin);
        } catch (SQLException e) {
            System.err.println("Failed to set PIN: " + e.getMessage());
        }
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }
}

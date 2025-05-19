package com.passwordmanager;

import com.passwordmanager.dao.UserDAO;
import com.passwordmanager.dao.PasswordEntryDAO;
import com.passwordmanager.service.AuthenticationService;
import com.passwordmanager.service.PasswordService;
import com.passwordmanager.ui.LoginMenu;
import com.passwordmanager.ui.PasswordManagerMenu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class PasswordManagerApp {
    private static final String DB_PATH = "password_manager.db";
    private static Connection conn;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            
            UserDAO userDAO = new UserDAO(conn);
            PasswordEntryDAO passwordDAO = new PasswordEntryDAO(conn);
            
            AuthenticationService authService = new AuthenticationService(userDAO);
            PasswordService passwordService = new PasswordService(passwordDAO);
            
            LoginMenu loginMenu = new LoginMenu(scanner, authService);
            PasswordManagerMenu passwordManagerMenu = new PasswordManagerMenu(scanner, passwordService);
            
            userDAO.createUserTable();
            passwordDAO.createPasswordTable();
            
            int result = loginMenu.show();
            
            if (result == 1) {
                if (authService.isLoggedIn()) {
                    passwordService.setCurrentUserId(authService.getCurrentUser().getId());
                    passwordManagerMenu.show();
                    authService.logout();
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error initializing application: " + e.getMessage());
            System.exit(1);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}

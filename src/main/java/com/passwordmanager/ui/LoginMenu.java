package com.passwordmanager.ui;

import com.passwordmanager.model.User;
import com.passwordmanager.service.AuthenticationService;
import com.passwordmanager.dao.UserDAO;

import java.sql.SQLException;
import java.util.Scanner;

public class LoginMenu {
    private final Scanner scanner;
    private final AuthenticationService authService;
    private final UserDAO userDAO;

    public LoginMenu(Scanner scanner, AuthenticationService authService) {
        this.scanner = scanner;
        this.authService = authService;
        this.userDAO = authService.getUserDAO();
    }

    public int show() {
        while (true) {
            System.out.println("\n=== Password Manager ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    handleRegister();
                    break;
                case 2:
                    int result = handleLogin();
                    if (result == 1) {
                        return result;
                    }
                    break;
                case 3:
                    return 0;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private void handleRegister() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (authService.register(username, password)) {
            System.out.println("Registration successful!");
        } else {
            System.out.println("Registration failed!");
        }
    }

    private int handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        User user = null;
        try {
            String existingPin = userDAO.getPin(userDAO.getUserId(username));
            if (existingPin != null) {
                System.out.print("Enter PIN (4 digits): ");
                String pin = scanner.nextLine();
                
                if (pin.length() != 4) {
                    System.out.println("PIN must be 4 digits!");
                    return 0;
                }
                
                user = authService.loginWithPin(username, pin);
                if (user == null) {
                    System.out.println("Invalid PIN!");
                    return 0;
                }
                return 1;
            }
        } catch (SQLException e) {
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        user = authService.login(username, password);
        if (user == null) {
            System.out.println("Invalid credentials!");
            return 0;
        }

        System.out.println("Login successful!");
        
        try {
            String existingPin = userDAO.getPin(user.getId());
            if (existingPin != null) {
                System.out.print("Enter PIN (4 digits): ");
                String pin = scanner.nextLine();
                
                if (pin.length() != 4) {
                    System.out.println("PIN must be 4 digits!");
                    return 0;
                }
                
                if (existingPin.equals(pin)) {
                    return 1;
                } else {
                    System.out.println("Invalid PIN!");
                    return 0;
                }
            }
            
            System.out.print("Would you like to set a PIN for quick login? (y/n): ");
            String setPin = scanner.nextLine();
            if (setPin.equalsIgnoreCase("y")) {
                System.out.print("Enter PIN (4 digits): ");
                String pin = scanner.nextLine();
                
                if (pin.length() != 4) {
                    System.out.println("PIN must be 4 digits!");
                    return 0;
                }
                
                try {
                    userDAO.setPin(user.getId(), pin);
                    System.out.println("PIN set successfully!");
                } catch (SQLException e) {
                    System.err.println("Failed to set PIN: " + e.getMessage());
                    return 0;
                }
            }
            return 1;
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
            return 0;
        }
    }
}

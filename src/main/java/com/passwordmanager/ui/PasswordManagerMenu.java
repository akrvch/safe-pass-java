package com.passwordmanager.ui;

import com.passwordmanager.model.PasswordEntry;
import com.passwordmanager.service.PasswordService;
import com.passwordmanager.util.AsciiTable;

import java.util.Scanner;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;

public class PasswordManagerMenu {
    private final Scanner scanner;
    private final PasswordService passwordService;

    public PasswordManagerMenu(Scanner scanner, PasswordService passwordService) {
        this.scanner = scanner;
        this.passwordService = passwordService;
    }

    public void show() {
        while (true) {
            System.out.println("\n=== Password Manager Menu ===");
            viewPasswords();
            System.out.println("\n1. Add New Password");
            System.out.println("2. Edit Password");
            System.out.println("3. Delete Password");
            System.out.println("4. Copy to Clipboard");
            System.out.println("5. Logout");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addPassword();
                    break;
                case 2:
                    editPassword();
                    break;
                case 3:
                    deletePassword();
                    break;
                case 4:
                    copyToClipboard();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private void viewPasswords() {
        List<PasswordEntry> passwords = passwordService.getPasswords();
        System.out.println("\n=== Your Passwords ===");
        
        AsciiTable table = new AsciiTable(new String[]{"ID", "Entry Name", "Username", "Password"});
        
        for (PasswordEntry password : passwords) {
            table.addRow(
                String.valueOf(password.getId()),
                password.getEntryName(),
                password.getUsername(),
                "••••••••"
            );
        }
        
        table.print();
    }

    private void addPassword() {
        System.out.print("Enter entry name: ");
        String entryName = scanner.nextLine();
        System.out.print("Enter username (optional): ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (passwordService.addPassword(entryName, username, password)) {
            System.out.println("Password added successfully!");
        } else {
            System.out.println("Failed to add password!");
        }
    }

    private void editPassword() {
        System.out.print("Enter password ID to edit: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter new entry name: ");
        String entryName = scanner.nextLine();
        System.out.print("Enter new username: ");
        String username = scanner.nextLine();
        System.out.print("Enter new password: ");
        String password = scanner.nextLine();

        if (passwordService.editPassword(id, entryName, username, password)) {
            System.out.println("Password updated successfully!");
        } else {
            System.out.println("Failed to update password!");
        }
    }

    private void copyToClipboard() {
        System.out.print("Enter password ID to copy: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        PasswordEntry password = passwordService.getPassword(id);
        if (password == null) {
            System.out.println("Password not found!");
            return;
        }

        String decryptedPassword = password.getPassword();
        StringSelection selection = new StringSelection(decryptedPassword);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
        System.out.println("Password copied to clipboard!");
        
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                StringSelection emptySelection = new StringSelection("");
                clipboard.setContents(emptySelection, null);
            }
        }, 15000);
    }

    private void deletePassword() {
        System.out.print("Enter password ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        if (passwordService.deletePassword(id)) {
            System.out.println("Password deleted successfully!");
        } else {
            System.out.println("Failed to delete password!");
        }
    }
}

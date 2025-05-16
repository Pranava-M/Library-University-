package com.library.ui.console;

import com.library.entities.User;
import com.library.exceptions.UserNotFoundException;
import com.library.services.LibraryService;

import java.util.List;

public class UserMenu extends ConsoleMenu {
    public UserMenu(LibraryService libraryService, Scanner scanner) {
        super(libraryService, scanner);
    }

    @Override
    public void show() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\nUser Management:");
            System.out.println("1. Register New User");
            System.out.println("2. Search Users");
            System.out.println("3. View User Details");
            System.out.println("4. Update User");
            System.out.println("5. Deactivate User");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter your choice (1-6): ");
            
            int choice = getIntInput("");
            
            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    searchUsers();
                    break;
                case 3:
                    viewUserDetails();
                    break;
                case 4:
                    updateUser();
                    break;
                case 5:
                    deactivateUser();
                    break;
                case 6:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void registerUser() {
        System.out.println("\nRegister New User");
        
        String userId = getInput("Enter user ID: ");
        String firstName = getInput("Enter first name: ");
        String lastName = getInput("Enter last name: ");
        
        System.out.println("Select user type:");
        System.out.println("1. Student");
        System.out.println("2. Faculty");
        System.out.println("3. Staff");
        System.out.println("4. Visitor");
        int typeChoice = getIntInput("Enter choice (1-4): ");
        
        User.UserType userType;
        switch (typeChoice) {
            case 1: userType = User.UserType.STUDENT; break;
            case 2: userType = User.UserType.FACULTY; break;
            case 3: userType = User.UserType.STAFF; break;
            case 4: userType = User.UserType.VISITOR; break;
            default:
                System.out.println("Invalid choice, defaulting to Student");
                userType = User.UserType.STUDENT;
        }
        
        User user = new User(userId, firstName, lastName, userType);
        
        try {
            libraryService.registerNewUser(user);
            System.out.println("User registered successfully!");
        } catch (Exception e) {
            System.out.println("Error registering user: " + e.getMessage());
        }
        
        pressAnyKeyToContinue();
    }

    private void searchUsers() {
        System.out.println("\nSearch Users");
        String query = getInput("Enter search term: ");
        
        List<User> users = libraryService.searchUsers(query);
        
        if (users.isEmpty()) {
            System.out.println("No users found matching your search.");
        } else {
            System.out.println("\nSearch Results:");
            users.forEach(user -> System.out.println(
                "- " + user.getFullName() + " (" + user.getUserId() + ") - " + user.getUserType()));
        }
        
        pressAnyKeyToContinue();
    }

    private void viewUserDetails() {
        System.out.println("\nView User Details");
        String userId = getInput("Enter user ID: ");
        
        try {
            User user = libraryService.getUserDetails(userId);
            displayUserDetails(user);
        } catch (UserNotFoundException e) {
            System.out.println("User not found: " + e.getMessage());
        }
        
        pressAnyKeyToContinue();
    }

    private void displayUserDetails(User user) {
        System.out.println("\nUser Details:");
        System.out.println("ID: " + user.getUserId());
        System.out.println("Name: " + user.getFullName());
        System.out.println("Type: " + user.getUserType());
        System.out.println("Email: " + (user.getEmail() != null ? user.getEmail() : "N/A"));
        System.out.println("Phone: " + (user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A"));
        System.out.println("Registered: " + user.getRegistrationDate());
        System.out.println("Status: " + (user.isActive() ? "Active" : "Inactive"));
        System.out.println("Fines: $" + user.getFines());
    }

    private void updateUser() {
        System.out.println("\nUpdate User");
        String userId = getInput("Enter user ID: ");
        
        try {
            User user = libraryService.getUserDetails(userId);
            displayUserDetails(user);
            
            System.out.println("\nEnter new details (leave blank to keep current value):");
            String newFirstName = getInput("New first name [" + user.getFirstName() + "]: ");
            if (!newFirstName.isEmpty()) {
                user.setFirstName(newFirstName);
            }
            
            String newLastName = getInput("New last name [" + user.getLastName() + "]: ");
            if (!newLastName.isEmpty()) {
                user.setLastName(newLastName);
            }
            
            // In a real application, we would update all fields
            
            libraryService.updateUserInformation(user);
            System.out.println("User updated successfully!");
        } catch (UserNotFoundException e) {
            System.out.println("User not found: " + e.getMessage());
        }
        
        pressAnyKeyToContinue();
    }

    private void deactivateUser() {
        System.out.println("\nDeactivate User");
        String userId = getInput("Enter user ID: ");
        
        try {
            libraryService.deactivateUser(userId);
            System.out.println("User deactivated successfully!");
        } catch (UserNotFoundException e) {
            System.out.println("User not found: " + e.getMessage());
        }
        
        pressAnyKeyToContinue();
    }
}
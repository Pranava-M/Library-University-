package com.library.ui.console;

import com.library.services.LibraryService;
import com.library.services.impl.LibraryServiceImpl;
import java.util.Scanner;

public class LibraryApp {
    private final LibraryService libraryService;
    private final Scanner scanner;
    private boolean running;

    public LibraryApp() {
        this.libraryService = new LibraryServiceImpl();
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    public void start() {
        System.out.println("Welcome to the Library Management System!");
        
        while (running) {
            displayMainMenu();
            int choice = getMenuChoice(1, 5);
            
            switch (choice) {
                case 1:
                    new BookMenu(libraryService, scanner).show();
                    break;
                case 2:
                    new UserMenu(libraryService, scanner).show();
                    break;
                case 3:
                    new LoanMenu(libraryService, scanner).show();
                    break;
                case 4:
                    showReportsMenu();
                    break;
                case 5:
                    running = false;
                    System.out.println("Thank you for using the Library Management System. Goodbye!");
                    break;
            }
        }
        
        scanner.close();
    }

    private void displayMainMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. Book Management");
        System.out.println("2. User Management");
        System.out.println("3. Loan Management");
        System.out.println("4. Reports");
        System.out.println("5. Exit");
        System.out.print("Enter your choice (1-5): ");
    }

    private int getMenuChoice(int min, int max) {
        while (true) {
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.printf("Please enter a number between %d and %d: ", min, max);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    private void showReportsMenu() {
        System.out.println("\nReports Menu:");
        System.out.println("1. Popular Books");
        System.out.println("2. Active Users");
        System.out.println("3. Overdue Loans");
        System.out.println("4. Total Fines");
        System.out.println("5. Back to Main Menu");
        System.out.print("Enter your choice (1-5): ");
        
        int choice = getMenuChoice(1, 5);
        
        switch (choice) {
            case 1:
                showPopularBooksReport();
                break;
            case 2:
                showActiveUsersReport();
                break;
            case 3:
                showOverdueLoansReport();
                break;
            case 4:
                showTotalFinesReport();
                break;
            case 5:
                // Return to main menu
                break;
        }
    }

    private void showPopularBooksReport() {
        System.out.print("Enter number of top books to show: ");
        int limit = getMenuChoice(1, 100);
        
        System.out.println("\nTop " + limit + " Most Popular Books:");
        libraryService.getPopularBooks(limit).forEach(book -> 
            System.out.println("- " + book.getTitle() + " (ISBN: " + book.getIsbn() + ")"));
    }

    private void showActiveUsersReport() {
        System.out.print("Enter number of top users to show: ");
        int limit = getMenuChoice(1, 100);
        
        System.out.println("\nTop " + limit + " Most Active Users:");
        libraryService.getActiveUsers(limit).forEach(user -> 
            System.out.println("- " + user.getFullName() + " (" + user.getUserId() + ")"));
    }

    private void showOverdueLoansReport() {
        System.out.println("\nOverdue Loans:");
        libraryService.getOverdueLoans().forEach(loan -> 
            System.out.println("- " + loan.getBook().getTitle() + " borrowed by " + 
                loan.getUser().getFullName() + ", due on " + loan.getDueDate()));
    }

    private void showTotalFinesReport() {
        System.out.printf("\nTotal Outstanding Fines: $%.2f\n", libraryService.calculateTotalFines());
    }

    public static void main(String[] args) {
        new LibraryApp().start();
    }
}
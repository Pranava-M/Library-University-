package com.library.ui.console;

import com.library.services.LibraryService;
import java.util.Scanner;

public abstract class ConsoleMenu {
    protected final LibraryService libraryService;
    protected final Scanner scanner;

    public ConsoleMenu(LibraryService libraryService, Scanner scanner) {
        this.libraryService = libraryService;
        this.scanner = scanner;
    }

    public abstract void show();

    protected String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    protected int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    protected double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    protected void pressAnyKeyToContinue() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
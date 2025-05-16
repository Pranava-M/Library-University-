package com.library.ui.console;

import com.library.entities.Loan;
import com.library.exceptions.BookNotFoundException;
import com.library.exceptions.LoanException;
import com.library.exceptions.UserNotFoundException;
import com.library.services.LibraryService;

import java.util.List;

public class LoanMenu extends ConsoleMenu {
    public LoanMenu(LibraryService libraryService, Scanner scanner) {
        super(libraryService, scanner);
    }

    @Override
    public void show() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\nLoan Management:");
            System.out.println("1. Checkout Book");
            System.out.println("2. Return Book");
            System.out.println("3. View User Loans");
            System.out.println("4. View Overdue Loans");
            System.out.println("5. Renew Loan");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter your choice (1-6): ");
            
            int choice = getIntInput("");
            
            switch (choice) {
                case 1:
                    checkoutBook();
                    break;
                case 2:
                    returnBook();
                    break;
                case 3:
                    viewUserLoans();
                    break;
                case 4:
                    viewOverdueLoans();
                    break;
                case 5:
                    renewLoan();
                    break;
                case 6:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void checkoutBook() {
        System.out.println("\nCheckout Book");
        String isbn = getInput("Enter book ISBN: ");
        String userId = getInput("Enter user ID: ");
        
        try {
            Loan loan = libraryService.checkoutBook(isbn, userId);
            System.out.println("Book checked out successfully!");
            System.out.println("Loan ID: " + loan.getLoanId());
            System.out.println("Due Date: " + loan.getDueDate());
        } catch (BookNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (LoanException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        pressAnyKeyToContinue();
    }

    private void returnBook() {
        System.out.println("\nReturn Book");
        String loanId = getInput("Enter loan ID: ");
        
        try {
            libraryService.returnBook(loanId);
            System.out.println("Book returned successfully!");
        } catch (LoanException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        pressAnyKeyToContinue();
    }

    private void viewUserLoans() {
        System.out.println("\nView User Loans");
        String userId = getInput("Enter user ID: ");
        
        try {
            List<Loan> loans = libraryService.getUserLoans(userId);
            
            if (loans.isEmpty()) {
                System.out.println("No loans found for this user.");
            } else {
                System.out.println("\nCurrent Loans:");
                loans.stream()
                    .filter(loan -> loan.getStatus() == Loan.LoanStatus.ACTIVE || 
                                   loan.getStatus() == Loan.LoanStatus.OVERDUE)
                    .forEach(this::displayLoan);
                
                System.out.println("\nLoan History:");
                loans.stream()
                    .filter(loan -> loan.getStatus() == Loan.LoanStatus.RETURNED || 
                                   loan.getStatus() == Loan.LoanStatus.LOST)
                    .forEach(this::displayLoan);
            }
        } catch (UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        pressAnyKeyToContinue();
    }

    private void displayLoan(Loan loan) {
        System.out.println("- " + loan.getBook().getTitle() + 
            " (Loan ID: " + loan.getLoanId() + ")");
        System.out.println("  Status: " + loan.getStatus() + 
            ", Due: " + loan.getDueDate() + 
            (loan.getReturnDate() != null ? ", Returned: " + loan.getReturnDate() : ""));
    }

    private void viewOverdueLoans() {
        System.out.println("\nOverdue Loans:");
        
        List<Loan> loans = libraryService.getOverdueLoans();
        
        if (loans.isEmpty()) {
            System.out.println("No overdue loans found.");
        } else {
            loans.forEach(this::displayLoan);
        }
        
        pressAnyKeyToContinue();
    }

    private void renewLoan() {
        System.out.println("\nRenew Loan");
        String loanId = getInput("Enter loan ID: ");
        
        try {
            libraryService.renewLoan(loanId);
            System.out.println("Loan renewed successfully!");
        } catch (LoanException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        pressAnyKeyToContinue();
    }
}
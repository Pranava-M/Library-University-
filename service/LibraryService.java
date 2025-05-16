package com.library.services;

import com.library.entities.Book;
import com.library.entities.Loan;
import com.library.entities.User;
import java.util.List;

public interface LibraryService {
    // Book related operations
    void addNewBook(Book book);
    Book findBookByIsbn(String isbn) throws BookNotFoundException;
    List<Book> searchBooks(String query);
    void updateBookDetails(Book book) throws BookNotFoundException;
    void removeBook(String isbn) throws BookNotFoundException;
    
    // User related operations
    void registerNewUser(User user);
    User getUserDetails(String userId) throws UserNotFoundException;
    void updateUserInformation(User user) throws UserNotFoundException;
    void deactivateUser(String userId) throws UserNotFoundException;
    
    // Loan related operations
    Loan checkoutBook(String isbn, String userId) throws BookNotFoundException, UserNotFoundException, LoanException;
    void returnBook(String loanId) throws LoanException;
    List<Loan> getUserLoans(String userId) throws UserNotFoundException;
    List<Loan> getOverdueLoans();
    void renewLoan(String loanId) throws LoanException;
    
    // Reporting
    List<Book> getPopularBooks(int limit);
    List<User> getActiveUsers(int limit);
    double calculateTotalFines();
}
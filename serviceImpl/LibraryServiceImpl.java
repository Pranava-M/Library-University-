package com.library.services.impl;

import com.library.dao.BookDAO;
import com.library.dao.LoanDAO;
import com.library.dao.UserDAO;
import com.library.entities.*;
import com.library.exceptions.BookNotFoundException;
import com.library.exceptions.LoanException;
import com.library.exceptions.UserNotFoundException;
import com.library.services.LibraryService;
import com.library.utils.DateUtils;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LibraryServiceImpl implements LibraryService {
    private final BookDAO bookDAO;
    private final UserDAO userDAO;
    private final LoanDAO loanDAO;
    
    public LibraryServiceImpl() {
        this.bookDAO = new BookDAOImpl();
        this.userDAO = new UserDAOImpl();
        this.loanDAO = new LoanDAOImpl();
    }

    @Override
    public void addNewBook(Book book) {
        bookDAO.addBook(book);
    }

    @Override
    public Book findBookByIsbn(String isbn) throws BookNotFoundException {
        return bookDAO.getBookByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
    }

    @Override
    public List<Book> searchBooks(String query) {
        return bookDAO.searchBooks(query);
    }

    @Override
    public void updateBookDetails(Book book) throws BookNotFoundException {
        if (!bookDAO.getBookByIsbn(book.getIsbn()).isPresent()) {
            throw new BookNotFoundException("Cannot update - book not found with ISBN: " + book.getIsbn());
        }
        bookDAO.updateBook(book);
    }

    @Override
    public void removeBook(String isbn) throws BookNotFoundException {
        if (!bookDAO.getBookByIsbn(isbn).isPresent()) {
            throw new BookNotFoundException("Cannot remove - book not found with ISBN: " + isbn);
        }
        bookDAO.deleteBook(isbn);
    }

    @Override
    public void registerNewUser(User user) {
        userDAO.addUser(user);
    }

    @Override
    public User getUserDetails(String userId) throws UserNotFoundException {
        return userDAO.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    @Override
    public void updateUserInformation(User user) throws UserNotFoundException {
        if (!userDAO.getUserById(user.getUserId()).isPresent()) {
            throw new UserNotFoundException("Cannot update - user not found with ID: " + user.getUserId());
        }
        userDAO.updateUser(user);
    }

    @Override
    public void deactivateUser(String userId) throws UserNotFoundException {
        User user = getUserDetails(userId);
        user.setActive(false);
        userDAO.updateUser(user);
    }

    @Override
    public Loan checkoutBook(String isbn, String userId) throws BookNotFoundException, UserNotFoundException, LoanException {
        Book book = findBookByIsbn(isbn);
        User user = getUserDetails(userId);
        
        // Check if book is available
        if (!bookDAO.isBookAvailable(isbn)) {
            throw new LoanException("Book is not available for checkout");
        }
        
        // Check if user can borrow more books
        if (!user.canBorrowMoreBooks()) {
            throw new LoanException("User cannot borrow more books. Reason: " + 
                (user.getCurrentLoans().size() >= user.getMaxBooksAllowed() ? "Loan limit reached" : 
                 user.getFines() > 0 ? "Outstanding fines" : "Account inactive"));
        }
        
        // Check if user already has this book checked out
        List<Loan> userLoans = loanDAO.getLoansByUser(userId);
        if (userLoans.stream().anyMatch(loan -> 
            loan.getBook().getIsbn().equals(isbn) && 
            (loan.getStatus() == Loan.LoanStatus.ACTIVE || loan.getStatus() == Loan.LoanStatus.OVERDUE))) {
            throw new LoanException("User already has this book checked out");
        }
        
        // Determine loan period based on user type
        int loanPeriod = getLoanPeriodForUser(user);
        
        // Create and save the loan
        Loan loan = new Loan(generateLoanId(), book, user, loanPeriod);
        loanDAO.addLoan(loan);
        user.addLoan(loan);
        
        return loan;
    }

    private int getLoanPeriodForUser(User user) {
        switch (user.getUserType()) {
            case FACULTY: return 28; // 4 weeks
            case STAFF: return 21;   // 3 weeks
            default: return 14;      // 2 weeks for students and visitors
        }
    }

    private String generateLoanId() {
        return "LN" + System.currentTimeMillis();
    }

    @Override
    public void returnBook(String loanId) throws LoanException {
        Loan loan = loanDAO.getLoanById(loanId)
                .orElseThrow(() -> new LoanException("Loan not found with ID: " + loanId));
        
        if (loan.getStatus() != Loan.LoanStatus.ACTIVE && loan.getStatus() != Loan.LoanStatus.OVERDUE) {
            throw new LoanException("Cannot return book - loan is already completed");
        }
        
        loan.returnBook();
        loanDAO.updateLoan(loan);
        
        // Update user's current loans
        User user = loan.getUser();
        user.returnLoan(loan);
        userDAO.updateUser(user);
    }

    @Override
    public List<Loan> getUserLoans(String userId) throws UserNotFoundException {
        getUserDetails(userId); // Verify user exists
        return loanDAO.getLoansByUser(userId);
    }

    @Override
    public List<Loan> getOverdueLoans() {
        return loanDAO.getOverdueLoans();
    }

    @Override
    public void renewLoan(String loanId) throws LoanException {
        Loan loan = loanDAO.getLoanById(loanId)
                .orElseThrow(() -> new LoanException("Loan not found with ID: " + loanId));
        
        // Check if loan can be renewed
        if (loan.getStatus() != Loan.LoanStatus.ACTIVE && loan.getStatus() != Loan.LoanStatus.OVERDUE) {
            throw new LoanException("Only active or overdue loans can be renewed");
        }
        
        if (loan.getRenewalCount() >= 2) { // Assuming we track renewals in Loan entity
            throw new LoanException("Maximum renewals reached for this loan");
        }
        
        // Check if book has been requested by another user
        if (isBookRequested(loan.getBook().getIsbn())) {
            throw new LoanException("Cannot renew - book has been requested by another user");
        }
        
        // Renew the loan
        int renewalPeriod = getLoanPeriodForUser(loan.getUser());
        loan.setDueDate(LocalDate.now().plusDays(renewalPeriod));
        loan.setStatus(Loan.LoanStatus.ACTIVE);
        loan.incrementRenewalCount();
        loanDAO.updateLoan(loan);
    }

    private boolean isBookRequested(String isbn) {
        // In a real implementation, we would check a reservation/request system
        return false;
    }

    @Override
    public List<Book> getPopularBooks(int limit) {
        // Get all loans, group by book, count loans per book, sort by count, limit results
        return loanDAO.getAllLoans().stream()
                .collect(Collectors.groupingBy(Loan::getBook, Collectors.counting()))
                .entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .map(e -> e.getKey())
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getActiveUsers(int limit) {
        // Get all loans, group by user, count loans per user, sort by count, limit results
        return loanDAO.getAllLoans().stream()
                .collect(Collectors.groupingBy(Loan::getUser, Collectors.counting()))
                .entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .map(e -> e.getKey())
                .collect(Collectors.toList());
    }

    @Override
    public double calculateTotalFines() {
        return loanDAO.getAllLoans().stream()
                .mapToDouble(Loan::getFineAmount)
                .sum();
    }
}
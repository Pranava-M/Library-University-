package com.library.dao;

import com.library.entities.Book;
import com.library.entities.Loan;
import com.library.entities.User;
import com.library.utils.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoanDAOTest {
    private LoanDAO loanDAO;
    private BookDAO bookDAO;
    private UserDAO userDAO;
    private Connection connection;
    private final String testLoanId = "TESTLOAN123";
    private final String testIsbn = "TESTBOOK123";
    private final String testUserId = "TESTUSER123";
    
    @BeforeAll
    void setup() throws SQLException {
        connection = DatabaseConnection.getConnection();
        loanDAO = new LoanDAOImpl();
        bookDAO = new BookDAOImpl();
        userDAO = new UserDAOImpl();
        
        // Clear any existing test data
        connection.prepareStatement("DELETE FROM loans WHERE loan_id = '" + testLoanId + "'").executeUpdate();
        connection.prepareStatement("DELETE FROM books WHERE isbn = '" + testIsbn + "'").executeUpdate();
        connection.prepareStatement("DELETE FROM users WHERE user_id = '" + testUserId + "'").executeUpdate();
        
        // Create test book and user
        Book book = new Book(testIsbn, "Test Book");
        bookDAO.addBook(book);
        
        User user = new User(testUserId, "Test", "User", User.UserType.STUDENT);
        userDAO.addUser(user);
    }
    
    @AfterAll
    void tearDown() throws SQLException {
        // Clean up
        connection.prepareStatement("DELETE FROM loans WHERE loan_id = '" + testLoanId + "'").executeUpdate();
        connection.prepareStatement("DELETE FROM books WHERE isbn = '" + testIsbn + "'").executeUpdate();
        connection.prepareStatement("DELETE FROM users WHERE user_id = '" + testUserId + "'").executeUpdate();
        DatabaseConnection.closeConnection();
    }
    
    @Test
    void testAddAndGetLoan() {
        Book book = bookDAO.getBookByIsbn(testIsbn).orElseThrow();
        User user = userDAO.getUserById(testUserId).orElseThrow();
        
        Loan loan = new Loan(testLoanId, book, user, 14);
        loanDAO.addLoan(loan);
        
        Optional<Loan> retrieved = loanDAO.getLoanById(testLoanId);
        assertTrue(retrieved.isPresent(), "Loan should be present after adding");
        assertEquals(testIsbn, retrieved.get().getBook().getIsbn(), "Book ISBN should match");
        assertEquals(testUserId, retrieved.get().getUser().getUserId(), "User ID should match");
    }
    
    @Test
    void testUpdateLoan() {
        Book book = bookDAO.getBookByIsbn(testIsbn).orElseThrow();
        User user = userDAO.getUserById(testUserId).orElseThrow();
        
        Loan loan = new Loan(testLoanId, book, user, 14);
        loanDAO.addLoan(loan);
        
        loan.setStatus(Loan.LoanStatus.RETURNED);
        loanDAO.updateLoan(loan);
        
        Optional<Loan> updated = loanDAO.getLoanById(testLoanId);
        assertTrue(updated.isPresent(), "Loan should be present");
        assertEquals(Loan.LoanStatus.RETURNED, updated.get().getStatus(), "Status should be updated");
    }
    
    @Test
    void testDeleteLoan() {
        Book book = bookDAO.getBookByIsbn(testIsbn).orElseThrow();
        User user = userDAO.getUserById(testUserId).orElseThrow();
        
        Loan loan = new Loan(testLoanId, book, user, 14);
        loanDAO.addLoan(loan);
        
        loanDAO.deleteLoan(testLoanId);
        
        Optional<Loan> deleted = loanDAO.getLoanById(testLoanId);
        assertFalse(deleted.isPresent(), "Loan should not be present after deletion");
    }
    
    @Test
    void testGetLoansByUser() {
        Book book = bookDAO.getBookByIsbn(testIsbn).orElseThrow();
        User user = userDAO.getUserById(testUserId).orElseThrow();
        
        Loan loan = new Loan(testLoanId, book, user, 14);
        loanDAO.addLoan(loan);
        
        List<Loan> userLoans = loanDAO.getLoansByUser(testUserId);
        assertFalse(userLoans.isEmpty(), "User loans should not be empty");
        assertTrue(userLoans.stream().anyMatch(l -> l.getLoanId().equals(testLoanId)), 
            "User loans should include the test loan");
    }
    
    @Test
    void testGetOverdueLoans() {
        Book book = bookDAO.getBookByIsbn(testIsbn).orElseThrow();
        User user = userDAO.getUserById(testUserId).orElseThrow();
        
        Loan loan = new Loan(testLoanId, book, user, 14);
        loan.setDueDate(LocalDate.now().minusDays(1)); // Set to yesterday to make it overdue
        loanDAO.addLoan(loan);
        
        List<Loan> overdueLoans = loanDAO.getOverdueLoans();
        assertFalse(overdueLoans.isEmpty(), "Overdue loans should not be empty");
        assertTrue(overdueLoans.stream().anyMatch(l -> l.getLoanId().equals(testLoanId)), 
            "Overdue loans should include the test loan");
    }
}
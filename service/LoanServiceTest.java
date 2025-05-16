package com.library.services;

import com.library.entities.Book;
import com.library.entities.Loan;
import com.library.entities.User;
import com.library.exceptions.BookNotFoundException;
import com.library.exceptions.LoanException;
import com.library.exceptions.UserNotFoundException;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoanServiceTest {
    private LibraryService libraryService;
    private final String testLoanId = "TESTSVCLN123";
    private final String testIsbn = "TESTSVCBK123";
    private final String testUserId = "TESTSVCUS123";
    
    @BeforeAll
    void setup() throws BookNotFoundException, UserNotFoundException, LoanException {
        libraryService = new LibraryServiceImpl();
        
        // Clean up any existing test data
        try {
            libraryService.removeBook(testIsbn);
        } catch (BookNotFoundException e) {
            // Ignore if not exists
        }
        
        try {
            libraryService.deactivateUser(testUserId);
        } catch (UserNotFoundException e) {
            // Ignore if not exists
        }
        
        // Create test book and user
        Book book = new Book(testIsbn, "Service Test Book");
        book.setQuantity(5);
        book.setAvailableQuantity(5);
        libraryService.addNewBook(book);
        
        User user = new User(testUserId, "Service", "Test", User.UserType.STUDENT);
        libraryService.registerNewUser(user);
    }
    
    @AfterAll
    void tearDown() throws BookNotFoundException, UserNotFoundException {
        // Clean up
        try {
            libraryService.removeBook(testIsbn);
        } catch (BookNotFoundException e) {
            // Ignore if not exists
        }
        
        try {
            libraryService.deactivateUser(testUserId);
        } catch (UserNotFoundException e) {
            // Ignore if not exists
        }
    }
    
    @Test
    void testCheckoutAndReturnBook() throws BookNotFoundException, UserNotFoundException, LoanException {
        Loan loan = libraryService.checkoutBook(testIsbn, testUserId);
        assertNotNull(loan, "Loan should be created");
        assertEquals(testIsbn, loan.getBook().getIsbn(), "Book ISBN should match");
        assertEquals(testUserId, loan.getUser().getUserId(), "User ID should match");
        
        // Verify book availability decreased
        Book book = libraryService.findBookByIsbn(testIsbn);
        assertEquals(4, book.getAvailableQuantity(), "Available quantity should decrease after checkout");
        
        // Return the book
        libraryService.returnBook(loan.getLoanId());
        
        // Verify book availability increased
        book = libraryService.findBookByIsbn(testIsbn);
        assertEquals(5, book.getAvailableQuantity(), "Available quantity should increase after return");
    }
    
    @Test
    void testGetUserLoans() throws BookNotFoundException, UserNotFoundException, LoanException {
        Loan loan = libraryService.checkoutBook(testIsbn, testUserId);
        
        List<Loan> userLoans = libraryService.getUserLoans(testUserId);
        assertFalse(userLoans.isEmpty(), "User should have loans");
        assertTrue(userLoans.stream().anyMatch(l -> l.getLoanId().equals(loan.getLoanId())), 
            "User loans should include the test loan");
        
        // Clean up
        libraryService.returnBook(loan.getLoanId());
    }
    
    @Test
    void testGetOverdueLoans() throws BookNotFoundException, UserNotFoundException, LoanException {
        Book book = libraryService.findBookByIsbn(testIsbn);
        User user = libraryService.getUserDetails(testUserId);
        
        // Create a loan with past due date
        Loan loan = new Loan(testLoanId, book, user, -1); // Negative days to make it overdue
        libraryService.addLoan(loan);
        
        List<Loan> overdueLoans = libraryService.getOverdueLoans();
        assertFalse(overdueLoans.isEmpty(), "Overdue loans should not be empty");
        assertTrue(overdueLoans.stream().anyMatch(l -> l.getLoanId().equals(testLoanId)), 
            "Overdue loans should include the test loan");
        
        // Clean up
        libraryService.returnBook(testLoanId);
    }
    
    @Test
    void testRenewLoan() throws BookNotFoundException, UserNotFoundException, LoanException {
        Loan loan = libraryService.checkoutBook(testIsbn, testUserId);
        LocalDate originalDueDate = loan.getDueDate();
        
        libraryService.renewLoan(loan.getLoanId());
        
        Loan renewed = libraryService.getLoanById(loan.getLoanId());
        assertTrue(renewed.getDueDate().isAfter(originalDueDate), "Due date should be extended after renewal");
        
        // Clean up
        libraryService.returnBook(loan.getLoanId());
    }
    
    @Test
    void testPopularBooksReport() throws BookNotFoundException, UserNotFoundException, LoanException {
        // Checkout the book multiple times to make it popular
        for (int i = 0; i < 3; i++) {
            Loan loan = libraryService.checkoutBook(testIsbn, testUserId);
            libraryService.returnBook(loan.getLoanId());
        }
        
        List<Book> popularBooks = libraryService.getPopularBooks(1);
        assertFalse(popularBooks.isEmpty(), "Popular books should not be empty");
        assertEquals(testIsbn, popularBooks.get(0).getIsbn(), "Test book should be the most popular");
    }
}
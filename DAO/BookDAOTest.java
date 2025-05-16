package com.library.dao;

import com.library.entities.Book;
import com.library.utils.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookDAOTest {
    private BookDAO bookDAO;
    private Connection connection;
    private final String testIsbn = "TEST1234567890";
    
    @BeforeAll
    void setup() throws SQLException {
        connection = DatabaseConnection.getConnection();
        bookDAO = new BookDAOImpl();
        
        // Clear any existing test data
        connection.prepareStatement("DELETE FROM books WHERE isbn = '" + testIsbn + "'").executeUpdate();
    }
    
    @AfterAll
    void tearDown() throws SQLException {
        // Clean up
        connection.prepareStatement("DELETE FROM books WHERE isbn = '" + testIsbn + "'").executeUpdate();
        DatabaseConnection.closeConnection();
    }
    
    @Test
    void testAddAndGetBook() {
        Book book = new Book(testIsbn, "Test Book");
        book.setPublicationDate(LocalDate.now());
        book.setQuantity(5);
        book.setAvailableQuantity(5);
        
        bookDAO.addBook(book);
        
        Optional<Book> retrieved = bookDAO.getBookByIsbn(testIsbn);
        assertTrue(retrieved.isPresent(), "Book should be present after adding");
        assertEquals("Test Book", retrieved.get().getTitle(), "Title should match");
        assertEquals(5, retrieved.get().getQuantity(), "Quantity should match");
    }
    
    @Test
    void testUpdateBook() {
        Book book = new Book(testIsbn, "Test Book");
        book.setPublicationDate(LocalDate.now());
        book.setQuantity(5);
        book.setAvailableQuantity(5);
        bookDAO.addBook(book);
        
        book.setTitle("Updated Test Book");
        book.setQuantity(10);
        bookDAO.updateBook(book);
        
        Optional<Book> updated = bookDAO.getBookByIsbn(testIsbn);
        assertTrue(updated.isPresent(), "Book should be present");
        assertEquals("Updated Test Book", updated.get().getTitle(), "Title should be updated");
        assertEquals(10, updated.get().getQuantity(), "Quantity should be updated");
    }
    
    @Test
    void testDeleteBook() {
        Book book = new Book(testIsbn, "Test Book");
        bookDAO.addBook(book);
        
        bookDAO.deleteBook(testIsbn);
        
        Optional<Book> deleted = bookDAO.getBookByIsbn(testIsbn);
        assertFalse(deleted.isPresent(), "Book should not be present after deletion");
    }
    
    @Test
    void testSearchBooks() {
        Book book = new Book(testIsbn, "Test Book for Search");
        bookDAO.addBook(book);
        
        List<Book> results = bookDAO.searchBooks("Test Book");
        assertFalse(results.isEmpty(), "Search should return results");
        assertTrue(results.stream().anyMatch(b -> b.getIsbn().equals(testIsbn)), 
            "Search results should include the test book");
    }
    
    @Test
    void testBookAvailability() {
        Book book = new Book(testIsbn, "Test Book");
        book.setQuantity(2);
        book.setAvailableQuantity(1);
        bookDAO.addBook(book);
        
        assertTrue(bookDAO.isBookAvailable(testIsbn), "Book should be available");
        assertEquals(1, bookDAO.getAvailableQuantity(testIsbn), "Available quantity should match");
    }
}
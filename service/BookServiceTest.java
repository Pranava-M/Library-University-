package com.library.services;

import com.library.entities.Book;
import com.library.exceptions.BookNotFoundException;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookServiceTest {
    private BookService bookService;
    private final String testIsbn = "TESTSVC123";
    
    @BeforeAll
    void setup() {
        bookService = new BookServiceImpl();
        
        // Clean up any existing test data
        try {
            bookService.deleteBook(testIsbn);
        } catch (BookNotFoundException e) {
            // Ignore if not exists
        }
    }
    
    @AfterAll
    void tearDown() {
        // Clean up
        try {
            bookService.deleteBook(testIsbn);
        } catch (BookNotFoundException e) {
            // Ignore if not exists
        }
    }
    
    @Test
    void testAddAndGetBook() throws BookNotFoundException {
        Book book = new Book(testIsbn, "Service Test Book");
        book.setPublicationDate(LocalDate.now());
        
        bookService.addBook(book);
        
        Book retrieved = bookService.getBookByIsbn(testIsbn);
        assertNotNull(retrieved, "Book should be retrievable after adding");
        assertEquals("Service Test Book", retrieved.getTitle(), "Title should match");
    }
    
    @Test
    void testUpdateBook() throws BookNotFoundException {
        Book book = new Book(testIsbn, "Service Test Book");
        bookService.addBook(book);
        
        book.setTitle("Updated Service Test Book");
        bookService.updateBook(book);
        
        Book updated = bookService.getBookByIsbn(testIsbn);
        assertEquals("Updated Service Test Book", updated.getTitle(), "Title should be updated");
    }
    
    @Test
    void testDeleteBook() {
        Book book = new Book(testIsbn, "Service Test Book");
        bookService.addBook(book);
        
        assertDoesNotThrow(() -> bookService.deleteBook(testIsbn), 
            "Deletion should not throw for existing book");
        
        assertThrows(BookNotFoundException.class, () -> bookService.getBookByIsbn(testIsbn), 
            "Book should not exist after deletion");
    }
    
    @Test
    void testSearchBooks() {
        Book book = new Book(testIsbn, "Service Search Test Book");
        bookService.addBook(book);
        
        List<Book> results = bookService.searchBooks("Search Test");
        assertFalse(results.isEmpty(), "Search should return results");
        assertTrue(results.stream().anyMatch(b -> b.getIsbn().equals(testIsbn)), 
            "Search results should include the test book");
    }
    
    @Test
    void testBookAvailability() throws BookNotFoundException {
        Book book = new Book(testIsbn, "Availability Test Book");
        book.setQuantity(3);
        book.setAvailableQuantity(1);
        bookService.addBook(book);
        
        assertTrue(bookService.isBookAvailable(testIsbn), "Book should be available");
        assertEquals(1, bookService.getAvailableQuantity(testIsbn), "Available quantity should match");
    }
}
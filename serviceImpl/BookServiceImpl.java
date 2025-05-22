package com.library.services.impl;
import com.library.dao.BookDAO;
import com.library.entities.Book;
import com.library.exceptions.BookNotFoundException;
import com.library.services.BookService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookServiceImpl implements BookService {
    private final BookDAO bookDAO;
    
    public BookServiceImpl() {
        this.bookDAO = new BookDAOImpl();
    }

    @Override
    public void addBook(Book book) {
        if (bookDAO.getBookByIsbn(book.getIsbn()).isPresent()) {
            throw new IllegalArgumentException("Book with this ISBN already exists");
        }
        bookDAO.addBook(book);
    }

    @Override
    public Book getBookByIsbn(String isbn) {
        return bookDAO.getBookByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
    }

    @Override
    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }

    @Override
    public List<Book> searchBooks(String query) {
        return bookDAO.searchBooks(query);
    }

    @Override
    public void updateBook(Book book) {
        if (!bookDAO.getBookByIsbn(book.getIsbn()).isPresent()) {
            throw new BookNotFoundException("Cannot update - book not found");
        }
        bookDAO.updateBook(book);
    }

    @Override
    public void deleteBook(String isbn) {
        if (!bookDAO.getBookByIsbn(isbn).isPresent()) {
            throw new BookNotFoundException("Cannot delete - book not found");
        }
        bookDAO.deleteBook(isbn);
    }

    @Override
    public boolean isBookAvailable(String isbn) {
        return bookDAO.isBookAvailable(isbn);
    }

    @Override
    public int getAvailableQuantity(String isbn) {
        return bookDAO.getAvailableQuantity(isbn);
    }

    @Override
    public List<Book> getBooksByAuthor(String authorName) {
        return bookDAO.getBooksByAuthor(authorName);
    }

    @Override
    public List<Book> getBooksByGenre(String genreId) {
        return bookDAO.getBooksByGenre(genreId);
    }

    @Override
    public List<Book> getNewArrivals(int limit) {
        return bookDAO.getAllBooks().stream()
                .filter(book -> book.getPublicationDate() != null)
                .sorted(Comparator.comparing(Book::getPublicationDate).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}

package com.library.dao;

import com.library.entities.Book;
import java.util.List;
import java.util.Optional;

public interface BookDAO {
    void addBook(Book book);
    Optional<Book> getBookByIsbn(String isbn);
    List<Book> getAllBooks();
    List<Book> getBooksByTitle(String title);
    List<Book> getBooksByAuthor(String authorName);
    List<Book> getBooksByGenre(String genreId);
    void updateBook(Book book);
    void deleteBook(String isbn);
    boolean isBookAvailable(String isbn);
    int getAvailableQuantity(String isbn);
    List<Book> searchBooks(String query);
}
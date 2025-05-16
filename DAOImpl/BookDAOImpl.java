package com.library.dao.impl;

import com.library.dao.BookDAO;
import com.library.entities.Book;
import com.library.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookDAOImpl implements BookDAO {
    private final Connection connection;

    public BookDAOImpl() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public void addBook(Book book) {
        String sql = "INSERT INTO books (isbn, title, publication_date, quantity, available_quantity, publisher, " +
                     "edition, description, language, page_count, is_reference_only, genre_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setDate(3, Date.valueOf(book.getPublicationDate()));
            stmt.setInt(4, book.getQuantity());
            stmt.setInt(5, book.getAvailableQuantity());
            stmt.setString(6, book.getPublisher());
            stmt.setInt(7, book.getEdition());
            stmt.setString(8, book.getDescription());
            stmt.setString(9, book.getLanguage());
            stmt.setInt(10, book.getPageCount());
            stmt.setBoolean(11, book.isReferenceOnly());
            stmt.setString(12, book.getGenre().getGenreId());
            
            stmt.executeUpdate();
            
            // Add book-author relationships
            addBookAuthors(book);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add book", e);
        }
    }

    private void addBookAuthors(Book book) throws SQLException {
        String sql = "INSERT INTO book_authors (book_isbn, author_id) VALUES (?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (Author author : book.getAuthors()) {
                stmt.setString(1, book.getIsbn());
                stmt.setString(2, author.getAuthorId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    @Override
    public Optional<Book> getBookByIsbn(String isbn) {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Book book = extractBookFromResultSet(rs);
                book.setAuthors(getAuthorsForBook(isbn));
                return Optional.of(book);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get book by ISBN", e);
        }
        
        return Optional.empty();
    }

    private Book extractBookFromResultSet(ResultSet rs) throws SQLException {
        Book book = new Book(rs.getString("isbn"), rs.getString("title"));
        book.setPublicationDate(rs.getDate("publication_date").toLocalDate());
        book.setQuantity(rs.getInt("quantity"));
        book.setAvailableQuantity(rs.getInt("available_quantity"));
        book.setPublisher(rs.getString("publisher"));
        book.setEdition(rs.getInt("edition"));
        book.setDescription(rs.getString("description"));
        book.setLanguage(rs.getString("language"));
        book.setPageCount(rs.getInt("page_count"));
        book.setReferenceOnly(rs.getBoolean("is_reference_only"));
        
        // Note: Genre would need to be set separately as it requires a GenreDAO
        return book;
    }

    private Set<Author> getAuthorsForBook(String isbn) throws SQLException {
        Set<Author> authors = new HashSet<>();
        String sql = "SELECT a.* FROM authors a JOIN book_authors ba ON a.author_id = ba.author_id WHERE ba.book_isbn = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Author author = new Author(
                    rs.getString("author_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name")
                );
                author.setDateOfBirth(rs.getDate("date_of_birth") != null ? 
                    rs.getDate("date_of_birth").toLocalDate() : null);
                author.setNationality(rs.getString("nationality"));
                author.setBiography(rs.getString("biography"));
                authors.add(author);
            }
        }
        
        return authors;
    }

    @Override
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT isbn FROM books";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                getBookByIsbn(rs.getString("isbn")).ifPresent(books::add);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all books", e);
        }
        
        return books;
    }

    @Override
    public List<Book> getBooksByTitle(String title) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT isbn FROM books WHERE title LIKE ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + title + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                getBookByIsbn(rs.getString("isbn")).ifPresent(books::add);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get books by title", e);
        }
        
        return books;
    }

    @Override
    public List<Book> getBooksByAuthor(String authorName) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT DISTINCT b.isbn FROM books b " +
                     "JOIN book_authors ba ON b.isbn = ba.book_isbn " +
                     "JOIN authors a ON ba.author_id = a.author_id " +
                     "WHERE CONCAT(a.first_name, ' ', a.last_name) LIKE ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + authorName + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                getBookByIsbn(rs.getString("isbn")).ifPresent(books::add);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get books by author", e);
        }
        
        return books;
    }

    @Override
    public List<Book> getBooksByGenre(String genreId) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT isbn FROM books WHERE genre_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, genreId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                getBookByIsbn(rs.getString("isbn")).ifPresent(books::add);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get books by genre", e);
        }
        
        return books;
    }

    @Override
    public void updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, publication_date = ?, quantity = ?, " +
                     "available_quantity = ?, publisher = ?, edition = ?, description = ?, " +
                     "language = ?, page_count = ?, is_reference_only = ?, genre_id = ? " +
                     "WHERE isbn = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setDate(2, Date.valueOf(book.getPublicationDate()));
            stmt.setInt(3, book.getQuantity());
            stmt.setInt(4, book.getAvailableQuantity());
            stmt.setString(5, book.getPublisher());
            stmt.setInt(6, book.getEdition());
            stmt.setString(7, book.getDescription());
            stmt.setString(8, book.getLanguage());
            stmt.setInt(9, book.getPageCount());
            stmt.setBoolean(10, book.isReferenceOnly());
            stmt.setString(11, book.getGenre().getGenreId());
            stmt.setString(12, book.getIsbn());
            
            stmt.executeUpdate();
            
            // Update authors - first remove all existing relationships
            removeAllAuthorsForBook(book.getIsbn());
            // Then add the current authors
            addBookAuthors(book);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update book", e);
        }
    }

    private void removeAllAuthorsForBook(String isbn) throws SQLException {
        String sql = "DELETE FROM book_authors WHERE book_isbn = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteBook(String isbn) {
        // First delete the author relationships
        try {
            removeAllAuthorsForBook(isbn);
            
            // Then delete the book
            String sql = "DELETE FROM books WHERE isbn = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, isbn);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete book", e);
        }
    }

    @Override
    public boolean isBookAvailable(String isbn) {
        String sql = "SELECT available_quantity FROM books WHERE isbn = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("available_quantity") > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check book availability", e);
        }
    }

    @Override
    public int getAvailableQuantity(String isbn) {
        String sql = "SELECT available_quantity FROM books WHERE isbn = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("available_quantity");
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get available quantity", e);
        }
    }

    @Override
    public List<Book> searchBooks(String query) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT DISTINCT b.isbn FROM books b " +
                     "LEFT JOIN book_authors ba ON b.isbn = ba.book_isbn " +
                     "LEFT JOIN authors a ON ba.author_id = a.author_id " +
                     "WHERE b.title LIKE ? OR b.description LIKE ? OR " +
                     "CONCAT(a.first_name, ' ', a.last_name) LIKE ? OR " +
                     "b.isbn LIKE ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String searchTerm = "%" + query + "%";
            stmt.setString(1, searchTerm);
            stmt.setString(2, searchTerm);
            stmt.setString(3, searchTerm);
            stmt.setString(4, searchTerm);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                getBookByIsbn(rs.getString("isbn")).ifPresent(books::add);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search books", e);
        }
        
        return books;
    }
}
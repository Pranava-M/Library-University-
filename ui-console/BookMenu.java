package com.library.ui.console;

import com.library.entities.Book;
import com.library.exceptions.BookNotFoundException;
import com.library.services.LibraryService;
import com.library.utils.InputValidator;

import java.util.List;

public class BookMenu extends ConsoleMenu {
    public BookMenu(LibraryService libraryService, Scanner scanner) {
        super(libraryService, scanner);
    }

    @Override
    public void show() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\nBook Management:");
            System.out.println("1. Add New Book");
            System.out.println("2. Search Books");
            System.out.println("3. View Book Details");
            System.out.println("4. Update Book");
            System.out.println("5. Delete Book");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter your choice (1-6): ");
            
            int choice = getIntInput("");
            
            switch (choice) {
                case 1:
                    addBook();
                    break;
                case 2:
                    searchBooks();
                    break;
                case 3:
                    viewBookDetails();
                    break;
                case 4:
                    updateBook();
                    break;
                case 5:
                    deleteBook();
                    break;
                case 6:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void addBook() {
        System.out.println("\nAdd New Book");
        
        String isbn = getInput("Enter ISBN: ");
        String title = getInput("Enter title: ");
        
        // In a real application, we would create a complete Book object with all fields
        Book book = new Book(isbn, title);
        
        try {
            libraryService.addNewBook(book);
            System.out.println("Book added successfully!");
        } catch (Exception e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
        
        pressAnyKeyToContinue();
    }

    private void searchBooks() {
        System.out.println("\nSearch Books");
        String query = getInput("Enter search term: ");
        
        List<Book> books = libraryService.searchBooks(query);
        
        if (books.isEmpty()) {
            System.out.println("No books found matching your search.");
        } else {
            System.out.println("\nSearch Results:");
            books.forEach(book -> System.out.println(
                "- " + book.getTitle() + " by " + getAuthorsString(book) + " (ISBN: " + book.getIsbn() + ")"));
        }
        
        pressAnyKeyToContinue();
    }

    private String getAuthorsString(Book book) {
        if (book.getAuthors() == null || book.getAuthors().isEmpty()) {
            return "Unknown Author";
        }
        return book.getAuthors().stream()
                .map(author -> author.getFirstName() + " " + author.getLastName())
                .collect(Collectors.joining(", "));
    }

    private void viewBookDetails() {
        System.out.println("\nView Book Details");
        String isbn = getInput("Enter ISBN: ");
        
        try {
            Book book = libraryService.findBookByIsbn(isbn);
            displayBookDetails(book);
        } catch (BookNotFoundException e) {
            System.out.println("Book not found: " + e.getMessage());
        }
        
        pressAnyKeyToContinue();
    }

    private void displayBookDetails(Book book) {
        System.out.println("\nBook Details:");
        System.out.println("Title: " + book.getTitle());
        System.out.println("ISBN: " + book.getIsbn());
        System.out.println("Authors: " + getAuthorsString(book));
        System.out.println("Publisher: " + (book.getPublisher() != null ? book.getPublisher() : "N/A"));
        System.out.println("Publication Date: " + (book.getPublicationDate() != null ? book.getPublicationDate() : "N/A"));
        System.out.println("Edition: " + book.getEdition());
        System.out.println("Available: " + book.getAvailableQuantity() + "/" + book.getQuantity());
        System.out.println("Description: " + (book.getDescription() != null ? 
            book.getDescription() : "No description available"));
    }

    private void updateBook() {
        System.out.println("\nUpdate Book");
        String isbn = getInput("Enter ISBN of book to update: ");
        
        try {
            Book book = libraryService.findBookByIsbn(isbn);
            displayBookDetails(book);
            
            System.out.println("\nEnter new details (leave blank to keep current value):");
            String newTitle = getInput("New title [" + book.getTitle() + "]: ");
            if (!newTitle.isEmpty()) {
                book.setTitle(newTitle);
            }
            
            // In a real application, we would update all fields
            
            libraryService.updateBookDetails(book);
            System.out.println("Book updated successfully!");
        } catch (BookNotFoundException e) {
            System.out.println("Book not found: " + e.getMessage());
        }
        
        pressAnyKeyToContinue();
    }

    private void deleteBook() {
        System.out.println("\nDelete Book");
        String isbn = getInput("Enter ISBN of book to delete: ");
        
        try {
            libraryService.removeBook(isbn);
            System.out.println("Book deleted successfully!");
        } catch (BookNotFoundException e) {
            System.out.println("Book not found: " + e.getMessage());
        }
        
        pressAnyKeyToContinue();
    }
}
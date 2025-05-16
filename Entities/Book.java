package com.library.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Book {
    private String isbn;
    private String title;
    private Set<Author> authors;
    private Genre genre;
    private LocalDate publicationDate;
    private int quantity;
    private int availableQuantity;
    private String publisher;
    private int edition;
    private String description;
    private String language;
    private int pageCount;
    private boolean isReferenceOnly;

    public Book(String isbn, String title) {
        this.isbn = isbn;
        this.title = title;
        this.authors = new HashSet<>();
    }

    // Getters and setters
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public Set<Author> getAuthors() { return authors; }
    public void addAuthor(Author author) { this.authors.add(author); }
    public void removeAuthor(Author author) { this.authors.remove(author); }
    
    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }
    
    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { 
        this.quantity = quantity;
        this.availableQuantity = quantity - (this.quantity - this.availableQuantity);
    }
    
    public int getAvailableQuantity() { return availableQuantity; }
    public void decreaseAvailableQuantity() { 
        if (availableQuantity > 0) availableQuantity--; 
    }
    public void increaseAvailableQuantity() { 
        if (availableQuantity < quantity) availableQuantity++; 
    }
    
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    
    public int getEdition() { return edition; }
    public void setEdition(int edition) { this.edition = edition; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }
    
    public boolean isReferenceOnly() { return isReferenceOnly; }
    public void setReferenceOnly(boolean referenceOnly) { isReferenceOnly = referenceOnly; }

    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", authors=" + authors +
                ", genre=" + genre +
                ", available=" + availableQuantity + "/" + quantity +
                '}';
    }
}
package com.library.entities;

import java.util.HashSet;
import java.util.Set;

public class Genre {
    private String genreId;
    private String name;
    private String description;
    private Set<Book> books;

    public Genre(String genreId, String name) {
        this.genreId = genreId;
        this.name = name;
        this.books = new HashSet<>();
    }

    // Getters and setters
    public String getGenreId() { return genreId; }
    public void setGenreId(String genreId) { this.genreId = genreId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Set<Book> getBooks() { return books; }
    public void addBook(Book book) { books.add(book); }
    public void removeBook(Book book) { books.remove(book); }

    @Override
    public String toString() {
        return "Genre{" +
                "name='" + name + '\'' +
                ", books=" + books.size() +
                '}';
    }
}
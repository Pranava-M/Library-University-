package com.library.entities;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Author {
    private String authorId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String nationality;
    private String biography;
    private Set<Book> books;

    public Author(String authorId, String firstName, String lastName) {
        this.authorId = authorId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.books = new HashSet<>();
    }

    // Getters and setters
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getFullName() { return firstName + " " + lastName; }
    
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    
    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }
    
    public Set<Book> getBooks() { return books; }
    public void addBook(Book book) { books.add(book); }
    public void removeBook(Book book) { books.remove(book); }

    @Override
    public String toString() {
        return "Author{" +
                "name='" + getFullName() + '\'' +
                ", books=" + books.size() +
                '}';
    }
}
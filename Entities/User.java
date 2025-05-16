package com.library.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate registrationDate;
    private LocalDate dateOfBirth;
    private String address;
    private UserType userType;
    private int maxBooksAllowed;
    private List<Loan> currentLoans;
    private List<Loan> loanHistory;
    private double fines;
    private boolean isActive;

    public enum UserType {
        STUDENT(5),
        FACULTY(10),
        STAFF(7),
        VISITOR(3);

        private final int maxBooks;

        UserType(int maxBooks) {
            this.maxBooks = maxBooks;
        }

        public int getMaxBooks() {
            return maxBooks;
        }
    }

    public User(String userId, String firstName, String lastName, UserType userType) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.maxBooksAllowed = userType.getMaxBooks();
        this.currentLoans = new ArrayList<>();
        this.loanHistory = new ArrayList<>();
        this.registrationDate = LocalDate.now();
        this.isActive = true;
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getFullName() { return firstName + " " + lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
    
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { 
        this.userType = userType;
        this.maxBooksAllowed = userType.getMaxBooks();
    }
    
    public int getMaxBooksAllowed() { return maxBooksAllowed; }
    
    public List<Loan> getCurrentLoans() { return currentLoans; }
    public void addLoan(Loan loan) { 
        currentLoans.add(loan); 
        loanHistory.add(loan);
    }
    public void returnLoan(Loan loan) { currentLoans.remove(loan); }
    
    public List<Loan> getLoanHistory() { return loanHistory; }
    
    public double getFines() { return fines; }
    public void addFine(double amount) { fines += amount; }
    public void payFine(double amount) { fines = Math.max(0, fines - amount); }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public boolean canBorrowMoreBooks() {
        return currentLoans.size() < maxBooksAllowed && fines == 0 && isActive;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + getFullName() + '\'' +
                ", type=" + userType +
                ", loans=" + currentLoans.size() + "/" + maxBooksAllowed +
                ", fines=" + fines +
                '}';
    }
}
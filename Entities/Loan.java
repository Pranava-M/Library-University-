package com.library.entities;

import java.time.LocalDate;

public class Loan {
    private String loanId;
    private Book book;
    private User user;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private LoanStatus status;
    private double fineAmount;

    public enum LoanStatus {
        ACTIVE,
        RETURNED,
        OVERDUE,
        LOST
    }

    public Loan(String loanId, Book book, User user, int loanPeriodDays) {
        this.loanId = loanId;
        this.book = book;
        this.user = user;
        this.loanDate = LocalDate.now();
        this.dueDate = loanDate.plusDays(loanPeriodDays);
        this.status = LoanStatus.ACTIVE;
        this.fineAmount = 0.0;
        book.decreaseAvailableQuantity();
    }

    // Getters and setters
    public String getLoanId() { return loanId; }
    
    public Book getBook() { return book; }
    
    public User getUser() { return user; }
    
    public LocalDate getLoanDate() { return loanDate; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    public LocalDate getReturnDate() { return returnDate; }
    
    public LoanStatus getStatus() { return status; }
    
    public double getFineAmount() { return fineAmount; }

    public void returnBook() {
        this.returnDate = LocalDate.now();
        this.status = LoanStatus.RETURNED;
        book.increaseAvailableQuantity();
        
        if (returnDate.isAfter(dueDate)) {
            calculateFine();
        }
    }

    public void markAsLost() {
        this.status = LoanStatus.LOST;
        this.fineAmount = book.getPrice() * 1.5; // Charge 1.5 times the book price
    }

    private void calculateFine() {
        long daysOverdue = returnDate.toEpochDay() - dueDate.toEpochDay();
        fineAmount = daysOverdue * 0.50; // $0.50 per day fine
        user.addFine(fineAmount);
    }

    public void checkOverdue() {
        if (status == LoanStatus.ACTIVE && LocalDate.now().isAfter(dueDate)) {
            status = LoanStatus.OVERDUE;
        }
    }

    @Override
    public String toString() {
        return "Loan{" +
                "book=" + book.getTitle() +
                ", user=" + user.getFullName() +
                ", status=" + status +
                ", due=" + dueDate +
                (returnDate != null ? ", returned=" + returnDate : "") +
                '}';
    }
}
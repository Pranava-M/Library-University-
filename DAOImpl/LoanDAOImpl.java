package com.library.dao.impl;

import com.library.dao.LoanDAO;
import com.library.entities.Loan;
import com.library.entities.Loan.LoanStatus;
import com.library.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoanDAOImpl implements LoanDAO {
    private final Connection connection;
    private final BookDAOImpl bookDAO;
    private final UserDAOImpl userDAO;

    public LoanDAOImpl() {
        this.connection = DatabaseConnection.getConnection();
        this.bookDAO = new BookDAOImpl();
        this.userDAO = new UserDAOImpl();
    }

    @Override
    public void addLoan(Loan loan) {
        String sql = "INSERT INTO loans (loan_id, book_isbn, user_id, loan_date, due_date, " +
                     "return_date, status, fine_amount) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, loan.getLoanId());
            stmt.setString(2, loan.getBook().getIsbn());
            stmt.setString(3, loan.getUser().getUserId());
            stmt.setDate(4, Date.valueOf(loan.getLoanDate()));
            stmt.setDate(5, Date.valueOf(loan.getDueDate()));
            stmt.setDate(6, loan.getReturnDate() != null ? Date.valueOf(loan.getReturnDate()) : null);
            stmt.setString(7, loan.getStatus().name());
            stmt.setDouble(8, loan.getFineAmount());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add loan", e);
        }
    }

    @Override
    public Optional<Loan> getLoanById(String loanId) {
        String sql = "SELECT * FROM loans WHERE loan_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, loanId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(extractLoanFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get loan by ID", e);
        }
        
        return Optional.empty();
    }

    private Loan extractLoanFromResultSet(ResultSet rs) throws SQLException {
        String bookIsbn = rs.getString("book_isbn");
        String userId = rs.getString("user_id");
        
        Loan loan = new Loan(
            rs.getString("loan_id"),
            bookDAO.getBookByIsbn(bookIsbn).orElseThrow(() -> 
                new RuntimeException("Book not found for ISBN: " + bookIsbn)),
            userDAO.getUserById(userId).orElseThrow(() -> 
                new RuntimeException("User not found for ID: " + userId)),
            14 // Default loan period - would normally come from user type
        );
        
        loan.setLoanDate(rs.getDate("loan_date").toLocalDate());
        loan.setDueDate(rs.getDate("due_date").toLocalDate());
        if (rs.getDate("return_date") != null) {
            loan.setReturnDate(rs.getDate("return_date").toLocalDate());
        }
        loan.setStatus(LoanStatus.valueOf(rs.getString("status")));
        loan.setFineAmount(rs.getDouble("fine_amount"));
        
        return loan;
    }

    @Override
    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT loan_id FROM loans";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                getLoanById(rs.getString("loan_id")).ifPresent(loans::add);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all loans", e);
        }
        
        return loans;
    }

    @Override
    public List<Loan> getLoansByUser(String userId) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT loan_id FROM loans WHERE user_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                getLoanById(rs.getString("loan_id")).ifPresent(loans::add);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get loans by user", e);
        }
        
        return loans;
    }

    @Override
    public List<Loan> getLoansByBook(String isbn) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT loan_id FROM loans WHERE book_isbn = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                getLoanById(rs.getString("loan_id")).ifPresent(loans::add);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get loans by book", e);
        }
        
        return loans;
    }

    @Override
    public List<Loan> getActiveLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT loan_id FROM loans WHERE status = 'ACTIVE'";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                getLoanById(rs.getString("loan_id")).ifPresent(loans::add);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get active loans", e);
        }
        
        return loans;
    }

    @Override
    public List<Loan> getOverdueLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT loan_id FROM loans WHERE status = 'OVERDUE' OR " +
                     "(status = 'ACTIVE' AND due_date < CURRENT_DATE)";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                getLoanById(rs.getString("loan_id")).ifPresent(loans::add);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get overdue loans", e);
        }
        
        return loans;
    }

    @Override
    public void updateLoan(Loan loan) {
        String sql = "UPDATE loans SET book_isbn = ?, user_id = ?, loan_date = ?, " +
                     "due_date = ?, return_date = ?, status = ?, fine_amount = ? " +
                     "WHERE loan_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, loan.getBook().getIsbn());
            stmt.setString(2, loan.getUser().getUserId());
            stmt.setDate(3, Date.valueOf(loan.getLoanDate()));
            stmt.setDate(4, Date.valueOf(loan.getDueDate()));
            stmt.setDate(5, loan.getReturnDate() != null ? Date.valueOf(loan.getReturnDate()) : null);
            stmt.setString(6, loan.getStatus().name());
            stmt.setDouble(7, loan.getFineAmount());
            stmt.setString(8, loan.getLoanId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update loan", e);
        }
    }

    @Override
    public void deleteLoan(String loanId) {
        String sql = "DELETE FROM loans WHERE loan_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, loanId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete loan", e);
        }
    }
}
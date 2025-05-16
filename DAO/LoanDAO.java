package com.library.dao;

import com.library.entities.Loan;
import java.util.List;
import java.util.Optional;

public interface LoanDAO {
    void addLoan(Loan loan);
    Optional<Loan> getLoanById(String loanId);
    List<Loan> getAllLoans();
    List<Loan> getLoansByUser(String userId);
    List<Loan> getLoansByBook(String isbn);
    List<Loan> getActiveLoans();
    List<Loan> getOverdueLoans();
    void updateLoan(Loan loan);
    void deleteLoan(String loanId);
}
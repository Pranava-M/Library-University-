package com.library.ui.gui;

import com.library.services.LibraryService;
import com.library.services.impl.LibraryServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageLoansPanel extends JPanel {
    private final LibraryService libraryService;
    private JTable loansTable;
    
    public ManageLoansPanel() {
        this.libraryService = new LibraryServiceImpl();
        initializeUI();
        loadLoans();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Toolbar
        JToolBar toolBar = new JToolBar();
        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(e -> showCheckoutDialog());
        JButton returnButton = new JButton("Return");
        returnButton.addActionListener(e -> returnSelectedLoan());
        JButton renewButton = new JButton("Renew");
        renewButton.addActionListener(e -> renewSelectedLoan());
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadLoans());
        
        toolBar.add(checkoutButton);
        toolBar.add(returnButton);
        toolBar.add(renewButton);
        toolBar.addSeparator();
        toolBar.add(refreshButton);
        
        add(toolBar, BorderLayout.NORTH);
        
        // Table
        loansTable = new JTable(new DefaultTableModel(
            new Object[]{"Loan ID", "Book", "User", "Loan Date", "Due Date", "Status"}, 0));
        JScrollPane scrollPane = new JScrollPane(loansTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> filterCombo = new JComboBox<>(new String[]{"All", "Active", "Overdue", "Returned"});
        filterCombo.addActionListener(e -> filterLoans((String) filterCombo.getSelectedItem()));
        
        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(filterCombo);
        
        add(filterPanel, BorderLayout.SOUTH);
    }
    
    private void loadLoans() {
        DefaultTableModel model = (DefaultTableModel) loansTable.getModel();
        model.setRowCount(0);
        
        List<Loan> loans = libraryService.getAllLoans();
        for (Loan loan : loans) {
            model.addRow(new Object[]{
                loan.getLoanId(),
                loan.getBook().getTitle(),
                loan.getUser().getFullName(),
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getStatus()
            });
        }
    }
    
    private void filterLoans(String filter) {
        // Implementation would filter loans based on the selected filter
    }
    
    private void showCheckoutDialog() {
        // Implementation would show a dialog to checkout a book
    }
    
    private void returnSelectedLoan() {
        // Implementation would return the selected loan
    }
    
    private void renewSelectedLoan() {
        // Implementation would renew the selected loan
    }
}
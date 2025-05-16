package com.library.ui.gui;

import com.library.services.BookService;
import com.library.services.impl.BookServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageBooksPanel extends JPanel {
    private final BookService bookService;
    private JTable booksTable;
    
    public ManageBooksPanel() {
        this.bookService = new BookServiceImpl();
        initializeUI();
        loadBooks();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Toolbar
        JToolBar toolBar = new JToolBar();
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> showAddBookDialog());
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> showEditBookDialog());
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedBook());
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadBooks());
        
        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        toolBar.addSeparator();
        toolBar.add(refreshButton);
        
        add(toolBar, BorderLayout.NORTH);
        
        // Table
        booksTable = new JTable(new DefaultTableModel(
            new Object[]{"ISBN", "Title", "Authors", "Available"}, 0));
        JScrollPane scrollPane = new JScrollPane(booksTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchBooks(searchField.getText()));
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        add(searchPanel, BorderLayout.SOUTH);
    }
    
    private void loadBooks() {
        DefaultTableModel model = (DefaultTableModel) booksTable.getModel();
        model.setRowCount(0);
        
        List<Book> books = bookService.getAllBooks();
        for (Book book : books) {
            model.addRow(new Object[]{
                book.getIsbn(),
                book.getTitle(),
                getAuthorsString(book),
                book.getAvailableQuantity() + "/" + book.getQuantity()
            });
        }
    }
    
    private String getAuthorsString(Book book) {
        // Implementation would return a string of authors' names
        return "Various Authors";
    }
    
    private void searchBooks(String query) {
        // Implementation would search books based on the query
    }
    
    private void showAddBookDialog() {
        // Implementation would show a dialog to add a new book
    }
    
    private void showEditBookDialog() {
        // Implementation would show a dialog to edit the selected book
    }
    
    private void deleteSelectedBook() {
        // Implementation would delete the selected book
    }
}
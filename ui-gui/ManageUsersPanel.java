package com.library.ui.gui;

import com.library.services.UserService;
import com.library.services.impl.UserServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageUsersPanel extends JPanel {
    private final UserService userService;
    private JTable usersTable;
    
    public ManageUsersPanel() {
        this.userService = new UserServiceImpl();
        initializeUI();
        loadUsers();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Toolbar
        JToolBar toolBar = new JToolBar();
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> showAddUserDialog());
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(e -> showEditUserDialog());
        JButton toggleButton = new JButton("Toggle Status");
        toggleButton.addActionListener(e -> toggleUserStatus());
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadUsers());
        
        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(toggleButton);
        toolBar.addSeparator();
        toolBar.add(refreshButton);
        
        add(toolBar, BorderLayout.NORTH);
        
        // Table
        usersTable = new JTable(new DefaultTableModel(
            new Object[]{"User ID", "Name", "Type", "Status", "Fines"}, 0));
        JScrollPane scrollPane = new JScrollPane(usersTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchUsers(searchField.getText()));
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        add(searchPanel, BorderLayout.SOUTH);
    }
    
    private void loadUsers() {
        DefaultTableModel model = (DefaultTableModel) usersTable.getModel();
        model.setRowCount(0);
        
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            model.addRow(new Object[]{
                user.getUserId(),
                user.getFullName(),
                user.getUserType(),
                user.isActive() ? "Active" : "Inactive",
                String.format("$%.2f", user.getFines())
            });
        }
    }
    
    private void searchUsers(String query) {
        // Implementation would search users based on the query
    }
    
    private void showAddUserDialog() {
        // Implementation would show a dialog to add a new user
    }
    
    private void showEditUserDialog() {
        // Implementation would show a dialog to edit the selected user
    }
    
    private void toggleUserStatus() {
        // Implementation would toggle the active status of the selected user
    }
}
package com.library.ui.gui;

import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {
    public MainDashboard(String userId) {
        initializeUI(userId);
    }
    
    private void initializeUI(String userId) {
        setTitle("Library Management System - Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        JMenu manageMenu = new JMenu("Manage");
        JMenuItem booksItem = new JMenuItem("Books");
        booksItem.addActionListener(e -> showManageBooksPanel());
        JMenuItem usersItem = new JMenuItem("Users");
        usersItem.addActionListener(e -> showManageUsersPanel());
        JMenuItem loansItem = new JMenuItem("Loans");
        loansItem.addActionListener(e -> showManageLoansPanel());
        manageMenu.add(booksItem);
        manageMenu.add(usersItem);
        manageMenu.add(loansItem);
        
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(manageMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
        
        // Create status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Logged in as: " + userId));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        
        // Main content area
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Books", new ManageBooksPanel());
        tabbedPane.addTab("Users", new ManageUsersPanel());
        tabbedPane.addTab("Loans", new ManageLoansPanel());
        
        // Add components to frame
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void showManageBooksPanel() {
        // Implementation would show the books management panel
    }
    
    private void showManageUsersPanel() {
        // Implementation would show the users management panel
    }
    
    private void showManageLoansPanel() {
        // Implementation would show the loans management panel
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this, 
            "Library Management System\nVersion 1.0", 
            "About", JOptionPane.INFORMATION_MESSAGE);
    }
}
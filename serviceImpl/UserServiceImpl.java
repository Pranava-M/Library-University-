package com.library.services.impl;

import com.library.dao.UserDAO;
import com.library.entities.User;
import com.library.exceptions.UserNotFoundException;
import com.library.services.UserService;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;
    
    public UserServiceImpl() {
        this.userDAO = new UserDAOImpl();
    }

    @Override
    public void registerUser(User user) {
        if (userDAO.userExists(user.getUserId())) {
            throw new IllegalArgumentException("User ID already exists");
        }
        userDAO.addUser(user);
    }

    @Override
    public User authenticateUser(String userId, String password) {
        if (!userDAO.authenticateUser(userId, password)) {
            throw new SecurityException("Authentication failed");
        }
        return userDAO.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found after authentication"));
    }

    @Override
    public User getUserProfile(String userId) {
        return userDAO.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public void updateUserProfile(User user) {
        if (!userDAO.userExists(user.getUserId())) {
            throw new UserNotFoundException("Cannot update - user not found");
        }
        userDAO.updateUser(user);
    }

    @Override
    public void changeUserStatus(String userId, boolean active) {
        User user = getUserProfile(userId);
        user.setActive(active);
        userDAO.updateUser(user);
    }

    @Override
    public List<User> searchUsers(String query) {
        return userDAO.getUsersByName(query);
    }

    @Override
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    @Override
    public double getUserFines(String userId) {
        User user = getUserProfile(userId);
        return user.getFines();
    }

    @Override
    public void payUserFines(String userId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        
        User user = getUserProfile(userId);
        double currentFines = user.getFines();
        
        if (amount > currentFines) {
            throw new IllegalArgumentException("Payment amount exceeds outstanding fines");
        }
        
        user.payFine(amount);
        userDAO.updateUser(user);
    }
}
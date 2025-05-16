package com.library.services;

import com.library.entities.User;
import java.util.List;

public interface UserService {
    void registerUser(User user);
    User authenticateUser(String userId, String password);
    User getUserProfile(String userId);
    void updateUserProfile(User user);
    void changeUserStatus(String userId, boolean active);
    List<User> searchUsers(String query);
    List<User> getAllUsers();
    double getUserFines(String userId);
    void payUserFines(String userId, double amount);
}
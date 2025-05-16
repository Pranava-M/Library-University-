package com.library.dao;

import com.library.entities.User;
import java.util.List;
import java.util.Optional;

public interface UserDAO {
    void addUser(User user);
    Optional<User> getUserById(String userId);
    List<User> getAllUsers();
    List<User> getUsersByName(String name);
    List<User> getUsersByType(User.UserType type);
    void updateUser(User user);
    void deleteUser(String userId);
    boolean userExists(String userId);
    boolean authenticateUser(String userId, String password);
}
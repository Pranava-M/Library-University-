package com.library.dao.impl;

import com.library.dao.UserDAO;
import com.library.entities.User;
import com.library.utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {
    private final Connection connection;

    public UserDAOImpl() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public void addUser(User user) {
        String sql = "INSERT INTO users (user_id, first_name, last_name, email, phone_number, " +
                     "registration_date, date_of_birth, address, user_type, max_books_allowed, " +
                     "fines, is_active, password_hash) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUserId());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPhoneNumber());
            stmt.setDate(6, Date.valueOf(user.getRegistrationDate()));
            stmt.setDate(7, user.getDateOfBirth() != null ? Date.valueOf(user.getDateOfBirth()) : null);
            stmt.setString(8, user.getAddress());
            stmt.setString(9, user.getUserType().name());
            stmt.setInt(10, user.getMaxBooksAllowed());
            stmt.setDouble(11, user.getFines());
            stmt.setBoolean(12, user.isActive());
            stmt.setString(13, "hashed_password_placeholder"); // In real app, store properly hashed password
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add user", e);
        }
    }

    @Override
    public Optional<User> getUserById(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get user by ID", e);
        }
        
        return Optional.empty();
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User(
            rs.getString("user_id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            User.UserType.valueOf(rs.getString("user_type"))
        );
        
        user.setEmail(rs.getString("email"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setRegistrationDate(rs.getDate("registration_date").toLocalDate());
        user.setDateOfBirth(rs.getDate("date_of_birth") != null ? 
            rs.getDate("date_of_birth").toLocalDate() : null);
        user.setAddress(rs.getString("address"));
        user.setFines(rs.getDouble("fines"));
        user.setActive(rs.getBoolean("is_active"));
        
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id FROM users";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                getUserById(rs.getString("user_id")).ifPresent(users::add);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all users", e);
        }
        
        return users;
    }

    @Override
    public List<User> getUsersByName(String name) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id FROM users WHERE CONCAT(first_name, ' ', last_name) LIKE ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                getUserById(rs.getString("user_id")).ifPresent(users::add);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get users by name", e);
        }
        
        return users;
    }

    @Override
    public List<User> getUsersByType(User.UserType type) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id FROM users WHERE user_type = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, type.name());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                getUserById(rs.getString("user_id")).ifPresent(users::add);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get users by type", e);
        }
        
        return users;
    }

    @Override
    public void updateUser(User user) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, " +
                     "phone_number = ?, date_of_birth = ?, address = ?, user_type = ?, " +
                     "max_books_allowed = ?, fines = ?, is_active = ? " +
                     "WHERE user_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setDate(5, user.getDateOfBirth() != null ? Date.valueOf(user.getDateOfBirth()) : null);
            stmt.setString(6, user.getAddress());
            stmt.setString(7, user.getUserType().name());
            stmt.setInt(8, user.getMaxBooksAllowed());
            stmt.setDouble(9, user.getFines());
            stmt.setBoolean(10, user.isActive());
            stmt.setString(11, user.getUserId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    public void deleteUser(String userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    @Override
    public boolean userExists(String userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check if user exists", e);
        }
    }

    @Override
    public boolean authenticateUser(String userId, String password) {
        // In a real application, you would:
        // 1. Get the hashed password from the database for this user
        // 2. Compare it with the hashed version of the provided password
        // For this example, we'll just check if the user exists and password is not empty
        
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        
        return userExists(userId);
    }
}
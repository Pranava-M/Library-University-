package com.library.dao;

import com.library.entities.User;
import com.library.utils.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDAOTest {
    private UserDAO userDAO;
    private Connection connection;
    private final String testUserId = "TESTUSER123";
    
    @BeforeAll
    void setup() throws SQLException {
        connection = DatabaseConnection.getConnection();
        userDAO = new UserDAOImpl();
        
        // Clear any existing test data
        connection.prepareStatement("DELETE FROM users WHERE user_id = '" + testUserId + "'").executeUpdate();
    }
    
    @AfterAll
    void tearDown() throws SQLException {
        // Clean up
        connection.prepareStatement("DELETE FROM users WHERE user_id = '" + testUserId + "'").executeUpdate();
        DatabaseConnection.closeConnection();
    }
    
    @Test
    void testAddAndGetUser() {
        User user = new User(testUserId, "Test", "User", User.UserType.STUDENT);
        user.setRegistrationDate(LocalDate.now());
        
        userDAO.addUser(user);
        
        Optional<User> retrieved = userDAO.getUserById(testUserId);
        assertTrue(retrieved.isPresent(), "User should be present after adding");
        assertEquals("Test User", retrieved.get().getFullName(), "Name should match");
        assertEquals(User.UserType.STUDENT, retrieved.get().getUserType(), "User type should match");
    }
    
    @Test
    void testUpdateUser() {
        User user = new User(testUserId, "Test", "User", User.UserType.STUDENT);
        userDAO.addUser(user);
        
        user.setLastName("Updated");
        userDAO.updateUser(user);
        
        Optional<User> updated = userDAO.getUserById(testUserId);
        assertTrue(updated.isPresent(), "User should be present");
        assertEquals("Test Updated", updated.get().getFullName(), "Name should be updated");
    }
    
    @Test
    void testDeleteUser() {
        User user = new User(testUserId, "Test", "User", User.UserType.STUDENT);
        userDAO.addUser(user);
        
        userDAO.deleteUser(testUserId);
        
        Optional<User> deleted = userDAO.getUserById(testUserId);
        assertFalse(deleted.isPresent(), "User should not be present after deletion");
    }
    
    @Test
    void testSearchUsers() {
        User user = new User(testUserId, "Search", "Test", User.UserType.STUDENT);
        userDAO.addUser(user);
        
        List<User> results = userDAO.getUsersByName("Search");
        assertFalse(results.isEmpty(), "Search should return results");
        assertTrue(results.stream().anyMatch(u -> u.getUserId().equals(testUserId)), 
            "Search results should include the test user");
    }
    
    @Test
    void testUserExists() {
        User user = new User(testUserId, "Exists", "Test", User.UserType.STUDENT);
        userDAO.addUser(user);
        
        assertTrue(userDAO.userExists(testUserId), "User should exist");
        assertFalse(userDAO.userExists("NONEXISTENT"), "Non-existent user should not exist");
    }
}	
package com.library.services;

import com.library.entities.User;
import com.library.exceptions.UserNotFoundException;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {
    private UserService userService;
    private final String testUserId = "TESTSVC123";
    
    @BeforeAll
    void setup() {
        userService = new UserServiceImpl();
        
        // Clean up any existing test data
        try {
            userService.changeUserStatus(testUserId, false);
        } catch (UserNotFoundException e) {
            // Ignore if not exists
        }
    }
    
    @AfterAll
    void tearDown() {
        // Clean up
        try {
            userService.changeUserStatus(testUserId, false);
        } catch (UserNotFoundException e) {
            // Ignore if not exists
        }
    }
    
    @Test
    void testRegisterAndGetUser() throws UserNotFoundException {
        User user = new User(testUserId, "Service", "Test", User.UserType.STUDENT);
        
        userService.registerUser(user);
        
        User retrieved = userService.getUserProfile(testUserId);
        assertNotNull(retrieved, "User should be retrievable after registration");
        assertEquals("Service Test", retrieved.getFullName(), "Name should match");
    }
    
    @Test
    void testUpdateUser() throws UserNotFoundException {
        User user = new User(testUserId, "Service", "Test", User.UserType.STUDENT);
        userService.registerUser(user);
        
        user.setLastName("Updated");
        userService.updateUserProfile(user);
        
        User updated = userService.getUserProfile(testUserId);
        assertEquals("Service Updated", updated.getFullName(), "Name should be updated");
    }
    
    @Test
    void testChangeUserStatus() throws UserNotFoundException {
        User user = new User(testUserId, "Status", "Test", User.UserType.STUDENT);
        userService.registerUser(user);
        
        userService.changeUserStatus(testUserId, false);
        User deactivated = userService.getUserProfile(testUserId);
        assertFalse(deactivated.isActive(), "User should be deactivated");
        
        userService.changeUserStatus(testUserId, true);
        User reactivated = userService.getUserProfile(testUserId);
        assertTrue(reactivated.isActive(), "User should be reactivated");
    }
    
    @Test
    void testSearchUsers() {
        User user = new User(testUserId, "Search", "Test", User.UserType.STUDENT);
        userService.registerUser(user);
        
        List<User> results = userService.searchUsers("Search");
        assertFalse(results.isEmpty(), "Search should return results");
        assertTrue(results.stream().anyMatch(u -> u.getUserId().equals(testUserId)), 
            "Search results should include the test user");
    }
    
    @Test
    void testUserFines() throws UserNotFoundException {
        User user = new User(testUserId, "Fines", "Test", User.UserType.STUDENT);
        userService.registerUser(user);
        
        // Add some fines
        userService.getUserProfile(testUserId).addFine(10.50);
        
        assertEquals(10.50, userService.getUserFines(testUserId), "Fines should match");
        
        // Pay some fines
        userService.payUserFines(testUserId, 5.25);
        assertEquals(5.25, userService.getUserFines(testUserId), "Fines should be reduced after payment");
    }
}
package com.emmanuelarhu.utils;

import com.emmanuelarhu.models.User;
import org.testng.annotations.DataProvider;

/**
 * Data provider class for test data
 */
public class TestDataProvider {

    @DataProvider(name = "validUserData")
    public Object[][] validUserData() {
        return new Object[][] {
                {new User(null, "testuser1", "test1@example.com", "password123")},
                {new User(null, "testuser2", "test2@example.com", "securePass456")},
                {new User(null, "john_doe", "john@test.com", "myPassword789")}
        };
    }

    @DataProvider(name = "invalidUserData")
    public Object[][] invalidUserData() {
        return new Object[][] {
                // Missing username
                {new User(null, "", "test@example.com", "password123"), "username"},
                // Missing email
                {new User(null, "testuser", "", "password123"), "email"},
                // Missing password
                {new User(null, "testuser", "test@example.com", ""), "password"},
                // Invalid email format
                {new User(null, "testuser", "invalid-email", "password123"), "email"},
                // Null values
                {new User(null, null, "test@example.com", "password123"), "username"},
                {new User(null, "testuser", null, "password123"), "email"},
                {new User(null, "testuser", "test@example.com", null), "password"}
        };
    }

    @DataProvider(name = "invalidUserIds")
    public Object[][] invalidUserIds() {
        return new Object[][] {
                {-1},
                {0},
                {9999},
                {Integer.MAX_VALUE}
        };
    }

    @DataProvider(name = "validUserIds")
    public Object[][] validUserIds() {
        return new Object[][] {
                {1},
                {2},
                {3},
                {4},
                {5}
        };
    }

    @DataProvider(name = "updateUserData")
    public Object[][] updateUserData() {
        return new Object[][] {
                {1, new User(1, "updated_user1", "updated1@example.com", "newPassword123")},
                {2, new User(2, "updated_user2", "updated2@example.com", "newPassword456")},
                {3, new User(3, "updated_user3", "updated3@example.com", "newPassword789")}
        };
    }
}
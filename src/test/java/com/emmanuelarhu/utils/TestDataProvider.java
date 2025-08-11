package com.emmanuelarhu.utils;

import com.emmanuelarhu.models.User;
import org.testng.annotations.DataProvider;

/**
 * Enhanced data provider class for comprehensive test data
 */
public class TestDataProvider {

    @DataProvider(name = "validUserData")
    public Object[][] validUserData() {
        return new Object[][] {
                {new User(null, "testuser1", "test1@example.com", "password123")},
                {new User(null, "testuser2", "test2@example.com", "securePass456")},
                {new User(null, "john_doe", "john@test.com", "myPassword789")},
                {new User(null, "jane_smith", "jane.smith@example.com", "strongPass2024")},
                {new User(null, "api_tester", "api.tester@qamail.com", "testPassword@123")}
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
                {10000},
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

    // NEW: Additional test data for edge cases
    @DataProvider(name = "specialCharacterUserData")
    public Object[][] specialCharacterUserData() {
        return new Object[][] {
                {new User(null, "user@123", "user123@test.com", "pass@123")},
                {new User(null, "user.name", "user.name@test.com", "password")},
                {new User(null, "user_name", "user_name@test.com", "password")}
        };
    }

    // NEW: Email validation test data
    @DataProvider(name = "invalidEmailFormats")
    public Object[][] invalidEmailFormats() {
        return new Object[][] {
                {"plainaddress"},
                {"@missingdomain.com"},
                {"missing@.com"},
                {"missing.domain@.com"},
                {"user@"},
                {"user@domain"},
                {"user space@domain.com"},
                {"user@domain@domain.com"}
        };
    }

    // NEW: SQL Injection test data
    @DataProvider(name = "sqlInjectionTestData")
    public Object[][] sqlInjectionTestData() {
        return new Object[][] {
                {"1' OR '1'='1"},
                {"1; DROP TABLE users;"},
                {"1' UNION SELECT * FROM users--"},
                {"1' OR 1=1--"},
                {"admin'--"}
        };
    }

    // NEW: XSS test data
    @DataProvider(name = "xssTestData")
    public Object[][] xssTestData() {
        return new Object[][] {
                {"<script>alert('xss')</script>"},
                {"<img src=x onerror=alert('xss')>"},
                {"javascript:alert('xss')"},
                {"<svg onload=alert('xss')>"},
                {"<iframe src=javascript:alert('xss')>"}
        };
    }

    // NEW: Long string test data
    @DataProvider(name = "longStringTestData")
    public Object[][] longStringTestData() {
        String longString = "a".repeat(1000); // 1000 character string
        return new Object[][] {
                {longString},
                {"user" + longString},
                {longString + "@example.com"}
        };
    }

    // NEW: Boundary value test data
    @DataProvider(name = "boundaryUserIds")
    public Object[][] boundaryUserIds() {
        return new Object[][] {
                {1},      // Minimum valid ID
                {10},     // Upper boundary of expected range
                {0},      // Lower boundary invalid
                {11},     // Upper boundary invalid
                {-1}      // Negative boundary
        };
    }

    // NEW: Concurrent test data
    @DataProvider(name = "concurrentTestData")
    public Object[][] concurrentTestData() {
        return new Object[][] {
                {1, "Thread-1"},
                {2, "Thread-2"},
                {3, "Thread-3"},
                {4, "Thread-4"},
                {5, "Thread-5"}
        };
    }

    // NEW: Partial update test data
    @DataProvider(name = "partialUpdateData")
    public Object[][] partialUpdateData() {
        return new Object[][] {
                {1, "{\"username\": \"updated_username\"}"},
                {2, "{\"email\": \"updated@example.com\"}"},
                {3, "{\"password\": \"newPassword123\"}"},
                {4, "{\"username\": \"new_user\", \"email\": \"new@example.com\"}"}
        };
    }

    // NEW: Malformed JSON test data
    @DataProvider(name = "malformedJsonData")
    public Object[][] malformedJsonData() {
        return new Object[][] {
                {"{username: 'test', email: 'invalid-json'"},
                {"{\"username\": \"test\", \"email\": }"},
                {"{\"username\": \"test\", \"email\": \"test@test.com\""},
                {"username: 'test', email: 'test@test.com'"},
                {"{\"username\": \"test\", \"email\": \"test@test.com\", }"}
        };
    }
}
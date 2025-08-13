package com.emmanuelarhu.utils;

import com.emmanuelarhu.models.LoginRequest;
import org.testng.annotations.DataProvider;

/**
 * Data provider class for Authentication API test data
 */
public class AuthTestDataProvider {

    @DataProvider(name = "validLoginCredentials")
    public Object[][] validLoginCredentials() {
        return new Object[][] {
                // Based on FakeStore API documentation examples
                {new LoginRequest("mor_2314", "83r5^_")},
                {new LoginRequest("kevinryan", "kev02937@")},
                {new LoginRequest("donero", "ewedon")},
                {new LoginRequest("derek", "jklg*_56")},
                {new LoginRequest("david_r", "3478*#54")}
        };
    }

    @DataProvider(name = "invalidLoginCredentials")
    public Object[][] invalidLoginCredentials() {
        return new Object[][] {
                // Wrong username
                {new LoginRequest("wrong_user", "83r5^_"), "username"},
                // Wrong password
                {new LoginRequest("mor_2314", "wrong_password"), "password"},
                // Both wrong
                {new LoginRequest("wrong_user", "wrong_password"), "both"},
                // Empty username
                {new LoginRequest("", "83r5^_"), "username"},
                // Empty password
                {new LoginRequest("mor_2314", ""), "password"},
                // Both empty
                {new LoginRequest("", ""), "both"},
                // Null username
                {new LoginRequest(null, "83r5^_"), "username"},
                // Null password
                {new LoginRequest("mor_2314", null), "password"},
                // Case sensitive username
                {new LoginRequest("MOR_2314", "83r5^_"), "username"},
                // Spaces in username
                {new LoginRequest("mor 2314", "83r5^_"), "username"},
                // Spaces in password
                {new LoginRequest("mor_2314", "83r5^ _"), "password"}
        };
    }

    @DataProvider(name = "malformedJsonData")
    public Object[][] malformedJsonData() {
        return new Object[][] {
                {"{username: 'mor_2314', password: '83r5^_'}"},
                {"{\"username\": \"mor_2314\", \"password\": }"},
                {"{\"username\": \"mor_2314\", \"password\": \"83r5^_\""},
                {"username: 'mor_2314', password: '83r5^_'"},
                {"{\"username\": \"mor_2314\", \"password\": \"83r5^_\", }"},
                {"{\"username\": \"mor_2314\" \"password\": \"83r5^_\"}"},
                {"{'username': 'mor_2314', 'password': '83r5^_'}"}
        };
    }

    @DataProvider(name = "sqlInjectionCredentials")
    public Object[][] sqlInjectionCredentials() {
        return new Object[][] {
                {"' OR '1'='1"},
                {"admin'--"},
                {"' OR 1=1--"},
                {"'; DROP TABLE users; --"},
                {"' UNION SELECT * FROM users--"},
                {"admin'; DELETE FROM users; --"},
                {"' OR 'x'='x"},
                {"1' OR '1'='1' /*"},
                {"admin'/*"},
                {"' OR 1=1#"}
        };
    }

    @DataProvider(name = "xssPayloads")
    public Object[][] xssPayloads() {
        return new Object[][] {
                {"<script>alert('xss')</script>"},
                {"<img src=x onerror=alert('xss')>"},
                {"javascript:alert('xss')"},
                {"<svg onload=alert('xss')>"},
                {"<iframe src=javascript:alert('xss')>"},
                {"<body onload=alert('xss')>"},
                {"';alert('xss');//"},
                {"<script>document.cookie</script>"},
                {"<img src=x onerror=document.location='http://evil.com'>"}
        };
    }

    @DataProvider(name = "specialCharacterCredentials")
    public Object[][] specialCharacterCredentials() {
        return new Object[][] {
                // Special characters in username
                {"user@domain.com", "password123"},
                {"user.name", "password123"},
                {"user_name", "password123"},
                {"user-name", "password123"},
                {"user+name", "password123"},
                {"user name", "password123"},
                {"user#name", "password123"},
                {"user$name", "password123"},
                {"user%name", "password123"},
                {"user&name", "password123"},
                {"user*name", "password123"},

                // Special characters in password
                {"testuser", "pass@word"},
                {"testuser", "pass#word"},
                {"testuser", "pass$word"},
                {"testuser", "pass%word"},
                {"testuser", "pass&word"},
                {"testuser", "pass*word"},
                {"testuser", "pass+word"},
                {"testuser", "pass=word"},
                {"testuser", "pass word"},
                {"testuser", "pass\tword"},
                {"testuser", "pass\nword"}
        };
    }

    @DataProvider(name = "boundaryCredentials")
    public Object[][] boundaryCredentials() {
        return new Object[][] {
                // Very short credentials
                {"a", "b"},
                {"ab", "cd"},
                {"abc", "def"},

                // Long but reasonable credentials
                {"a".repeat(50), "b".repeat(50)},
                {"a".repeat(100), "b".repeat(100)},

                // Maximum reasonable length
                {"a".repeat(255), "b".repeat(255)}
        };
    }

    @DataProvider(name = "encodingTestCredentials")
    public Object[][] encodingTestCredentials() {
        return new Object[][] {
                // Unicode characters
                {"usér", "passwörd"},
                {"用户", "密码"},
                {"пользователь", "пароль"},
                {"مستخدم", "كلمة مرور"},
                {"ユーザー", "パスワード"},

                // Emoji
                {"user😀", "pass🔒"},
                {"👤user", "🔑password"},

                // URL encoded characters
                {"user%20name", "pass%40word"},
                {"user%2Bname", "pass%26word"}
        };
    }

    @DataProvider(name = "caseVariations")
    public Object[][] caseVariations() {
        return new Object[][] {
                // Test case sensitivity
                {"MOR_2314", "83r5^_"},
                {"mor_2314", "83R5^_"},
                {"MOR_2314", "83R5^_"},
                {"Mor_2314", "83r5^_"},
                {"mor_2314", "83r5^_"}, // Original (should work)

                // Mixed case
                {"KeViNrYaN", "kev02937@"},
                {"kevinryan", "KEV02937@"},
                {"KEVINRYAN", "kev02937@"}
        };
    }

    @DataProvider(name = "numericCredentials")
    public Object[][] numericCredentials() {
        return new Object[][] {
                // Purely numeric
                {"12345", "67890"},
                {"123", "456"},
                {"1", "1"},

                // Mix of numbers and characters
                {"user123", "pass456"},
                {"123user", "456pass"},
                {"user1", "pass1"}
        };
    }

    @DataProvider(name = "whitespaceVariations")
    public Object[][] whitespaceVariations() {
        return new Object[][] {
                // Leading/trailing spaces
                {" mor_2314", "83r5^_"},
                {"mor_2314 ", "83r5^_"},
                {" mor_2314 ", "83r5^_"},
                {"mor_2314", " 83r5^_"},
                {"mor_2314", "83r5^_ "},
                {"mor_2314", " 83r5^_ "},

                // Tabs and newlines
                {"\tmor_2314", "83r5^_"},
                {"mor_2314\n", "83r5^_"},
                {"mor_2314", "\t83r5^_"},
                {"mor_2314", "83r5^_\n"}
        };
    }
}
package com.emmanuelarhu.tests;

import com.emmanuelarhu.base.BaseTest;
import com.emmanuelarhu.models.LoginRequest;
import com.emmanuelarhu.utils.AuthTestDataProvider;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Comprehensive test suite for FakeStore API Authentication endpoints
 * Tests cover authentication with positive, negative, and security cases
 */
@Epic("FakeStore API Testing")
@Feature("Authentication")
@Listeners({io.qameta.allure.testng.AllureTestNg.class})
public class AuthApiTest extends BaseTest {

    protected static final String AUTH_ENDPOINT = "/auth/login";

    // POST /auth/login tests with valid credentials
    @Test(priority = 1, groups = {"smoke", "auth"}, dataProvider = "validLoginCredentials", dataProviderClass = AuthTestDataProvider.class)
    @Story("User Authentication")
    @Description("Verify that users can login with valid credentials")
    @Severity(SeverityLevel.BLOCKER)
    public void testLoginWithValidCredentials(LoginRequest loginRequest) {
        logTestStep("Testing POST /auth/login with valid credentials: " + loginRequest.getUsername());

        try {
            Response response = given()
                    .spec(getRequestSpec())
                    .body(loginRequest)
                    .when()
                    .post(AUTH_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(200), equalTo(201)))
                    .log().all()
                    .extract().response();

            // Only verify token if login was successful
            if (response.getStatusCode() == 201 || response.getStatusCode() == 200) {
                System.out.println("✅ Login successful, status code: " + response.getStatusCode());
                try {
                    String token = response.jsonPath().getString("token");
                    assertNotNull(token, "Login response should contain a token");
                    assertFalse(token.isEmpty(), "Token should not be empty");
                    System.out.println("✅ Login successful, token received");
                } catch (Exception e) {
                    System.out.println("⚠️ Could not extract token from response: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            handleApiException(e, "POST /auth/login with valid credentials");
        }
    }

    @Test(priority = 2, groups = {"negative", "auth"}, dataProvider = "invalidLoginCredentials", dataProviderClass = AuthTestDataProvider.class)
    @Story("User Authentication")
    @Description("Verify appropriate error handling for invalid login credentials")
    @Severity(SeverityLevel.CRITICAL)
    public void testLoginWithInvalidCredentials(LoginRequest loginRequest, String invalidField) {
        logTestStep("Testing POST /auth/login with invalid " + invalidField);

        try {
            given()
                    .spec(getRequestSpec())
                    .body(loginRequest)
                    .when()
                    .post(AUTH_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(401), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "POST /auth/login with invalid " + invalidField);
        }
    }

    @Test(priority = 3, groups = {"negative", "auth"})
    @Story("User Authentication")
    @Description("Verify error handling for empty request body")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginWithEmptyBody() {
        logTestStep("Testing POST /auth/login with empty body");

        try {
            given()
                    .spec(getRequestSpec())
                    .body("{}")
                    .when()
                    .post(AUTH_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(401), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "POST /auth/login with empty body");
        }
    }

    @Test(priority = 4, groups = {"negative", "auth"})
    @Story("User Authentication")
    @Description("Verify error handling for missing username")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginWithMissingUsername() {
        logTestStep("Testing POST /auth/login with missing username");

        try {
            String requestBody = "{\"password\": \"m38rmF$\"}";

            given()
                    .spec(getRequestSpec())
                    .body(requestBody)
                    .when()
                    .post(AUTH_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(401), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "POST /auth/login with missing username");
        }
    }

    @Test(priority = 5, groups = {"negative", "auth"})
    @Story("User Authentication")
    @Description("Verify error handling for missing password")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginWithMissingPassword() {
        logTestStep("Testing POST /auth/login with missing password");

        try {
            String requestBody = "{\"username\": \"mor_2314\"}";

            given()
                    .spec(getRequestSpec())
                    .body(requestBody)
                    .when()
                    .post(AUTH_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(401), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "POST /auth/login with missing password");
        }
    }

    @Test(priority = 6, groups = {"negative", "auth"}, dataProvider = "malformedJsonData", dataProviderClass = AuthTestDataProvider.class)
    @Story("User Authentication")
    @Description("Verify error handling for malformed JSON in login request")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginWithMalformedJson(String malformedJson) {
        logTestStep("Testing POST /auth/login with malformed JSON");

        try {
            given()
                    .spec(getRequestSpec())
                    .body(malformedJson)
                    .when()
                    .post(AUTH_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(401), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "POST /auth/login with malformed JSON");
        }
    }

    // Security tests
    @Test(priority = 7, groups = {"security", "auth"}, dataProvider = "sqlInjectionCredentials", dataProviderClass = AuthTestDataProvider.class)
    @Story("Security Tests")
    @Description("Verify SQL injection protection in login credentials")
    @Severity(SeverityLevel.CRITICAL)
    public void testSqlInjectionInLogin(String maliciousInput) {
        logTestStep("Testing SQL injection protection in login with: " + maliciousInput);

        try {
            LoginRequest loginRequest = new LoginRequest(maliciousInput, maliciousInput);

            given()
                    .spec(getRequestSpec())
                    .body(loginRequest)
                    .when()
                    .post(AUTH_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(401), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "SQL injection test in login");
        }
    }

    @Test(priority = 8, groups = {"security", "auth"}, dataProvider = "xssPayloads", dataProviderClass = AuthTestDataProvider.class)
    @Story("Security Tests")
    @Description("Verify XSS protection in login credentials")
    @Severity(SeverityLevel.NORMAL)
    public void testXssProtectionInLogin(String xssPayload) {
        logTestStep("Testing XSS protection in login with payload");

        try {
            LoginRequest loginRequest = new LoginRequest(xssPayload, "password123");

            given()
                    .spec(getRequestSpec())
                    .body(loginRequest)
                    .when()
                    .post(AUTH_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(401), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "XSS protection test in login");
        }
    }

    // Edge cases and negative scenarios
    @Test(priority = 9, groups = {"negative", "auth"})
    @Story("Edge Cases")
    @Description("Test login with extremely long username")
    @Severity(SeverityLevel.MINOR)
    public void testLoginWithLongUsername() {
        logTestStep("Testing POST /auth/login with extremely long username");

        try {
            String longUsername = "a".repeat(10000); // 10,000 character username
            LoginRequest loginRequest = new LoginRequest(longUsername, "password123");

            given()
                    .spec(getRequestSpec())
                    .body(loginRequest)
                    .when()
                    .post(AUTH_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(401), equalTo(413), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "POST /auth/login with long username");
        }
    }

    @Test(priority = 10, groups = {"negative", "auth"})
    @Story("Edge Cases")
    @Description("Test login with extremely long password")
    @Severity(SeverityLevel.MINOR)
    public void testLoginWithLongPassword() {
        logTestStep("Testing POST /auth/login with extremely long password");

        try {
            String longPassword = "a".repeat(10000); // 10,000 character password
            LoginRequest loginRequest = new LoginRequest("testuser", longPassword);

            given()
                    .spec(getRequestSpec())
                    .body(loginRequest)
                    .when()
                    .post(AUTH_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(401), equalTo(413), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "POST /auth/login with long password");
        }
    }

    @Test(priority = 11, groups = {"negative", "auth"}, dataProvider = "specialCharacterCredentials", dataProviderClass = AuthTestDataProvider.class)
    @Story("Edge Cases")
    @Description("Test login with special characters in credentials")
    @Severity(SeverityLevel.MINOR)
    public void testLoginWithSpecialCharacters(String username, String password) {
        logTestStep("Testing POST /auth/login with special characters");

        try {
            LoginRequest loginRequest = new LoginRequest(username, password);

            given()
                    .spec(getRequestSpec())
                    .body(loginRequest)
                    .when()
                    .post(AUTH_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(401), equalTo(403), equalTo(200)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "POST /auth/login with special characters");
        }
    }

    @Test(priority = 12, groups = {"negative", "auth"})
    @Story("Edge Cases")
    @Description("Test login with null values")
    @Severity(SeverityLevel.MINOR)
    public void testLoginWithNullValues() {
        logTestStep("Testing POST /auth/login with null values");

        try {
            LoginRequest loginRequest = new LoginRequest(null, null);

            given()
                    .spec(getRequestSpec())
                    .body(loginRequest)
                    .when()
                    .post(AUTH_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(401), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "POST /auth/login with null values");
        }
    }

    @Test(priority = 13, groups = {"negative", "auth"})
    @Story("Edge Cases")
    @Description("Test login response time")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginResponseTime() {
        logTestStep("Testing POST /auth/login response time");

        try {
            LoginRequest loginRequest = new LoginRequest("mor_2314", "83r5^_");

            given()
                    .spec(getRequestSpec())
                    .body(loginRequest)
                    .when()
                    .post(AUTH_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(200), equalTo(201), equalTo(401), equalTo(403)))
                    .time(lessThan(30000L)) // Should respond within 30 seconds
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "POST /auth/login response time test");
        }
    }
}
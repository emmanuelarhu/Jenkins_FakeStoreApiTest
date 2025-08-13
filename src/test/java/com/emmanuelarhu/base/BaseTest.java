package com.emmanuelarhu.base;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 * Base test class containing common configurations and utilities
 * for all API test classes
 */
public class BaseTest {

    protected static final String BASE_URL = "https://fakestoreapi.com";
    protected static final String USERS_ENDPOINT = "/users";
    protected static final String PRODUCTS_ENDPOINT = "/products";
    protected static final String CARTS_ENDPOINT = "/carts";
    protected static final String AUTH_ENDPOINT = "/auth/login";

    // Common timeouts
    protected static final int CONNECTION_TIMEOUT = 30000; // 30 seconds
    protected static final int SOCKET_TIMEOUT = 30000; // 30 seconds

    @BeforeClass
    public void setUp() {
        // Configure RestAssured
        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Configure timeouts and connection settings
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", CONNECTION_TIMEOUT)
                        .setParam("http.socket.timeout", SOCKET_TIMEOUT));

        System.out.println("🔧 BaseTest setup completed - Base URL: " + BASE_URL);
    }

    @BeforeMethod
    public void beforeMethod() {
        System.out.println("🚀 Starting test execution...");
    }

    /**
     * Get a configured request specification with Allure reporting for POST/PUT requests
     * @return RequestSpecification with common configurations
     */
    protected RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("User-Agent", "FakeStore-API-Test-Suite/1.0")
                .relaxedHTTPSValidation(); // Handle SSL issues if any
    }

    /**
     * Get a configured request specification for GET requests
     * @return RequestSpecification for GET requests
     */
    protected RequestSpecification getRequestSpecForGet() {
        return RestAssured.given()
                .filter(new AllureRestAssured())
                .header("Accept", "application/json")
                .header("User-Agent", "FakeStore-API-Test-Suite/1.0")
                .relaxedHTTPSValidation(); // Handle SSL issues if any
    }

    /**
     * Get a configured request specification for DELETE requests
     * @return RequestSpecification for DELETE requests
     */
    protected RequestSpecification getRequestSpecForDelete() {
        return RestAssured.given()
                .filter(new AllureRestAssured())
                .header("Accept", "application/json")
                .header("User-Agent", "FakeStore-API-Test-Suite/1.0")
                .relaxedHTTPSValidation(); // Handle SSL issues if any
    }

    /**
     * Handle common API exceptions and provide meaningful error messages
     * @param e Exception to handle
     * @param operation The operation being performed
     * @return Formatted error message
     */
    protected String handleApiException(Exception e, String operation) {
        String errorMessage = String.format("API operation '%s' failed: %s", operation, e.getMessage());
        System.err.println("❌ " + errorMessage);
        return errorMessage;
    }

    /**
     * Log test step for better debugging
     * @param stepDescription Description of the test step
     */
    protected void logTestStep(String stepDescription) {
        System.out.println("📋 " + stepDescription);
    }
}
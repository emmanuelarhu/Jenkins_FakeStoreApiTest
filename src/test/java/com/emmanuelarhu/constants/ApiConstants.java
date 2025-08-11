package com.emmanuelarhu.constants;

/**
 * API Constants for FakeStore API testing
 */
public class ApiConstants {

    // Base URL and Endpoints
    public static final String BASE_URL = "https://fakestoreapi.com";
    public static final String USERS_ENDPOINT = "/users";

    // HTTP Status Codes
    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int NO_CONTENT = 204;
    public static final int BAD_REQUEST = 400;
    public static final int NOT_FOUND = 404;
    public static final int UNPROCESSABLE_ENTITY = 422;

    // Content Types
    public static final String JSON_CONTENT_TYPE = "application/json";
    public static final String JSON_CONTENT_TYPE_UTF8 = "application/json; charset=utf-8";

    // Timeouts (in milliseconds)
    public static final long MAX_RESPONSE_TIME = 5000L;
    public static final long MAX_RESPONSE_TIME_EXTENDED = 10000L;

    // Test Data Limits
    public static final int MAX_USERNAME_LENGTH = 50;
    public static final int MAX_EMAIL_LENGTH = 100;
    public static final int MAX_PASSWORD_LENGTH = 100;

    // Email validation regex
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";

    // Common test messages
    public static final String USER_CREATED_SUCCESS = "User created successfully";
    public static final String USER_UPDATED_SUCCESS = "User updated successfully";
    public static final String USER_DELETED_SUCCESS = "User deleted successfully";

    // Error messages
    public static final String INVALID_USER_ID = "Invalid user ID provided";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String INVALID_REQUEST_BODY = "Invalid request body";
}
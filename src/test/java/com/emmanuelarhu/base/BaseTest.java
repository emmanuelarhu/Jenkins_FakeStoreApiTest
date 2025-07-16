package com.emmanuelarhu.base;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;

/**
 * Base test class containing common configurations and utilities
 * for all API test classes
 */
public class BaseTest {

    protected static final String BASE_URL = "https://fakestoreapi.com";
    protected static final String USERS_ENDPOINT = "/users";

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    /**
     * Get a configured request specification with Allure reporting
     * @return RequestSpecification with common configurations
     */
    protected RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .accept("application/json");
    }

    /**
     * Get a configured request specification without content type
     * @return RequestSpecification for GET requests
     */
    protected RequestSpecification getRequestSpecForGet() {
        return RestAssured.given()
                .filter(new AllureRestAssured())
                .accept("application/json");
    }
}
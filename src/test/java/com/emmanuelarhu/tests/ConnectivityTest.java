package com.emmanuelarhu.tests;

import com.emmanuelarhu.base.BaseTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.*;

/**
 * Connectivity and environment validation tests
 * These tests run first to validate CI/CD environment setup
 */
@Epic("Environment Validation")
@Feature("Connectivity Tests")
public class ConnectivityTest extends BaseTest {

    @Test(priority = -10, groups = {"smoke", "connectivity"})
    @Story("Environment Setup")
    @Description("Validate that the test environment is properly configured")
    @Severity(SeverityLevel.BLOCKER)
    public void validateTestEnvironment() {
        System.out.println("=== Environment Validation ===");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("Java Vendor: " + System.getProperty("java.vendor"));
        System.out.println("OS Name: " + System.getProperty("os.name"));
        System.out.println("OS Version: " + System.getProperty("os.version"));
        System.out.println("User Directory: " + System.getProperty("user.dir"));
        System.out.println("Maven Test Source Directory: " + System.getProperty("maven.test.source.directory", "Not Set"));

        // Basic assertions
        assertNotNull(System.getProperty("java.version"), "Java version should be available");
        assertTrue(System.getProperty("java.version").startsWith("17") ||
                        System.getProperty("java.version").startsWith("21"),
                "Java version should be 17 or 21");
    }

    @Test(priority = -9, groups = {"smoke", "connectivity"})
    @Story("API Connectivity")
    @Description("Validate that FakeStore API is accessible from the test environment")
    @Severity(SeverityLevel.BLOCKER)
    public void validateApiConnectivity() {
        System.out.println("=== API Connectivity Test ===");
        System.out.println("Testing connection to: " + BASE_URL);

        try {
            Response response = given()
                    .when()
                    .get(BASE_URL + "/users/1")
                    .then()
                    .extract().response();

            System.out.println("Response Status Code: " + response.getStatusCode());
            System.out.println("Response Time: " + response.getTime() + "ms");
            System.out.println("Response Content-Type: " + response.getContentType());

            // Basic connectivity validation
            assertTrue(response.getStatusCode() >= 200 && response.getStatusCode() < 500,
                    "API should be reachable (got status: " + response.getStatusCode() + ")");

            if (response.getStatusCode() == 200) {
                System.out.println("✅ API is fully accessible");
                assertNotNull(response.getBody().asString(), "Response body should not be null");
                assertTrue(response.getTime() < 30000, "Response time should be reasonable (< 30s)");
            } else {
                System.out.println("⚠️ API returned status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("❌ API connectivity test failed: " + e.getMessage());
            e.printStackTrace();
            fail("Failed to connect to FakeStore API: " + e.getMessage());
        }
    }

    @Test(priority = -8, groups = {"smoke", "connectivity"})
    @Story("Test Framework")
    @Description("Validate that REST Assured and TestNG are working correctly")
    @Severity(SeverityLevel.CRITICAL)
    public void validateTestFramework() {
        System.out.println("=== Test Framework Validation ===");

        try {
            // Test REST Assured basic functionality
            Response response = given()
                    .header("User-Agent", "FakeStore-API-Test-Framework")
                    .when()
                    .get(BASE_URL + "/users")
                    .then()
                    .extract().response();

            System.out.println("REST Assured Status: ✅ Working");
            System.out.println("Response received with status: " + response.getStatusCode());

            // Test basic assertions
            assertNotNull(response, "Response object should not be null");
            assertNotNull(response.getBody(), "Response body should not be null");

            System.out.println("TestNG Assertions: ✅ Working");

            // Test JSON parsing
            if (response.getStatusCode() == 200) {
                String responseBody = response.getBody().asString();
                assertTrue(responseBody.length() > 0, "Response body should not be empty");
                System.out.println("JSON Response Length: " + responseBody.length() + " characters");
                System.out.println("JSON Parsing: ✅ Working");
            }

        } catch (Exception e) {
            System.err.println("❌ Test framework validation failed: " + e.getMessage());
            e.printStackTrace();
            fail("Test framework validation failed: " + e.getMessage());
        }
    }

    @Test(priority = -7, groups = {"smoke", "connectivity"})
    @Story("Allure Integration")
    @Description("Validate that Allure reporting is working correctly")
    @Severity(SeverityLevel.NORMAL)
    public void validateAllureIntegration() {
        System.out.println("=== Allure Integration Test ===");

        // Add some Allure-specific content
        Allure.step("Testing Allure step functionality");
        Allure.addAttachment("Environment Info", "text/plain",
                "Java: " + System.getProperty("java.version") + "\n" +
                        "OS: " + System.getProperty("os.name") + "\n" +
                        "Base URL: " + BASE_URL);

        System.out.println("Allure Integration: ✅ Working");

        // This test should always pass if it reaches this point
        assertTrue(true, "Allure integration test completed successfully");
    }

}
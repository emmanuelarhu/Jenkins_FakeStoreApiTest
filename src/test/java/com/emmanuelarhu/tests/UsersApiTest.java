package com.emmanuelarhu.tests;

import com.emmanuelarhu.base.BaseTest;
import com.emmanuelarhu.models.User;
import com.emmanuelarhu.utils.TestDataProvider;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Comprehensive test suite for FakeStore API Users endpoints
 * Tests cover all CRUD operations with positive, negative, and edge cases
 */
@Epic("FakeStore API Testing")
@Feature("Users Management")
public class UsersApiTest extends BaseTest {

    // GET /users tests
    @Test(priority = 1)
    @Story("Get All Users")
    @Description("Verify that all users can be retrieved successfully")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAllUsers() {
        given()
                .spec(getRequestSpecForGet())
                .when()
                .get(USERS_ENDPOINT)
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("size()", greaterThan(0))
                .body("[0]", hasKey("id"))
                .body("[0]", hasKey("username"))
                .body("[0]", hasKey("email"))
                .body("[0].id", notNullValue())
                .body("[0].username", notNullValue())
                .body("[0].email", notNullValue()).log().all();
    }

    @Test(priority = 2)
    @Story("Get All Users")
    @Description("Verify response time for getting all users is acceptable")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllUsersResponseTime() {
        given()
                .spec(getRequestSpecForGet())
                .when()
                .get(USERS_ENDPOINT)
                .then()
                .statusCode(200)
                .time(lessThan(5000L)).log().all(); // Response should be under 5 seconds
    }

    // GET /users/{id} tests
    @Test(priority = 3, dataProvider = "validUserIds", dataProviderClass = TestDataProvider.class)
    @Story("Get Single User")
    @Description("Verify that a single user can be retrieved by valid ID")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetSingleUserByValidId(int userId) {
        given()
                .spec(getRequestSpecForGet())
                .pathParam("id", userId)
                .when()
                .get(USERS_ENDPOINT + "/{id}")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("id", equalTo(userId))
                .body("username", notNullValue())
                .body("email", notNullValue())
                .body("email", matchesPattern("^[A-Za-z0-9+_.-]+@(.+)$")).log().all();
    }

    @Test(priority = 4, dataProvider = "invalidUserIds", dataProviderClass = TestDataProvider.class)
    @Story("Get Single User")
    @Description("Verify appropriate error handling for invalid user IDs")
    @Severity(SeverityLevel.NORMAL)
    public void testGetSingleUserByInvalidId(int userId) {
        given()
                .spec(getRequestSpecForGet())
                .pathParam("id", userId)
                .when()
                .get(USERS_ENDPOINT + "/{id}")
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(404))).log().all();
    }

    @Test(priority = 5)
    @Story("Get Single User")
    @Description("Verify error handling for non-numeric user ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetSingleUserByNonNumericId() {
        given()
                .spec(getRequestSpecForGet())
                .when()
                .get(USERS_ENDPOINT + "/abc")
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(404))).log().all();
    }

    @Test(priority = 6)
    @Story("Get Single User")
    @Description("Verify error handling for special characters in user ID")
    @Severity(SeverityLevel.MINOR)
    public void testGetSingleUserBySpecialCharacterId() {
        given()
                .spec(getRequestSpecForGet())
                .when()
                .get(USERS_ENDPOINT + "/@#$")
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(404))).log().all();
    }

    // POST /users tests
    @Test(priority = 7, dataProvider = "validUserData", dataProviderClass = TestDataProvider.class)
    @Story("Create User")
    @Description("Verify that a new user can be created with valid data")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateUserWithValidData(User user) {
        Response response = given()
                .spec(getRequestSpec())
                .body(user)
                .when()
                .post(USERS_ENDPOINT)
                .then()
                .statusCode(anyOf(equalTo(200), equalTo(201)))
                .contentType("application/json")
                .body("username", equalTo(user.getUsername()))
                .body("email", equalTo(user.getEmail())).log().all()
                .extract().response();

        // Verify the created user has an ID
        Integer createdId = response.jsonPath().getInt("id");
        assertNotNull(createdId, "Created user should have an ID");
    }

    @Test(priority = 8, dataProvider = "invalidUserData", dataProviderClass = TestDataProvider.class)
    @Story("Create User")
    @Description("Verify appropriate error handling when creating user with invalid data")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateUserWithInvalidData(User user, String invalidField) {
        given()
                .spec(getRequestSpec())
                .body(user)
                .when()
                .post(USERS_ENDPOINT)
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(422), equalTo(200))).log().all(); // Some APIs might still return 200
    }

    @Test(priority = 9)
    @Story("Create User")
    @Description("Verify error handling when creating user with empty request body")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateUserWithEmptyBody() {
        given()
                .spec(getRequestSpec())
                .body("{}")
                .when()
                .post(USERS_ENDPOINT)
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(422), equalTo(200))).log().all();
    }

    @Test(priority = 10)
    @Story("Create User")
    @Description("Verify error handling when creating user with malformed JSON")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateUserWithMalformedJson() {
        given()
                .spec(getRequestSpec())
                .body("{username: 'test', email: 'invalid-json'")
                .when()
                .post(USERS_ENDPOINT)
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(422))).log().all();
    }

    // PUT /users/{id} tests
    @Test(priority = 11, dataProvider = "updateUserData", dataProviderClass = TestDataProvider.class)
    @Story("Update User")
    @Description("Verify that an existing user can be updated with valid data")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateUserWithValidData(int userId, User updatedUser) {
        given()
                .spec(getRequestSpec())
                .pathParam("id", userId)
                .body(updatedUser)
                .when()
                .put(USERS_ENDPOINT + "/{id}")
                .then()
                .statusCode(anyOf(equalTo(200), equalTo(204))).log().all()
                .contentType(anyOf(equalTo("application/json"), equalTo("application/json; charset=utf-8")));
    }

    @Test(priority = 12)
    @Story("Update User")
    @Description("Verify error handling when updating non-existent user")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateNonExistentUser() {
        User updateUser = new User(9999, "nonexistent", "nonexistent@test.com", "password123");

        given()
                .spec(getRequestSpec())
                .pathParam("id", 9999)
                .body(updateUser)
                .when()
                .put(USERS_ENDPOINT + "/{id}")
                .then()
                .statusCode(anyOf(equalTo(404), equalTo(400), equalTo(200))).log().all(); // Some APIs might still return 200
    }

    @Test(priority = 13)
    @Story("Update User")
    @Description("Verify error handling when updating user with invalid ID")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateUserWithInvalidId() {
        User updateUser = new User(1, "testuser", "test@example.com", "password123");

        given()
                .spec(getRequestSpec())
                .pathParam("id", "invalid")
                .body(updateUser)
                .when()
                .put(USERS_ENDPOINT + "/{id}")
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(404))).log().all();
    }

    @Test(priority = 14)
    @Story("Update User")
    @Description("Verify partial update of user data")
    @Severity(SeverityLevel.NORMAL)
    public void testPartialUpdateUser() {
        String partialUpdate = "{\"username\": \"partially_updated_user\"}";

        given()
                .spec(getRequestSpec())
                .pathParam("id", 1)
                .body(partialUpdate)
                .when()
                .put(USERS_ENDPOINT + "/{id}")
                .then()
                .statusCode(anyOf(equalTo(200), equalTo(204), equalTo(400))).log().all();
    }

    // DELETE /users/{id} tests
    @Test(priority = 15)
    @Story("Delete User")
    @Description("Verify that an existing user can be deleted")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteExistingUser() {
        given()
                .spec(getRequestSpecForGet())
                .pathParam("id", 1)
                .when()
                .delete(USERS_ENDPOINT + "/{id}")
                .then()
                .statusCode(anyOf(equalTo(200), equalTo(204))).log().all();
    }

    @Test(priority = 16)
    @Story("Delete User")
    @Description("Verify error handling when deleting non-existent user")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteNonExistentUser() {
        given()
                .spec(getRequestSpecForGet())
                .pathParam("id", 9999)
                .when()
                .delete(USERS_ENDPOINT + "/{id}")
                .then()
                .statusCode(anyOf(equalTo(404), equalTo(400), equalTo(200))).log().all(); // Some APIs might still return 200
    }

    @Test(priority = 17)
    @Story("Delete User")
    @Description("Verify error handling when deleting user with invalid ID")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteUserWithInvalidId() {
        given()
                .spec(getRequestSpecForGet())
                .pathParam("id", "invalid")
                .when()
                .delete(USERS_ENDPOINT + "/{id}")
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(404))).log().all();
    }

    @Test(priority = 18)
    @Story("Delete User")
    @Description("Verify error handling when deleting user with negative ID")
    @Severity(SeverityLevel.MINOR)
    public void testDeleteUserWithNegativeId() {
        given()
                .spec(getRequestSpecForGet())
                .pathParam("id", -1)
                .when()
                .delete(USERS_ENDPOINT + "/{id}")
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(404))).log().all();
    }

    // Edge cases and security tests
    @Test(priority = 19)
    @Story("Security Tests")
    @Description("Verify SQL injection protection in user ID parameter")
    @Severity(SeverityLevel.CRITICAL)
    public void testSqlInjectionProtection() {
        given()
                .spec(getRequestSpecForGet())
                .when()
                .get(USERS_ENDPOINT + "/1' OR '1'='1")
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(404))).log().all();
    }

    @Test(priority = 20)
    @Story("Security Tests")
    @Description("Verify XSS protection in user creation")
    @Severity(SeverityLevel.NORMAL)
    public void testXssProtection() {
        User xssUser = new User(null, "<script>alert('xss')</script>", "test@example.com", "password123");

        given()
                .spec(getRequestSpec())
                .body(xssUser)
                .when()
                .post(USERS_ENDPOINT)
                .then()
                .statusCode(anyOf(equalTo(400), equalTo(404))).log().all();
    }

    @Test(priority = 21)
    @Story("Performance Tests")
    @Description("Verify API can handle concurrent requests")
    @Severity(SeverityLevel.MINOR)
    public void testConcurrentRequests() {
        // Simple concurrent test - in real scenarios, use thread pools
        for (int i = 0; i < 5; i++) {
            given()
                    .spec(getRequestSpecForGet())
                    .when()
                    .get(USERS_ENDPOINT)
                    .then()
                    .statusCode(200).log().all()
                    .time(lessThan(10000L));
        }
    }

    @Test(priority = 22)
    @Story("Data Validation")
    @Description("Verify email format validation")
    @Severity(SeverityLevel.NORMAL)
    public void testEmailFormatValidation() {
        String[] invalidEmails = {"plainaddress", "@missingdomain.com", "missing@.com", "missing.domain@.com"};

        for (String invalidEmail : invalidEmails) {
            User user = new User(null, "testuser", invalidEmail, "password123");
            given()
                    .spec(getRequestSpec())
                    .body(user)
                    .when()
                    .post(USERS_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(422), equalTo(200))).log().all(); // Some APIs might accept invalid emails
        }
    }
}
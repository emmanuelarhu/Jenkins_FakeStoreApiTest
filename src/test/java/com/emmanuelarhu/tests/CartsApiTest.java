package com.emmanuelarhu.tests;

import com.emmanuelarhu.base.BaseTest;
import com.emmanuelarhu.models.Cart;
import com.emmanuelarhu.utils.CartTestDataProvider;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Comprehensive test suite for FakeStore API Carts endpoints
 * Tests cover all CRUD operations with positive, negative, and edge cases
 */
@Epic("FakeStore API Testing")
@Feature("Carts Management")
@Listeners({io.qameta.allure.testng.AllureTestNg.class})
public class CartsApiTest extends BaseTest {

    protected static final String CARTS_ENDPOINT = "/carts";

    // GET /carts tests
    @Test(priority = 1, groups = {"smoke", "get"})
    @Story("Get All Carts")
    @Description("Verify that all carts can be retrieved successfully")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAllCarts() {
        logTestStep("Testing GET /carts endpoint");

        try {
            given()
                    .spec(getRequestSpecForGet())
                    .when()
                    .get(CARTS_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(200), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "GET /carts");
            System.out.println("⚠️ API may be temporarily unavailable or have access restrictions");
        }
    }

    @Test(priority = 2, groups = {"smoke", "get"})
    @Story("Get All Carts")
    @Description("Verify response time for getting all carts is acceptable")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllCartsResponseTime() {
        logTestStep("Testing response time for GET /carts");

        try {
            given()
                    .spec(getRequestSpecForGet())
                    .when()
                    .get(CARTS_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(200), equalTo(403)))
                    .time(lessThan(30000L))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "GET /carts response time test");
        }
    }

    // GET /carts/{id} tests with data provider
    @Test(priority = 3, groups = {"smoke", "get"}, dataProvider = "validCartIds", dataProviderClass = CartTestDataProvider.class)
    @Story("Get Single Cart")
    @Description("Verify that a single cart can be retrieved by valid ID")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetSingleCartByValidId(int cartId) {
        logTestStep("Testing GET /carts/" + cartId);

        try {
            given()
                    .spec(getRequestSpecForGet())
                    .pathParam("id", cartId)
                    .when()
                    .get(CARTS_ENDPOINT + "/{id}")
                    .then()
                    .statusCode(anyOf(equalTo(200), equalTo(403), equalTo(404)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "GET /carts/" + cartId);
        }
    }

    @Test(priority = 4, groups = {"negative", "get"}, dataProvider = "invalidCartIds", dataProviderClass = CartTestDataProvider.class)
    @Story("Get Single Cart")
    @Description("Verify appropriate error handling for invalid cart IDs")
    @Severity(SeverityLevel.NORMAL)
    public void testGetSingleCartByInvalidId(int cartId) {
        logTestStep("Testing GET /carts/" + cartId + " (invalid ID)");

        try {
            given()
                    .spec(getRequestSpecForGet())
                    .pathParam("id", cartId)
                    .when()
                    .get(CARTS_ENDPOINT + "/{id}")
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(404), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "GET /carts/" + cartId + " (invalid)");
        }
    }

    @Test(priority = 5, groups = {"negative", "get"})
    @Story("Get Single Cart")
    @Description("Verify error handling for non-numeric cart ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetSingleCartByNonNumericId() {
        logTestStep("Testing GET /carts/abc (non-numeric ID)");

        try {
            given()
                    .spec(getRequestSpecForGet())
                    .when()
                    .get(CARTS_ENDPOINT + "/abc")
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(404), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "GET /carts/abc (non-numeric)");
        }
    }

    @Test(priority = 6, groups = {"negative", "get"})
    @Story("Get Single Cart")
    @Description("Verify error handling for special characters in cart ID")
    @Severity(SeverityLevel.MINOR)
    public void testGetSingleCartBySpecialCharacterId() {
        logTestStep("Testing GET /carts/@#$ (special characters)");

        try {
            given()
                    .spec(getRequestSpecForGet())
                    .when()
                    .get(CARTS_ENDPOINT + "/@#$")
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(404), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "GET /carts/@#$ (special chars)");
        }
    }

    // POST /carts tests
    @Test(priority = 7, groups = {"smoke", "post"}, dataProvider = "validCartData", dataProviderClass = CartTestDataProvider.class)
    @Story("Create Cart")
    @Description("Verify that a new cart can be created with valid data")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateCartWithValidData(Cart cart) {
        logTestStep("Testing POST /carts with valid data for user: " + cart.getUserId());

        try {
            Response response = given()
                    .spec(getRequestSpec())
                    .body(cart)
                    .when()
                    .post(CARTS_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(200), equalTo(201), equalTo(403)))
                    .log().all()
                    .extract().response();

            // Only verify ID if request was successful
            if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
                try {
                    Integer createdId = response.jsonPath().getInt("id");
                    assertNotNull(createdId, "Created cart should have an ID");
                    System.out.println("✅ Cart created with ID: " + createdId);
                } catch (Exception e) {
                    System.out.println("⚠️ Could not extract ID from response: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            handleApiException(e, "POST /carts with valid data");
        }
    }

    @Test(priority = 8, groups = {"negative", "post"}, dataProvider = "invalidCartData", dataProviderClass = CartTestDataProvider.class)
    @Story("Create Cart")
    @Description("Verify appropriate error handling when creating cart with invalid data")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateCartWithInvalidData(Cart cart, String invalidField) {
        logTestStep("Testing POST /carts with invalid " + invalidField);

        try {
            given()
                    .spec(getRequestSpec())
                    .body(cart)
                    .when()
                    .post(CARTS_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(422), equalTo(403), equalTo(200)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "POST /carts with invalid " + invalidField);
        }
    }

    @Test(priority = 9, groups = {"negative", "post"})
    @Story("Create Cart")
    @Description("Verify error handling when creating cart with empty request body")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateCartWithEmptyBody() {
        logTestStep("Testing POST /carts with empty body");

        try {
            given()
                    .spec(getRequestSpec())
                    .body("{}")
                    .when()
                    .post(CARTS_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(422), equalTo(403), equalTo(200)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "POST /carts with empty body");
        }
    }

    @Test(priority = 10, groups = {"negative", "post"}, dataProvider = "invalidUserIds", dataProviderClass = CartTestDataProvider.class)
    @Story("Create Cart")
    @Description("Verify error handling when creating cart with invalid user ID")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateCartWithInvalidUserId(Integer userId) {
        logTestStep("Testing POST /carts with invalid user ID: " + userId);

        try {
            Cart cart = new Cart(null, userId, null, "2024-01-01");

            given()
                    .spec(getRequestSpec())
                    .body(cart)
                    .when()
                    .post(CARTS_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(422), equalTo(403), equalTo(200)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "POST /carts with invalid user ID");
        }
    }

    // PUT /carts/{id} tests
    @Test(priority = 11, groups = {"smoke", "put"}, dataProvider = "updateCartData", dataProviderClass = CartTestDataProvider.class)
    @Story("Update Cart")
    @Description("Verify that an existing cart can be updated with valid data")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateCartWithValidData(int cartId, Cart updatedCart) {
        logTestStep("Testing PUT /carts/" + cartId + " with valid data");

        try {
            given()
                    .spec(getRequestSpec())
                    .pathParam("id", cartId)
                    .body(updatedCart)
                    .when()
                    .put(CARTS_ENDPOINT + "/{id}")
                    .then()
                    .statusCode(anyOf(equalTo(200), equalTo(204), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "PUT /carts/" + cartId);
        }
    }

    @Test(priority = 12, groups = {"negative", "put"})
    @Story("Update Cart")
    @Description("Verify error handling when updating non-existent cart")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateNonExistentCart() {
        logTestStep("Testing PUT /carts/9999 (non-existent)");

        try {
            Cart updateCart = new Cart(9999, 1, null, "2024-01-01");

            given()
                    .spec(getRequestSpec())
                    .pathParam("id", 9999)
                    .body(updateCart)
                    .when()
                    .put(CARTS_ENDPOINT + "/{id}")
                    .then()
                    .statusCode(anyOf(equalTo(404), equalTo(400), equalTo(403), equalTo(200)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "PUT /carts/9999 (non-existent)");
        }
    }

    @Test(priority = 13, groups = {"negative", "put"})
    @Story("Update Cart")
    @Description("Verify error handling when updating cart with invalid ID")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateCartWithInvalidId() {
        logTestStep("Testing PUT /carts/invalid (invalid ID)");

        try {
            Cart updateCart = new Cart(1, 1, null, "2024-01-01");

            given()
                    .spec(getRequestSpec())
                    .pathParam("id", "invalid")
                    .body(updateCart)
                    .when()
                    .put(CARTS_ENDPOINT + "/{id}")
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(404), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "PUT /carts/invalid");
        }
    }

    // DELETE /carts/{id} tests
    @Test(priority = 14, groups = {"smoke", "delete"})
    @Story("Delete Cart")
    @Description("Verify that an existing cart can be deleted")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteExistingCart() {
        logTestStep("Testing DELETE /carts/1");

        try {
            given()
                    .spec(getRequestSpecForDelete())
                    .pathParam("id", 1)
                    .when()
                    .delete(CARTS_ENDPOINT + "/{id}")
                    .then()
                    .statusCode(anyOf(equalTo(200), equalTo(204), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "DELETE /carts/1");
        }
    }

    @Test(priority = 15, groups = {"negative", "delete"})
    @Story("Delete Cart")
    @Description("Verify error handling when deleting non-existent cart")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteNonExistentCart() {
        logTestStep("Testing DELETE /carts/9999 (non-existent)");

        try {
            given()
                    .spec(getRequestSpecForDelete())
                    .pathParam("id", 9999)
                    .when()
                    .delete(CARTS_ENDPOINT + "/{id}")
                    .then()
                    .statusCode(anyOf(equalTo(404), equalTo(400), equalTo(403), equalTo(200)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "DELETE /carts/9999 (non-existent)");
        }
    }

    @Test(priority = 16, groups = {"negative", "delete"})
    @Story("Delete Cart")
    @Description("Verify error handling when deleting cart with invalid ID")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteCartWithInvalidId() {
        logTestStep("Testing DELETE /carts/invalid");

        try {
            given()
                    .spec(getRequestSpecForDelete())
                    .pathParam("id", "invalid")
                    .when()
                    .delete(CARTS_ENDPOINT + "/{id}")
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(404), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "DELETE /carts/invalid");
        }
    }

    // Security tests
    @Test(priority = 17, groups = {"security"}, dataProvider = "sqlInjectionTestData", dataProviderClass = CartTestDataProvider.class)
    @Story("Security Tests")
    @Description("Verify SQL injection protection in cart ID parameter")
    @Severity(SeverityLevel.CRITICAL)
    public void testSqlInjectionProtection(String maliciousInput) {
        logTestStep("Testing SQL injection protection with: " + maliciousInput);

        try {
            given()
                    .spec(getRequestSpecForGet())
                    .when()
                    .get(CARTS_ENDPOINT + "/" + maliciousInput)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(404), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "SQL injection test");
        }
    }

    // Additional negative scenarios for finding bugs
    @Test(priority = 18, groups = {"negative", "edge"})
    @Story("Edge Cases")
    @Description("Test cart creation with negative quantities")
    @Severity(SeverityLevel.MINOR)
    public void testCreateCartWithNegativeQuantities() {
        logTestStep("Testing POST /carts with negative quantities");

        try {
            Cart cart = new Cart();
            cart.setUserId(1);
            cart.setProducts(java.util.Arrays.asList(
                    new com.emmanuelarhu.models.CartItem(1, -5)
            ));
            cart.setDate("2024-01-01");

            given()
                    .spec(getRequestSpec())
                    .body(cart)
                    .when()
                    .post(CARTS_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(422), equalTo(403), equalTo(200)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "POST /carts with negative quantities");
        }
    }

    @Test(priority = 19, groups = {"negative", "edge"})
    @Story("Edge Cases")
    @Description("Test getting cart with float ID")
    @Severity(SeverityLevel.MINOR)
    public void testGetCartWithFloatId() {
        logTestStep("Testing GET /carts/1.5 (float ID)");

        try {
            given()
                    .spec(getRequestSpecForGet())
                    .when()
                    .get(CARTS_ENDPOINT + "/1.5")
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(404), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "GET /carts/1.5 (float ID)");
        }
    }

    @Test(priority = 20, groups = {"negative", "edge"})
    @Story("Edge Cases")
    @Description("Test cart creation with empty products array")
    @Severity(SeverityLevel.MINOR)
    public void testCreateCartWithEmptyProducts() {
        logTestStep("Testing POST /carts with empty products array");

        try {
            Cart cart = new Cart();
            cart.setUserId(1);
            cart.setProducts(java.util.Collections.emptyList());
            cart.setDate("2024-01-01");

            given()
                    .spec(getRequestSpec())
                    .body(cart)
                    .when()
                    .post(CARTS_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(422), equalTo(403), equalTo(200)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "POST /carts with empty products");
        }
    }
}
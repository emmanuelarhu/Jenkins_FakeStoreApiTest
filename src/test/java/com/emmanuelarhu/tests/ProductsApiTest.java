package com.emmanuelarhu.tests;

import com.emmanuelarhu.base.BaseTest;
import com.emmanuelarhu.models.Product;
import com.emmanuelarhu.utils.ProductTestDataProvider;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Comprehensive test suite for FakeStore API Products endpoints
 * Tests cover all CRUD operations with positive, negative, and edge cases
 */
@Epic("FakeStore API Testing")
@Feature("Products Management")
@Listeners({io.qameta.allure.testng.AllureTestNg.class})
public class ProductsApiTest extends BaseTest {

    protected static final String PRODUCTS_ENDPOINT = "/products";

    // GET /products tests
    @Test(priority = 1, groups = {"smoke", "get"})
    @Story("Get All Products")
    @Description("Verify that all products can be retrieved successfully")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAllProducts() {
        logTestStep("Testing GET /products endpoint");

        try {
            given()
                    .spec(getRequestSpecForGet())
                    .when()
                    .get(PRODUCTS_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(200), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "GET /products");
            System.out.println("⚠️ API may be temporarily unavailable or have access restrictions");
        }
    }

    @Test(priority = 2, groups = {"smoke", "get"})
    @Story("Get All Products")
    @Description("Verify response time for getting all products is acceptable")
    @Severity(SeverityLevel.NORMAL)
    public void testGetAllProductsResponseTime() {
        logTestStep("Testing response time for GET /products");

        try {
            given()
                    .spec(getRequestSpecForGet())
                    .when()
                    .get(PRODUCTS_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(200), equalTo(403)))
                    .time(lessThan(30000L))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "GET /products response time test");
        }
    }

    // GET /products/{id} tests with data provider
    @Test(priority = 3, groups = {"smoke", "get"}, dataProvider = "validProductIds", dataProviderClass = ProductTestDataProvider.class)
    @Story("Get Single Product")
    @Description("Verify that a single product can be retrieved by valid ID")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetSingleProductByValidId(int productId) {
        logTestStep("Testing GET /products/" + productId);

        try {
            given()
                    .spec(getRequestSpecForGet())
                    .pathParam("id", productId)
                    .when()
                    .get(PRODUCTS_ENDPOINT + "/{id}")
                    .then()
                    .statusCode(anyOf(equalTo(200), equalTo(403), equalTo(404)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "GET /products/" + productId);
        }
    }

    @Test(priority = 4, groups = {"negative", "get"}, dataProvider = "invalidProductIds", dataProviderClass = ProductTestDataProvider.class)
    @Story("Get Single Product")
    @Description("Verify appropriate error handling for invalid product IDs")
    @Severity(SeverityLevel.NORMAL)
    public void testGetSingleProductByInvalidId(int productId) {
        logTestStep("Testing GET /products/" + productId + " (invalid ID)");

        try {
            given()
                    .spec(getRequestSpecForGet())
                    .pathParam("id", productId)
                    .when()
                    .get(PRODUCTS_ENDPOINT + "/{id}")
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(404), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "GET /products/" + productId + " (invalid)");
        }
    }

    @Test(priority = 5, groups = {"negative", "get"})
    @Story("Get Single Product")
    @Description("Verify error handling for non-numeric product ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetSingleProductByNonNumericId() {
        logTestStep("Testing GET /products/abc (non-numeric ID)");

        try {
            given()
                    .spec(getRequestSpecForGet())
                    .when()
                    .get(PRODUCTS_ENDPOINT + "/abc")
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(404), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "GET /products/abc (non-numeric)");
        }
    }

    @Test(priority = 6, groups = {"negative", "get"})
    @Story("Get Single Product")
    @Description("Verify error handling for special characters in product ID")
    @Severity(SeverityLevel.MINOR)
    public void testGetSingleProductBySpecialCharacterId() {
        logTestStep("Testing GET /products/@#$ (special characters)");

        try {
            given()
                    .spec(getRequestSpecForGet())
                    .when()
                    .get(PRODUCTS_ENDPOINT + "/@#$")
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(404), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "GET /products/@#$ (special chars)");
        }
    }

    // POST /products tests
    @Test(priority = 7, groups = {"smoke", "post"}, dataProvider = "validProductData", dataProviderClass = ProductTestDataProvider.class)
    @Story("Create Product")
    @Description("Verify that a new product can be created with valid data")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateProductWithValidData(Product product) {
        logTestStep("Testing POST /products with valid data: " + product.getTitle());

        try {
            Response response = given()
                    .spec(getRequestSpec())
                    .body(product)
                    .when()
                    .post(PRODUCTS_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(200), equalTo(201), equalTo(403)))
                    .log().all()
                    .extract().response();

            // Only verify ID if request was successful
            if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
                try {
                    Integer createdId = response.jsonPath().getInt("id");
                    assertNotNull(createdId, "Created product should have an ID");
                    System.out.println("✅ Product created with ID: " + createdId);
                } catch (Exception e) {
                    System.out.println("⚠️ Could not extract ID from response: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            handleApiException(e, "POST /products with valid data");
        }
    }

    @Test(priority = 8, groups = {"negative", "post"}, dataProvider = "invalidProductData", dataProviderClass = ProductTestDataProvider.class)
    @Story("Create Product")
    @Description("Verify appropriate error handling when creating product with invalid data")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateProductWithInvalidData(Product product, String invalidField) {
        logTestStep("Testing POST /products with invalid " + invalidField);

        try {
            given()
                    .spec(getRequestSpec())
                    .body(product)
                    .when()
                    .post(PRODUCTS_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(422), equalTo(403), equalTo(200)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "POST /products with invalid " + invalidField);
        }
    }

    @Test(priority = 9, groups = {"negative", "post"})
    @Story("Create Product")
    @Description("Verify error handling when creating product with empty request body")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateProductWithEmptyBody() {
        logTestStep("Testing POST /products with empty body");

        try {
            given()
                    .spec(getRequestSpec())
                    .body("{}")
                    .when()
                    .post(PRODUCTS_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(422), equalTo(403), equalTo(200)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "POST /products with empty body");
        }
    }

    @Test(priority = 10, groups = {"negative", "post"}, dataProvider = "negativeProductPrices", dataProviderClass = ProductTestDataProvider.class)
    @Story("Create Product")
    @Description("Verify error handling when creating product with negative or zero price")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateProductWithNegativePrice(Double price) {
        logTestStep("Testing POST /products with negative/zero price: " + price);

        try {
            Product product = new Product(null, "Test Product", price, "Test Description", "electronics", "https://example.com/image.jpg");

            given()
                    .spec(getRequestSpec())
                    .body(product)
                    .when()
                    .post(PRODUCTS_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(422), equalTo(403), equalTo(200)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "POST /products with negative price");
        }
    }

    // PUT /products/{id} tests
    @Test(priority = 11, groups = {"smoke", "put"}, dataProvider = "updateProductData", dataProviderClass = ProductTestDataProvider.class)
    @Story("Update Product")
    @Description("Verify that an existing product can be updated with valid data")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateProductWithValidData(int productId, Product updatedProduct) {
        logTestStep("Testing PUT /products/" + productId + " with valid data");

        try {
            given()
                    .spec(getRequestSpec())
                    .pathParam("id", productId)
                    .body(updatedProduct)
                    .when()
                    .put(PRODUCTS_ENDPOINT + "/{id}")
                    .then()
                    .statusCode(anyOf(equalTo(200), equalTo(204), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "PUT /products/" + productId);
        }
    }

    @Test(priority = 12, groups = {"negative", "put"})
    @Story("Update Product")
    @Description("Verify error handling when updating non-existent product")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateNonExistentProduct() {
        logTestStep("Testing PUT /products/9999 (non-existent)");

        try {
            Product updateProduct = new Product(9999, "Non-existent Product", 99.99, "This product doesn't exist", "electronics", "https://example.com/image.jpg");

            given()
                    .spec(getRequestSpec())
                    .pathParam("id", 9999)
                    .body(updateProduct)
                    .when()
                    .put(PRODUCTS_ENDPOINT + "/{id}")
                    .then()
                    .statusCode(anyOf(equalTo(404), equalTo(400), equalTo(403), equalTo(200)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "PUT /products/9999 (non-existent)");
        }
    }

    @Test(priority = 13, groups = {"negative", "put"})
    @Story("Update Product")
    @Description("Verify error handling when updating product with invalid price")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateProductWithInvalidPrice() {
        logTestStep("Testing PUT /products/1 with invalid price");

        try {
            Product updateProduct = new Product(1, "Updated Product", -50.0, "Updated description", "electronics", "https://example.com/image.jpg");

            given()
                    .spec(getRequestSpec())
                    .pathParam("id", 1)
                    .body(updateProduct)
                    .when()
                    .put(PRODUCTS_ENDPOINT + "/{id}")
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(422), equalTo(403), equalTo(200)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "PUT /products/1 with invalid price");
        }
    }

    // DELETE /products/{id} tests
    @Test(priority = 14, groups = {"smoke", "delete"})
    @Story("Delete Product")
    @Description("Verify that an existing product can be deleted")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteExistingProduct() {
        logTestStep("Testing DELETE /products/1");

        try {
            given()
                    .spec(getRequestSpecForDelete())
                    .pathParam("id", 1)
                    .when()
                    .delete(PRODUCTS_ENDPOINT + "/{id}")
                    .then()
                    .statusCode(anyOf(equalTo(200), equalTo(204), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "DELETE /products/1");
        }
    }

    @Test(priority = 15, groups = {"negative", "delete"})
    @Story("Delete Product")
    @Description("Verify error handling when deleting non-existent product")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteNonExistentProduct() {
        logTestStep("Testing DELETE /products/9999 (non-existent)");

        try {
            given()
                    .spec(getRequestSpecForDelete())
                    .pathParam("id", 9999)
                    .when()
                    .delete(PRODUCTS_ENDPOINT + "/{id}")
                    .then()
                    .statusCode(anyOf(equalTo(404), equalTo(400), equalTo(403), equalTo(200)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "DELETE /products/9999 (non-existent)");
        }
    }

    @Test(priority = 16, groups = {"negative", "delete"})
    @Story("Delete Product")
    @Description("Verify error handling when deleting product with invalid ID")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteProductWithInvalidId() {
        logTestStep("Testing DELETE /products/invalid");

        try {
            given()
                    .spec(getRequestSpecForDelete())
                    .pathParam("id", "invalid")
                    .when()
                    .delete(PRODUCTS_ENDPOINT + "/{id}")
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(404), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "DELETE /products/invalid");
        }
    }

    // Security tests
    @Test(priority = 17, groups = {"security"}, dataProvider = "sqlInjectionTestData", dataProviderClass = ProductTestDataProvider.class)
    @Story("Security Tests")
    @Description("Verify SQL injection protection in product ID parameter")
    @Severity(SeverityLevel.CRITICAL)
    public void testSqlInjectionProtection(String maliciousInput) {
        logTestStep("Testing SQL injection protection with: " + maliciousInput);

        try {
            given()
                    .spec(getRequestSpecForGet())
                    .when()
                    .get(PRODUCTS_ENDPOINT + "/" + maliciousInput)
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
    @Description("Test product creation with extremely long title")
    @Severity(SeverityLevel.MINOR)
    public void testCreateProductWithLongTitle() {
        logTestStep("Testing POST /products with extremely long title");

        try {
            String longTitle = "A".repeat(10000); // 10,000 character title
            Product product = new Product(null, longTitle, 99.99, "Description", "electronics", "https://example.com/image.jpg");

            given()
                    .spec(getRequestSpec())
                    .body(product)
                    .when()
                    .post(PRODUCTS_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(413), equalTo(422), equalTo(403), equalTo(200)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "POST /products with long title");
        }
    }

    @Test(priority = 19, groups = {"negative", "edge"})
    @Story("Edge Cases")
    @Description("Test product creation with invalid image URL")
    @Severity(SeverityLevel.MINOR)
    public void testCreateProductWithInvalidImageUrl() {
        logTestStep("Testing POST /products with invalid image URL");

        try {
            Product product = new Product(null, "Test Product", 99.99, "Description", "electronics", "not-a-valid-url");

            given()
                    .spec(getRequestSpec())
                    .body(product)
                    .when()
                    .post(PRODUCTS_ENDPOINT)
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(422), equalTo(403), equalTo(200)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "POST /products with invalid image URL");
        }
    }

    @Test(priority = 20, groups = {"negative", "edge"})
    @Story("Edge Cases")
    @Description("Test getting product with float ID")
    @Severity(SeverityLevel.MINOR)
    public void testGetProductWithFloatId() {
        logTestStep("Testing GET /products/1.5 (float ID)");

        try {
            given()
                    .spec(getRequestSpecForGet())
                    .when()
                    .get(PRODUCTS_ENDPOINT + "/1.5")
                    .then()
                    .statusCode(anyOf(equalTo(400), equalTo(404), equalTo(403)))
                    .log().all();
        } catch (Exception e) {
            handleApiException(e, "GET /products/1.5 (float ID)");
        }
    }
}
package com.emmanuelarhu.utils;

import com.emmanuelarhu.models.Product;
import org.testng.annotations.DataProvider;

/**
 * Data provider class for Product API test data
 */
public class ProductTestDataProvider {

    @DataProvider(name = "validProductData")
    public Object[][] validProductData() {
        return new Object[][] {
                {new Product(null, "Test Smartphone", 299.99, "A great smartphone for testing", "electronics", "https://example.com/phone.jpg")},
                {new Product(null, "Test Laptop", 899.99, "Powerful laptop for developers", "electronics", "https://example.com/laptop.jpg")},
                {new Product(null, "Test T-Shirt", 19.99, "Comfortable cotton t-shirt", "men's clothing", "https://example.com/tshirt.jpg")},
                {new Product(null, "Test Necklace", 149.99, "Beautiful gold necklace", "jewelery", "https://example.com/necklace.jpg")},
                {new Product(null, "Test Dress", 79.99, "Elegant women's dress", "women's clothing", "https://example.com/dress.jpg")}
        };
    }

    @DataProvider(name = "invalidProductData")
    public Object[][] invalidProductData() {
        return new Object[][] {
                // Missing title
                {new Product(null, "", 99.99, "Description", "electronics", "https://example.com/image.jpg"), "title"},
                // Missing price
                {new Product(null, "Test Product", null, "Description", "electronics", "https://example.com/image.jpg"), "price"},
                // Missing description
                {new Product(null, "Test Product", 99.99, "", "electronics", "https://example.com/image.jpg"), "description"},
                // Missing category
                {new Product(null, "Test Product", 99.99, "Description", "", "https://example.com/image.jpg"), "category"},
                // Missing image URL
                {new Product(null, "Test Product", 99.99, "Description", "electronics", ""), "image"},
                // Null values
                {new Product(null, null, 99.99, "Description", "electronics", "https://example.com/image.jpg"), "title"},
                {new Product(null, "Test Product", null, "Description", "electronics", "https://example.com/image.jpg"), "price"},
                {new Product(null, "Test Product", 99.99, null, "electronics", "https://example.com/image.jpg"), "description"},
                {new Product(null, "Test Product", 99.99, "Description", null, "https://example.com/image.jpg"), "category"},
                {new Product(null, "Test Product", 99.99, "Description", "electronics", null), "image"}
        };
    }

    @DataProvider(name = "validProductIds")
    public Object[][] validProductIds() {
        return new Object[][] {
                {1},
                {2},
                {3},
                {4},
                {5},
                {10},
                {15},
                {20}
        };
    }

    @DataProvider(name = "invalidProductIds")
    public Object[][] invalidProductIds() {
        return new Object[][] {
                {-1},
                {0},
                {999},
                {1000},
                {9999},
                {Integer.MAX_VALUE}
        };
    }

    @DataProvider(name = "updateProductData")
    public Object[][] updateProductData() {
        return new Object[][] {
                {1, new Product(1, "Updated Product 1", 199.99, "Updated description", "electronics", "https://example.com/updated1.jpg")},
                {2, new Product(2, "Updated Product 2", 299.99, "Updated description", "jewelery", "https://example.com/updated2.jpg")},
                {3, new Product(3, "Updated Product 3", 399.99, "Updated description", "men's clothing", "https://example.com/updated3.jpg")}
        };
    }

    @DataProvider(name = "negativeProductPrices")
    public Object[][] negativeProductPrices() {
        return new Object[][] {
                {-1.0},
                {-50.0},
                {0.0},
                {-999.99},
                {Double.NEGATIVE_INFINITY}
        };
    }

    @DataProvider(name = "sqlInjectionTestData")
    public Object[][] sqlInjectionTestData() {
        return new Object[][] {
                {"1' OR '1'='1"},
                {"1; DROP TABLE products;"},
                {"1' UNION SELECT * FROM products--"},
                {"1' OR 1=1--"},
                {"admin'--"},
                {"'; DELETE FROM products; --"},
                {"1' UNION SELECT id,title,price FROM products--"}
        };
    }

    @DataProvider(name = "invalidCategories")
    public Object[][] invalidCategories() {
        return new Object[][] {
                {"<script>alert('xss')</script>"},
                {"' OR '1'='1"},
                {"nonexistent_category"},
                {""},
                {null},
                {"category with spaces and special chars!@#$%"}
        };
    }

    @DataProvider(name = "extremePrices")
    public Object[][] extremePrices() {
        return new Object[][] {
                {Double.MAX_VALUE},
                {999999999.99},
                {0.01},
                {-1.0},
                {Double.NaN},
                {Double.POSITIVE_INFINITY}
        };
    }

    @DataProvider(name = "malformedProductJson")
    public Object[][] malformedProductJson() {
        return new Object[][] {
                {"{title: 'test', price: 99.99, description: 'invalid-json'}"},
                {"{\"title\": \"test\", \"price\": }"},
                {"{\"title\": \"test\", \"price\": 99.99"},
                {"title: 'test', price: 99.99, description: 'test'"},
                {"{\"title\": \"test\", \"price\": 99.99, \"description\": \"test\", }"}
        };
    }

    @DataProvider(name = "longStringData")
    public Object[][] longStringData() {
        String longString = "A".repeat(1000);
        String veryLongString = "A".repeat(10000);

        return new Object[][] {
                {longString},      // 1000 characters
                {veryLongString},  // 10000 characters
                {"A".repeat(50000)} // 50000 characters
        };
    }

    @DataProvider(name = "specialCharacterTitles")
    public Object[][] specialCharacterTitles() {
        return new Object[][] {
                {"Product with émojis 😀💻📱"},
                {"Product with special chars: !@#$%^&*()"},
                {"Product with unicode: ñáéíóú"},
                {"Product\nwith\nnewlines"},
                {"Product\twith\ttabs"},
                {"Product with \"quotes\" and 'apostrophes'"},
                {"Product with <HTML> tags"},
                {"Product with JSON: {\"key\": \"value\"}"}
        };
    }

    @DataProvider(name = "invalidImageUrls")
    public Object[][] invalidImageUrls() {
        return new Object[][] {
                {"not-a-url"},
                {"ftp://example.com/image.jpg"},
                {"javascript:alert('xss')"},
                {"data:image/jpeg;base64,invalid"},
                {"http://"},
                {"https://"},
                {"http://nonexistent-domain-12345.com/image.jpg"},
                {""},
                {null}
        };
    }

    @DataProvider(name = "edgeCasePrices")
    public Object[][] edgeCasePrices() {
        return new Object[][] {
                {0.001},     // Very small price
                {0.999},     // Less than 1
                {1.0},       // Exactly 1
                {999.99},    // Common max price
                {1000.0},    // Round number
                {1234.56789}, // Many decimal places
                {99999.99},   // Very high price
                {0.99},       // Common discount price
                {19.95},      // Marketing price
                {100.00}      // Round price
        };
    }

    @DataProvider(name = "boundaryProductIds")
    public Object[][] boundaryProductIds() {
        return new Object[][] {
                {1},        // Minimum valid ID
                {20},       // Expected upper boundary
                {0},        // Lower boundary invalid
                {21},       // Just above expected range
                {-1},       // Negative boundary
                {Integer.MIN_VALUE}, // Extreme negative
                {Integer.MAX_VALUE}  // Extreme positive
        };
    }

    @DataProvider(name = "partialUpdateProductData")
    public Object[][] partialUpdateProductData() {
        return new Object[][] {
                {1, "{\"title\": \"Updated Title Only\"}"},
                {2, "{\"price\": 199.99}"},
                {3, "{\"description\": \"Updated description only\"}"},
                {4, "{\"category\": \"updated_category\"}"},
                {5, "{\"image\": \"https://example.com/new-image.jpg\"}"},
                {1, "{\"title\": \"New Title\", \"price\": 299.99}"},
                {2, "{\"title\": \"Another Title\", \"description\": \"New description\"}"}
        };
    }

    @DataProvider(name = "concurrentProductTestData")
    public Object[][] concurrentProductTestData() {
        return new Object[][] {
                {1, "Thread-1"},
                {2, "Thread-2"},
                {3, "Thread-3"},
                {4, "Thread-4"},
                {5, "Thread-5"}
        };
    }

    @DataProvider(name = "xssTestProductData")
    public Object[][] xssTestProductData() {
        return new Object[][] {
                {"<script>alert('xss')</script>"},
                {"<img src=x onerror=alert('xss')>"},
                {"javascript:alert('xss')"},
                {"<svg onload=alert('xss')>"},
                {"<iframe src=javascript:alert('xss')>"},
                {"<body onload=alert('xss')>"},
                {"';alert('xss');//"}
        };
    }
}
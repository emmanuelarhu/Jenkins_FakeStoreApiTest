package com.emmanuelarhu.utils;

import com.emmanuelarhu.models.Cart;
import com.emmanuelarhu.models.CartItem;
import org.testng.annotations.DataProvider;
import java.util.Arrays;
import java.util.Collections;

/**
 * Data provider class for Cart API test data
 */
public class CartTestDataProvider {

    @DataProvider(name = "validCartData")
    public Object[][] validCartData() {
        return new Object[][] {
                {createValidCart1()},
                {createValidCart2()},
                {createValidCart3()},
                {createValidCart4()},
                {createValidCart5()}
        };
    }

    @DataProvider(name = "invalidCartData")
    public Object[][] invalidCartData() {
        return new Object[][] {
                // Missing userId
                {new Cart(null, null, Arrays.asList(new CartItem(1, 2)), "2024-01-01"), "userId"},
                // Invalid userId (negative)
                {new Cart(null, -1, Arrays.asList(new CartItem(1, 2)), "2024-01-01"), "userId"},
                // Invalid userId (zero)
                {new Cart(null, 0, Arrays.asList(new CartItem(1, 2)), "2024-01-01"), "userId"},
                // Missing products
                {new Cart(null, 1, null, "2024-01-01"), "products"},
                // Missing date
                {new Cart(null, 1, Arrays.asList(new CartItem(1, 2)), null), "date"},
                // Invalid date format
                {new Cart(null, 1, Arrays.asList(new CartItem(1, 2)), "invalid-date"), "date"}
        };
    }

    @DataProvider(name = "validCartIds")
    public Object[][] validCartIds() {
        return new Object[][] {
                {1},
                {2},
                {3},
                {4},
                {5}
        };
    }

    @DataProvider(name = "invalidCartIds")
    public Object[][] invalidCartIds() {
        return new Object[][] {
                {-1},
                {0},
                {999},
                {1000},
                {9999},
                {Integer.MAX_VALUE}
        };
    }

    @DataProvider(name = "invalidUserIds")
    public Object[][] invalidUserIds() {
        return new Object[][] {
                {-1},
                {0},
                {null},
                {999},
                {1000},
                {Integer.MAX_VALUE},
                {Integer.MIN_VALUE}
        };
    }

    @DataProvider(name = "updateCartData")
    public Object[][] updateCartData() {
        return new Object[][] {
                {1, createUpdatedCart1()},
                {2, createUpdatedCart2()},
                {3, createUpdatedCart3()}
        };
    }

    @DataProvider(name = "sqlInjectionTestData")
    public Object[][] sqlInjectionTestData() {
        return new Object[][] {
                {"1' OR '1'='1"},
                {"1; DROP TABLE carts;"},
                {"1' UNION SELECT * FROM carts--"},
                {"1' OR 1=1--"},
                {"admin'--"},
                {"'; DELETE FROM carts; --"},
                {"1' UNION SELECT id,userId FROM carts--"}
        };
    }

    @DataProvider(name = "invalidProductIds")
    public Object[][] invalidProductIds() {
        return new Object[][] {
                {-1},
                {0},
                {null},
                {999},
                {1000},
                {Integer.MAX_VALUE}
        };
    }

    @DataProvider(name = "invalidQuantities")
    public Object[][] invalidQuantities() {
        return new Object[][] {
                {-1},
                {-50},
                {0},
                {null},
                {Integer.MAX_VALUE},
                {Integer.MIN_VALUE}
        };
    }

    @DataProvider(name = "extremeQuantities")
    public Object[][] extremeQuantities() {
        return new Object[][] {
                {1000000},     // Very large quantity
                {-999999},     // Very negative quantity
                {0},           // Zero quantity
                {1},           // Minimum valid
                {999}          // Large but reasonable
        };
    }

    @DataProvider(name = "malformedCartJson")
    public Object[][] malformedCartJson() {
        return new Object[][] {
                {"{userId: 1, products: [], date: '2024-01-01'}"},
                {"{\"userId\": 1, \"products\": }"},
                {"{\"userId\": 1, \"products\": []"},
                {"userId: 1, products: [], date: '2024-01-01'"},
                {"{\"userId\": 1, \"products\": [], \"date\": \"2024-01-01\", }"}
        };
    }

    @DataProvider(name = "invalidDateFormats")
    public Object[][] invalidDateFormats() {
        return new Object[][] {
                {"invalid-date"},
                {"2024/01/01"},
                {"01-01-2024"},
                {"2024-13-01"},  // Invalid month
                {"2024-01-32"},  // Invalid day
                {""},
                {null},
                {"not-a-date"},
                {"2024-01-01T25:00:00"} // Invalid time
        };
    }

    @DataProvider(name = "boundaryCartIds")
    public Object[][] boundaryCartIds() {
        return new Object[][] {
                {1},                    // Minimum valid ID
                {5},                    // Expected upper boundary
                {0},                    // Lower boundary invalid
                {6},                    // Just above expected range
                {-1},                   // Negative boundary
                {Integer.MIN_VALUE},    // Extreme negative
                {Integer.MAX_VALUE}     // Extreme positive
        };
    }

    @DataProvider(name = "duplicateProductIds")
    public Object[][] duplicateProductIds() {
        return new Object[][] {
                // Same product multiple times
                {Arrays.asList(
                        new CartItem(1, 2),
                        new CartItem(1, 3),
                        new CartItem(1, 1)
                )},
                // Mix of duplicates and unique
                {Arrays.asList(
                        new CartItem(1, 2),
                        new CartItem(2, 1),
                        new CartItem(1, 5)
                )}
        };
    }

    // Helper methods to create test cart objects
    private static Cart createValidCart1() {
        Cart cart = new Cart();
        cart.setUserId(1);
        cart.setProducts(Arrays.asList(
                new CartItem(1, 2),
                new CartItem(2, 1)
        ));
        cart.setDate("2024-01-01");
        return cart;
    }

    private static Cart createValidCart2() {
        Cart cart = new Cart();
        cart.setUserId(2);
        cart.setProducts(Arrays.asList(
                new CartItem(3, 1),
                new CartItem(4, 3),
                new CartItem(5, 2)
        ));
        cart.setDate("2024-01-02");
        return cart;
    }

    private static Cart createValidCart3() {
        Cart cart = new Cart();
        cart.setUserId(3);
        cart.setProducts(Arrays.asList(
                new CartItem(1, 5)
        ));
        cart.setDate("2024-01-03");
        return cart;
    }

    private static Cart createValidCart4() {
        Cart cart = new Cart();
        cart.setUserId(4);
        cart.setProducts(Arrays.asList(
                new CartItem(2, 2),
                new CartItem(6, 1)
        ));
        cart.setDate("2024-01-04");
        return cart;
    }

    private static Cart createValidCart5() {
        Cart cart = new Cart();
        cart.setUserId(5);
        cart.setProducts(Arrays.asList(
                new CartItem(7, 1),
                new CartItem(8, 2),
                new CartItem(9, 1),
                new CartItem(10, 1)
        ));
        cart.setDate("2024-01-05");
        return cart;
    }

    private static Cart createUpdatedCart1() {
        Cart cart = new Cart();
        cart.setId(1);
        cart.setUserId(1);
        cart.setProducts(Arrays.asList(
                new CartItem(1, 5),
                new CartItem(3, 2)
        ));
        cart.setDate("2024-02-01");
        return cart;
    }

    private static Cart createUpdatedCart2() {
        Cart cart = new Cart();
        cart.setId(2);
        cart.setUserId(2);
        cart.setProducts(Arrays.asList(
                new CartItem(4, 1)
        ));
        cart.setDate("2024-02-02");
        return cart;
    }

    private static Cart createUpdatedCart3() {
        Cart cart = new Cart();
        cart.setId(3);
        cart.setUserId(3);
        cart.setProducts(Arrays.asList(
                new CartItem(1, 3),
                new CartItem(2, 3),
                new CartItem(5, 1)
        ));
        cart.setDate("2024-02-03");
        return cart;
    }
}
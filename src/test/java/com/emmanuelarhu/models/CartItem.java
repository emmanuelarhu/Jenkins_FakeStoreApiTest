package com.emmanuelarhu.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * CartItem model representing individual products in a cart
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartItem {

    @JsonProperty("productId")
    private Integer productId;

    @JsonProperty("quantity")
    private Integer quantity;

    // Default constructor
    public CartItem() {}

    // Constructor
    public CartItem(Integer productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                '}';
    }
}
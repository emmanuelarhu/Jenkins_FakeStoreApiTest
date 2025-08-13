package com.emmanuelarhu.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Cart model representing the Cart entity from FakeStore API
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cart {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("userId")
    private Integer userId;

    @JsonProperty("products")
    private List<CartItem> products;

    @JsonProperty("date")
    private String date;

    // Default constructor
    public Cart() {}

    // Constructor for creating test carts
    public Cart(Integer id, Integer userId, List<CartItem> products, String date) {
        this.id = id;
        this.userId = userId;
        this.products = products;
        this.date = date;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<CartItem> getProducts() {
        return products;
    }

    public void setProducts(List<CartItem> products) {
        this.products = products;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", userId=" + userId +
                ", products=" + products +
                ", date='" + date + '\'' +
                '}';
    }
}
package com.ecommerce.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A shopper. Composition: a customer has a cart, a wishlist and a list of orders. */
public class Customer {

    private final String id;
    private final String name;
    private final String email;
    private final String address;
    private final Cart cart = new Cart();
    private final Wishlist wishlist = new Wishlist();
    private final List<Order> orders = new ArrayList<>();

    public Customer(String id, String name, String email, String address) {
        this.id = id;
        this.name = name;
        this.email = email.toLowerCase();
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public Cart getCart() {
        return cart;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }
}

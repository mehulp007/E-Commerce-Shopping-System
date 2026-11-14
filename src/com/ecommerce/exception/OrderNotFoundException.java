package com.ecommerce.exception;

/** Thrown when an order ID does not belong to the current customer. */
public class OrderNotFoundException extends ShopException {

    public OrderNotFoundException(String message) {
        super(message);
    }
}

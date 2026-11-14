package com.ecommerce.exception;

/** Thrown when an order can no longer be cancelled (already shipped, delivered or cancelled). */
public class OrderCancellationException extends ShopException {

    public OrderCancellationException(String message) {
        super(message);
    }
}

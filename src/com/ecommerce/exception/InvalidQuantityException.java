package com.ecommerce.exception;

/** Thrown when a quantity is zero, negative or above the per-item limit. */
public class InvalidQuantityException extends ShopException {

    public InvalidQuantityException(String message) {
        super(message);
    }
}

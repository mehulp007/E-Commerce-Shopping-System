package com.ecommerce.exception;

/** Thrown when the customer tries to check out with nothing in the cart. */
public class EmptyCartException extends ShopException {

    public EmptyCartException(String message) {
        super(message);
    }
}

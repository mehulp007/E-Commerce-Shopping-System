package com.ecommerce.exception;

/**
 * Base class for every business error in the shop.
 * It is a checked exception, so callers must handle it or declare it.
 */
public class ShopException extends Exception {

    public ShopException(String message) {
        super(message);
    }

    public ShopException(String message, Throwable cause) {
        super(message, cause);
    }
}

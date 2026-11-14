package com.ecommerce.exception;

/** Thrown when user input such as an email address or name is not valid. */
public class ValidationException extends ShopException {

    public ValidationException(String message) {
        super(message);
    }
}

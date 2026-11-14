package com.ecommerce.exception;

/** Thrown when a payment is declined, for example because of invalid card details. */
public class PaymentFailedException extends ShopException {

    public PaymentFailedException(String message) {
        super(message);
    }
}

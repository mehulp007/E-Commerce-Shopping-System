package com.ecommerce.exception;

/** Thrown when a coupon code is unknown or cannot be used on the current cart. */
public class InvalidCouponException extends ShopException {

    public InvalidCouponException(String message) {
        super(message);
    }
}

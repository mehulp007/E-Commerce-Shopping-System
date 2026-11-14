package com.ecommerce.exception;

/** Thrown when a product ID does not exist in the catalog, cart or wishlist. */
public class ProductNotFoundException extends ShopException {

    public ProductNotFoundException(String message) {
        super(message);
    }
}

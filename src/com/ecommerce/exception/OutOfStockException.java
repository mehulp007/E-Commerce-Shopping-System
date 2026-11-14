package com.ecommerce.exception;

/** Thrown when the requested quantity is more than the stock available. */
public class OutOfStockException extends ShopException {

    public OutOfStockException(String productName, int available, int requested) {
        super(available == 0
                ? "'" + productName + "' is out of stock"
                : "Only " + available + " unit(s) of '" + productName + "' available (you asked for " + requested + ")");
    }
}

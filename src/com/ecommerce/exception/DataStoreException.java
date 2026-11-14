package com.ecommerce.exception;

/** Thrown when the data files cannot be read or written. Wraps the underlying IOException. */
public class DataStoreException extends ShopException {

    public DataStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}

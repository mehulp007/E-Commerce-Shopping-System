package com.ecommerce.payment;

import com.ecommerce.exception.PaymentFailedException;

/** Anything that can take payment for an order. Each payment method implements this. */
public interface Payment {

    /** Charges the amount and returns a receipt, or throws if the payment is declined. */
    PaymentReceipt pay(double amount) throws PaymentFailedException;

    String getMethodName();
}

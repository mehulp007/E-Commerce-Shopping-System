package com.ecommerce.payment;

import com.ecommerce.exception.PaymentFailedException;

/** Simulated UPI payment. Checks that the UPI ID looks like name@bank. */
public class UpiPayment extends AbstractPayment {

    private static final String UPI_PATTERN = "[a-zA-Z0-9._-]{2,}@[a-zA-Z]{2,}";
    private static final double UPI_LIMIT = 100_000;

    private final String upiId;

    public UpiPayment(String upiId) {
        this.upiId = upiId.trim();
    }

    @Override
    public String getMethodName() {
        return "UPI";
    }

    @Override
    protected void validate(double amount) throws PaymentFailedException {
        if (!upiId.matches(UPI_PATTERN)) {
            throw new PaymentFailedException("'" + upiId + "' is not a valid UPI ID (example: name@okbank)");
        }
        if (amount > UPI_LIMIT) {
            throw new PaymentFailedException("UPI payments are limited to ₹1,00,000 per transaction");
        }
    }

    @Override
    protected String getMaskedDetails() {
        return "UPI " + upiId;
    }

    @Override
    protected String getTransactionPrefix() {
        return "UPI";
    }
}

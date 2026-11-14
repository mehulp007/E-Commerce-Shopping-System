package com.ecommerce.payment;

/**
 * Proof of a payment, stored with the order.
 * Holds only safe-to-keep details (e.g. a masked card number), never the full card or CVV.
 */
public class PaymentReceipt {

    private final String method;
    private final String transactionId;
    private final String details;
    private final double amount;
    private final boolean paidNow;

    public PaymentReceipt(String method, String transactionId, String details, double amount, boolean paidNow) {
        this.method = method;
        this.transactionId = transactionId;
        this.details = details;
        this.amount = amount;
        this.paidNow = paidNow;
    }

    public String getMethod() {
        return method;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getDetails() {
        return details;
    }

    public double getAmount() {
        return amount;
    }

    /** False for Cash on Delivery, where the money is collected later. */
    public boolean isPaidNow() {
        return paidNow;
    }
}

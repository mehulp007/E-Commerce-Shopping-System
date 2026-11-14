package com.ecommerce.payment;

import com.ecommerce.exception.PaymentFailedException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Shared steps for every payment method: check the amount, validate the method's own details,
 * then issue a receipt. Subclasses only fill in the parts that differ.
 */
public abstract class AbstractPayment implements Payment {

    private static final DateTimeFormatter TXN_FORMAT = DateTimeFormatter.ofPattern("yyMMddHHmmss");

    @Override
    public final PaymentReceipt pay(double amount) throws PaymentFailedException {
        if (amount <= 0) {
            throw new PaymentFailedException("Payment amount must be more than zero");
        }
        validate(amount);
        return new PaymentReceipt(getMethodName(), generateTransactionId(), getMaskedDetails(), amount, isPaidNow());
    }

    /** Throws if this method's details are invalid or it cannot be used for this amount. */
    protected abstract void validate(double amount) throws PaymentFailedException;

    /** Details safe to print on the invoice, e.g. "Card XXXX-XXXX-XXXX-1111". */
    protected abstract String getMaskedDetails();

    protected abstract String getTransactionPrefix();

    protected boolean isPaidNow() {
        return true;
    }

    private String generateTransactionId() {
        return getTransactionPrefix() + LocalDateTime.now().format(TXN_FORMAT)
                + ThreadLocalRandom.current().nextInt(100, 1000);
    }
}

package com.ecommerce.payment;

import com.ecommerce.exception.PaymentFailedException;
import com.ecommerce.util.Money;

/** Pay in cash when the order arrives. Not allowed for very large orders. */
public class CashOnDelivery extends AbstractPayment {

    public static final double COD_LIMIT = 50_000;

    @Override
    public String getMethodName() {
        return "Cash on Delivery";
    }

    @Override
    protected void validate(double amount) throws PaymentFailedException {
        if (amount > COD_LIMIT) {
            throw new PaymentFailedException(
                    "Cash on Delivery is only available for orders up to " + Money.format(COD_LIMIT));
        }
    }

    @Override
    protected String getMaskedDetails() {
        return "Pay in cash on delivery";
    }

    @Override
    protected String getTransactionPrefix() {
        return "COD";
    }

    @Override
    protected boolean isPaidNow() {
        return false;
    }
}

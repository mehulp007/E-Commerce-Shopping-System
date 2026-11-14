package com.ecommerce.payment;

import com.ecommerce.exception.PaymentFailedException;
import java.util.List;

/** Simulated net banking payment through one of the supported banks. */
public class NetBankingPayment extends AbstractPayment {

    public static final List<String> SUPPORTED_BANKS =
            List.of("State Bank of India", "HDFC Bank", "ICICI Bank", "Axis Bank", "Kotak Mahindra Bank");

    private final String bank;

    public NetBankingPayment(String bank) {
        this.bank = bank;
    }

    @Override
    public String getMethodName() {
        return "Net Banking";
    }

    @Override
    protected void validate(double amount) throws PaymentFailedException {
        if (!SUPPORTED_BANKS.contains(bank)) {
            throw new PaymentFailedException("Net banking is not supported for '" + bank + "'");
        }
    }

    @Override
    protected String getMaskedDetails() {
        return "Net Banking - " + bank;
    }

    @Override
    protected String getTransactionPrefix() {
        return "NBK";
    }
}

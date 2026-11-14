package com.ecommerce.payment;

import com.ecommerce.exception.PaymentFailedException;
import java.time.DateTimeException;
import java.time.YearMonth;

/** Simulated credit/debit card payment. No money moves; the details are only checked for format. */
public class CardPayment extends AbstractPayment {

    private final String cardNumber;
    private final String holderName;
    private final String expiry;
    private final String cvv;

    public CardPayment(String cardNumber, String holderName, String expiry, String cvv) {
        this.cardNumber = cardNumber.replaceAll("[\\s-]", "");
        this.holderName = holderName.trim();
        this.expiry = expiry.trim();
        this.cvv = cvv.trim();
    }

    @Override
    public String getMethodName() {
        return "Card";
    }

    @Override
    protected void validate(double amount) throws PaymentFailedException {
        if (!cardNumber.matches("\\d{16}")) {
            throw new PaymentFailedException("Card number must have 16 digits");
        }
        if (!passesLuhnCheck(cardNumber)) {
            throw new PaymentFailedException("Card number is not valid (failed checksum)");
        }
        if (holderName.isEmpty()) {
            throw new PaymentFailedException("Card holder name is required");
        }
        if (!cvv.matches("\\d{3}")) {
            throw new PaymentFailedException("CVV must be 3 digits");
        }
        if (!expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            throw new PaymentFailedException("Expiry must be in MM/YY format");
        }
        try {
            int month = Integer.parseInt(expiry.substring(0, 2));
            int year = 2000 + Integer.parseInt(expiry.substring(3));
            if (YearMonth.of(year, month).isBefore(YearMonth.now())) {
                throw new PaymentFailedException("Card has expired");
            }
        } catch (DateTimeException e) {
            throw new PaymentFailedException("Expiry date is not valid");
        }
    }

    @Override
    protected String getMaskedDetails() {
        return "Card XXXX-XXXX-XXXX-" + cardNumber.substring(12) + " (" + holderName + ")";
    }

    @Override
    protected String getTransactionPrefix() {
        return "CRD";
    }

    /** Standard checksum used by real card numbers to catch typing mistakes. */
    private static boolean passesLuhnCheck(String number) {
        int sum = 0;
        boolean doubleIt = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = number.charAt(i) - '0';
            if (doubleIt) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
            doubleIt = !doubleIt;
        }
        return sum % 10 == 0;
    }
}

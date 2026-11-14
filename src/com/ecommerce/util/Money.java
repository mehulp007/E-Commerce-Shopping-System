package com.ecommerce.util;

import java.util.Locale;

/** Helpers for formatting and rounding rupee amounts. */
public final class Money {

    public static final String SYMBOL = "₹";

    private Money() {
    }

    public static String format(double amount) {
        return String.format(Locale.ENGLISH, "%s%,.2f", SYMBOL, amount);
    }

    /** Rounds to 2 decimal places (paise). */
    public static double round(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }
}

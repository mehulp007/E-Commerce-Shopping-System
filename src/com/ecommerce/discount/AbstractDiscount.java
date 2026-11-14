package com.ecommerce.discount;

import com.ecommerce.model.Cart;
import com.ecommerce.util.Money;

/** Holds the coupon code and makes sure no discount is ever bigger than the subtotal. */
public abstract class AbstractDiscount implements Discount {

    private final String code;

    protected AbstractDiscount(String code) {
        this.code = code.toUpperCase();
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public final double calculateDiscount(Cart cart) {
        if (!isApplicable(cart)) {
            return 0;
        }
        return Money.round(Math.min(rawDiscount(cart), cart.getSubtotal()));
    }

    /** The discount before capping it at the subtotal. */
    protected abstract double rawDiscount(Cart cart);

    @Override
    public String toString() {
        return code + " - " + getDescription();
    }
}

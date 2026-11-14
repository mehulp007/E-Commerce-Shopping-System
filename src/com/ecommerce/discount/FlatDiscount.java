package com.ecommerce.discount;

import com.ecommerce.model.Cart;
import com.ecommerce.util.Money;

/** A fixed rupee amount off, with a minimum order value. */
public class FlatDiscount extends AbstractDiscount {

    private final double amount;
    private final double minimumOrder;

    public FlatDiscount(String code, double amount, double minimumOrder) {
        super(code);
        this.amount = amount;
        this.minimumOrder = minimumOrder;
    }

    @Override
    public String getDescription() {
        return Money.format(amount) + " off on orders of " + Money.format(minimumOrder) + " or more";
    }

    @Override
    public boolean isApplicable(Cart cart) {
        return !cart.isEmpty() && cart.getSubtotal() >= minimumOrder;
    }

    @Override
    protected double rawDiscount(Cart cart) {
        return amount;
    }
}

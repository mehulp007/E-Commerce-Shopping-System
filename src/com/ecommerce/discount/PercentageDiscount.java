package com.ecommerce.discount;

import com.ecommerce.model.Cart;
import com.ecommerce.util.Money;

/** A percentage off the whole cart, with a minimum order value. */
public class PercentageDiscount extends AbstractDiscount {

    private final double percent;
    private final double minimumOrder;

    public PercentageDiscount(String code, double percent, double minimumOrder) {
        super(code);
        this.percent = percent;
        this.minimumOrder = minimumOrder;
    }

    @Override
    public String getDescription() {
        return String.format("%.0f%% off on orders of %s or more", percent, Money.format(minimumOrder));
    }

    @Override
    public boolean isApplicable(Cart cart) {
        return !cart.isEmpty() && cart.getSubtotal() >= minimumOrder;
    }

    @Override
    protected double rawDiscount(Cart cart) {
        return cart.getSubtotal() * percent / 100;
    }
}

package com.ecommerce.discount;

import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;

/** A percentage off only the items of one category. */
public class CategoryDiscount extends AbstractDiscount {

    private final String category;
    private final double percent;

    public CategoryDiscount(String code, String category, double percent) {
        super(code);
        this.category = category;
        this.percent = percent;
    }

    @Override
    public String getDescription() {
        return String.format("%.0f%% off all %s items", percent, category);
    }

    @Override
    public boolean isApplicable(Cart cart) {
        return cart.getItems().stream()
                .anyMatch(item -> item.getProduct().getCategory().equalsIgnoreCase(category));
    }

    @Override
    protected double rawDiscount(Cart cart) {
        double categoryTotal = cart.getItems().stream()
                .filter(item -> item.getProduct().getCategory().equalsIgnoreCase(category))
                .mapToDouble(CartItem::getLineTotal)
                .sum();
        return categoryTotal * percent / 100;
    }
}

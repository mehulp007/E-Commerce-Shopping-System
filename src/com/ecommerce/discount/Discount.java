package com.ecommerce.discount;

import com.ecommerce.model.Cart;

/** A coupon that can reduce the cart total. */
public interface Discount {

    String getCode();

    /** Human-readable rule, e.g. "10% off on orders of ₹1,000.00 or more". */
    String getDescription();

    boolean isApplicable(Cart cart);

    /** The amount to take off the cart subtotal (never more than the subtotal). */
    double calculateDiscount(Cart cart);
}

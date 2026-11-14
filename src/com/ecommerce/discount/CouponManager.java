package com.ecommerce.discount;

import com.ecommerce.exception.InvalidCouponException;
import com.ecommerce.model.Cart;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Holds every coupon the shop offers and checks codes typed by the customer. */
public final class CouponManager {

    private final Map<String, Discount> coupons = new LinkedHashMap<>();

    public CouponManager() {
        addCoupon(new PercentageDiscount("SAVE10", 10, 1000));
        addCoupon(new FlatDiscount("FLAT200", 200, 1500));
        addCoupon(new CategoryDiscount("ELEC15", "Electronics", 15));
        addCoupon(new CategoryDiscount("BOOKS20", "Books", 20));
    }

    public void addCoupon(Discount discount) {
        coupons.put(discount.getCode(), discount);
    }

    public Discount getApplicableDiscount(String code, Cart cart) throws InvalidCouponException {
        Discount discount = coupons.get(code.trim().toUpperCase());
        if (discount == null) {
            throw new InvalidCouponException("'" + code + "' is not a valid coupon code");
        }
        if (!discount.isApplicable(cart)) {
            throw new InvalidCouponException(
                    discount.getCode() + " can't be used on this cart. Rule: " + discount.getDescription());
        }
        return discount;
    }

    public Collection<Discount> getAllCoupons() {
        return Collections.unmodifiableCollection(coupons.values());
    }
}

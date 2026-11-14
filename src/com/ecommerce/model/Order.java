package com.ecommerce.model;

import com.ecommerce.invoice.Invoice;
import com.ecommerce.payment.PaymentReceipt;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/** A placed order. Composition: it has order items, a payment receipt and an invoice. */
public class Order {

    /** There is no admin to ship orders, so status moves forward on a short demo timer. */
    public static final Duration SHIP_AFTER = Duration.ofMinutes(2);
    public static final Duration DELIVER_AFTER = Duration.ofMinutes(5);

    private final String orderId;
    private final String customerEmail;
    private final String customerName;
    private final String shippingAddress;
    private final LocalDateTime placedAt;
    private final List<OrderItem> items;
    private final double subtotal;
    private final String couponCode;
    private final double discountAmount;
    private final double gstAmount;
    private final double grandTotal;
    private final PaymentReceipt paymentReceipt;
    private boolean cancelled;

    public Order(String orderId, String customerEmail, String customerName, String shippingAddress,
                 LocalDateTime placedAt, List<OrderItem> items, double subtotal, String couponCode,
                 double discountAmount, double gstAmount, double grandTotal,
                 PaymentReceipt paymentReceipt, boolean cancelled) {
        this.orderId = orderId;
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.shippingAddress = shippingAddress;
        this.placedAt = placedAt;
        this.items = List.copyOf(items);
        this.subtotal = subtotal;
        this.couponCode = couponCode == null ? "" : couponCode;
        this.discountAmount = discountAmount;
        this.gstAmount = gstAmount;
        this.grandTotal = grandTotal;
        this.paymentReceipt = paymentReceipt;
        this.cancelled = cancelled;
    }

    public OrderStatus getStatus() {
        if (cancelled) {
            return OrderStatus.CANCELLED;
        }
        Duration age = Duration.between(placedAt, LocalDateTime.now());
        if (age.compareTo(DELIVER_AFTER) >= 0) {
            return OrderStatus.DELIVERED;
        }
        if (age.compareTo(SHIP_AFTER) >= 0) {
            return OrderStatus.SHIPPED;
        }
        return OrderStatus.PLACED;
    }

    public void markCancelled() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public Invoice getInvoice() {
        return new Invoice(this);
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public LocalDateTime getPlacedAt() {
        return placedAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public int getTotalQuantity() {
        return items.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getTaxableAmount() {
        return subtotal - discountAmount;
    }

    public double getGstAmount() {
        return gstAmount;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public PaymentReceipt getPaymentReceipt() {
        return paymentReceipt;
    }
}

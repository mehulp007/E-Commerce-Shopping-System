package com.ecommerce.repository;

import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.payment.PaymentReceipt;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Saves one order per line. The order's items go in the last field:
 * items are separated by ';' and each item's parts by '~'.
 */
public class OrderRepository extends AbstractFileRepository<Order> {

    public OrderRepository(Path file) {
        super(file);
    }

    @Override
    protected String header() {
        return "orderId|email|name|address|placedAt|subtotal|coupon|discount|gst|grandTotal"
                + "|paymentMethod|transactionId|paymentDetails|paidNow|cancelled|items(id~name~category~price~qty;...)";
    }

    @Override
    protected Order fromFields(String[] f) {
        PaymentReceipt receipt = new PaymentReceipt(f[10], f[11], f[12],
                Double.parseDouble(f[9]), Boolean.parseBoolean(f[13]));
        return new Order(f[0], f[1], f[2], f[3], LocalDateTime.parse(f[4]), parseItems(f[15]),
                Double.parseDouble(f[5]), f[6], Double.parseDouble(f[7]), Double.parseDouble(f[8]),
                Double.parseDouble(f[9]), receipt, Boolean.parseBoolean(f[14]));
    }

    @Override
    protected String[] toFields(Order o) {
        PaymentReceipt r = o.getPaymentReceipt();
        String items = o.getItems().stream()
                .map(i -> String.join("~", i.getProductId(), clean(i.getProductName()), i.getCategory(),
                        String.valueOf(i.getUnitPrice()), String.valueOf(i.getQuantity())))
                .collect(Collectors.joining(";"));
        return new String[] {
            o.getOrderId(), o.getCustomerEmail(), o.getCustomerName(), o.getShippingAddress(),
            o.getPlacedAt().toString(), String.valueOf(o.getSubtotal()), o.getCouponCode(),
            String.valueOf(o.getDiscountAmount()), String.valueOf(o.getGstAmount()),
            String.valueOf(o.getGrandTotal()), r.getMethod(), r.getTransactionId(), r.getDetails(),
            String.valueOf(r.isPaidNow()), String.valueOf(o.isCancelled()), items
        };
    }

    private static List<OrderItem> parseItems(String field) {
        List<OrderItem> items = new ArrayList<>();
        for (String part : field.split(";")) {
            String[] p = part.split("~", -1);
            items.add(new OrderItem(p[0], p[1], p[2], Double.parseDouble(p[3]), Integer.parseInt(p[4])));
        }
        if (items.isEmpty()) {
            throw new IllegalArgumentException("order has no items");
        }
        return items;
    }

    /** Removes the characters used as item separators. */
    private static String clean(String text) {
        return text.replaceAll("[~;]", " ");
    }
}

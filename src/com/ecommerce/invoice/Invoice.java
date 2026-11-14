package com.ecommerce.invoice;

import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.payment.PaymentReceipt;
import com.ecommerce.service.OrderService;
import com.ecommerce.util.Money;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** The tax invoice for an order, printed in the terminal and saved as a text file. */
public class Invoice {

    public static final String SHOP_NAME = "ShopEasy";
    private static final int WIDTH = 66;
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH);

    private final Order order;

    public Invoice(Order order) {
        this.order = order;
    }

    public String getInvoiceNumber() {
        return "INV-" + order.getOrderId();
    }

    public String render() {
        String doubleLine = "=".repeat(WIDTH);
        String line = "-".repeat(WIDTH);
        StringBuilder sb = new StringBuilder();

        sb.append(doubleLine).append('\n');
        sb.append(center(SHOP_NAME.toUpperCase() + "  -  TAX INVOICE")).append('\n');
        sb.append(doubleLine).append('\n');
        sb.append(String.format("Invoice No : %s%n", getInvoiceNumber()));
        sb.append(String.format("Order ID   : %s%n", order.getOrderId()));
        sb.append(String.format("Date       : %s%n", order.getPlacedAt().format(DATE_FORMAT)));
        sb.append(String.format("Customer   : %s (%s)%n", order.getCustomerName(), order.getCustomerEmail()));
        sb.append(String.format("Ship To    : %s%n", order.getShippingAddress()));
        sb.append(line).append('\n');
        sb.append(String.format("%-3s %-30s %4s %12s %13s%n", "#", "Item", "Qty", "Unit Price", "Amount"));
        sb.append(line).append('\n');

        List<OrderItem> items = order.getItems();
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            sb.append(String.format("%-3d %-30s %4d %12s %13s%n", i + 1, truncate(item.getProductName(), 30),
                    item.getQuantity(), Money.format(item.getUnitPrice()), Money.format(item.getLineTotal())));
        }

        sb.append(line).append('\n');
        sb.append(totalRow("Subtotal", Money.format(order.getSubtotal())));
        if (order.getDiscountAmount() > 0) {
            sb.append(totalRow("Discount (" + order.getCouponCode() + ")", "-" + Money.format(order.getDiscountAmount())));
        }
        sb.append(totalRow("Taxable amount", Money.format(order.getTaxableAmount())));
        sb.append(totalRow(String.format("GST @ %.0f%%", OrderService.GST_RATE * 100), Money.format(order.getGstAmount())));
        sb.append(totalRow("GRAND TOTAL", Money.format(order.getGrandTotal())));
        sb.append(line).append('\n');

        PaymentReceipt receipt = order.getPaymentReceipt();
        sb.append(String.format("Payment    : %s%n", receipt.getMethod()));
        sb.append(String.format("Details    : %s%n", receipt.getDetails()));
        sb.append(String.format("Txn ID     : %s%n", receipt.getTransactionId()));
        sb.append(String.format("Status     : %s%n", receipt.isPaidNow() ? "PAID" : "TO BE PAID ON DELIVERY"));
        sb.append(doubleLine).append('\n');
        sb.append(center("Thank you for shopping with " + SHOP_NAME + "!")).append('\n');
        sb.append(center("This is a computer-generated invoice.")).append('\n');
        sb.append(doubleLine).append('\n');
        return sb.toString();
    }

    private static String totalRow(String label, String value) {
        return String.format("%49s : %14s%n", label, value);
    }

    private static String center(String text) {
        int padding = Math.max(0, (WIDTH - text.length()) / 2);
        return " ".repeat(padding) + text;
    }

    private static String truncate(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }
}

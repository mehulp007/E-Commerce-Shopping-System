package com.ecommerce.service;

import com.ecommerce.discount.Discount;
import com.ecommerce.exception.EmptyCartException;
import com.ecommerce.exception.OrderCancellationException;
import com.ecommerce.exception.OrderNotFoundException;
import com.ecommerce.exception.OutOfStockException;
import com.ecommerce.exception.PaymentFailedException;
import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Customer;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.payment.Payment;
import com.ecommerce.payment.PaymentReceipt;
import com.ecommerce.util.Money;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** Checkout and order management: billing, placing orders and cancelling them. */
public class OrderService {

    public static final double GST_RATE = 0.18;

    private final ProductCatalog catalog;
    private final List<Order> allOrders;
    private int nextOrderNumber = 1001;

    public OrderService(ProductCatalog catalog, Collection<Order> existingOrders) {
        this.catalog = catalog;
        this.allOrders = new ArrayList<>(existingOrders);
        for (Order order : existingOrders) {
            try {
                nextOrderNumber = Math.max(nextOrderNumber, Integer.parseInt(order.getOrderId().substring(3)) + 1);
            } catch (NumberFormatException | IndexOutOfBoundsException ignored) {
                // Unusual IDs don't affect numbering.
            }
        }
    }

    /** Checks the cart is not empty and every item is still in stock. */
    public void ensureCheckoutPossible(Cart cart) throws EmptyCartException, OutOfStockException {
        if (cart.isEmpty()) {
            throw new EmptyCartException("Your cart is empty. Add some products first.");
        }
        for (CartItem item : cart.getItems()) {
            if (item.getQuantity() > item.getProduct().getStock()) {
                throw new OutOfStockException(item.getProduct().getName(), item.getProduct().getStock(), item.getQuantity());
            }
        }
    }

    /** Subtotal - discount = taxable amount; GST is charged on the taxable amount. */
    public Bill calculateBill(Cart cart, Discount discount) {
        double subtotal = Money.round(cart.getSubtotal());
        double discountAmount = discount == null ? 0 : discount.calculateDiscount(cart);
        double taxable = Money.round(subtotal - discountAmount);
        double gst = Money.round(taxable * GST_RATE);
        return new Bill(subtotal, discountAmount, taxable, gst, Money.round(taxable + gst));
    }

    public Order placeOrder(Customer customer, Discount discount, Payment payment)
            throws EmptyCartException, OutOfStockException, PaymentFailedException {
        Cart cart = customer.getCart();
        ensureCheckoutPossible(cart);
        Bill bill = calculateBill(cart, discount);

        // Take payment first; stock is only reduced once payment succeeds.
        PaymentReceipt receipt = payment.pay(bill.grandTotal());

        List<OrderItem> items = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            cartItem.getProduct().reduceStock(cartItem.getQuantity());
            items.add(new OrderItem(cartItem.getProduct().getId(), cartItem.getProduct().getName(),
                    cartItem.getProduct().getCategory(), cartItem.getProduct().getPrice(), cartItem.getQuantity()));
        }

        Order order = new Order("ORD" + nextOrderNumber++, customer.getEmail(), customer.getName(),
                customer.getAddress(), LocalDateTime.now(), items, bill.subtotal(),
                bill.discount() > 0 ? discount.getCode() : "", bill.discount(), bill.gst(), bill.grandTotal(),
                receipt, false);
        allOrders.add(order);
        customer.addOrder(order);
        cart.clear();
        return order;
    }

    /** Cancels an order that hasn't shipped yet and puts its items back in stock. */
    public Order cancelOrder(Customer customer, String orderId)
            throws OrderNotFoundException, OrderCancellationException {
        Order order = findOrder(customer, orderId);
        OrderStatus status = order.getStatus();
        if (status != OrderStatus.PLACED) {
            throw new OrderCancellationException("Order " + order.getOrderId() + " is already "
                    + status.getLabel().toLowerCase() + " and can't be cancelled");
        }
        order.markCancelled();
        for (OrderItem item : order.getItems()) {
            catalog.findOptional(item.getProductId()).ifPresent(p -> p.restock(item.getQuantity()));
        }
        return order;
    }

    public Order findOrder(Customer customer, String orderId) throws OrderNotFoundException {
        return customer.getOrders().stream()
                .filter(o -> o.getOrderId().equalsIgnoreCase(orderId.trim()))
                .findFirst()
                .orElseThrow(() -> new OrderNotFoundException("You have no order with ID '" + orderId + "'"));
    }

    public List<Order> getAllOrders() {
        return Collections.unmodifiableList(allOrders);
    }
}

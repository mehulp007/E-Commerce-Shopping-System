package com.ecommerce.model;

import com.ecommerce.exception.InvalidQuantityException;
import com.ecommerce.exception.OutOfStockException;
import com.ecommerce.exception.ProductNotFoundException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Shopping cart. Keyed by product ID; LinkedHashMap keeps items in the order they were added. */
public class Cart {

    public static final int MAX_QUANTITY_PER_ITEM = 10;

    private final Map<String, CartItem> items = new LinkedHashMap<>();

    public void addItem(Product product, int quantity) throws InvalidQuantityException, OutOfStockException {
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be at least 1");
        }
        CartItem existing = items.get(product.getId());
        int newQuantity = quantity + (existing == null ? 0 : existing.getQuantity());
        validate(product, newQuantity);
        if (existing == null) {
            items.put(product.getId(), new CartItem(product, quantity));
        } else {
            existing.setQuantity(newQuantity);
        }
    }

    /** Sets a new quantity. A quantity of 0 removes the item. */
    public void updateQuantity(String productId, int quantity)
            throws ProductNotFoundException, InvalidQuantityException, OutOfStockException {
        CartItem item = getItem(productId);
        if (quantity < 0) {
            throw new InvalidQuantityException("Quantity cannot be negative");
        }
        if (quantity == 0) {
            items.remove(item.getProduct().getId());
            return;
        }
        validate(item.getProduct(), quantity);
        item.setQuantity(quantity);
    }

    public void removeItem(String productId) throws ProductNotFoundException {
        items.remove(getItem(productId).getProduct().getId());
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public Collection<CartItem> getItems() {
        return Collections.unmodifiableCollection(items.values());
    }

    public int getTotalQuantity() {
        return items.values().stream().mapToInt(CartItem::getQuantity).sum();
    }

    public double getSubtotal() {
        return items.values().stream().mapToDouble(CartItem::getLineTotal).sum();
    }

    private CartItem getItem(String productId) throws ProductNotFoundException {
        CartItem item = items.get(productId.toUpperCase());
        if (item == null) {
            throw new ProductNotFoundException("Product '" + productId + "' is not in your cart");
        }
        return item;
    }

    private void validate(Product product, int quantity) throws InvalidQuantityException, OutOfStockException {
        if (quantity > MAX_QUANTITY_PER_ITEM) {
            throw new InvalidQuantityException(
                    "You can buy at most " + MAX_QUANTITY_PER_ITEM + " units of each product");
        }
        if (quantity > product.getStock()) {
            throw new OutOfStockException(product.getName(), product.getStock(), quantity);
        }
    }
}

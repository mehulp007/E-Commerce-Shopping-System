package com.ecommerce.model;

/**
 * A product line frozen at the moment the order was placed,
 * so later price or name changes don't alter past orders.
 */
public class OrderItem {

    private final String productId;
    private final String productName;
    private final String category;
    private final double unitPrice;
    private final int quantity;

    public OrderItem(String productId, String productName, String category, double unitPrice, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategory() {
        return category;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getLineTotal() {
        return unitPrice * quantity;
    }
}

package com.ecommerce.model;

/** One line in the cart: a product and how many of it. */
public class CartItem {

    private final Product product;
    private int quantity;

    CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getLineTotal() {
        return product.getPrice() * quantity;
    }
}

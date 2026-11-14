package com.ecommerce.model;

import com.ecommerce.exception.OutOfStockException;
import com.ecommerce.util.Money;
import java.util.Locale;
import java.util.Objects;

/**
 * Base class for everything the shop sells.
 * Each category (Electronics, Clothing, Book, Grocery) extends it and adds its own fields.
 */
public abstract class Product implements Comparable<Product> {

    private final String id;
    private final String name;
    private final double price;
    private int stock;
    private final double rating;

    protected Product(String id, String name, double price, int stock, double rating) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        this.id = Objects.requireNonNull(id).toUpperCase();
        this.name = Objects.requireNonNull(name);
        this.price = price;
        this.stock = stock;
        this.rating = rating;
    }

    /** The category name shown to customers and used when saving to file. */
    public abstract String getCategory();

    /** Category-specific lines for the details screen, e.g. warranty or author. */
    protected abstract String getSpecificDetails();

    /** Category-specific fields in the order the repository saves them. */
    public abstract String[] getExtraFields();

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public double getRating() {
        return rating;
    }

    public boolean isInStock() {
        return stock > 0;
    }

    public void reduceStock(int quantity) throws OutOfStockException {
        if (quantity > stock) {
            throw new OutOfStockException(name, stock, quantity);
        }
        stock -= quantity;
    }

    public void restock(int quantity) {
        if (quantity > 0) {
            stock += quantity;
        }
    }

    public String getDetails() {
        return String.format(Locale.ENGLISH,
                "%s  [%s]%n  Category : %s%n  Price    : %s%n  Stock    : %s%n  Rating   : %.1f / 5%n%s",
                name, id, getCategory(), Money.format(price),
                isInStock() ? stock + " available" : "Out of stock",
                rating, getSpecificDetails());
    }

    @Override
    public int compareTo(Product other) {
        return name.compareToIgnoreCase(other.name);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Product other && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id + " - " + name + " (" + Money.format(price) + ")";
    }
}

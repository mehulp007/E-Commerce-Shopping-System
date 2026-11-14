package com.ecommerce.service;

import com.ecommerce.model.Product;
import java.util.Comparator;

/** The ways a product list can be sorted, each with its own Comparator. */
public enum SortOption {
    PRICE_LOW_TO_HIGH("Price: low to high", Comparator.comparingDouble(Product::getPrice)),
    PRICE_HIGH_TO_LOW("Price: high to low", Comparator.comparingDouble(Product::getPrice).reversed()),
    RATING("Rating: highest first", Comparator.comparingDouble(Product::getRating).reversed()),
    NAME("Name: A to Z", Comparator.naturalOrder());

    private final String label;
    private final Comparator<Product> comparator;

    SortOption(String label, Comparator<Product> comparator) {
        this.label = label;
        this.comparator = comparator;
    }

    public String getLabel() {
        return label;
    }

    public Comparator<Product> getComparator() {
        return comparator;
    }
}

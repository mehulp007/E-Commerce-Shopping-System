package com.ecommerce.model;

import com.ecommerce.exception.ProductNotFoundException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/** Products saved for later. A Set stops the same product being added twice. */
public class Wishlist {

    private final Set<Product> products = new LinkedHashSet<>();

    /** Returns false if the product was already in the wishlist. */
    public boolean add(Product product) {
        return products.add(product);
    }

    public void remove(Product product) throws ProductNotFoundException {
        if (!products.remove(product)) {
            throw new ProductNotFoundException("'" + product.getName() + "' is not in your wishlist");
        }
    }

    public boolean contains(Product product) {
        return products.contains(product);
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }

    public int size() {
        return products.size();
    }

    public Set<Product> getProducts() {
        return Collections.unmodifiableSet(products);
    }
}

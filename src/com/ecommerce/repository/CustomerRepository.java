package com.ecommerce.repository;

import com.ecommerce.model.Customer;
import com.ecommerce.model.Product;
import com.ecommerce.service.ProductCatalog;
import java.nio.file.Path;
import java.util.stream.Collectors;

/** Saves customers and their wishlists. Wishlists are stored as product IDs separated by ';'. */
public class CustomerRepository extends AbstractFileRepository<Customer> {

    private final ProductCatalog catalog;

    public CustomerRepository(Path file, ProductCatalog catalog) {
        super(file);
        this.catalog = catalog;
    }

    @Override
    protected String header() {
        return "id|name|email|address|wishlistProductIds";
    }

    @Override
    protected Customer fromFields(String[] f) {
        Customer customer = new Customer(f[0], f[1], f[2], f[3]);
        if (!f[4].isBlank()) {
            for (String productId : f[4].split(";")) {
                // Products that no longer exist are simply dropped from the wishlist.
                catalog.findOptional(productId).ifPresent(customer.getWishlist()::add);
            }
        }
        return customer;
    }

    @Override
    protected String[] toFields(Customer c) {
        String wishlist = c.getWishlist().getProducts().stream()
                .map(Product::getId)
                .collect(Collectors.joining(";"));
        return new String[] {c.getId(), c.getName(), c.getEmail(), c.getAddress(), wishlist};
    }
}

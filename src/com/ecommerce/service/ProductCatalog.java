package com.ecommerce.service;

import com.ecommerce.exception.ProductNotFoundException;
import com.ecommerce.model.Product;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/** All products in the shop, with lookup, search, filter and sort. */
public class ProductCatalog {

    private final Map<String, Product> productsById = new LinkedHashMap<>();

    public ProductCatalog(Collection<Product> products) {
        for (Product product : products) {
            productsById.put(product.getId(), product);
        }
    }

    public Product findById(String id) throws ProductNotFoundException {
        return findOptional(id).orElseThrow(
                () -> new ProductNotFoundException("No product found with ID '" + id + "'"));
    }

    public Optional<Product> findOptional(String id) {
        return Optional.ofNullable(productsById.get(id.trim().toUpperCase()));
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(productsById.values());
    }

    /** Category names in alphabetical order (TreeSet keeps them sorted). */
    public Set<String> getCategories() {
        return productsById.values().stream()
                .map(Product::getCategory)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    /** Products grouped by category; TreeMap sorts the categories alphabetically. */
    public Map<String, List<Product>> groupByCategory() {
        return productsById.values().stream()
                .sorted()
                .collect(Collectors.groupingBy(Product::getCategory, TreeMap::new, Collectors.toList()));
    }

    public List<Product> searchByName(String keyword) {
        String lower = keyword.trim().toLowerCase();
        return productsById.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    public List<Product> filterByCategory(String category) {
        return productsById.values().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Product> filterByPriceRange(double min, double max) {
        return productsById.values().stream()
                .filter(p -> p.getPrice() >= min && p.getPrice() <= max)
                .collect(Collectors.toList());
    }

    public List<Product> sort(List<Product> products, SortOption option) {
        List<Product> sorted = new ArrayList<>(products);
        sorted.sort(option.getComparator());
        return sorted;
    }
}

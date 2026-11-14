package com.ecommerce.repository;

import com.ecommerce.model.Book;
import com.ecommerce.model.Clothing;
import com.ecommerce.model.Electronics;
import com.ecommerce.model.Grocery;
import com.ecommerce.model.Product;
import java.nio.file.Path;
import java.time.LocalDate;

public class ProductRepository extends AbstractFileRepository<Product> {

    public ProductRepository(Path file) {
        super(file);
    }

    @Override
    protected String header() {
        return "category|id|name|price|stock|rating|extra1|extra2";
    }

    @Override
    protected Product fromFields(String[] f) {
        String category = f[0];
        String id = f[1];
        String name = f[2];
        double price = Double.parseDouble(f[3]);
        int stock = Integer.parseInt(f[4]);
        double rating = Double.parseDouble(f[5]);
        return switch (category) {
            case "Electronics" -> new Electronics(id, name, price, stock, rating, f[6], Integer.parseInt(f[7]));
            case "Clothing" -> new Clothing(id, name, price, stock, rating, f[6], f[7]);
            case "Books" -> new Book(id, name, price, stock, rating, f[6], Integer.parseInt(f[7]));
            case "Grocery" -> new Grocery(id, name, price, stock, rating, Integer.parseInt(f[6]), LocalDate.parse(f[7]));
            default -> throw new IllegalArgumentException("unknown category '" + category + "'");
        };
    }

    @Override
    protected String[] toFields(Product p) {
        String[] extra = p.getExtraFields();
        return new String[] {
            p.getCategory(), p.getId(), p.getName(), String.valueOf(p.getPrice()),
            String.valueOf(p.getStock()), String.valueOf(p.getRating()), extra[0], extra[1]
        };
    }
}

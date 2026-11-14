package com.ecommerce.repository;

import com.ecommerce.model.Book;
import com.ecommerce.model.Clothing;
import com.ecommerce.model.Electronics;
import com.ecommerce.model.Grocery;
import com.ecommerce.model.Product;
import java.time.LocalDate;
import java.util.List;

/** Sample catalog used the first time the app runs (when data/products.csv doesn't exist yet). */
public final class DataSeeder {

    private DataSeeder() {
    }

    public static List<Product> sampleProducts() {
        return List.of(
                // id, name, price, stock, rating, brand, warranty (months)
                new Electronics("E101", "Apple iPhone 17 (256 GB)", 82900, 18, 4.7, "Apple", 12),
                new Electronics("E102", "Samsung Galaxy S25 (256 GB)", 80999, 22, 4.5, "Samsung", 12),
                new Electronics("E103", "OnePlus 13 (12 GB / 256 GB)", 69999, 25, 4.4, "OnePlus", 12),
                new Electronics("E104", "MacBook Air M4 13-inch", 99900, 4, 4.8, "Apple", 12),
                new Electronics("E105", "Sony WH-1000XM5 Headphones", 29990, 0, 4.6, "Sony", 12),
                new Electronics("E106", "Apple AirPods Pro 2", 24900, 30, 4.7, "Apple", 12),
                new Electronics("E107", "Kindle Paperwhite (16 GB)", 16999, 40, 4.6, "Amazon", 12),
                new Electronics("E108", "Logitech MX Master 3S Mouse", 9995, 35, 4.7, "Logitech", 12),

                // id, name, price, stock, rating, size, material
                new Clothing("C201", "Levi's 511 Slim Fit Jeans", 3299, 40, 4.4, "32", "Stretch Denim"),
                new Clothing("C202", "Uniqlo Supima Cotton T-Shirt", 1290, 60, 4.5, "M", "Supima Cotton"),
                new Clothing("C203", "Allen Solly Oxford Shirt", 1799, 35, 4.2, "L", "Cotton"),
                new Clothing("C204", "Nike Dri-FIT Running Tee", 1995, 45, 4.3, "M", "Recycled Polyester"),
                new Clothing("C205", "H&M Relaxed Fit Hoodie", 2299, 30, 4.1, "L", "Cotton Blend"),
                new Clothing("C206", "Tommy Hilfiger Pique Polo", 4499, 20, 4.5, "M", "Pique Cotton"),
                new Clothing("C207", "The North Face Puffer Jacket", 15999, 3, 4.7, "L", "Recycled Nylon"),
                new Clothing("C208", "Manyavar Silk Blend Kurta", 3499, 15, 4.4, "L", "Silk Blend"),

                // id, name, price, stock, rating, author, pages
                new Book("B301", "Clean Code", 599, 50, 4.7, "Robert C. Martin", 464),
                new Book("B302", "Effective Java (3rd Edition)", 899, 30, 4.8, "Joshua Bloch", 412),
                new Book("B303", "Head First Java (3rd Edition)", 1250, 25, 4.6,
                        "Kathy Sierra, Bert Bates & Trisha Gee", 754),
                new Book("B304", "Design Patterns", 799, 20, 4.5,
                        "Gamma, Helm, Johnson & Vlissides", 395),
                new Book("B305", "The Pragmatic Programmer", 849, 28, 4.7, "David Thomas & Andrew Hunt", 352),
                new Book("B306", "Introduction to Algorithms", 1495, 12, 4.6,
                        "Cormen, Leiserson, Rivest & Stein", 1312),
                new Book("B307", "Atomic Habits", 499, 80, 4.8, "James Clear", 320),
                new Book("B308", "Deep Work", 399, 45, 4.5, "Cal Newport", 296),

                // id, name, price, stock, rating, weight (grams), best before
                new Grocery("G401", "Blue Tokai Coffee Beans 250g", 595, 40, 4.6, 250, LocalDate.of(2027, 4, 30)),
                new Grocery("G402", "Lindt 70% Dark Chocolate 100g", 350, 60, 4.7, 100, LocalDate.of(2027, 6, 30)),
                new Grocery("G403", "Pintola Peanut Butter 1kg", 449, 50, 4.4, 1000, LocalDate.of(2027, 5, 31)),
                new Grocery("G404", "Yogabar Fruit & Nut Muesli", 475, 35, 4.3, 700, LocalDate.of(2027, 3, 31)),
                new Grocery("G405", "Daawat Basmati Rice 5kg", 899, 30, 4.5, 5000, LocalDate.of(2028, 1, 31)),
                new Grocery("G406", "Figaro Olive Oil 1L", 1099, 25, 4.4, 1000, LocalDate.of(2027, 11, 30)),
                new Grocery("G407", "Happilo California Almonds", 599, 45, 4.5, 500, LocalDate.of(2027, 2, 28)),
                new Grocery("G408", "Tetley Green Tea (100 Bags)", 550, 55, 4.3, 150, LocalDate.of(2027, 9, 30)));
    }
}

package com.ecommerce.repository;

import com.ecommerce.exception.DataStoreException;
import com.ecommerce.model.Customer;
import com.ecommerce.model.Order;
import com.ecommerce.model.Product;
import com.ecommerce.service.CustomerService;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductCatalog;
import java.nio.file.Path;
import java.util.List;

/** Loads and saves all shop data from the data/ folder. */
public class DataStore {

    private final Path dataFolder;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private CustomerRepository customerRepository;

    public DataStore(Path dataFolder) {
        this.dataFolder = dataFolder;
        this.productRepository = new ProductRepository(dataFolder.resolve("products.csv"));
        this.orderRepository = new OrderRepository(dataFolder.resolve("orders.csv"));
    }

    /** Loads products, or fills the catalog with sample products on the first run. */
    public ProductCatalog loadCatalog() throws DataStoreException {
        List<Product> products = productRepository.loadAll();
        if (products.isEmpty()) {
            products = DataSeeder.sampleProducts();
            productRepository.saveAll(products);
            System.out.println("First run: added " + products.size() + " sample products to the catalog.");
        }
        return new ProductCatalog(products);
    }

    /** Must be called after loadCatalog(), because wishlists refer to products. */
    public List<Customer> loadCustomers(ProductCatalog catalog) throws DataStoreException {
        customerRepository = new CustomerRepository(dataFolder.resolve("customers.csv"), catalog);
        return customerRepository.loadAll();
    }

    public List<Order> loadOrders() throws DataStoreException {
        return orderRepository.loadAll();
    }

    public void saveAll(ProductCatalog catalog, CustomerService customers, OrderService orders)
            throws DataStoreException {
        if (customerRepository == null) {
            throw new IllegalStateException("loadCustomers() must be called before saveAll()");
        }
        productRepository.saveAll(catalog.getAllProducts());
        customerRepository.saveAll(customers.getAllCustomers());
        orderRepository.saveAll(orders.getAllOrders());
    }
}

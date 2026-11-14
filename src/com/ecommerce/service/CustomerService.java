package com.ecommerce.service;

import com.ecommerce.exception.ValidationException;
import com.ecommerce.model.Customer;
import com.ecommerce.model.Order;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Finds and registers customers. Customers are identified by email (no passwords). */
public class CustomerService {

    private static final String EMAIL_PATTERN = "[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+";

    private final Map<String, Customer> customersByEmail = new LinkedHashMap<>();
    private int nextIdNumber = 1001;

    public CustomerService(Collection<Customer> customers) {
        for (Customer customer : customers) {
            customersByEmail.put(customer.getEmail(), customer);
            nextIdNumber = Math.max(nextIdNumber, parseIdNumber(customer.getId()) + 1);
        }
    }

    public Optional<Customer> findByEmail(String email) {
        return Optional.ofNullable(customersByEmail.get(email.trim().toLowerCase()));
    }

    public void validateEmail(String email) throws ValidationException {
        if (!email.trim().matches(EMAIL_PATTERN)) {
            throw new ValidationException("'" + email + "' is not a valid email address (example: name@gmail.com)");
        }
    }

    public Customer register(String name, String email, String address) throws ValidationException {
        validateEmail(email);
        if (name.isBlank()) {
            throw new ValidationException("Name cannot be empty");
        }
        if (address.isBlank()) {
            throw new ValidationException("Address cannot be empty");
        }
        if (findByEmail(email).isPresent()) {
            throw new ValidationException("An account with " + email + " already exists");
        }
        Customer customer = new Customer("C" + nextIdNumber++, name.trim(), email.trim(), address.trim());
        customersByEmail.put(customer.getEmail(), customer);
        return customer;
    }

    /** Links loaded orders back to their customers, oldest first. */
    public void attachOrders(Collection<Order> orders) {
        orders.stream()
                .sorted(Comparator.comparing(Order::getPlacedAt))
                .forEach(order -> findByEmail(order.getCustomerEmail()).ifPresent(c -> c.addOrder(order)));
    }

    public Collection<Customer> getAllCustomers() {
        return Collections.unmodifiableCollection(customersByEmail.values());
    }

    private static int parseIdNumber(String id) {
        try {
            return Integer.parseInt(id.substring(1));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return 0;
        }
    }
}

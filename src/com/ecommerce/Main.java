package com.ecommerce;

import com.ecommerce.discount.CouponManager;
import com.ecommerce.exception.DataStoreException;
import com.ecommerce.invoice.InvoiceWriter;
import com.ecommerce.repository.DataStore;
import com.ecommerce.service.CustomerService;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductCatalog;
import com.ecommerce.ui.ConsoleMenu;
import com.ecommerce.ui.InputHelper;
import java.nio.file.Path;
import java.util.Scanner;

/** Entry point: loads saved data, wires the objects together and starts the menu. */
public class Main {

    public static void main(String[] args) {
        DataStore dataStore = new DataStore(Path.of("data"));
        try (Scanner scanner = new Scanner(System.in)) {
            ProductCatalog catalog = dataStore.loadCatalog();
            CustomerService customerService = new CustomerService(dataStore.loadCustomers(catalog));
            OrderService orderService = new OrderService(catalog, dataStore.loadOrders());
            customerService.attachOrders(orderService.getAllOrders());

            ConsoleMenu menu = new ConsoleMenu(catalog, customerService, orderService, new CouponManager(),
                    dataStore, new InvoiceWriter(Path.of("invoices")), new InputHelper(scanner));
            menu.start();
        } catch (DataStoreException e) {
            System.out.println("Could not start the shop: " + e.getMessage());
            System.out.println("Cause: " + e.getCause());
        }
    }
}

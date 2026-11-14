package com.ecommerce.ui;

import com.ecommerce.discount.CouponManager;
import com.ecommerce.discount.Discount;
import com.ecommerce.exception.DataStoreException;
import com.ecommerce.exception.EmptyCartException;
import com.ecommerce.exception.InvalidCouponException;
import com.ecommerce.exception.OutOfStockException;
import com.ecommerce.exception.PaymentFailedException;
import com.ecommerce.exception.ShopException;
import com.ecommerce.exception.ValidationException;
import com.ecommerce.invoice.Invoice;
import com.ecommerce.invoice.InvoiceWriter;
import com.ecommerce.model.Cart;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Customer;
import com.ecommerce.model.Order;
import com.ecommerce.model.Product;
import com.ecommerce.model.Wishlist;
import com.ecommerce.payment.CardPayment;
import com.ecommerce.payment.CashOnDelivery;
import com.ecommerce.payment.NetBankingPayment;
import com.ecommerce.payment.Payment;
import com.ecommerce.payment.UpiPayment;
import com.ecommerce.repository.DataStore;
import com.ecommerce.service.Bill;
import com.ecommerce.service.CustomerService;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductCatalog;
import com.ecommerce.service.SortOption;
import com.ecommerce.util.Money;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/** The terminal menus. All user interaction happens here; the business rules live in the services. */
public class ConsoleMenu {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH);
    private static final String PRODUCT_ROW = "%-6s %-34s %13s  %-12s %6s%n";

    private final ProductCatalog catalog;
    private final CustomerService customerService;
    private final OrderService orderService;
    private final CouponManager couponManager;
    private final DataStore dataStore;
    private final InvoiceWriter invoiceWriter;
    private final InputHelper in;
    private Customer customer;

    public ConsoleMenu(ProductCatalog catalog, CustomerService customerService, OrderService orderService,
                       CouponManager couponManager, DataStore dataStore, InvoiceWriter invoiceWriter,
                       InputHelper in) {
        this.catalog = catalog;
        this.customerService = customerService;
        this.orderService = orderService;
        this.couponManager = couponManager;
        this.dataStore = dataStore;
        this.invoiceWriter = invoiceWriter;
        this.in = in;
    }

    public void start() {
        printBanner();
        try {
            customer = loginOrRegister();
            mainLoop();
        } catch (NoSuchElementException e) {
            // Input was closed (Ctrl+Z / Ctrl+D or end of a piped file).
            System.out.println();
        } finally {
            save();
            System.out.println("\nYour data has been saved. Thank you for shopping with " + Invoice.SHOP_NAME + "!");
        }
    }

    // ------------------------------------------------------------------ login

    private Customer loginOrRegister() {
        String email;
        while (true) {
            email = in.readNonEmpty("Enter your email to log in or sign up: ");
            try {
                customerService.validateEmail(email);
                break;
            } catch (ValidationException e) {
                printError(e.getMessage());
            }
        }

        Optional<Customer> existing = customerService.findByEmail(email);
        if (existing.isPresent()) {
            Customer found = existing.get();
            printSuccess("Welcome back, " + found.getName() + "!");
            return found;
        }

        System.out.println("\nLooks like you're new here. Let's create your account.");
        while (true) {
            String name = in.readNonEmpty("Full name        : ");
            String address = in.readNonEmpty("Delivery address : ");
            try {
                Customer created = customerService.register(name, email, address);
                save();
                printSuccess("Account created. Welcome, " + created.getName() + "!");
                return created;
            } catch (ValidationException e) {
                printError(e.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------ main menu

    private void mainLoop() {
        while (true) {
            printHeader("MAIN MENU");
            System.out.printf("Logged in as %s  |  Cart: %d item(s), %s  |  Wishlist: %d%n%n",
                    customer.getName(), customer.getCart().getTotalQuantity(),
                    Money.format(customer.getCart().getSubtotal()), customer.getWishlist().size());
            System.out.println(" 1. Browse all products");
            System.out.println(" 2. Search, filter & sort");
            System.out.println(" 3. View product details");
            System.out.println(" 4. Add product to cart");
            System.out.println(" 5. View cart");
            System.out.println(" 6. Wishlist");
            System.out.println(" 7. Checkout");
            System.out.println(" 8. My orders");
            System.out.println(" 9. Available coupons");
            System.out.println(" 0. Save & exit");

            int choice = in.readInt("\nChoose an option: ", 0, 9);
            switch (choice) {
                case 1 -> browseProducts();
                case 2 -> searchAndSort();
                case 3 -> viewProductDetails();
                case 4 -> addToCart();
                case 5 -> cartMenu();
                case 6 -> wishlistMenu();
                case 7 -> checkout();
                case 8 -> ordersMenu();
                case 9 -> showCoupons();
                default -> {
                    return;
                }
            }
        }
    }

    // ------------------------------------------------------------------ browsing

    private void browseProducts() {
        printHeader("ALL PRODUCTS");
        for (Map.Entry<String, List<Product>> entry : catalog.groupByCategory().entrySet()) {
            System.out.println("\n>> " + entry.getKey().toUpperCase());
            printProductTable(entry.getValue());
        }
        in.pause();
    }

    private void searchAndSort() {
        printHeader("SEARCH, FILTER & SORT");
        System.out.println(" 1. Search by name");
        System.out.println(" 2. Filter by category");
        System.out.println(" 3. Filter by price range");
        System.out.println(" 4. Sort all products");
        System.out.println(" 0. Back");
        int choice = in.readInt("\nChoose an option: ", 0, 4);

        List<Product> results;
        switch (choice) {
            case 1 -> results = catalog.searchByName(in.readNonEmpty("Search for: "));
            case 2 -> {
                List<String> categories = new ArrayList<>(catalog.getCategories());
                for (int i = 0; i < categories.size(); i++) {
                    System.out.printf(" %d. %s%n", i + 1, categories.get(i));
                }
                int index = in.readInt("Choose a category: ", 1, categories.size());
                results = catalog.filterByCategory(categories.get(index - 1));
            }
            case 3 -> {
                double min = in.readDouble("Minimum price (₹): ", 0);
                double max = in.readDouble("Maximum price (₹): ", 0);
                if (min > max) {
                    double temp = min;
                    min = max;
                    max = temp;
                }
                results = catalog.filterByPriceRange(min, max);
            }
            case 4 -> results = catalog.getAllProducts();
            default -> {
                return;
            }
        }

        if (results.isEmpty()) {
            printError("No products matched.");
            in.pause();
            return;
        }
        SortOption sort = chooseSortOption();
        if (sort != null) {
            results = catalog.sort(results, sort);
        }
        System.out.println("\n" + results.size() + " product(s) found"
                + (sort == null ? "" : ", sorted by " + sort.getLabel()) + ":");
        printProductTable(results);
        in.pause();
    }

    private SortOption chooseSortOption() {
        System.out.println("\nSort the results by:");
        SortOption[] options = SortOption.values();
        for (int i = 0; i < options.length; i++) {
            System.out.printf(" %d. %s%n", i + 1, options[i].getLabel());
        }
        System.out.println(" 0. Don't sort");
        int choice = in.readInt("Choose an option: ", 0, options.length);
        return choice == 0 ? null : options[choice - 1];
    }

    private void viewProductDetails() {
        try {
            Product product = catalog.findById(in.readNonEmpty("Enter product ID (e.g. E101): "));
            printHeader("PRODUCT DETAILS");
            System.out.print(product.getDetails());
            System.out.println("\n 1. Add to cart   2. Add to wishlist   0. Back");
            int choice = in.readInt("Choose an option: ", 0, 2);
            if (choice == 1) {
                addProductToCart(product);
            } else if (choice == 2) {
                addProductToWishlist(product);
            }
        } catch (ShopException e) {
            printError(e.getMessage());
        }
    }

    // ------------------------------------------------------------------ cart

    private void addToCart() {
        try {
            Product product = catalog.findById(in.readNonEmpty("Enter product ID (e.g. E101): "));
            addProductToCart(product);
        } catch (ShopException e) {
            printError(e.getMessage());
        }
    }

    private void addProductToCart(Product product) throws ShopException {
        int quantity = in.readInt("Quantity for '" + product.getName() + "': ");
        customer.getCart().addItem(product, quantity);
        printSuccess(quantity + " x " + product.getName() + " added to your cart.");
    }

    private void cartMenu() {
        while (true) {
            printHeader("YOUR CART");
            Cart cart = customer.getCart();
            if (cart.isEmpty()) {
                System.out.println("Your cart is empty.");
                in.pause();
                return;
            }
            printCart(cart);
            System.out.println("\n 1. Change quantity   2. Remove item   3. Clear cart   4. Checkout   0. Back");
            int choice = in.readInt("Choose an option: ", 0, 4);
            try {
                switch (choice) {
                    case 1 -> {
                        String id = in.readNonEmpty("Product ID: ");
                        int quantity = in.readInt("New quantity (0 removes it): ");
                        cart.updateQuantity(id, quantity);
                        printSuccess("Cart updated.");
                    }
                    case 2 -> {
                        cart.removeItem(in.readNonEmpty("Product ID: "));
                        printSuccess("Item removed.");
                    }
                    case 3 -> {
                        if (in.confirm("Remove everything from your cart?")) {
                            cart.clear();
                            printSuccess("Cart cleared.");
                        }
                    }
                    case 4 -> {
                        checkout();
                        return;
                    }
                    default -> {
                        return;
                    }
                }
            } catch (ShopException e) {
                printError(e.getMessage());
            }
        }
    }

    private void printCart(Cart cart) {
        System.out.printf("%-6s %-34s %5s %13s %14s%n", "ID", "Product", "Qty", "Unit Price", "Total");
        System.out.println("-".repeat(76));
        for (CartItem item : cart.getItems()) {
            Product p = item.getProduct();
            System.out.printf("%-6s %-34s %5d %13s %14s%n", p.getId(), truncate(p.getName(), 34),
                    item.getQuantity(), Money.format(p.getPrice()), Money.format(item.getLineTotal()));
        }
        System.out.println("-".repeat(76));
        System.out.printf("%61s %14s%n", "Subtotal:", Money.format(cart.getSubtotal()));
    }

    // ------------------------------------------------------------------ wishlist

    private void wishlistMenu() {
        while (true) {
            printHeader("YOUR WISHLIST");
            Wishlist wishlist = customer.getWishlist();
            if (wishlist.isEmpty()) {
                System.out.println("Your wishlist is empty.");
            } else {
                printProductTable(wishlist.getProducts());
            }
            System.out.println("\n 1. Add product   2. Remove product   3. Move product to cart   0. Back");
            int choice = in.readInt("Choose an option: ", 0, 3);
            if (choice == 0) {
                return;
            }
            try {
                Product product = catalog.findById(in.readNonEmpty("Product ID: "));
                switch (choice) {
                    case 1 -> addProductToWishlist(product);
                    case 2 -> {
                        wishlist.remove(product);
                        save();
                        printSuccess("'" + product.getName() + "' removed from your wishlist.");
                    }
                    default -> {
                        if (!wishlist.contains(product)) {
                            printError("'" + product.getName() + "' is not in your wishlist.");
                            continue;
                        }
                        customer.getCart().addItem(product, 1);
                        wishlist.remove(product);
                        save();
                        printSuccess("'" + product.getName() + "' moved to your cart.");
                    }
                }
            } catch (ShopException e) {
                printError(e.getMessage());
            }
        }
    }

    private void addProductToWishlist(Product product) {
        if (customer.getWishlist().add(product)) {
            save();
            printSuccess("'" + product.getName() + "' added to your wishlist.");
        } else {
            printError("'" + product.getName() + "' is already in your wishlist.");
        }
    }

    // ------------------------------------------------------------------ checkout

    private void checkout() {
        printHeader("CHECKOUT");
        Cart cart = customer.getCart();
        try {
            orderService.ensureCheckoutPossible(cart);
        } catch (EmptyCartException | OutOfStockException e) {
            printError(e.getMessage());
            return;
        }

        printCart(cart);
        Discount discount = askForCoupon(cart);
        Bill bill = orderService.calculateBill(cart, discount);
        printBill(bill, discount);

        if (!in.confirm("\nProceed to payment?")) {
            System.out.println("Checkout cancelled. Your cart has not been changed.");
            return;
        }

        while (true) {
            Payment payment = choosePayment();
            if (payment == null) {
                System.out.println("Checkout cancelled. Your cart has not been changed.");
                return;
            }
            try {
                System.out.println("\nProcessing payment of " + Money.format(bill.grandTotal()) + "...");
                Order order = orderService.placeOrder(customer, discount, payment);
                printSuccess("Order " + order.getOrderId() + " placed successfully!\n");
                Invoice invoice = order.getInvoice();
                System.out.print(invoice.render());
                saveInvoice(invoice);
                save();
                in.pause();
                return;
            } catch (PaymentFailedException e) {
                printError("Payment failed: " + e.getMessage());
                if (!in.confirm("Try again?")) {
                    System.out.println("Checkout cancelled. Your cart has not been changed.");
                    return;
                }
            } catch (EmptyCartException | OutOfStockException e) {
                printError(e.getMessage());
                return;
            }
        }
    }

    private Discount askForCoupon(Cart cart) {
        System.out.println("\nAvailable coupons:");
        for (Discount d : couponManager.getAllCoupons()) {
            System.out.printf("  %-8s %s%n", d.getCode(), d.getDescription());
        }
        while (true) {
            String code = in.readLine("\nEnter a coupon code (or press Enter to skip): ");
            if (code.isEmpty()) {
                return null;
            }
            try {
                Discount discount = couponManager.getApplicableDiscount(code, cart);
                printSuccess("Coupon " + discount.getCode() + " applied: you save "
                        + Money.format(discount.calculateDiscount(cart)));
                return discount;
            } catch (InvalidCouponException e) {
                printError(e.getMessage());
            }
        }
    }

    private void printBill(Bill bill, Discount discount) {
        System.out.println("\n----------------- BILL SUMMARY -----------------");
        System.out.printf("%-30s %17s%n", "Subtotal", Money.format(bill.subtotal()));
        if (bill.discount() > 0) {
            System.out.printf("%-30s %17s%n", "Discount (" + discount.getCode() + ")", "-" + Money.format(bill.discount()));
        }
        System.out.printf("%-30s %17s%n", "Taxable amount", Money.format(bill.taxableAmount()));
        System.out.printf("%-30s %17s%n", String.format("GST @ %.0f%%", OrderService.GST_RATE * 100), Money.format(bill.gst()));
        System.out.println("-".repeat(48));
        System.out.printf("%-30s %17s%n", "GRAND TOTAL", Money.format(bill.grandTotal()));
    }

    /** Returns the chosen payment method, or null if the customer cancels. */
    private Payment choosePayment() {
        System.out.println("\nChoose a payment method (payments are simulated, no real money is charged):");
        System.out.println(" 1. Credit / Debit card");
        System.out.println(" 2. UPI");
        System.out.println(" 3. Net banking");
        System.out.println(" 4. Cash on delivery");
        System.out.println(" 0. Cancel checkout");
        int choice = in.readInt("Choose an option: ", 0, 4);
        return switch (choice) {
            case 1 -> {
                System.out.println("(Test card that passes the check: 4111 1111 1111 1111)");
                String number = in.readNonEmpty("Card number (16 digits): ");
                String holder = in.readNonEmpty("Name on card          : ");
                String expiry = in.readNonEmpty("Expiry (MM/YY)        : ");
                String cvv = in.readNonEmpty("CVV (3 digits)        : ");
                yield new CardPayment(number, holder, expiry, cvv);
            }
            case 2 -> new UpiPayment(in.readNonEmpty("UPI ID (e.g. name@okbank): "));
            case 3 -> {
                List<String> banks = NetBankingPayment.SUPPORTED_BANKS;
                for (int i = 0; i < banks.size(); i++) {
                    System.out.printf(" %d. %s%n", i + 1, banks.get(i));
                }
                int bank = in.readInt("Choose your bank: ", 1, banks.size());
                yield new NetBankingPayment(banks.get(bank - 1));
            }
            case 4 -> new CashOnDelivery();
            default -> null;
        };
    }

    private void saveInvoice(Invoice invoice) {
        try {
            Path file = invoiceWriter.write(invoice);
            printSuccess("Invoice saved to " + file);
        } catch (DataStoreException e) {
            printError(e.getMessage());
        }
    }

    // ------------------------------------------------------------------ orders

    private void ordersMenu() {
        while (true) {
            printHeader("MY ORDERS");
            List<Order> orders = customer.getOrders();
            if (orders.isEmpty()) {
                System.out.println("You haven't placed any orders yet.");
                in.pause();
                return;
            }
            System.out.printf("%-9s %-22s %6s %14s  %-10s %-16s%n", "Order ID", "Date", "Items", "Total", "Status", "Payment");
            System.out.println("-".repeat(82));
            for (Order o : orders) {
                System.out.printf("%-9s %-22s %6d %14s  %-10s %-16s%n", o.getOrderId(),
                        o.getPlacedAt().format(DATE_FORMAT), o.getTotalQuantity(), Money.format(o.getGrandTotal()),
                        o.getStatus().getLabel(), o.getPaymentReceipt().getMethod());
            }
            System.out.println("\nOrders ship " + Order.SHIP_AFTER.toMinutes() + " min after being placed"
                    + " and can only be cancelled before that.");
            System.out.println("\n 1. View invoice   2. Cancel an order   0. Back");
            int choice = in.readInt("Choose an option: ", 0, 2);
            if (choice == 0) {
                return;
            }
            try {
                String orderId = in.readNonEmpty("Order ID: ");
                if (choice == 1) {
                    Order order = orderService.findOrder(customer, orderId);
                    System.out.println();
                    System.out.print(order.getInvoice().render());
                    System.out.println("Current status: " + order.getStatus().getLabel());
                    in.pause();
                } else {
                    orderService.findOrder(customer, orderId);
                    if (!in.confirm("Are you sure you want to cancel order " + orderId.toUpperCase() + "?")) {
                        continue;
                    }
                    Order cancelled = orderService.cancelOrder(customer, orderId);
                    save();
                    printSuccess("Order " + cancelled.getOrderId() + " has been cancelled.");
                    if (cancelled.getPaymentReceipt().isPaidNow()) {
                        System.out.println("A refund of " + Money.format(cancelled.getGrandTotal())
                                + " will go back to your " + cancelled.getPaymentReceipt().getMethod() + ".");
                    }
                }
            } catch (ShopException e) {
                printError(e.getMessage());
            }
        }
    }

    private void showCoupons() {
        printHeader("AVAILABLE COUPONS");
        for (Discount d : couponManager.getAllCoupons()) {
            System.out.printf("  %-8s %s%n", d.getCode(), d.getDescription());
        }
        System.out.println("\nEnter a code at checkout to use it. One coupon per order.");
        in.pause();
    }

    // ------------------------------------------------------------------ helpers

    private void save() {
        try {
            dataStore.saveAll(catalog, customerService, orderService);
        } catch (DataStoreException e) {
            printError("Could not save data: " + e.getMessage());
        }
    }

    private void printProductTable(Collection<Product> products) {
        System.out.printf(PRODUCT_ROW, "ID", "Name", "Price", "Stock", "Rating");
        System.out.println("-".repeat(77));
        for (Product p : products) {
            String stock = p.isInStock() ? p.getStock() + " left" : "OUT OF STOCK";
            System.out.printf(PRODUCT_ROW, p.getId(), truncate(p.getName(), 34), Money.format(p.getPrice()),
                    stock, String.format(Locale.ENGLISH, "%.1f*", p.getRating()));
        }
    }

    private static void printBanner() {
        System.out.println("=".repeat(60));
        System.out.println("        Welcome to " + Invoice.SHOP_NAME + " - Online Shopping System");
        System.out.println("=".repeat(60));
    }

    private static void printHeader(String title) {
        System.out.println("\n========== " + title + " ==========");
    }

    static void printError(String message) {
        System.out.println("  [!] " + message);
    }

    static void printSuccess(String message) {
        System.out.println("  [OK] " + message);
    }

    private static String truncate(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }
}

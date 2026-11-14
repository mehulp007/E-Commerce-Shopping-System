# ShopEasy – E-Commerce Shopping System (Java OOP)

A menu-driven online shopping system that runs in the terminal, built in plain Java to demonstrate core
Object-Oriented Programming concepts: **Inheritance, Interfaces, Composition, Collections and Exception Handling**.

## Features
- **Product browsing**: 32 sample products in 4 categories (Electronics, Clothing, Books, Grocery)
- **Search, filter & sort**: search by name; filter by category or price range; sort by price, rating or name
- **Cart**: add, change quantity, remove, clear (stock and per-item limit checks)
- **Wishlist**: save products for later and move them to the cart
- **Discount coupons**: percentage, flat-amount and category-specific coupons
- **Payment (simulated)**: Card (Luhn checksum, expiry and CVV checks), UPI, Net Banking, Cash on Delivery
- **Orders**: order history, live status (Placed → Shipped → Delivered) and cancellation with stock restored
- **Invoice**: GST tax invoice printed in the terminal and saved to `invoices/INV-<orderId>.txt`
- **Saved data**: products, customers, wishlists and orders are saved to `data/` and reloaded on the next run

## How to run
You only need the **Java JDK (version 17 or newer)** installed.

**Windows:** double-click `run.bat`, or open a terminal in this folder and type:
```bash
run.bat
```

**macOS / Linux / Git Bash:**
```bash
sh run.sh
```

The script compiles everything into `out/` and starts the app. Log in with any email address. A new email
creates a new account, and the same email later brings back your orders and wishlist.

**Handy test values**
- Card that passes validation: `4111 1111 1111 1111`, any future expiry such as `12/30`, any 3-digit CVV
- UPI ID: `yourname@okaxis`
- Coupons: `SAVE10`, `FLAT200`, `ELEC15`, `BOOKS20`

To reset the shop to its original state, delete the `data/` folder.

## Project structure
```
src/com/ecommerce/
├── Main.java          Entry point: loads data, wires objects, starts the menu
├── model/             Product hierarchy, Cart, CartItem, Wishlist, Customer, Order, OrderItem, OrderStatus
├── payment/           Payment interface + card / UPI / net banking / COD implementations
├── discount/          Discount interface + percentage / flat / category coupons, CouponManager
├── invoice/           Invoice (formatting) and InvoiceWriter (saves .txt files)
├── exception/         ShopException and its specific subclasses
├── repository/        Generic Repository<T> and file-based repositories, sample data
├── service/           ProductCatalog, CustomerService, OrderService (business rules)
├── ui/                ConsoleMenu (all menus) and InputHelper (safe keyboard input)
└── util/              Money formatting
```

## Where each OOP concept is used

### 1. Inheritance
| Parent | Children | What it shows |
|---|---|---|
| `model/Product` (abstract) | `Electronics`, `Clothing`, `Book`, `Grocery` | Each adds its own fields and overrides `getCategory()`, `getSpecificDetails()`, `getExtraFields()` (polymorphism) |
| `payment/AbstractPayment` (abstract) | `CardPayment`, `UpiPayment`, `NetBankingPayment`, `CashOnDelivery` | Template method: `pay()` is fixed, subclasses implement `validate()` |
| `discount/AbstractDiscount` (abstract) | `PercentageDiscount`, `FlatDiscount`, `CategoryDiscount` | Shared code and the cap on the discount; subclasses define the rule |
| `repository/AbstractFileRepository<T>` | `ProductRepository`, `CustomerRepository`, `OrderRepository` | Shared file I/O; subclasses only convert records ↔ fields |
| `exception/ShopException` | 10 specific exceptions | A custom exception hierarchy |

### 2. Interfaces
- `payment/Payment`: `pay(amount)` and `getMethodName()`. Checkout works with *any* payment method.
- `discount/Discount`: `isApplicable(cart)`, `calculateDiscount(cart)` and `getDescription()`
- `repository/Repository<T>`: a generic interface, `loadAll()` and `saveAll()`
- `Comparable<Product>`: the natural ordering of products by name

### 3. Composition ("has-a")
- `Customer` **has a** `Cart`, a `Wishlist` and a `List<Order>`
- `Cart` **has** `CartItem`s, and each `CartItem` **has a** `Product`
- `Order` **has** `OrderItem`s, a `PaymentReceipt` and produces an `Invoice`

### 4. Collections
| Collection | Where | Why |
|---|---|---|
| `LinkedHashMap<String, CartItem>` | `Cart` | Fast lookup by product ID while keeping the order items were added |
| `LinkedHashMap<String, Product>` | `ProductCatalog` | Product lookup by ID |
| `LinkedHashSet<Product>` | `Wishlist` | No duplicates (uses `equals`/`hashCode`) |
| `ArrayList<Order>` | `Customer`, `OrderService` | Order history |
| `TreeMap` / `TreeSet` | `ProductCatalog.groupByCategory()`, `getCategories()` | Categories kept in sorted order |
| `Comparator` + `enum` | `service/SortOption` | Sort strategies |
| Streams | Search, filter, totals | `filter`, `map`, `groupingBy`, `sum` |

### 5. Exception handling
- **Custom checked exceptions:** `ProductNotFoundException`, `OutOfStockException`, `InvalidQuantityException`,
  `EmptyCartException`, `InvalidCouponException`, `PaymentFailedException`, `OrderNotFoundException`,
  `OrderCancellationException`, `ValidationException`, `DataStoreException`
- **Multi-catch:** `catch (EmptyCartException | OutOfStockException e)` in `ConsoleMenu.checkout()`
- **try-with-resources:** every file read and write in `AbstractFileRepository` and `InvoiceWriter`
- **Exception wrapping:** `IOException` is wrapped in `DataStoreException` (with its cause kept)
- **finally:** `ConsoleMenu.start()` always saves data when the program ends
- **Bad input:** `NumberFormatException` is caught in `InputHelper`, and a corrupt data line is skipped with a warning

## Billing rules
```
Subtotal − Coupon discount = Taxable amount
GST       = 18% of the taxable amount
Grand total = Taxable amount + GST
```
Cash on Delivery is allowed up to ₹50,000 and UPI up to ₹1,00,000. You can buy at most 10 units of each product.
Orders can be cancelled only while they are *Placed*. As a demo they become *Shipped* 2 minutes after being placed
and *Delivered* after 5 minutes.

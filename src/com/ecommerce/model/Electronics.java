package com.ecommerce.model;

public class Electronics extends Product {

    private final String brand;
    private final int warrantyMonths;

    public Electronics(String id, String name, double price, int stock, double rating,
                       String brand, int warrantyMonths) {
        super(id, name, price, stock, rating);
        this.brand = brand;
        this.warrantyMonths = warrantyMonths;
    }

    public String getBrand() {
        return brand;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    @Override
    public String getCategory() {
        return "Electronics";
    }

    @Override
    protected String getSpecificDetails() {
        return String.format("  Brand    : %s%n  Warranty : %d months%n", brand, warrantyMonths);
    }

    @Override
    public String[] getExtraFields() {
        return new String[] {brand, String.valueOf(warrantyMonths)};
    }
}

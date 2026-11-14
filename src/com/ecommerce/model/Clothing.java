package com.ecommerce.model;

public class Clothing extends Product {

    private final String size;
    private final String material;

    public Clothing(String id, String name, double price, int stock, double rating,
                    String size, String material) {
        super(id, name, price, stock, rating);
        this.size = size;
        this.material = material;
    }

    public String getSize() {
        return size;
    }

    public String getMaterial() {
        return material;
    }

    @Override
    public String getCategory() {
        return "Clothing";
    }

    @Override
    protected String getSpecificDetails() {
        return String.format("  Size     : %s%n  Material : %s%n", size, material);
    }

    @Override
    public String[] getExtraFields() {
        return new String[] {size, material};
    }
}

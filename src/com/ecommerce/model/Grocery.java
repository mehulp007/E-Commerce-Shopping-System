package com.ecommerce.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Grocery extends Product {

    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    private final int weightGrams;
    private final LocalDate bestBefore;

    public Grocery(String id, String name, double price, int stock, double rating,
                   int weightGrams, LocalDate bestBefore) {
        super(id, name, price, stock, rating);
        this.weightGrams = weightGrams;
        this.bestBefore = bestBefore;
    }

    public int getWeightGrams() {
        return weightGrams;
    }

    public LocalDate getBestBefore() {
        return bestBefore;
    }

    @Override
    public String getCategory() {
        return "Grocery";
    }

    @Override
    protected String getSpecificDetails() {
        String weight = weightGrams >= 1000
                ? String.format(Locale.ENGLISH, "%.3f", weightGrams / 1000.0).replaceAll("\\.?0+$", "") + " kg"
                : weightGrams + " g";
        return String.format("  Weight   : %s%n  Best before : %s%n", weight, bestBefore.format(DISPLAY));
    }

    @Override
    public String[] getExtraFields() {
        return new String[] {String.valueOf(weightGrams), bestBefore.toString()};
    }
}

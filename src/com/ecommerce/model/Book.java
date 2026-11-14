package com.ecommerce.model;

public class Book extends Product {

    private final String author;
    private final int pages;

    public Book(String id, String name, double price, int stock, double rating,
                String author, int pages) {
        super(id, name, price, stock, rating);
        this.author = author;
        this.pages = pages;
    }

    public String getAuthor() {
        return author;
    }

    public int getPages() {
        return pages;
    }

    @Override
    public String getCategory() {
        return "Books";
    }

    @Override
    protected String getSpecificDetails() {
        return String.format("  Author   : %s%n  Pages    : %d%n", author, pages);
    }

    @Override
    public String[] getExtraFields() {
        return new String[] {author, String.valueOf(pages)};
    }
}

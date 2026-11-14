package com.ecommerce.service;

/** The money breakdown shown before payment: subtotal, discount, GST and total. */
public record Bill(double subtotal, double discount, double taxableAmount, double gst, double grandTotal) {
}

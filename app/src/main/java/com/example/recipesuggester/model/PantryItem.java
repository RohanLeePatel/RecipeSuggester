package com.example.recipesuggester.model;

import java.math.BigDecimal;

// one ingredient in the user's pantry (one row in the pantry_items table)
public class PantryItem {

    private long id;
    private String name;
    private double quantity;
    private String unit;
    private String expiryDate; // "yyyy-MM-dd", or null if the user didn't set one

    public PantryItem(long id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    // for new items that aren't in the database yet so they don't have an id
    public PantryItem(String name, double quantity, String unit, String expiryDate) {
        this(0, name, quantity, unit, expiryDate);
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public String getExpiryDate() { return expiryDate; }

    public void setId(long id) { this.id = id; }

    // turns 2.0 into "2" and 0.50 into "0.5" so the list doesn't show ugly decimals
    public static String formatQuantity(double quantity) {
        return BigDecimal.valueOf(quantity).stripTrailingZeros().toPlainString();
    }

    // e.g. "250 g" or "3 pcs"
    public String getQuantityText() {
        return formatQuantity(quantity) + " " + unit;
    }
}

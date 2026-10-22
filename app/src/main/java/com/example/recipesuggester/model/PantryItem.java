package com.example.recipesuggester.model;

import com.example.recipesuggester.util.Units;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
        return formatQuantity(quantity) + " " + Units.display(unit);
    }

    // how many days until this item expires: 0 = today, 1 = tomorrow, negative = already expired.
    // returns null if there's no expiry date (or it can't be read).
    // using Calendar instead of java.time because java.time needs Android 8 and my minSdk is 24 (Android 7)
    public Integer daysUntilExpiry() {
        if (expiryDate == null) {
            return null;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            format.setLenient(false);
            Calendar expiry = Calendar.getInstance();
            expiry.setTime(format.parse(expiryDate)); // parse gives midnight of that day

            // today at midnight, so the time of day doesn't change the answer
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            long millisPerDay = 24L * 60 * 60 * 1000;
            // Math.round because a daylight saving day is 23 or 25 hours long
            return (int) Math.round((expiry.getTimeInMillis() - today.getTimeInMillis()) / (double) millisPerDay);
        } catch (ParseException e) {
            return null;
        }
    }
}

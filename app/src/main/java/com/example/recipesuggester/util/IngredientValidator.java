package com.example.recipesuggester.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

// checks what the user typed into the add/edit ingredient form.
// each method returns an error message, or null if the value is fine.
// it's plain Java (no Android stuff) so the form code stays short
public class IngredientValidator {

    // the units the app supports. has to match the list in res/values/arrays.xml
    public static final List<String> UNITS = Arrays.asList("g", "kg", "ml", "l", "pcs");

    public static final int MAX_NAME_LENGTH = 40;
    public static final double MAX_QUANTITY = 100000;

    // letters, spaces, hyphens and apostrophes only, e.g. "self-raising flour" or "baker's yeast"
    private static final String NAME_PATTERN = "[\\p{L} '\\-]+";

    public static String validateName(String name) {
        String trimmed = name == null ? "" : name.trim();
        if (trimmed.isEmpty()) {
            return "Please enter an ingredient name";
        }
        if (trimmed.length() > MAX_NAME_LENGTH) {
            return "Name is too long (max " + MAX_NAME_LENGTH + " characters)";
        }
        // the pattern alone would let "---" through, so also check there's at least one letter
        if (!trimmed.matches(NAME_PATTERN) || !trimmed.matches(".*\\p{L}.*")) {
            return "Use letters only (spaces, - and ' are ok)";
        }
        return null;
    }

    public static String validateQuantity(String text) {
        Double value = parseQuantity(text);
        if (text == null || text.trim().isEmpty()) {
            return "Please enter a quantity";
        }
        if (value == null) {
            return "Quantity must be a number";
        }
        if (value <= 0) {
            return "Quantity must be more than 0";
        }
        if (value > MAX_QUANTITY) {
            return "That quantity is too big";
        }
        return null;
    }

    public static String validateUnit(String unit) {
        if (unit == null || !UNITS.contains(unit)) {
            return "Please pick a unit";
        }
        return null;
    }

    // expiry date is optional so empty/null is fine
    public static String validateExpiryDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        // regex first because SimpleDateFormat would accept stuff like "2026-1-5"
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return "Date must look like 2026-12-31";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        format.setLenient(false); // otherwise it turns 2026-02-30 into March 2nd
        try {
            format.parse(date);
            return null;
        } catch (ParseException e) {
            return "That date doesn't exist";
        }
    }

    // turns the typed text into a number, or null if it isn't one.
    // some phone keyboards type a comma for decimals (like 0,5) so I swap that for a dot
    public static Double parseQuantity(String text) {
        if (text == null) {
            return null;
        }
        try {
            double value = Double.parseDouble(text.trim().replace(',', '.'));
            // parseDouble accepts "NaN" and "Infinity" which obviously aren't quantities
            return Double.isNaN(value) || Double.isInfinite(value) ? null : value;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

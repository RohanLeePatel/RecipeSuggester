package com.example.recipesuggester.util;

// unit conversion for the matching. every unit belongs to a group, and amounts can only
// be compared inside the same group:
//   MASS   - g, kg  (base unit g)
//   VOLUME - ml, l  (base unit ml)
//   COUNT  - pcs
// so 1 kg flour can cover 200 g flour, but 2 pcs flour can NOT cover 200 g flour,
// because there's no safe way to know how much "2 pieces" of flour weighs
public class Units {

    public enum Group { MASS, VOLUME, COUNT }

    // which group a unit is in, or null if the app doesn't know the unit
    public static Group groupOf(String unit) {
        if (unit == null) {
            return null;
        }
        switch (unit.trim().toLowerCase(java.util.Locale.ROOT)) {
            case "g":
            case "kg":
                return Group.MASS;
            case "ml":
            case "l":
                return Group.VOLUME;
            case "pcs":
                return Group.COUNT;
            default:
                return null;
        }
    }

    // how a unit is shown on screen. litres are stored as "l" but a lowercase l looks
    // like the number 1 ("1 l"), so it gets shown as "L" instead
    public static String display(String unit) {
        return "l".equals(unit) ? "L" : unit;
    }

    // converts an amount to its group's base unit, e.g. 1.5 kg -> 1500 (g), 2 l -> 2000 (ml).
    // only call this with units where groupOf() isn't null
    public static double toBase(double quantity, String unit) {
        switch (unit.trim().toLowerCase(java.util.Locale.ROOT)) {
            case "kg":
            case "l":
                return quantity * 1000;
            default:
                return quantity; // g, ml and pcs are already the base unit
        }
    }
}

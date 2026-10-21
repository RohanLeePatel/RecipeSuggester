package com.example.recipesuggester.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// turns whatever the user typed into one standard name so the matching can compare names
// with a plain equals(). e.g. "  Tomatoes", "tomato" and "TOMATO " all become "tomato",
// and "ground beef" becomes "minced beef".
//
// I'm deliberately NOT doing substring matching (like name.contains("cheese")) because then
// "cheese" would match "feta cheese" and "apple" would match "pineapple", which is wrong
public class IngredientNames {

    // different names for the same ingredient -> the name the recipes use.
    // the keys are written AFTER the singular step, so "olive oils" also works
    private static final Map<String, String> ALIASES = new HashMap<>();

    static {
        alias("oil", "vegetable oil", "sunflower oil", "olive oil", "cooking oil", "canola oil");
        alias("minced beef", "mince", "beef mince", "ground beef", "minced meat");
        alias("flour", "plain flour", "all purpose flour", "all-purpose flour", "cake flour", "cake wheat flour");
        alias("sugar", "white sugar", "caster sugar", "granulated sugar");
        alias("butter", "salted butter", "unsalted butter");
        alias("milk", "full cream milk", "whole milk", "low fat milk");
        alias("cheese", "cheddar", "cheddar cheese", "grated cheese");
        alias("feta cheese", "feta");
        alias("chicken breast", "chicken fillet", "chicken breast fillet");
        alias("bell pepper", "capsicum", "green pepper", "red pepper", "yellow pepper");
        alias("yoghurt", "yogurt", "plain yoghurt", "plain yogurt", "greek yoghurt", "greek yogurt");
        alias("mayonnaise", "mayo");
        alias("oats", "oat", "rolled oat");
        alias("stock", "chicken stock", "vegetable stock", "beef stock", "broth");
        alias("soy sauce", "soya sauce");
        alias("tuna", "tinned tuna", "canned tuna", "tuna can");
        alias("garlic", "garlic clove");
        alias("pasta", "spaghetti", "penne", "macaroni", "fusilli");
        alias("bread", "slice of bread", "bread slice");
        alias("bread roll", "roll", "burger bun", "bun");
        // NOT adding baking soda -> baking powder, they're different things in baking
    }

    private static void alias(String standardName, String... otherNames) {
        for (String other : otherNames) {
            ALIASES.put(other, standardName);
        }
    }

    // the one method everything uses: lowercase -> tidy spaces -> singular -> alias
    public static String normalize(String name) {
        if (name == null) {
            return "";
        }
        // Locale.ROOT so lowercasing works the same on every phone's language setting
        String cleaned = name.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        if (cleaned.isEmpty()) {
            return "";
        }
        String singular = singularLastWord(cleaned);
        String standard = ALIASES.get(singular);
        return standard != null ? standard : singular;
    }

    // only the last word gets made singular: "chicken breasts" -> "chicken breast"
    private static String singularLastWord(String name) {
        int lastSpace = name.lastIndexOf(' ');
        String start = name.substring(0, lastSpace + 1);
        String lastWord = name.substring(lastSpace + 1);
        return start + singular(lastWord);
    }

    // simple english plural rules. it doesn't have to be perfect english, it just has to turn
    // the singular and plural of the same word into the same thing
    static String singular(String word) {
        if (word.length() <= 3) {
            return word; // "gas", "bus" etc, too short to guess safely
        }
        if (word.endsWith("ies")) {
            return word.substring(0, word.length() - 3) + "y"; // berries -> berry
        }
        if (word.endsWith("oes")) {
            return word.substring(0, word.length() - 2); // tomatoes -> tomato, potatoes -> potato
        }
        if (word.endsWith("ches") || word.endsWith("shes") || word.endsWith("xes")
                || word.endsWith("sses")) {
            return word.substring(0, word.length() - 2); // peaches -> peach, radishes -> radish
        }
        if (word.endsWith("s") && !word.endsWith("ss") && !word.endsWith("us")) {
            return word.substring(0, word.length() - 1); // eggs -> egg (but not "glass" or "hummus")
        }
        return word;
    }
}

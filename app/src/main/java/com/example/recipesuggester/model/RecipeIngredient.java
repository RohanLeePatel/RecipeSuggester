package com.example.recipesuggester.model;

import com.example.recipesuggester.util.Units;

// one ingredient that a recipe needs, e.g. 200 g flour (one row in recipe_ingredients)
public class RecipeIngredient {

    private final String name;
    private final double quantity;
    private final String unit;

    public RecipeIngredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }

    // e.g. "200 g flour"
    @Override
    public String toString() {
        return PantryItem.formatQuantity(quantity) + " " + Units.display(unit) + " " + name;
    }
}

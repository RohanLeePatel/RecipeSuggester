package com.example.recipesuggester.model;

import java.util.ArrayList;
import java.util.List;

// one recipe plus the list of ingredients it needs.
// in the database the ingredients are in their own table, RecipeDao joins them back together
public class Recipe {

    private long id;
    private final String name;
    private final String description;
    private final String instructions; // one step per line
    private final List<RecipeIngredient> ingredients;

    public Recipe(long id, String name, String description, String instructions,
                  List<RecipeIngredient> ingredients) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.instructions = instructions;
        this.ingredients = new ArrayList<>(ingredients);
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getInstructions() { return instructions; }
    public List<RecipeIngredient> getIngredients() { return ingredients; }

    public void setId(long id) { this.id = id; }
}

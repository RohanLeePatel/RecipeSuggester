package com.example.recipesuggester.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.recipesuggester.model.Recipe;
import com.example.recipesuggester.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// reading recipes (and inserting the seed recipes) for the recipes + recipe_ingredients tables
public class RecipeDao {

    private final DatabaseHelper dbHelper;

    public RecipeDao(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    // every recipe with its ingredients, sorted by name.
    // I load all the ingredients in ONE query and sort them into their recipes with a map,
    // instead of running a separate query for each recipe (that would be 22+ queries)
    public List<Recipe> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<Long, List<RecipeIngredient>> ingredientsByRecipe = new HashMap<>();
        try (Cursor c = db.query(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, null, null,
                null, null, DatabaseHelper.COL_RI_ID)) {
            while (c.moveToNext()) {
                long recipeId = c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_RI_RECIPE_ID));
                // computeIfAbsent makes the list the first time we see this recipe id
                ingredientsByRecipe.computeIfAbsent(recipeId, k -> new ArrayList<>())
                        .add(ingredientFromCursor(c));
            }
        }

        List<Recipe> recipes = new ArrayList<>();
        try (Cursor c = db.query(DatabaseHelper.TABLE_RECIPES, null, null, null, null, null,
                DatabaseHelper.COL_RECIPE_NAME + " COLLATE NOCASE ASC")) {
            while (c.moveToNext()) {
                long id = c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_RECIPE_ID));
                List<RecipeIngredient> ingredients = ingredientsByRecipe.get(id);
                recipes.add(recipeFromCursor(c, ingredients != null ? ingredients : new ArrayList<>()));
            }
        }
        return recipes;
    }

    // one recipe with its ingredients, or null if there's no recipe with that id
    public Recipe getById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] args = {String.valueOf(id)};

        List<RecipeIngredient> ingredients = new ArrayList<>();
        try (Cursor c = db.query(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null,
                DatabaseHelper.COL_RI_RECIPE_ID + " = ?", args, null, null, DatabaseHelper.COL_RI_ID)) {
            while (c.moveToNext()) {
                ingredients.add(ingredientFromCursor(c));
            }
        }

        try (Cursor c = db.query(DatabaseHelper.TABLE_RECIPES, null,
                DatabaseHelper.COL_RECIPE_ID + " = ?", args, null, null, null)) {
            return c.moveToFirst() ? recipeFromCursor(c, ingredients) : null;
        }
    }

    public int count() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_RECIPES, null)) {
            c.moveToFirst();
            return c.getInt(0);
        }
    }

    // puts all the seed recipes into the database. this is static and takes the db because
    // it gets called from inside DatabaseHelper.onCreate/onUpgrade, which already run in a
    // transaction - so if anything fails halfway, NONE of the recipes get saved (no half-seeded db)
    static void insertSeedRecipes(SQLiteDatabase db) {
        for (Recipe recipe : RecipeSeedData.getRecipes()) {
            ContentValues r = new ContentValues();
            r.put(DatabaseHelper.COL_RECIPE_NAME, recipe.getName());
            r.put(DatabaseHelper.COL_RECIPE_DESCRIPTION, recipe.getDescription());
            r.put(DatabaseHelper.COL_RECIPE_INSTRUCTIONS, recipe.getInstructions());
            long recipeId = db.insertOrThrow(DatabaseHelper.TABLE_RECIPES, null, r);

            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                ContentValues i = new ContentValues();
                i.put(DatabaseHelper.COL_RI_RECIPE_ID, recipeId); // links it to the recipe above
                i.put(DatabaseHelper.COL_RI_NAME, ingredient.getName());
                i.put(DatabaseHelper.COL_RI_QUANTITY, ingredient.getQuantity());
                i.put(DatabaseHelper.COL_RI_UNIT, ingredient.getUnit());
                db.insertOrThrow(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, i);
            }
        }
    }

    private static RecipeIngredient ingredientFromCursor(Cursor c) {
        return new RecipeIngredient(
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_RI_NAME)),
                c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COL_RI_QUANTITY)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_RI_UNIT)));
    }

    private static Recipe recipeFromCursor(Cursor c, List<RecipeIngredient> ingredients) {
        return new Recipe(
                c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_RECIPE_ID)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_RECIPE_NAME)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_RECIPE_DESCRIPTION)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_RECIPE_INSTRUCTIONS)),
                ingredients);
    }
}

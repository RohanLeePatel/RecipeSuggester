package com.example.recipesuggester.util;

import com.example.recipesuggester.model.PantryItem;
import com.example.recipesuggester.model.Recipe;
import com.example.recipesuggester.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// decides which recipes the user can actually make with what's in their pantry.
// a recipe can only be suggested if EVERY ingredient it needs is in the pantry
// in AT LEAST the amount the recipe needs. one missing or short ingredient = not suggested.
//
// how it works:
//  1. add up the pantry by ingredient name (normalized) and unit group, in base units.
//     e.g. "Flour 500 g" + "flour 1 kg" -> flour: MASS 1500 g
//  2. for each ingredient the recipe needs, look up that name + unit group and check
//     there's enough. a different unit group (pcs vs g) doesn't count, because they
//     can't be compared safely
//  3. if anything is missing or short, the recipe isn't makeable
//
// it's plain Java with no Android or database code, so it can be unit tested on its own
public class RecipeMatcher {

    // tiny allowance for decimal rounding, so 0.3 l still counts as 300 ml
    private static final double EPSILON = 1e-9;

    private final Map<String, Map<Units.Group, Double>> pantryTotals = new HashMap<>();

    // builds the pantry totals once, then canMake() can be called for lots of recipes
    public RecipeMatcher(List<PantryItem> pantry) {
        for (PantryItem item : pantry) {
            Units.Group group = Units.groupOf(item.getUnit());
            if (group == null) {
                continue; // unknown unit, can't be compared with anything so it's ignored
            }
            String name = IngredientNames.normalize(item.getName());
            pantryTotals.computeIfAbsent(name, k -> new EnumMap<>(Units.Group.class))
                    .merge(group, Units.toBase(item.getQuantity(), item.getUnit()), Double::sum);
        }
    }

    // the ingredients this recipe still needs (missing completely OR not enough).
    // an empty list means the user has everything
    public List<RecipeIngredient> getMissing(Recipe recipe) {
        List<RecipeIngredient> missing = new ArrayList<>();
        for (RecipeIngredient needed : recipe.getIngredients()) {
            if (!hasEnough(needed)) {
                missing.add(needed);
            }
        }
        return missing;
    }

    public boolean canMake(Recipe recipe) {
        // a recipe with no ingredients would "match" everything, so never suggest that
        return !recipe.getIngredients().isEmpty() && getMissing(recipe).isEmpty();
    }

    // filters a list of recipes down to only the ones the user can make right now
    public List<Recipe> findMakeable(List<Recipe> recipes) {
        List<Recipe> makeable = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (canMake(recipe)) {
                makeable.add(recipe);
            }
        }
        return makeable;
    }

    private boolean hasEnough(RecipeIngredient needed) {
        Units.Group group = Units.groupOf(needed.getUnit());
        if (group == null) {
            return false; // a unit the app doesn't understand can't be checked, so don't assume it's there
        }
        Map<Units.Group, Double> have = pantryTotals.get(IngredientNames.normalize(needed.getName()));
        if (have == null || !have.containsKey(group)) {
            return false; // not in the pantry at all (or only in a unit that can't be compared)
        }
        double needAmount = Units.toBase(needed.getQuantity(), needed.getUnit());
        return have.get(group) + EPSILON >= needAmount;
    }
}

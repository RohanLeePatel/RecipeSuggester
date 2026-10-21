package com.example.recipesuggester.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.recipesuggester.model.Recipe;
import com.example.recipesuggester.model.RecipeIngredient;
import com.example.recipesuggester.util.IngredientValidator;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

// checks the seed recipes are complete and follow my naming/unit rules,
// so a typo in RecipeSeedData gets caught here instead of breaking the matching later
public class RecipeSeedDataTest {

    private final List<Recipe> recipes = RecipeSeedData.getRecipes();

    @Test
    public void atLeastTwentyRecipes() {
        assertTrue("only " + recipes.size() + " recipes", recipes.size() >= 20);
    }

    @Test
    public void recipeNamesAreUnique() {
        Set<String> names = new HashSet<>();
        for (Recipe r : recipes) {
            assertTrue("duplicate recipe " + r.getName(), names.add(r.getName().toLowerCase(Locale.ROOT)));
        }
    }

    @Test
    public void everyRecipeIsComplete() {
        for (Recipe r : recipes) {
            assertFalse(r.getName() + " has no description", r.getDescription().trim().isEmpty());
            assertFalse(r.getName() + " has no instructions", r.getInstructions().trim().isEmpty());
            assertFalse(r.getName() + " has no ingredients", r.getIngredients().isEmpty());
        }
    }

    @Test
    public void ingredientsUseValidUnitsAndQuantities() {
        for (Recipe r : recipes) {
            Set<String> seen = new HashSet<>();
            for (RecipeIngredient i : r.getIngredients()) {
                String where = r.getName() + " / " + i.getName();
                assertTrue(where + " bad unit " + i.getUnit(), IngredientValidator.UNITS.contains(i.getUnit()));
                assertTrue(where + " quantity must be > 0", i.getQuantity() > 0);
                // lowercase + trimmed so the matching has a consistent starting point
                assertTrue(where + " should be lowercase", i.getName().equals(i.getName().trim().toLowerCase(Locale.ROOT)));
                assertTrue(where + " listed twice", seen.add(i.getName()));
            }
        }
    }
}

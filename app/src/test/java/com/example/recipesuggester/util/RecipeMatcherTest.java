package com.example.recipesuggester.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.recipesuggester.data.RecipeSeedData;
import com.example.recipesuggester.model.PantryItem;
import com.example.recipesuggester.model.Recipe;
import com.example.recipesuggester.model.RecipeIngredient;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// tests for the recipe matching. tried to cover every way it could go wrong
public class RecipeMatcherTest {

    // simple 3 ingredient recipe to test with: 2 eggs, 200 g flour, 100 ml milk
    private static final Recipe BATTER = recipe("Batter",
            ing("egg", 2, "pcs"), ing("flour", 200, "g"), ing("milk", 100, "ml"));

    private static boolean canMake(Recipe recipe, PantryItem... pantry) {
        return new RecipeMatcher(Arrays.asList(pantry)).canMake(recipe);
    }

    // ---------- exact matches ----------

    @Test
    public void exactAmountsMatch() {
        assertTrue(canMake(BATTER, item("egg", 2, "pcs"), item("flour", 200, "g"), item("milk", 100, "ml")));
    }

    @Test
    public void moreThanEnoughMatches() {
        assertTrue(canMake(BATTER, item("egg", 12, "pcs"), item("flour", 900, "g"), item("milk", 750, "ml")));
    }

    // ---------- missing ingredients ----------

    @Test
    public void oneMissingIngredientMeansNoMatch() {
        // 2 out of 3 is NOT good enough
        assertFalse(canMake(BATTER, item("egg", 2, "pcs"), item("milk", 100, "ml")));
    }

    @Test
    public void fourOutOfFiveIsNotEnough() {
        Recipe r = recipe("Five", ing("a", 1, "pcs"), ing("b", 1, "pcs"), ing("c", 1, "pcs"),
                ing("d", 1, "pcs"), ing("e", 1, "pcs"));
        assertFalse(canMake(r, item("a", 1, "pcs"), item("b", 1, "pcs"), item("c", 1, "pcs"),
                item("d", 1, "pcs")));
    }

    @Test
    public void getMissingListsExactlyWhatsMissing() {
        RecipeMatcher matcher = new RecipeMatcher(Arrays.asList(item("egg", 2, "pcs"), item("milk", 50, "ml")));
        List<RecipeIngredient> missing = matcher.getMissing(BATTER);
        assertEquals(2, missing.size()); // no flour, and not enough milk
        assertEquals("flour", missing.get(0).getName());
        assertEquals("milk", missing.get(1).getName());
    }

    // ---------- not enough ----------

    @Test
    public void oneEggWhenTwoNeededIsNoMatch() {
        assertFalse(canMake(BATTER, item("egg", 1, "pcs"), item("flour", 200, "g"), item("milk", 100, "ml")));
    }

    @Test
    public void slightlyShortIsNoMatch() {
        assertFalse(canMake(BATTER, item("egg", 2, "pcs"), item("flour", 199.9, "g"), item("milk", 100, "ml")));
    }

    // ---------- units ----------

    @Test
    public void kilogramsCoverGrams() {
        assertTrue(canMake(BATTER, item("egg", 2, "pcs"), item("flour", 1, "kg"), item("milk", 100, "ml")));
    }

    @Test
    public void litresCoverMillilitres() {
        assertTrue(canMake(BATTER, item("egg", 2, "pcs"), item("flour", 200, "g"), item("milk", 0.1, "l")));
    }

    @Test
    public void smallKilogramAmountIsStillChecked() {
        // 0.15 kg = 150 g, which is less than 200 g
        assertFalse(canMake(BATTER, item("egg", 2, "pcs"), item("flour", 0.15, "kg"), item("milk", 100, "ml")));
    }

    @Test
    public void decimalRoundingDoesNotBreakExactAmounts() {
        Recipe r = recipe("Milk", ing("milk", 300, "ml"));
        assertTrue(canMake(r, item("milk", 0.3, "l")));
        assertTrue(canMake(r, item("milk", 0.1, "l"), item("milk", 0.2, "l")));
    }

    @Test
    public void incompatibleUnitsNeverMatch() {
        // "2 pcs" of flour can't be compared with 200 g, so it must NOT count
        assertFalse(canMake(BATTER, item("egg", 2, "pcs"), item("flour", 2, "pcs"), item("milk", 100, "ml")));
        // and grams of milk can't be compared to ml
        assertFalse(canMake(BATTER, item("egg", 2, "pcs"), item("flour", 200, "g"), item("milk", 500, "g")));
    }

    @Test
    public void unknownUnitsNeverMatch() {
        assertFalse(canMake(BATTER, item("egg", 2, "pcs"), item("flour", 200, "cups"), item("milk", 100, "ml")));
        Recipe weird = recipe("Weird", ing("salt", 1, "pinch"));
        assertFalse(canMake(weird, item("salt", 1000, "g")));
    }

    @Test
    public void duplicatePantryRowsAreAddedTogether() {
        // 100 g + 0.1 kg = 200 g, exactly enough
        assertTrue(canMake(BATTER, item("egg", 1, "pcs"), item("Eggs", 1, "pcs"),
                item("flour", 100, "g"), item("flour", 0.1, "kg"), item("milk", 100, "ml")));
    }

    // ---------- names ----------

    @Test
    public void capitalsSpacesAndPluralsMatch() {
        assertTrue(canMake(BATTER, item("  EGGS ", 2, "pcs"), item("Flour", 200, "g"), item("milk  ", 100, "ml")));
        Recipe r = recipe("Salad", ing("tomato", 3, "pcs"), ing("chicken breast", 2, "pcs"));
        assertTrue(canMake(r, item("Tomatoes", 3, "pcs"), item("Chicken   Breasts", 2, "pcs")));
    }

    @Test
    public void aliasesMatch() {
        Recipe r = recipe("Burger", ing("minced beef", 500, "g"), ing("oil", 15, "ml"), ing("yoghurt", 100, "g"));
        assertTrue(canMake(r, item("ground beef", 500, "g"), item("Olive Oil", 30, "ml"), item("yogurt", 100, "g")));
    }

    @Test
    public void noUnsafeSubstringMatches() {
        // having plain cheese does NOT mean you have feta cheese
        assertFalse(canMake(recipe("Greek", ing("feta cheese", 100, "g")), item("cheese", 500, "g")));
        // bread is not a bread roll
        assertFalse(canMake(recipe("Burger", ing("bread roll", 4, "pcs")), item("bread", 10, "pcs")));
        // pineapple is not apple
        assertFalse(canMake(recipe("Pie", ing("apple", 2, "pcs")), item("pineapple", 2, "pcs")));
        // black pepper is not a bell pepper
        assertFalse(canMake(recipe("Stir fry", ing("bell pepper", 1, "pcs")), item("pepper", 50, "g")));
    }

    @Test
    public void normalizeExamples() {
        assertEquals("tomato", IngredientNames.normalize("  Tomatoes "));
        assertEquals("potato", IngredientNames.normalize("POTATOES"));
        assertEquals("berry", IngredientNames.normalize("berries"));
        assertEquals("chicken breast", IngredientNames.normalize("chicken  breasts"));
        assertEquals("minced beef", IngredientNames.normalize("Mince"));
        assertEquals("oats", IngredientNames.normalize("oats"));
        assertEquals("oats", IngredientNames.normalize("Rolled Oats"));
        assertEquals("hummus", IngredientNames.normalize("hummus")); // not "hummu"
        assertEquals("", IngredientNames.normalize("   "));
    }

    // ---------- empty cases ----------

    @Test
    public void emptyPantryMatchesNothing() {
        RecipeMatcher matcher = new RecipeMatcher(Collections.emptyList());
        assertTrue(matcher.findMakeable(RecipeSeedData.getRecipes()).isEmpty());
    }

    @Test
    public void recipeWithNoIngredientsIsNeverSuggested() {
        assertFalse(canMake(recipe("Air"), item("egg", 2, "pcs")));
    }

    // ---------- pantry changes ----------

    @Test
    public void eligibilityChangesWhenPantryChanges() {
        List<PantryItem> pantry = new ArrayList<>(Arrays.asList(
                item("egg", 2, "pcs"), item("flour", 200, "g")));
        assertFalse(new RecipeMatcher(pantry).canMake(BATTER)); // no milk yet

        pantry.add(item("milk", 1, "l"));
        assertTrue(new RecipeMatcher(pantry).canMake(BATTER)); // milk added -> now makeable

        pantry.set(0, item("egg", 1, "pcs"));
        assertFalse(new RecipeMatcher(pantry).canMake(BATTER)); // used an egg -> not anymore
    }

    // ---------- with the real seed recipes ----------

    @Test
    public void exactPancakeIngredientsSuggestPancakesAndRemovingOneDoesNot() {
        Recipe pancakes = null;
        for (Recipe r : RecipeSeedData.getRecipes()) {
            if (r.getName().equals("Pancakes")) pancakes = r;
        }
        List<PantryItem> pantry = new ArrayList<>();
        for (RecipeIngredient i : pancakes.getIngredients()) {
            pantry.add(item(i.getName(), i.getQuantity(), i.getUnit()));
        }

        List<Recipe> makeable = new RecipeMatcher(pantry).findMakeable(RecipeSeedData.getRecipes());
        assertTrue(names(makeable).contains("Pancakes"));

        pantry.remove(pantry.size() - 1); // take away the butter
        makeable = new RecipeMatcher(pantry).findMakeable(RecipeSeedData.getRecipes());
        assertFalse(names(makeable).contains("Pancakes"));
    }

    @Test
    public void findMakeableOnlyReturnsFullMatches() {
        // enough for a toasted cheese sandwich, but not french toast (no egg, milk or sugar)
        RecipeMatcher matcher = new RecipeMatcher(Arrays.asList(
                item("bread", 4, "pcs"), item("cheddar", 100, "g"), item("butter", 250, "g")));
        assertEquals(Collections.singletonList("Toasted Cheese Sandwich"),
                names(matcher.findMakeable(RecipeSeedData.getRecipes())));
    }

    // ---------- helpers ----------

    private static PantryItem item(String name, double quantity, String unit) {
        return new PantryItem(name, quantity, unit, null);
    }

    private static RecipeIngredient ing(String name, double quantity, String unit) {
        return new RecipeIngredient(name, quantity, unit);
    }

    private static Recipe recipe(String name, RecipeIngredient... ingredients) {
        return new Recipe(0, name, "", "", Arrays.asList(ingredients));
    }

    private static List<String> names(List<Recipe> recipes) {
        List<String> names = new ArrayList<>();
        for (Recipe r : recipes) names.add(r.getName());
        return names;
    }
}

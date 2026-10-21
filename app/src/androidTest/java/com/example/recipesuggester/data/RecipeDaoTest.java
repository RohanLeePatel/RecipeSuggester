package com.example.recipesuggester.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.recipesuggester.model.Recipe;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// checks the seed recipes really ended up in SQLite, exactly once, with all their ingredients.
// these tests only read, so they don't need the rollback trick
@RunWith(AndroidJUnit4.class)
public class RecipeDaoTest {

    private RecipeDao dao;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dao = new RecipeDao(context);
    }

    @Test
    public void recipesAreSeededExactlyOnce() {
        // if seeding ran twice the UNIQUE name column would fail, and the count would be off
        assertEquals(RecipeSeedData.getRecipes().size(), dao.count());
    }

    @Test
    public void everyRecipeComesBackWithAllItsIngredients() {
        Map<String, Integer> expected = new HashMap<>();
        for (Recipe r : RecipeSeedData.getRecipes()) {
            expected.put(r.getName(), r.getIngredients().size());
        }

        List<Recipe> fromDb = dao.getAll();
        assertEquals(expected.size(), fromDb.size());
        for (Recipe r : fromDb) {
            assertEquals(r.getName(), expected.get(r.getName()), (Integer) r.getIngredients().size());
        }
    }

    @Test
    public void getByIdMatchesGetAll() {
        Recipe first = dao.getAll().get(0);
        Recipe byId = dao.getById(first.getId());
        assertNotNull(byId);
        assertEquals(first.getName(), byId.getName());
        assertEquals(first.getIngredients().size(), byId.getIngredients().size());
        assertEquals(first.getInstructions(), byId.getInstructions());
    }

    @Test
    public void unknownIdReturnsNull() {
        assertNull(dao.getById(-12345));
    }
}

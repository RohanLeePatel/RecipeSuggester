package com.example.recipesuggester.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.recipesuggester.model.PantryItem;
import com.example.recipesuggester.model.Recipe;
import com.example.recipesuggester.util.RecipeMatcher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

// end-to-end check on the emulator: real pantry rows + real seeded recipes + the matcher.
// shows a recipe appears and disappears as the pantry changes in the database.
// runs in a rolled-back transaction like the other database tests
@RunWith(AndroidJUnit4.class)
public class MatchingWithDatabaseTest {

    private SQLiteDatabase db;
    private PantryDao pantryDao;
    private RecipeDao recipeDao;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        pantryDao = new PantryDao(context);
        recipeDao = new RecipeDao(context);
        db = DatabaseHelper.getInstance(context).getWritableDatabase();
        db.beginTransaction();
        // start from an empty pantry inside the transaction (gets undone afterwards)
        db.delete(DatabaseHelper.TABLE_PANTRY, null, null);
    }

    @After
    public void tearDown() {
        db.endTransaction();
    }

    private List<String> makeableNames() {
        List<String> names = new ArrayList<>();
        for (Recipe r : new RecipeMatcher(pantryDao.getAll()).findMakeable(recipeDao.getAll())) {
            names.add(r.getName());
        }
        return names;
    }

    @Test
    public void toastedCheeseSandwichAppearsAndDisappears() {
        assertTrue(makeableNames().isEmpty()); // empty pantry -> nothing

        pantryDao.insert(new PantryItem("Bread", 2, "pcs", null));
        long cheeseId = pantryDao.insert(new PantryItem("Cheddar", 60, "g", null));
        assertFalse(makeableNames().contains("Toasted Cheese Sandwich")); // still no butter

        long butterId = pantryDao.insert(new PantryItem("Butter", 0.25, "kg", null));
        assertTrue(makeableNames().contains("Toasted Cheese Sandwich")); // now it has everything

        // editing the cheese down to 50 g (recipe needs 60 g) should remove it again
        pantryDao.update(new PantryItem(cheeseId, "Cheddar", 50, "g", null));
        assertFalse(makeableNames().contains("Toasted Cheese Sandwich"));

        pantryDao.update(new PantryItem(cheeseId, "Cheddar", 60, "g", null));
        assertTrue(makeableNames().contains("Toasted Cheese Sandwich"));

        // deleting the butter removes it
        pantryDao.delete(butterId);
        assertFalse(makeableNames().contains("Toasted Cheese Sandwich"));
    }
}

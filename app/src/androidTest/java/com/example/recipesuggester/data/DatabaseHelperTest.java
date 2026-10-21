package com.example.recipesuggester.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

// Tests for the database tables, these run on the emulator.
// Each test happens inside a transaction that never gets committed,
// so nothing actually gets saved to the app's database
@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {

    private SQLiteDatabase db;

    @Before
    public void setUp() {
        db = DatabaseHelper.getInstance(
                InstrumentationRegistry.getInstrumentation().getTargetContext())
                .getWritableDatabase();
        db.beginTransaction();
    }

    @After
    public void tearDown() {
        db.endTransaction(); // not calling setTransactionSuccessful() so everything gets rolled back
    }

    @Test
    public void allTablesExist() {
        for (String table : new String[]{DatabaseHelper.TABLE_PANTRY,
                DatabaseHelper.TABLE_RECIPES, DatabaseHelper.TABLE_RECIPE_INGREDIENTS}) {
            try (Cursor c = db.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                    new String[]{table})) {
                assertTrue("missing table " + table, c.moveToFirst());
            }
        }
    }

    @Test
    public void pantryRejectsZeroQuantity() {
        ContentValues v = new ContentValues();
        v.put(DatabaseHelper.COL_PANTRY_NAME, "flour");
        v.put(DatabaseHelper.COL_PANTRY_QUANTITY, 0);
        v.put(DatabaseHelper.COL_PANTRY_UNIT, "g");
        try {
            db.insertOrThrow(DatabaseHelper.TABLE_PANTRY, null, v);
            fail("quantity 0 should be rejected by the CHECK constraint");
        } catch (SQLiteConstraintException expected) {
            // good, it threw an error like it should
        }
    }

    @Test
    public void ingredientNeedsExistingRecipe() {
        ContentValues v = ingredient(999_999, "egg");
        try {
            db.insertOrThrow(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, v);
            fail("foreign key should reject an unknown recipe_id");
        } catch (SQLiteConstraintException expected) {
            // good, it threw an error like it should
        }
    }

    @Test
    public void deletingRecipeDeletesItsIngredients() {
        ContentValues r = new ContentValues();
        r.put(DatabaseHelper.COL_RECIPE_NAME, "Test omelette");
        r.put(DatabaseHelper.COL_RECIPE_DESCRIPTION, "test");
        r.put(DatabaseHelper.COL_RECIPE_INSTRUCTIONS, "test");
        long recipeId = db.insertOrThrow(DatabaseHelper.TABLE_RECIPES, null, r);
        db.insertOrThrow(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, ingredient(recipeId, "egg"));
        db.insertOrThrow(DatabaseHelper.TABLE_RECIPE_INGREDIENTS, null, ingredient(recipeId, "milk"));

        db.delete(DatabaseHelper.TABLE_RECIPES, DatabaseHelper.COL_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)});

        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_RECIPE_INGREDIENTS
                + " WHERE " + DatabaseHelper.COL_RI_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)})) {
            c.moveToFirst();
            assertEquals(0, c.getInt(0));
        }
    }

    private static ContentValues ingredient(long recipeId, String name) {
        ContentValues v = new ContentValues();
        v.put(DatabaseHelper.COL_RI_RECIPE_ID, recipeId);
        v.put(DatabaseHelper.COL_RI_NAME, name);
        v.put(DatabaseHelper.COL_RI_QUANTITY, 2);
        v.put(DatabaseHelper.COL_RI_UNIT, "pcs");
        return v;
    }
}

package com.example.recipesuggester.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.recipesuggester.model.PantryItem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

// tests the pantry CRUD methods on the emulator. same trick as DatabaseHelperTest,
// everything happens in a transaction that gets rolled back so the real pantry isn't touched
@RunWith(AndroidJUnit4.class)
public class PantryDaoTest {

    private SQLiteDatabase db;
    private PantryDao dao;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dao = new PantryDao(context);
        db = DatabaseHelper.getInstance(context).getWritableDatabase();
        db.beginTransaction();
    }

    @After
    public void tearDown() {
        db.endTransaction(); // rolled back
    }

    @Test
    public void insertThenRead() {
        int before = dao.getAll().size();
        long id = dao.insert(new PantryItem("Test flour", 500, "g", "2026-12-31"));

        PantryItem saved = dao.getById(id);
        assertEquals("Test flour", saved.getName());
        assertEquals(500, saved.getQuantity(), 0.0001);
        assertEquals("g", saved.getUnit());
        assertEquals("2026-12-31", saved.getExpiryDate());
        assertEquals(before + 1, dao.getAll().size());
    }

    @Test
    public void expiryCanBeEmpty() {
        long id = dao.insert(new PantryItem("Test salt", 1, "kg", null));
        assertNull(dao.getById(id).getExpiryDate());
    }

    @Test
    public void updateChangesTheSameRowInsteadOfAddingOne() {
        long id = dao.insert(new PantryItem("Test milk", 1, "l", null));
        int countAfterInsert = dao.getAll().size();

        PantryItem edited = new PantryItem(id, "Test milk", 250, "ml", "2026-11-01");
        assertEquals(1, dao.update(edited));

        assertEquals(countAfterInsert, dao.getAll().size()); // no duplicate
        PantryItem saved = dao.getById(id);
        assertEquals(250, saved.getQuantity(), 0.0001);
        assertEquals("ml", saved.getUnit());
        assertEquals("2026-11-01", saved.getExpiryDate());
    }

    @Test
    public void deleteRemovesTheRow() {
        long id = dao.insert(new PantryItem("Test eggs", 6, "pcs", null));
        assertEquals(1, dao.delete(id));
        assertNull(dao.getById(id));
    }
}

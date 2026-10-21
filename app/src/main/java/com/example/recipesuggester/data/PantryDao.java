package com.example.recipesuggester.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.recipesuggester.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

// all the create/read/update/delete stuff for the pantry_items table.
// keeping the SQL in here means the activities don't have to deal with cursors
public class PantryDao {

    private final DatabaseHelper dbHelper;

    public PantryDao(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    // CREATE - returns the new row's id. insertOrThrow throws an SQLException if
    // it fails (normal insert() just returns -1 which is easy to miss)
    public long insert(PantryItem item) throws SQLException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = db.insertOrThrow(DatabaseHelper.TABLE_PANTRY, null, toValues(item));
        item.setId(id);
        return id;
    }

    // READ - every item, sorted by name so the list is easy to scan
    public List<PantryItem> getAll() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // COLLATE NOCASE so "apple" and "Banana" sort properly together
        try (Cursor c = db.query(DatabaseHelper.TABLE_PANTRY, null, null, null, null, null,
                DatabaseHelper.COL_PANTRY_NAME + " COLLATE NOCASE ASC")) {
            while (c.moveToNext()) {
                items.add(fromCursor(c));
            }
        }
        return items;
    }

    // READ one item, returns null if it doesn't exist anymore (e.g. it got deleted)
    public PantryItem getById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor c = db.query(DatabaseHelper.TABLE_PANTRY, null,
                DatabaseHelper.COL_PANTRY_ID + " = ?", new String[]{String.valueOf(id)},
                null, null, null)) {
            return c.moveToFirst() ? fromCursor(c) : null;
        }
    }

    // UPDATE - changes the existing row with this id instead of adding a new one,
    // that's how editing doesn't create duplicates. returns how many rows changed
    public int update(PantryItem item) throws SQLException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.update(DatabaseHelper.TABLE_PANTRY, toValues(item),
                DatabaseHelper.COL_PANTRY_ID + " = ?", new String[]{String.valueOf(item.getId())});
    }

    // DELETE - returns how many rows got deleted (should be 1)
    public int delete(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_PANTRY,
                DatabaseHelper.COL_PANTRY_ID + " = ?", new String[]{String.valueOf(id)});
    }

    // puts the item's fields into ContentValues so I can insert/update with them.
    // the id isn't included because SQLite makes that itself
    private static ContentValues toValues(PantryItem item) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_PANTRY_NAME, item.getName());
        values.put(DatabaseHelper.COL_PANTRY_QUANTITY, item.getQuantity());
        values.put(DatabaseHelper.COL_PANTRY_UNIT, item.getUnit());
        values.put(DatabaseHelper.COL_PANTRY_EXPIRY, item.getExpiryDate()); // can be null
        return values;
    }

    // reads the current cursor row back into a PantryItem
    private static PantryItem fromCursor(Cursor c) {
        int expiryCol = c.getColumnIndexOrThrow(DatabaseHelper.COL_PANTRY_EXPIRY);
        return new PantryItem(
                c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COL_PANTRY_ID)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_PANTRY_NAME)),
                c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COL_PANTRY_QUANTITY)),
                c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_PANTRY_UNIT)),
                c.isNull(expiryCol) ? null : c.getString(expiryCol));
    }
}

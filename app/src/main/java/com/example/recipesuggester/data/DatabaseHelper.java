package com.example.recipesuggester.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Database helper for the app. It makes the smart_pantry.db file and all the tables.
//
// pantry_items       - the ingredients the user has at home
// recipes            - all the recipes (these get added the first time the app runs)
// recipe_ingredients - what each recipe needs. one recipe has lots of these rows,
//                      linked back to the recipe with recipe_id
//
// Settings aren't stored in here, I'm using SharedPreferences for those because
// it's only a couple of on/off values
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // pantry table columns
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String COL_PANTRY_ID = "_id";
    public static final String COL_PANTRY_NAME = "name";
    public static final String COL_PANTRY_QUANTITY = "quantity";
    public static final String COL_PANTRY_UNIT = "unit";
    public static final String COL_PANTRY_EXPIRY = "expiry_date"; // "yyyy-MM-dd" or NULL

    // recipes table columns
    public static final String TABLE_RECIPES = "recipes";
    public static final String COL_RECIPE_ID = "_id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_DESCRIPTION = "description";
    public static final String COL_RECIPE_INSTRUCTIONS = "instructions";

    // recipe ingredients table columns
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";
    public static final String COL_RI_ID = "_id";
    public static final String COL_RI_RECIPE_ID = "recipe_id";
    public static final String COL_RI_NAME = "ingredient_name";
    public static final String COL_RI_QUANTITY = "quantity";
    public static final String COL_RI_UNIT = "unit";

    private static final String CREATE_PANTRY = "CREATE TABLE " + TABLE_PANTRY + " ("
            + COL_PANTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_PANTRY_NAME + " TEXT NOT NULL, "
            + COL_PANTRY_QUANTITY + " REAL NOT NULL CHECK (" + COL_PANTRY_QUANTITY + " > 0), "
            + COL_PANTRY_UNIT + " TEXT NOT NULL, "
            + COL_PANTRY_EXPIRY + " TEXT)";

    private static final String CREATE_RECIPES = "CREATE TABLE " + TABLE_RECIPES + " ("
            + COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_RECIPE_NAME + " TEXT NOT NULL UNIQUE, "
            + COL_RECIPE_DESCRIPTION + " TEXT NOT NULL, "
            + COL_RECIPE_INSTRUCTIONS + " TEXT NOT NULL)";

    // ON DELETE CASCADE means if a recipe gets deleted, its ingredients get deleted too
    private static final String CREATE_RECIPE_INGREDIENTS = "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " ("
            + COL_RI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_RI_RECIPE_ID + " INTEGER NOT NULL REFERENCES " + TABLE_RECIPES
            + "(" + COL_RECIPE_ID + ") ON DELETE CASCADE, "
            + COL_RI_NAME + " TEXT NOT NULL, "
            + COL_RI_QUANTITY + " REAL NOT NULL CHECK (" + COL_RI_QUANTITY + " > 0), "
            + COL_RI_UNIT + " TEXT NOT NULL)";

    // index on recipe_id so finding a recipe's ingredients is faster
    private static final String CREATE_RI_INDEX = "CREATE INDEX idx_recipe_ingredients_recipe ON "
            + TABLE_RECIPE_INGREDIENTS + "(" + COL_RI_RECIPE_ID + ")";

    private static DatabaseHelper instance;

    // only one helper for the whole app so there aren't a bunch of connections
    // open to the same database file
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // foreign keys are off by default in SQLite, so they have to be turned on here
        db.setForeignKeyConstraintsEnabled(true);
    }

    // this only runs the first time the database gets created
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PANTRY);
        db.execSQL(CREATE_RECIPES);
        db.execSQL(CREATE_RECIPE_INGREDIENTS);
        db.execSQL(CREATE_RI_INDEX);
    }

    // runs when DATABASE_VERSION goes up. since it's only version 1 I'm just dropping
    // and remaking the tables (this would wipe the pantry though, so it would need a
    // proper migration if I ever change the tables after people are using it)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }
}

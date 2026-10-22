# Smart Pantry Manager

An Android app for my Mobile App Development 700 assignment at Richfield.

You enter the ingredients you have at home and the app shows recipes you can
make with them. A recipe only shows up if you have every ingredient it needs,
in sufficient quantities. The idea is to use up what's already in the kitchen
instead of throwing food away.

## Features

- Pantry list: add, edit and delete ingredients (name, quantity, unit and an optional expiry date)
- Form validation (no empty names, no zero or negative amounts, a unit has to be picked)
- 22 recipes that get loaded into the database the first time the app opens
- Suggested Recipes: only shows recipes where you have every ingredient in a big enough amount
- Matching handles plurals ("Tomatoes" = "tomato"), capitals, a few other names
  (e.g. "ground beef" = "minced beef") and converts g/kg and ml/L
- Recipe detail screen with the ingredients and the method
- Settings: turn expiry alerts on/off and choose how many days before to warn.
  Expired items show in red and ones about to expire in orange
- Works offline, nothing leaves the phone

## Built with

- Java
- Android Studio
- XML layouts
- SQLite (SQLiteOpenHelper)
- Min SDK 24 (Android 7.0), target SDK 37

## Database

I used **SQLite** with `SQLiteOpenHelper`. I picked it because:

- it's built into Android so there's nothing extra to set up, no account or server
- the app works with no internet connection
- the data is relational (one recipe has many ingredients), which suits SQL tables
- it's what we covered in the module's persistent data chapter

Tables:

| Table | What it stores |
|---|---|
| `pantry_items` | the user's ingredients: `_id`, `name`, `quantity`, `unit`, `expiry_date` (optional) |
| `recipes` | `_id`, `name` (unique), `description`, `instructions` |
| `recipe_ingredients` | what each recipe needs: `_id`, `recipe_id` (foreign key to `recipes`), `ingredient_name`, `quantity`, `unit` |

One recipe has many rows in `recipe_ingredients`. The recipes are inserted in
`DatabaseHelper.onCreate()`, which only runs once, so they don't get added again
every time the app opens. The two settings are saved with SharedPreferences
instead of a table since it's just two values.

## How to run

1. Clone the repo and open the folder in Android Studio.
2. Wait for Gradle to sync.
3. Run the app on an emulator or phone (Android 7.0 or newer).

To build from the command line instead (Windows):

```
gradlew.bat assembleDebug
```

The APK ends up in `app/build/outputs/apk/debug/`.

## Repo

https://github.com/RohanLeePatel/RecipeSuggester

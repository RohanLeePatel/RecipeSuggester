package com.example.recipesuggester;

import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recipesuggester.data.PantryDao;
import com.example.recipesuggester.data.RecipeDao;
import com.example.recipesuggester.model.Recipe;
import com.example.recipesuggester.model.RecipeIngredient;
import com.example.recipesuggester.util.RecipeMatcher;
import com.example.recipesuggester.util.SystemBars;

// shows one recipe: name, description, ingredients and the method.
// it gets the recipe id from SuggestedRecipesActivity's intent
public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "com.example.recipesuggester.EXTRA_RECIPE_ID";
    private static final String TAG = "RecipeDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemBars.enableEdgeToEdge(this);
        setContentView(R.layout.activity_recipe_detail);
        SystemBars.setUpToolbar(this, findViewById(R.id.main), findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        Recipe recipe;
        boolean canMake;
        try {
            recipe = new RecipeDao(this).getById(recipeId);
            // check AGAIN here with the pantry as it is right now. the suggestions list could
            // be out of date by the time the user taps (e.g. the pantry changed in between),
            // and this screen should only open for recipes they can actually make
            canMake = recipe != null
                    && new RecipeMatcher(new PantryDao(this).getAll()).canMake(recipe);
        } catch (SQLException e) {
            Log.e(TAG, "loading recipe " + recipeId + " failed", e);
            Toast.makeText(this, R.string.error_loading_recipes, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (recipe == null) {
            Toast.makeText(this, R.string.recipe_not_found, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (!canMake) {
            Toast.makeText(this, getString(R.string.recipe_no_longer_available, recipe.getName()),
                    Toast.LENGTH_LONG).show();
            finish(); // goes back to the suggestions, which reload in onResume and drop it
            return;
        }

        showRecipe(recipe);
    }

    private void showRecipe(Recipe recipe) {
        getSupportActionBar().setTitle(recipe.getName());
        ((TextView) findViewById(R.id.detail_name)).setText(recipe.getName());
        ((TextView) findViewById(R.id.detail_description)).setText(recipe.getDescription());

        // one ingredient per line with a bullet, e.g. "•  200 g flour"
        StringBuilder ingredients = new StringBuilder();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            if (ingredients.length() > 0) ingredients.append('\n');
            ingredients.append("•  ").append(ingredient);
        }
        ((TextView) findViewById(R.id.detail_ingredients)).setText(ingredients);

        // the instructions are stored one step per line, so number each line
        StringBuilder method = new StringBuilder();
        String[] steps = recipe.getInstructions().split("\n");
        for (int i = 0; i < steps.length; i++) {
            if (i > 0) method.append("\n\n");
            method.append(i + 1).append(". ").append(steps[i].trim());
        }
        ((TextView) findViewById(R.id.detail_method)).setText(method);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

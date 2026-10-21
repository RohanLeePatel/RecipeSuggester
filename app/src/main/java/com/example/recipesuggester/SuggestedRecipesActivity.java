package com.example.recipesuggester;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipesuggester.adapter.RecipeAdapter;
import com.example.recipesuggester.data.PantryDao;
import com.example.recipesuggester.data.RecipeDao;
import com.example.recipesuggester.model.Recipe;
import com.example.recipesuggester.util.RecipeMatcher;
import com.example.recipesuggester.util.SystemBars;

import java.util.List;

// shows ONLY the recipes the user can make right now with what's in their pantry.
// the actual decision is made by RecipeMatcher, this screen just displays the result
public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.Listener {

    private static final String TAG = "SuggestedRecipes";

    private PantryDao pantryDao;
    private RecipeDao recipeDao;
    private RecipeAdapter adapter;
    private RecyclerView list;
    private View emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemBars.enableEdgeToEdge(this);
        setContentView(R.layout.activity_suggested_recipes);
        SystemBars.setUpToolbar(this, findViewById(R.id.main), findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pantryDao = new PantryDao(this);
        recipeDao = new RecipeDao(this);

        list = findViewById(R.id.recipe_list);
        emptyState = findViewById(R.id.empty_state);
        adapter = new RecipeAdapter(this);
        list.setAdapter(adapter);
    }

    // same idea as the pantry screen - re-run the matching every time this screen comes
    // back to the front, so if the pantry changed the suggestions are always up to date
    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        try {
            // fresh from the database every time, nothing cached
            RecipeMatcher matcher = new RecipeMatcher(pantryDao.getAll());
            List<Recipe> makeable = matcher.findMakeable(recipeDao.getAll());
            adapter.setRecipes(makeable);
            boolean empty = makeable.isEmpty();
            list.setVisibility(empty ? View.GONE : View.VISIBLE);
            emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        } catch (SQLException e) {
            Log.e(TAG, "loading suggestions failed", e);
            Toast.makeText(this, R.string.error_loading_recipes, Toast.LENGTH_LONG).show();
        }
    }

    // explicit intent to the detail screen, passing just the recipe id.
    // the detail screen loads the recipe itself so it always has the latest data
    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

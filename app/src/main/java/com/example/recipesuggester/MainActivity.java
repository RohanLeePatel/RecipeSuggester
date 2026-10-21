package com.example.recipesuggester;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipesuggester.adapter.PantryAdapter;
import com.example.recipesuggester.data.PantryDao;
import com.example.recipesuggester.model.PantryItem;
import com.example.recipesuggester.util.SystemBars;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

// Pantry list screen (the first screen you see). shows everything in pantry_items
// in a RecyclerView, + button to add, tap a card to edit, bin icon to delete
public class MainActivity extends AppCompatActivity implements PantryAdapter.Listener {

    private static final String TAG = "MainActivity";

    private PantryDao pantryDao;
    private PantryAdapter adapter;
    private RecyclerView list;
    private View emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemBars.enableEdgeToEdge(this);
        setContentView(R.layout.activity_main);
        SystemBars.setUpToolbar(this, findViewById(R.id.main), findViewById(R.id.toolbar));

        pantryDao = new PantryDao(this);

        list = findViewById(R.id.pantry_list);
        emptyState = findViewById(R.id.empty_state);
        adapter = new PantryAdapter(this);
        list.setAdapter(adapter);

        // explicit intent = I'm naming the exact activity to open.
        // no extras means the form opens in "add" mode
        findViewById(R.id.fab_add).setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));
    }

    // onResume runs every time this screen comes back to the front, including when the
    // user finishes on the add/edit screen. so reloading here means the list always shows
    // what's actually in the database, I don't need to pass results back
    @Override
    protected void onResume() {
        super.onResume();
        loadPantry();
    }

    private void loadPantry() {
        // reading on the main thread is ok for a small pantry list, it would need
        // a background thread if the table ever got really big
        try {
            List<PantryItem> items = pantryDao.getAll();
            adapter.setItems(items);
            // swap between the list and the "pantry is empty" message
            boolean empty = items.isEmpty();
            list.setVisibility(empty ? View.GONE : View.VISIBLE);
            emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        } catch (SQLException e) {
            Log.e(TAG, "loading pantry failed", e);
            Toast.makeText(this, R.string.error_loading, Toast.LENGTH_LONG).show();
        }
    }

    // puts the recipes button in the toolbar (from res/menu/menu_main.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_recipes) {
            startActivity(new Intent(this, SuggestedRecipesActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // tapping a card opens the same form but in edit mode, by passing the item's id
    @Override
    public void onItemClick(PantryItem item) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    // deleting can't be undone so ask first
    @Override
    public void onDeleteClick(PantryItem item) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_title)
                .setMessage(getString(R.string.delete_message, item.getName()))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteItem(item))
                .show();
    }

    private void deleteItem(PantryItem item) {
        try {
            pantryDao.delete(item.getId());
            Snackbar.make(list, getString(R.string.deleted, item.getName()), Snackbar.LENGTH_SHORT).show();
        } catch (SQLException e) {
            Log.e(TAG, "deleting item " + item.getId() + " failed", e);
            Toast.makeText(this, R.string.error_deleting, Toast.LENGTH_LONG).show();
        }
        loadPantry();
    }
}

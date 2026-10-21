package com.example.recipesuggester;

import android.app.DatePickerDialog;
import android.database.SQLException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recipesuggester.data.PantryDao;
import com.example.recipesuggester.model.PantryItem;
import com.example.recipesuggester.util.IngredientValidator;
import com.example.recipesuggester.util.SystemBars;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

// form for adding a new ingredient OR editing one that's already in the pantry.
// if MainActivity sends an item id in the intent it's edit mode, otherwise it's add mode
public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "com.example.recipesuggester.EXTRA_ITEM_ID";
    private static final String TAG = "AddEditIngredient";

    private PantryDao pantryDao;
    private long itemId = -1; // stays -1 in add mode

    private TextInputLayout nameLayout, quantityLayout, unitLayout, expiryLayout;
    private EditText nameInput, quantityInput, expiryInput;
    private MaterialAutoCompleteTextView unitInput;
    private MaterialButton clearDateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemBars.enableEdgeToEdge(this);
        setContentView(R.layout.activity_add_edit_ingredient);
        SystemBars.setUpToolbar(this, findViewById(R.id.main), findViewById(R.id.toolbar));
        // back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pantryDao = new PantryDao(this);

        nameLayout = findViewById(R.id.layout_name);
        quantityLayout = findViewById(R.id.layout_quantity);
        unitLayout = findViewById(R.id.layout_unit);
        expiryLayout = findViewById(R.id.layout_expiry);
        nameInput = findViewById(R.id.input_name);
        quantityInput = findViewById(R.id.input_quantity);
        unitInput = findViewById(R.id.input_unit);
        expiryInput = findViewById(R.id.input_expiry);
        clearDateButton = findViewById(R.id.button_clear_date);

        // get rid of a field's red error as soon as the user starts fixing it
        clearErrorWhenEdited(nameInput, nameLayout);
        clearErrorWhenEdited(quantityInput, quantityLayout);
        clearErrorWhenEdited(unitInput, unitLayout);
        clearErrorWhenEdited(expiryInput, expiryLayout);

        expiryInput.setOnClickListener(v -> showDatePicker());
        clearDateButton.setOnClickListener(v -> expiryInput.setText(""));
        findViewById(R.id.button_save).setOnClickListener(v -> save());

        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (itemId != -1) {
            // has to go through the action bar, setTitle() on the activity didn't change
            // the toolbar because the layout already sets app:title
            getSupportActionBar().setTitle(R.string.title_edit_ingredient);
            // only fill the form from the database the first time. after a rotation
            // android restores what the user typed by itself, and I don't want to overwrite that
            if (savedInstanceState == null && !loadItem()) {
                return;
            }
        }
        updateClearDateButton();
    }

    // fills the form with the item being edited. returns false if it couldn't be found
    private boolean loadItem() {
        PantryItem item = pantryDao.getById(itemId);
        if (item == null) {
            // e.g. it got deleted somehow, so there's nothing to edit
            Toast.makeText(this, R.string.item_not_found, Toast.LENGTH_LONG).show();
            finish();
            return false;
        }
        nameInput.setText(item.getName());
        quantityInput.setText(PantryItem.formatQuantity(item.getQuantity()));
        unitInput.setText(item.getUnit(), false); // false = don't pop the dropdown open
        expiryInput.setText(item.getExpiryDate() == null ? "" : item.getExpiryDate());
        return true;
    }

    private void showDatePicker() {
        // start the picker on the date that's already set, or today if there isn't one
        Calendar cal = Calendar.getInstance();
        String current = expiryInput.getText().toString();
        if (IngredientValidator.validateExpiryDate(current) == null && !current.isEmpty()) {
            String[] parts = current.split("-");
            cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
        }
        new DatePickerDialog(this, (picker, year, month, day) ->
                // month starts at 0 in DatePicker so +1. Locale.US so the digits are always 0-9
                expiryInput.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void save() {
        String name = nameInput.getText().toString().trim();
        String quantityText = quantityInput.getText().toString();
        String unit = unitInput.getText().toString();
        String expiry = expiryInput.getText().toString();

        // check every field so the user sees all the problems at once, not one at a time
        String nameError = IngredientValidator.validateName(name);
        String quantityError = IngredientValidator.validateQuantity(quantityText);
        String unitError = IngredientValidator.validateUnit(unit);
        String expiryError = IngredientValidator.validateExpiryDate(expiry);
        nameLayout.setError(nameError);
        quantityLayout.setError(quantityError);
        unitLayout.setError(unitError);
        expiryLayout.setError(expiryError);
        if (nameError != null || quantityError != null || unitError != null || expiryError != null) {
            return;
        }

        PantryItem item = new PantryItem(name, IngredientValidator.parseQuantity(quantityText),
                unit, expiry.isEmpty() ? null : expiry);

        try {
            if (itemId == -1) {
                pantryDao.insert(item);
            } else {
                // edit mode: keep the same id so the row gets UPDATEd, not added a second time
                item.setId(itemId);
                if (pantryDao.update(item) == 0) {
                    Toast.makeText(this, R.string.item_not_found, Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "saving ingredient failed", e);
            Toast.makeText(this, R.string.error_saving, Toast.LENGTH_LONG).show();
            return; // stay on the form so they don't lose what they typed
        }

        Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
        // finishing goes back to MainActivity, which reloads the list in onResume
        finish();
    }

    // only show "Clear date" when there's actually a date to clear
    private void updateClearDateButton() {
        clearDateButton.setVisibility(
                TextUtils.isEmpty(expiryInput.getText()) ? View.GONE : View.VISIBLE);
    }

    private void clearErrorWhenEdited(EditText input, TextInputLayout layout) {
        input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                layout.setError(null);
                if (input == expiryInput) {
                    updateClearDateButton();
                }
            }
        });
    }

    // toolbar back arrow just closes the form without saving
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

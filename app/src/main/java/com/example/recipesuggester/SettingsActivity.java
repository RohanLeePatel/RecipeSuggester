package com.example.recipesuggester;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.recipesuggester.util.Settings;
import com.example.recipesuggester.util.SystemBars;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

// settings screen: expiry alerts on/off, how many days before to warn, and app info.
// every change is saved straight away, there's no save button
public class SettingsActivity extends AppCompatActivity {

    private Settings settings;
    private TextInputLayout warningDaysLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemBars.enableEdgeToEdge(this);
        setContentView(R.layout.activity_settings);
        SystemBars.setUpToolbar(this, findViewById(R.id.main), findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settings = new Settings(this);

        // expiry alerts switch - show the saved value, save whenever it's flipped
        MaterialSwitch alertsSwitch = findViewById(R.id.switch_expiry_alerts);
        warningDaysLayout = findViewById(R.id.layout_warning_days);
        alertsSwitch.setChecked(settings.isExpiryAlertsOn());
        warningDaysLayout.setEnabled(settings.isExpiryAlertsOn());
        alertsSwitch.setOnCheckedChangeListener((button, isChecked) -> {
            settings.setExpiryAlertsOn(isChecked);
            // the days choice only matters when alerts are on, so grey it out otherwise
            warningDaysLayout.setEnabled(isChecked);
        });

        setUpWarningDays();
        showAppInfo();
    }

    // dropdown with "1 day", "3 days", "7 days"
    private void setUpWarningDays() {
        MaterialAutoCompleteTextView daysInput = findViewById(R.id.input_warning_days);
        int[] options = Settings.WARNING_DAY_OPTIONS;
        String[] labels = new String[options.length];
        for (int i = 0; i < options.length; i++) {
            labels[i] = getResources().getQuantityString(R.plurals.day_count, options[i], options[i]);
        }
        daysInput.setSimpleItems(labels);
        daysInput.setText(getResources().getQuantityString(R.plurals.day_count,
                settings.getWarningDays(), settings.getWarningDays()), false);
        daysInput.setOnItemClickListener((parent, view, position, id) ->
                settings.setWarningDays(options[position]));
    }

    private void showAppInfo() {
        String version;
        try {
            // the version comes from versionName in app/build.gradle.kts
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "?"; // can't really happen, it's asking about this app itself
        }
        ((TextView) findViewById(R.id.text_version)).setText(getString(R.string.settings_version, version));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

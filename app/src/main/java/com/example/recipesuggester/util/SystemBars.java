package com.example.recipesuggester.util;

import android.graphics.Color;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

// every screen has the same green toolbar at the top, so this does the
// edge-to-edge + toolbar setup once instead of copying it into every activity
public class SystemBars {

    // call this BEFORE setContentView
    public static void enableEdgeToEdge(AppCompatActivity activity) {
        // dark() gives white status bar icons so they show up on the green toolbar
        EdgeToEdge.enable(activity, SystemBarStyle.dark(Color.TRANSPARENT));
    }

    // call this AFTER setContentView. root is the layout's outer view
    public static void setUpToolbar(AppCompatActivity activity, View root, MaterialToolbar toolbar) {
        activity.setSupportActionBar(toolbar);

        // since the app draws behind the status bar and nav bar, I have to add the padding
        // myself, otherwise the toolbar ends up under the status bar.
        // the keyboard (ime) is included so it doesn't cover the form fields
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, 0, bars.right, Math.max(bars.bottom, ime.bottom));
            toolbar.setPadding(0, bars.top, 0, 0);
            return insets;
        });
    }
}

package aeu.ratha.beckie_deal_app.utils.helpers;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import aeu.ratha.beckie_deal_app.utils.preferences.ThemePreferences;

/**
 * Base activity for the app.
 *
 * Dark mode is now handled by letting Android naturally recreate activities
 * when AppCompatDelegate.setDefaultNightMode() is called. No manual
 * configChanges or recreate() needed — this avoids the infinite recreation
 * loop that occurred when multiple activities in the back-stack were
 * fighting over recreation.
 *
 * The saved dark mode preference is restored here so it applies to every
 * activity on app launch.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Restore the user's saved dark mode preference before the UI is drawn.
        ThemePreferences.applySavedTheme(this);
        super.onCreate(savedInstanceState);
    }
}

package aeu.ratha.beckie_deal_app;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Base activity for the app.
 *
 * Dark mode is now handled by letting Android naturally recreate activities
 * when AppCompatDelegate.setDefaultNightMode() is called. No manual
 * configChanges or recreate() needed — this avoids the infinite recreation
 * loop that occurred when multiple activities in the back-stack were
 * fighting over recreation.
 */
public abstract class BaseActivity extends AppCompatActivity {
}

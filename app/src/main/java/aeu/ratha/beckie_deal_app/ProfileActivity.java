package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.materialswitch.MaterialSwitch;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupTopBar();
        setupActionButtons();
        setupDarkModeSwitch();
        setupBottomNavigation();
    }

    private void setupTopBar() {
        ImageView menuIcon = findViewById(R.id.menuIcon);
        ImageView cartIcon = findViewById(R.id.cartIcon);

        if (menuIcon != null) menuIcon.setOnClickListener(v -> Toast.makeText(this, "Menu", Toast.LENGTH_SHORT).show());
        if (cartIcon != null) cartIcon.setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });
    }

    private void setupActionButtons() {
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);

        if (loginButton != null) {
            loginButton.setOnClickListener(v -> {
                // Login action
            });
        }
        
        if (registerButton != null) {
            registerButton.setOnClickListener(v -> {
                // Register action
            });
        }
    }

    private void setupDarkModeSwitch() {
        // Changed to MaterialSwitch to match activity_profile.xml and fix compilation
        MaterialSwitch darkModeSwitch = findViewById(R.id.darkModeSwitch);
        if (darkModeSwitch == null) return;

        // Set current state based on system configuration
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        darkModeSwitch.setChecked(currentNightMode == Configuration.UI_MODE_NIGHT_YES);

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;

        bottomNav.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        com.google.android.material.card.MaterialCardView container = findViewById(R.id.bottomNavigationContainer);
        if (container != null) container.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(this, R.color.surface));

        bottomNav.setSelectedItemId(R.id.nav_account);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_categories) {
                startActivity(new Intent(this, CategoriesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_about) {
                startActivity(new Intent(this, AboutActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_account) {
                return true;
            }
            return false;
        });
    }
}

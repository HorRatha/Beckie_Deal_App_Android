package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupTopBar();
        setupActionButtons();
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
                startActivity(new Intent(this, LoginActivity.class));
            });
        }
        
        if (registerButton != null) {
            registerButton.setOnClickListener(v -> {
                startActivity(new Intent(this, RegisterActivity.class));
            });
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;

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

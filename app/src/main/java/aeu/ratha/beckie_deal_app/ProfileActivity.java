package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupTopBar();
        setupAuthButtons();
        setupDarkModeSwitch();
        setupSettingsRows();
        setupBottomNavigation();
    }

    private void setupTopBar() {
        ImageView menuIcon = findViewById(R.id.menuIcon);
        View cartContainer = findViewById(R.id.cartContainer);
        TextView cartBadge = findViewById(R.id.cartBadge);

        if (menuIcon != null) menuIcon.setOnClickListener(v ->
                Toast.makeText(this, "Menu", Toast.LENGTH_SHORT).show());
        if (cartContainer != null) cartContainer.setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));

        int count = CartManager.getInstance().getTotalUnits();
        if (cartBadge != null) {
            cartBadge.setText(String.valueOf(count));
            cartBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void setupAuthButtons() {
        MaterialButton loginButton    = findViewById(R.id.loginButton);
        MaterialButton registerButton = findViewById(R.id.registerButton);

        if (loginButton != null) loginButton.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
        if (registerButton != null) registerButton.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void setupDarkModeSwitch() {
        MaterialSwitch darkModeSwitch = findViewById(R.id.darkModeSwitch);
        if (darkModeSwitch == null) return;

        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        darkModeSwitch.setChecked(currentNightMode == Configuration.UI_MODE_NIGHT_YES);

        darkModeSwitch.setOnCheckedChangeListener((btn, isChecked) ->
                AppCompatDelegate.setDefaultNightMode(isChecked
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO));
    }

    private void setupSettingsRows() {
        LinearLayout ordersRow        = findViewById(R.id.ordersRow);
        LinearLayout notificationsRow = findViewById(R.id.notificationsRow);
        LinearLayout languageRow      = findViewById(R.id.languageRow);
        LinearLayout privacyRow       = findViewById(R.id.privacyRow);
        LinearLayout termsRow         = findViewById(R.id.termsRow);

        if (ordersRow != null) ordersRow.setOnClickListener(v ->
                Toast.makeText(this, "My Orders — coming soon", Toast.LENGTH_SHORT).show());
        if (notificationsRow != null) notificationsRow.setOnClickListener(v ->
                Toast.makeText(this, "Notification settings", Toast.LENGTH_SHORT).show());
        if (languageRow != null) languageRow.setOnClickListener(v ->
                Toast.makeText(this, "Language settings", Toast.LENGTH_SHORT).show());
        if (privacyRow != null) privacyRow.setOnClickListener(v ->
                Toast.makeText(this, "Privacy Policy", Toast.LENGTH_SHORT).show());
        if (termsRow != null) termsRow.setOnClickListener(v ->
                Toast.makeText(this, "Terms of Service", Toast.LENGTH_SHORT).show());
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;

        bottomNav.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        MaterialCardView container = findViewById(R.id.bottomNavigationContainer);
        if (container != null) container.setCardBackgroundColor(
                androidx.core.content.ContextCompat.getColor(this, R.color.surface));

        bottomNav.setSelectedItemId(R.id.nav_account);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_categories) {
                startActivity(new Intent(this, CategoriesActivity.class));
            } else if (id == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(this, AboutActivity.class));
            } else {
                return true;
            }
            overridePendingTransition(0, 0);
            finish();
            return true;
        });
    }
}

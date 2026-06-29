package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setupTopBar();
        setupBottomNavigation();
    }

    private void setupTopBar() {
        ImageView menuIcon = findViewById(R.id.menuIcon);
        View cartContainer = findViewById(R.id.cartContainer);
        TextView cartBadge = findViewById(R.id.cartBadge);

        if (menuIcon != null) menuIcon.setOnClickListener(v -> Toast.makeText(this, "Menu", Toast.LENGTH_SHORT).show());
        if (cartContainer != null) cartContainer.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));

        int count = CartManager.getInstance().getTotalUnits();
        if (cartBadge != null) {
            cartBadge.setText(String.valueOf(count));
            cartBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;

        bottomNav.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        com.google.android.material.card.MaterialCardView container = findViewById(R.id.bottomNavigationContainer);
        if (container != null) container.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(this, R.color.surface));

        bottomNav.setSelectedItemId(R.id.nav_about);

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
                return true;
            } else if (itemId == R.id.nav_account) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }
}

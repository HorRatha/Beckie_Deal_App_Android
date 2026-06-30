package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

public class AboutActivity extends AppCompatActivity {

    // Headquarters coordinates — Capitol Hill, Seattle
    private static final double HQ_LAT  = 47.6205;
    private static final double HQ_LNG  = -122.3212;
    private static final String HQ_LABEL = "TechStore HQ, Capitol Hill, Seattle, WA 98102";

    private EditText inputName, inputEmail, inputMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        inputName    = findViewById(R.id.inputName);
        inputEmail   = findViewById(R.id.inputEmail);
        inputMessage = findViewById(R.id.inputMessage);

        setupTopBar();
        setupSubmitButton();
        setupMapCard();
        setupBottomNavigation();
    }

    private void setupTopBar() {
        ImageView menuIcon       = findViewById(R.id.menuIcon);
        View      cartContainer  = findViewById(R.id.cartContainer);
        TextView  cartBadge      = findViewById(R.id.cartBadge);

        if (menuIcon != null)
            menuIcon.setOnClickListener(v ->
                Toast.makeText(this, "Menu", Toast.LENGTH_SHORT).show());

        if (cartContainer != null)
            cartContainer.setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));

        int count = CartManager.getInstance().getTotalUnits();
        if (cartBadge != null) {
            cartBadge.setText(String.valueOf(count));
            cartBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void setupSubmitButton() {
        Button btnSubmit = findViewById(R.id.btnSubmit);
        if (btnSubmit == null) return;

        btnSubmit.setOnClickListener(v -> {
            String name    = inputName.getText().toString().trim();
            String email   = inputEmail.getText().toString().trim();
            String message = inputMessage.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                inputName.setError("Please enter your name");
                inputName.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                inputEmail.setError("Please enter a valid email");
                inputEmail.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(message)) {
                inputMessage.setError("Please enter your message");
                inputMessage.requestFocus();
                return;
            }

            // Compose email intent
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:support@techstore.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry from " + name);
            emailIntent.putExtra(Intent.EXTRA_TEXT,
                "Name: " + name + "\nEmail: " + email + "\n\n" + message);

            try {
                startActivity(Intent.createChooser(emailIntent, "Send email via..."));
            } catch (android.content.ActivityNotFoundException e) {
                Toast.makeText(this, "No email app found.", Toast.LENGTH_SHORT).show();
            }

            // Clear fields
            inputName.setText("");
            inputEmail.setText("");
            inputMessage.setText("");
            Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupMapCard() {
        MaterialCardView mapCard = findViewById(R.id.mapCard);
        ImageView mapImage       = findViewById(R.id.mapImage);

        // Load a real map snapshot via Google Maps Static API
        // Replace YOUR_API_KEY with your actual Maps Static API key.
        // If you don't have one yet the card still works — tapping opens the Maps app.
        String staticMapUrl = "https://maps.googleapis.com/maps/api/staticmap"
                + "?center=" + HQ_LAT + "," + HQ_LNG
                + "&zoom=15"
                + "&size=600x300"
                + "&scale=2"
                + "&markers=color:red%7C" + HQ_LAT + "," + HQ_LNG
                + "&key=YOUR_API_KEY";

        if (mapImage != null) {
            Glide.with(this)
                    .load(staticMapUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .centerCrop()
                    .into(mapImage);
        }

        if (mapCard != null) {
            mapCard.setOnClickListener(v -> openGoogleMaps());
        }
    }

    private void openGoogleMaps() {
        // Try the geo URI first — opens Google Maps (or any maps app) and drops a pin
        Uri geoUri = Uri.parse("geo:" + HQ_LAT + "," + HQ_LNG
                + "?q=" + Uri.encode(HQ_LABEL));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        mapIntent.setPackage("com.google.android.apps.maps"); // prefer Google Maps

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Fallback: open in browser if Google Maps is not installed
            Uri browserUri = Uri.parse(
                    "https://www.google.com/maps/search/?api=1&query="
                    + HQ_LAT + "," + HQ_LNG);
            startActivity(new Intent(Intent.ACTION_VIEW, browserUri));
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;

        bottomNav.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        com.google.android.material.card.MaterialCardView container =
            findViewById(R.id.bottomNavigationContainer);
        if (container != null)
            container.setCardBackgroundColor(
                androidx.core.content.ContextCompat.getColor(this, R.color.surface));

        bottomNav.setSelectedItemId(R.id.nav_about);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_categories) {
                startActivity(new Intent(this, CategoriesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_about) {
                return true;
            } else if (id == R.id.nav_account) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }
}

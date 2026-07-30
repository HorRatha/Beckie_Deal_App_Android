package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

public class AboutActivity extends BaseActivity {

    // Beckie Deal Cambodia store location
    private static final double HQ_LAT  = 11.5564;
    private static final double HQ_LNG  = 104.9282;
    private static final String HQ_LABEL = "Beckie Deal Cambodia, Phnom Penh";

    private EditText inputName, inputEmail, inputMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        inputName    = findViewById(R.id.inputName);
        inputEmail   = findViewById(R.id.inputEmail);
        inputMessage = findViewById(R.id.inputMessage);

        AppDrawerHelper.attach(this, R.id.drawerLayout, 0);
        setupTopBar();
        setupSubmitButton();
        setupMapCard();
        setupBottomNavigation();
        setupEntranceAnimation();
    }

    private void setupTopBar() {
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) backButton.setOnClickListener(v -> finish());

        View notificationContainer = findViewById(R.id.notificationContainer);
        if (notificationContainer != null)
            notificationContainer.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));

        UiAnimations.pressBounce(backButton, notificationContainer);
    }

    /** Staggers every section of the About page's scroll body up into place on load. */
    private void setupEntranceAnimation() {
        NestedScrollView scroll = findViewById(R.id.aboutContentScroll);
        if (scroll != null && scroll.getChildCount() > 0) {
            View listRoot = scroll.getChildAt(0);
            if (listRoot instanceof ViewGroup) {
                UiAnimations.staggerChildrenIn((ViewGroup) listRoot, 0L);
            }
        }
    }

    private void setupSubmitButton() {
        Button btnSubmit = findViewById(R.id.btnSubmit);
        if (btnSubmit == null) return;
        UiAnimations.pressBounce(btnSubmit);

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
            emailIntent.setData(Uri.parse("mailto:beckiedeal@gmail.com"));
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
            UiAnimations.pressBounce(mapCard);
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
        BottomNavHelper.setup(this, BottomNavHelper.NavItem.ACCOUNT);
    }
}

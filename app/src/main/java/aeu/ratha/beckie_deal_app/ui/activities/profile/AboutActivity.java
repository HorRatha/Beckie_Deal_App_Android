package aeu.ratha.beckie_deal_app.ui.activities.profile;

import aeu.ratha.beckie_deal_app.R;


import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.content.Intent;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.graphics.Typeface;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.net.Uri;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.os.Bundle;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.text.TextUtils;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.view.View;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.view.ViewGroup;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.widget.Button;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.widget.EditText;
import android.widget.LinearLayout;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.widget.ImageView;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.widget.TextView;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
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
        setupAboutUsImage();
        setupAccordions();
        setupSubmitButton();
        setupMapCard();
        setupBottomNavigation();
        setupKhmerFont();
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

    /**
     * Setup accordion toggle functionality for Warranty Terms and Contact Us sections.
     */
    private void setupAccordions() {
        // Warranty Terms Accordion
        LinearLayout accWarrantyHeader = findViewById(R.id.accWarrantyHeader);
        LinearLayout accWarrantyBody = findViewById(R.id.accWarrantyBody);
        TextView accWarrantyIcon = findViewById(R.id.accWarrantyIcon);

        if (accWarrantyHeader != null && accWarrantyBody != null && accWarrantyIcon != null) {
            accWarrantyHeader.setOnClickListener(v -> {
                boolean isVisible = accWarrantyBody.getVisibility() == View.VISIBLE;
                toggleAccordion(accWarrantyBody, accWarrantyIcon, !isVisible);
            });
        }

        // Contact Us Accordion
        LinearLayout accContactHeader = findViewById(R.id.accContactHeader);
        LinearLayout accContactBody = findViewById(R.id.accContactBody);
        TextView accContactIcon = findViewById(R.id.accContactIcon);

        if (accContactHeader != null && accContactBody != null && accContactIcon != null) {
            accContactHeader.setOnClickListener(v -> {
                boolean isVisible = accContactBody.getVisibility() == View.VISIBLE;
                toggleAccordion(accContactBody, accContactIcon, !isVisible);
            });
        }

        UiAnimations.pressBounce(accWarrantyHeader, accContactHeader);
    }

    /**
     * Toggle accordion body visibility with animation and icon rotation.
     */
    private void toggleAccordion(LinearLayout body, TextView icon, boolean show) {
        if (show) {
            body.setVisibility(View.VISIBLE);
            icon.animate().rotation(45).setDuration(300).start();
            UiAnimations.fadeSlideDown(body, 0);
        } else {
            icon.animate().rotation(0).setDuration(300).start();
            body.animate().alpha(0f).setDuration(200).withEndAction(() -> 
                body.setVisibility(View.GONE)
            ).start();
        }
    }

    /**
     * Load and display the About Us image from the remote URL.
     */
    private void setupAboutUsImage() {
        ImageView aboutUsImage = findViewById(R.id.aboutUsImage);
        if (aboutUsImage != null) {
            String imageUrl = "https://www.beckie-khmer.com/storage/about_us/jMTY9dOrs58PMFifuA3OHw821SfcL3BmwreIodlb.jpeg";
            
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .centerCrop()
                    .into(aboutUsImage);
        }
        
        // Apply gradient to hero text
        applyGradientToTextView(findViewById(R.id.heroTitle));
        applyGradientToTextView(findViewById(R.id.heroSubtitle));
    }
    
    /**
     * Apply gradient color to a TextView using a shader.
     */
    private void applyGradientToTextView(TextView textView) {
        if (textView == null) return;
        
        textView.post(() -> {
            int width = textView.getWidth();
            int height = textView.getHeight();
            
            if (width > 0 && height > 0) {
                android.graphics.Shader textShader = new android.graphics.LinearGradient(
                    0, 0, width, height,
                    androidx.core.content.ContextCompat.getColor(this, R.color.yellow_star),
                    androidx.core.content.ContextCompat.getColor(this, R.color.brand_orange),
                    android.graphics.Shader.TileMode.CLAMP
                );
                textView.getPaint().setShader(textShader);
                textView.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.yellow_star));
                textView.invalidate();
            }
        });
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

    /**
     * Apply Khmer-compatible font to all TextViews in the About page
     * to ensure proper rendering of Khmer Unicode characters.
     */
    private void setupKhmerFont() {
        try {
            Typeface khmerFont = ResourcesCompat.getFont(this, R.font.khmer_font);
            if (khmerFont == null) return;

            NestedScrollView scroll = findViewById(R.id.aboutContentScroll);
            if (scroll != null && scroll.getChildCount() > 0) {
                View listRoot = scroll.getChildAt(0);
                if (listRoot instanceof ViewGroup) {
                    applyKhmerFontToViewGroup((ViewGroup) listRoot, khmerFont);
                }
            }
        } catch (Exception e) {
            // Fallback: If custom font fails, continue with system font
            e.printStackTrace();
        }
    }

    /**
     * Recursively apply Khmer font to all TextViews in a ViewGroup.
     * Checks if the TextView contains Khmer characters before applying the font.
     */
    private void applyKhmerFontToViewGroup(ViewGroup viewGroup, Typeface khmerFont) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            
            if (child instanceof TextView) {
                TextView textView = (TextView) child;
                CharSequence text = textView.getText();
                
                // Apply font if text contains Khmer Unicode characters (U+1780 to U+17FF)
                if (text != null && containsKhmerCharacters(text.toString())) {
                    textView.setTypeface(khmerFont);
                }
            } else if (child instanceof ViewGroup) {
                // Recursively apply to nested ViewGroups
                applyKhmerFontToViewGroup((ViewGroup) child, khmerFont);
            }
        }
    }

    /**
     * Check if a string contains Khmer Unicode characters.
     */
    private boolean containsKhmerCharacters(String text) {
        for (char c : text.toCharArray()) {
            if (c >= '\u1780' && c <= '\u17FF') {
                return true;
            }
        }
        return false;
    }
}

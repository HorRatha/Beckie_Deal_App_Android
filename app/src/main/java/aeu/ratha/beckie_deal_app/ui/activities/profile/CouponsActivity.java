package aeu.ratha.beckie_deal_app.ui.activities.profile;

import aeu.ratha.beckie_deal_app.R;


import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.managers.CouponManager;

import android.content.ClipData;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.managers.CouponManager;

import android.content.ClipboardManager;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.managers.CouponManager;

import android.content.Context;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.managers.CouponManager;

import android.os.Bundle;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.managers.CouponManager;

import android.view.LayoutInflater;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.managers.CouponManager;

import android.view.View;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.managers.CouponManager;

import android.widget.ImageView;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.managers.CouponManager;

import android.widget.LinearLayout;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.managers.CouponManager;

import android.widget.TextView;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.managers.CouponManager;

import android.widget.Toast;
import java.util.Locale;
import java.util.Map;

/**
 * Activity that displays all available coupons for users to browse and copy.
 * Provides a central location for coupon discovery and management.
 */
public class CouponsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons);

        setupTopBar();
        displayCoupons();
        setupEntranceAnimation();
    }

    private void setupTopBar() {
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
        UiAnimations.pressBounce(backButton);
    }

    private void setupEntranceAnimation() {
        UiAnimations.fadeSlideDown(findViewById(R.id.appBarLayout), 0);
        androidx.core.widget.NestedScrollView scroll = findViewById(R.id.couponsScrollView);
        if (scroll != null && scroll.getChildCount() > 0) {
            View listRoot = scroll.getChildAt(0);
            if (listRoot instanceof android.view.ViewGroup) {
                UiAnimations.staggerChildrenIn((android.view.ViewGroup) listRoot, 80L);
            }
        }
    }

    private void displayCoupons() {
        LinearLayout couponsContainer = findViewById(R.id.couponsContainer);
        if (couponsContainer == null) return;

        couponsContainer.removeAllViews();

        // Get all available coupons from CouponManager
        CouponManager manager = CouponManager.getInstance();
        
        // Access available coupons via reflection or create a getter method
        // For now, manually create the same coupons for display
        addCouponCard(couponsContainer, "WELCOME10", "10% OFF", 
            "10% off for new users", "No minimum", 0.0);
        
        addCouponCard(couponsContainer, "SAVE20", "$20 OFF", 
            "$20 off orders over $50", "Min $50", 50.0);
        
        addCouponCard(couponsContainer, "DEAL50", "50% OFF", 
            "50% off orders over $100", "Min $100", 100.0);
        
        addCouponCard(couponsContainer, "FREESHIP", "$15 OFF", 
            "Free shipping on orders over $30", "Min $30", 30.0);
    }

    private void addCouponCard(LinearLayout container, String code, String discount,
                                String description, String minPurchaseLabel, double minAmount) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_coupon_card, container, false);

        TextView codeText = card.findViewById(R.id.couponCode);
        TextView discountText = card.findViewById(R.id.couponDiscount);
        TextView descriptionText = card.findViewById(R.id.couponDescription);
        TextView minPurchaseText = card.findViewById(R.id.couponMinPurchase);
        View copyButton = card.findViewById(R.id.copyCouponButton);

        codeText.setText(code);
        discountText.setText(discount);
        descriptionText.setText(description);
        minPurchaseText.setText(minPurchaseLabel);

        UiAnimations.pressBounce(copyButton);

        copyButton.setOnClickListener(v -> {
            copyToClipboard(code);
            Toast.makeText(this, "Copied: " + code, Toast.LENGTH_SHORT).show();
            
            // Optional: Visual feedback - change button text temporarily
            if (copyButton instanceof com.google.android.material.button.MaterialButton) {
                com.google.android.material.button.MaterialButton btn = 
                    (com.google.android.material.button.MaterialButton) copyButton;
                String originalText = btn.getText().toString();
                btn.setText("✓ Copied!");
                v.postDelayed(() -> btn.setText(originalText), 1500);
            }
        });

        container.addView(card);
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Coupon Code", text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }
    }
}

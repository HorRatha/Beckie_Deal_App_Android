package aeu.ratha.beckie_deal_app.ui.activities.profile;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Promotion;
import aeu.ratha.beckie_deal_app.data.repository.PromotionRepository;
import aeu.ratha.beckie_deal_app.ui.adapters.CouponAdapter;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic Coupons Activity fetching promotions from backend API endpoint
 * (/api/v1/promotions), supporting optional banner images, tabs, and code claiming.
 */
public class CouponsActivity extends BaseActivity {

    private boolean isDiscoverTab = true;
    private RecyclerView rvCoupons;
    private TextView tvEmpty, tvCodeMessage, tvNewBadge, tvMineLabel;
    private EditText etCode;
    private CouponAdapter adapter;

    private List<Promotion> discoverList;
    private List<Promotion> myCouponsList;
    private PromotionRepository promotionRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons);

        discoverList = new ArrayList<>();
        myCouponsList = new ArrayList<>();
        promotionRepository = new PromotionRepository(this);

        setupViews();
        setupTabs();
        setupClaimSection();
        fetchPromotions();

        aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper.setup(this, aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper.NavItem.ACCOUNT);
        aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper.updateCartBadge(this);
    }

    private void fetchPromotions() {
        showCodeMessage("Loading promotions...", false);
        promotionRepository.getPromotions(new PromotionRepository.PromotionCallback() {
            @Override
            public void onSuccess(List<Promotion> promotions) {
                runOnUiThread(() -> {
                    discoverList.clear();
                    if (promotions != null) {
                        discoverList.addAll(promotions);
                    }
                    // Populate initial mock collected/my coupons if myCouponsList is empty
                    if (myCouponsList.isEmpty() && !discoverList.isEmpty()) {
                        // Add the first promotion as collected for demo purposes
                        myCouponsList.add(discoverList.get(0));
                    }
                    if (tvCodeMessage != null) {
                        tvCodeMessage.setVisibility(View.GONE);
                    }
                    if (isDiscoverTab) {
                        adapter.updateList(discoverList);
                    } else {
                        adapter.updateList(myCouponsList);
                    }
                    updateDynamicCounts();
                    updateEmptyState();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    showCodeMessage("Failed to load promotions: " + errorMessage, true);
                    updateEmptyState();
                });
            }
        });
    }

    private void setupViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
            UiAnimations.pressBounce(btnBack);
        }

        etCode = findViewById(R.id.etCode);
        tvCodeMessage = findViewById(R.id.tvCodeMessage);
        rvCoupons = findViewById(R.id.rvCoupons);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvNewBadge = findViewById(R.id.tvNewBadge);
        tvMineLabel = findViewById(R.id.tvMineLabel);

        rvCoupons.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CouponAdapter(discoverList, this::collectCoupon);
        rvCoupons.setAdapter(adapter);

        updateDynamicCounts();
        updateEmptyState();
    }

    private void collectCoupon(Promotion promotion) {
        copyToClipboard(promotion.getCode());
        Toast.makeText(this, "Collected & Copied: " + promotion.getCode(), Toast.LENGTH_SHORT).show();

        // Add to myCouponsList if not already present
        boolean exists = false;
        for (Promotion p : myCouponsList) {
            if (p.getId() == promotion.getId() || p.getCode().equalsIgnoreCase(promotion.getCode())) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            myCouponsList.add(promotion);
        }

        updateDynamicCounts();
        if (!isDiscoverTab) {
            adapter.updateList(myCouponsList);
        }
    }

    private void setupTabs() {
        LinearLayout tabDiscover = findViewById(R.id.tabDiscover);
        LinearLayout tabMine = findViewById(R.id.tabMine);

        if (tabDiscover != null && tabMine != null) {
            tabDiscover.setSelected(true);
            tabMine.setSelected(false);

            tabDiscover.setOnClickListener(v -> {
                if (!isDiscoverTab) {
                    isDiscoverTab = true;
                    tabDiscover.setSelected(true);
                    tabMine.setSelected(false);
                    adapter.updateList(discoverList);
                    updateEmptyState();
                }
            });

            tabMine.setOnClickListener(v -> {
                if (isDiscoverTab) {
                    isDiscoverTab = false;
                    tabMine.setSelected(true);
                    tabDiscover.setSelected(false);
                    adapter.updateList(myCouponsList);
                    updateEmptyState();
                }
            });
        }
    }

    private void setupClaimSection() {
        View btnClaim = findViewById(R.id.btnClaim);
        if (btnClaim != null && etCode != null) {
            UiAnimations.pressBounce(btnClaim);
            btnClaim.setOnClickListener(v -> {
                String code = etCode.getText().toString().trim().toUpperCase();
                if (code.isEmpty()) {
                    showCodeMessage("Please enter a coupon code", true);
                    return;
                }

                btnClaim.setEnabled(false);
                showCodeMessage("Applying coupon...", false);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    btnClaim.setEnabled(true);

                    // Check if code matches any available promotion
                    Promotion foundPromo = null;
                    for (Promotion p : discoverList) {
                        if (p.getCode().equalsIgnoreCase(code)) {
                            foundPromo = p;
                            break;
                        }
                    }

                    if (foundPromo != null) {
                        collectCoupon(foundPromo);
                        showCodeMessage("✓ Coupon " + code + " claimed successfully!", false);
                        Toast.makeText(this, "Coupon " + code + " applied!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Create custom promo for entered code
                        Promotion customPromo = new Promotion();
                        customPromo.setId((int) (Math.random() * 1000));
                        customPromo.setName("Claimed Coupon");
                        customPromo.setCode(code);
                        customPromo.setType("percentage");
                        customPromo.setValue(15);
                        customPromo.setDescription("Custom claimed promotion code");
                        customPromo.setMinOrderAmount(0);
                        myCouponsList.add(customPromo);

                        showCodeMessage("✓ Coupon " + code + " claimed successfully!", false);
                        Toast.makeText(this, "Coupon " + code + " added to wallet!", Toast.LENGTH_SHORT).show();
                    }

                    etCode.setText("");
                    updateDynamicCounts();
                    if (!isDiscoverTab) {
                        adapter.updateList(myCouponsList);
                    }
                    updateEmptyState();
                }, 600);
            });
        }
    }

    private void showCodeMessage(String message, boolean isError) {
        if (tvCodeMessage != null) {
            tvCodeMessage.setText(message);
            tvCodeMessage.setVisibility(View.VISIBLE);
            tvCodeMessage.setTextColor(ContextCompat.getColor(this, isError ? R.color.error_red : R.color.green_stock));
        }
    }

    private void updateDynamicCounts() {
        if (tvNewBadge != null) {
            int count = discoverList.size();
            if (count > 0) {
                tvNewBadge.setText(String.format("%d new", count));
                tvNewBadge.setVisibility(View.VISIBLE);
            } else {
                tvNewBadge.setVisibility(View.GONE);
            }
        }

        if (tvMineLabel != null) {
            tvMineLabel.setText(String.format("My Coupons · %d", myCouponsList.size()));
        }
    }

    private void updateEmptyState() {
        int count = adapter.getItemCount();
        if (count == 0) {
            rvCoupons.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText(isDiscoverTab ? "No discover promotions available." : "No coupons in your wallet yet.\nCollect one from Discover.");
        } else {
            rvCoupons.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
        }
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Coupon Code", text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }
    }
}

package aeu.ratha.beckie_deal_app.ui.components;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Brand;
import aeu.ratha.beckie_deal_app.ui.activities.product.AllBrandsActivityV2;

/**
 * Manager for displaying a preview of all brands in a grid format.
 * Shows 6 brands (2 rows × 3 columns) with option to view all brands.
 * Used in home/main pages to showcase available brands.
 */
public class AllBrandsPreviewManager {

    private Activity activity;
    private View rootView;
    private GridLayout brandsGrid;
    private HorizontalScrollView scrollView;
    private MaterialCardView browseAllBtn;
    private List<Brand> allBrands;

    public AllBrandsPreviewManager(Activity activity, View rootView) {
        this.activity = activity;
        this.rootView = rootView;
        initializeViews();
    }

    /**
     * Initialize UI components
     */
    private void initializeViews() {
        // This manager is no longer actively used
        // Layout references have been moved to other sections
    }

    /**
     * Display brands in the grid
     */
    public void displayBrands(List<Brand> brands) {
        this.allBrands = brands;
        
        if (brands == null || brands.isEmpty() || brandsGrid == null) {
            rootView.setVisibility(View.GONE);
            return;
        }

        // Clear existing grid items
        brandsGrid.removeAllViews();

        // Display first 6 brands (2 rows × 3 columns) or all if less than 6
        int itemsToDisplay = Math.min(6, brands.size());
        
        for (int i = 0; i < itemsToDisplay; i++) {
            Brand brand = brands.get(i);
            View brandCard = createBrandCard(brand, i);
            
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec(i / 3);
            params.columnSpec = GridLayout.spec(i % 3);
            params.width = 0;  // Will be set by columnCount weight
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.setMargins(4, 4, 4, 4);
            
            brandsGrid.addView(brandCard, params);
        }

        rootView.setVisibility(View.VISIBLE);
    }

    /**
     * Create a single brand card view
     */
    private View createBrandCard(Brand brand, int position) {
        View cardView = LayoutInflater.from(activity)
                .inflate(R.layout.item_brand_square, null, false);

        AppCompatImageView logoIcon = cardView.findViewById(R.id.brandIcon);
        TextView brandName = cardView.findViewById(R.id.brandName);
        TextView productCount = cardView.findViewById(R.id.brandProductCount);

        // Set brand name
        if (brandName != null) {
            brandName.setText(brand.getName());
        }

        // Set product count
        if (productCount != null) {
            int count = brand.getProductsCount();
            productCount.setText(count + " products");
            productCount.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        }

        // Set brand logo
        if (logoIcon != null) {
            try {
                int logoResId = brand.getLogoResourceId();
                if (logoResId != android.R.drawable.ic_dialog_info) {
                    logoIcon.setImageResource(logoResId);
                    logoIcon.setVisibility(View.VISIBLE);
                } else {
                    logoIcon.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                logoIcon.setVisibility(View.GONE);
            }
        }

        // Apply colored border based on position (removed since we're using clean logo layout)
        // The layout now shows just the logo without card backgrounds

        // Handle card click to view brand details
        cardView.setOnClickListener(v -> {
            openBrandDetails(brand);
        });

        return cardView;
    }

    /**
     * Apply gradient colored border to brand card
     */
    private void applyBrandBorderColor(MaterialCardView card, int position) {
        int[] borderColors = {
                R.color.brand_tint_fg_1, R.color.brand_tint_fg_2, R.color.brand_tint_fg_3,
                R.color.brand_tint_fg_4, R.color.brand_tint_fg_5, R.color.brand_tint_fg_6
        };

        int colorIndex = position % borderColors.length;
        int borderColor = androidx.core.content.ContextCompat.getColor(
                activity, borderColors[colorIndex]);
        
        card.setStrokeColor(borderColor);
        card.setStrokeWidth(2);
    }

    /**
     * Open all brands page
     */
    private void openAllBrandsPage() {
        Intent intent = new Intent(activity, AllBrandsActivityV2.class);
        activity.startActivity(intent);
    }

    /**
     * Open brand details/category filtered by brand
     */
    private void openBrandDetails(Brand brand) {
        Intent intent = new Intent(activity, AllBrandsActivityV2.class);
        intent.putExtra("scroll_to_brand", brand.getSlug());
        activity.startActivity(intent);
    }

    /**
     * Show/hide the entire section
     */
    public void setVisibility(int visibility) {
        if (rootView != null) {
            rootView.setVisibility(visibility);
        }
    }

    /**
     * Get all brands
     */
    public List<Brand> getBrands() {
        return allBrands;
    }

    /**
     * Refresh the display
     */
    public void refresh(List<Brand> brands) {
        displayBrands(brands);
    }
}

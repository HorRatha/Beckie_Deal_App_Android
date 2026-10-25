package aeu.ratha.beckie_deal_app.ui.activities.product;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Brand;
import aeu.ratha.beckie_deal_app.data.repository.BrandRepository;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;

/**
 * AllBrandsActivityV2 - Displays all available brands in a grid layout.
 * Uses Repository pattern for data fetching and static drawable resources for brand logos.
 */
public class AllBrandsActivityV2 extends BaseActivity {
    
    private static final String TAG = "AllBrandsActivityV2";
    private static final int GRID_COLUMNS = 4;
    
    // UI Components
    private GridLayout allBrandsGrid;
    private ProgressBar progressBar;
    
    // Data
    private BrandRepository brandRepository;
    private List<Brand> allBrands = new ArrayList<>();
    private boolean isLoading = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_brands);
        
        Log.d(TAG, "onCreate called");
        initViews();
        setupTopBar();
        initRepository();
        loadBrands(false);

        aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper.setup(this, aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper.NavItem.CATEGORIES);
        aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper.updateCartBadge(this);
        
        // Animate top bar
        View topAppBar = findViewById(R.id.topAppBar);
        if (topAppBar != null) {
            UiAnimations.fadeSlideDown(topAppBar, 0);
        }
    }
    
    /**
     * Initialize view references.
     */
    private void initViews() {
        Log.d(TAG, "initViews called");
        allBrandsGrid = findViewById(R.id.allBrandsGrid);
        progressBar = findViewById(R.id.progressBar);
        
        if (allBrandsGrid == null) {
            Log.e(TAG, "ERROR: allBrandsGrid not found!");
        }
        if (progressBar == null) {
            Log.e(TAG, "ERROR: progressBar not found!");
        }
    }
    
    /**
     * Setup top bar with back button.
     */
    private void setupTopBar() {
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                Log.d(TAG, "Back button clicked");
                finish();
            });
            try {
                UiAnimations.pressBounce(backButton);
            } catch (Exception e) {
                Log.w(TAG, "Animation error on back button", e);
            }
        } else {
            Log.w(TAG, "Back button not found in layout");
        }
    }
    
    /**
     * Initialize brand repository.
     */
    private void initRepository() {
        brandRepository = BrandRepository.getInstance(this);
    }
    
    /**
     * Load brands from repository.
     */
    private void loadBrands(boolean forceRefresh) {
        if (isLoading) return;
        
        isLoading = true;
        showLoading(true);
        
        // Fetch all brands at once (with large limit to get all)
        brandRepository.fetchBrands(100, 0, forceRefresh, 
            new BrandRepository.BrandCallback() {
                @Override
                public void onSuccess(List<Brand> brands, int total) {
                    isLoading = false;
                    showLoading(false);
                    
                    // Filter active brands
                    List<Brand> activeBrands = brandRepository.filterActiveBrands(brands);
                    allBrands.clear();
                    allBrands.addAll(activeBrands);
                    
                    // Display brands in grid
                    displayBrands(activeBrands);
                    
                    Log.d(TAG, "Loaded " + activeBrands.size() + " active brands (total: " + total + ")");
                }
                
                @Override
                public void onError(String errorMessage) {
                    isLoading = false;
                    showLoading(false);
                    showError(errorMessage);
                    Log.e(TAG, "Error loading brands: " + errorMessage);
                }
            });
    }
    
    /**
     * Display brands in the grid layout.
     */
    private void displayBrands(List<Brand> brands) {
        if (allBrandsGrid == null) return;
        
        allBrandsGrid.removeAllViews();
        
        for (Brand brand : brands) {
            try {
                // Inflate brand card view
                View brandCardView = getLayoutInflater().inflate(R.layout.item_brand_square, allBrandsGrid, false);
                
                // Find views in the card
                ImageView brandIcon = brandCardView.findViewById(R.id.brandIcon);
                TextView brandName = brandCardView.findViewById(R.id.brandName);
                
                // Set brand name
                brandName.setText(brand.getName());
                
                // Load brand logo
                try {
                    int logoResId = brand.getLogoResourceId();
                    if (logoResId != 0) {
                        brandIcon.setImageResource(logoResId);
                        brandIcon.setVisibility(View.VISIBLE);
                    } else {
                        brandIcon.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error loading brand logo for: " + brand.getName(), e);
                    brandIcon.setVisibility(View.GONE);
                }
                
                // Add click listener
                final Brand finalBrand = brand;
                brandCardView.setOnClickListener(v -> onBrandClick(finalBrand));
                
                // Add to grid
                allBrandsGrid.addView(brandCardView);
                
            } catch (Exception e) {
                Log.e(TAG, "Error creating brand card for: " + brand.getName(), e);
            }
        }
    }
    
    /**
     * Handle brand click.
     */
    private void onBrandClick(Brand brand) {
        Log.d(TAG, "Brand clicked: " + brand.getName() + " (ID: " + brand.getId() + ")");
        
        // Navigate to category detail filtered by brand
        Intent intent = new Intent(this, CategoryDetailActivity.class);
        intent.putExtra(CategoryDetailActivity.EXTRA_BRAND_FILTER, brand.getName());
        intent.putExtra("BRAND_ID", brand.getId());
        intent.putExtra("BRAND_SLUG", brand.getSlug());
        intent.putExtra("BRAND_WEBSITE", brand.getWebsite());
        startActivity(intent);
    }
    
    /**
     * Show or hide loading indicator.
     */
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (allBrandsGrid != null) {
            allBrandsGrid.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    
    /**
     * Show error message.
     */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

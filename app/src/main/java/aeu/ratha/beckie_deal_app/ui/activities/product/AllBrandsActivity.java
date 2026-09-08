package aeu.ratha.beckie_deal_app.ui.activities.product;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.data.api.BrandApiService;
import aeu.ratha.beckie_deal_app.data.models.Brand;
import aeu.ratha.beckie_deal_app.data.models.BrandResponse;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity that displays all available brands in a grid layout.
 * Fetches brand data from the API and displays them as clickable cards.
 */
public class AllBrandsActivity extends BaseActivity {
    
    private static final String TAG = "AllBrandsActivity";
    
    private GridLayout allBrandsGrid;
    private ProgressBar progressBar;
    private BrandApiService brandApiService;
    private List<Brand> brandsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_brands);

        initViews();
        setupTopBar();
        initApiService();
        fetchBrandsFromApi();
        
        UiAnimations.fadeSlideDown(findViewById(R.id.topAppBar), 0);
    }
    
    /**
     * Initialize view references.
     */
    private void initViews() {
        allBrandsGrid = findViewById(R.id.allBrandsGrid);
        progressBar = findViewById(R.id.progressBar);
    }
    
    /**
     * Initialize API service.
     */
    private void initApiService() {
        brandApiService = RetrofitClient.getClient(this).create(BrandApiService.class);
    }

    /**
     * Setup top bar with back button.
     */
    private void setupTopBar() {
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
            UiAnimations.pressBounce(backButton);
        }
    }
    
    /**
     * Fetch brands from API with pagination.
     */
    private void fetchBrandsFromApi() {
        showLoading(true);
        
        // Fetch brands with limit=20, offset=0 (first page)
        Call<BrandResponse> call = brandApiService.getBrands(20, 0);
        
        call.enqueue(new Callback<BrandResponse>() {
            @Override
            public void onResponse(Call<BrandResponse> call, Response<BrandResponse> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    BrandResponse brandResponse = response.body();
                    
                    if ("success".equals(brandResponse.getStatus()) && 
                        brandResponse.getData() != null && 
                        brandResponse.getData().getBrands() != null) {
                        
                        brandsList = brandResponse.getData().getBrands();
                        
                        Log.d(TAG, "Brands fetched successfully: " + brandsList.size() + " items");
                        Log.d(TAG, "Total brands available: " + 
                              brandResponse.getData().getPagination().getTotal());
                        
                        displayBrands(brandsList);
                    } else {
                        Log.e(TAG, "Invalid response structure");
                        showError("Failed to load brands");
                        displayFallbackBrands();
                    }
                } else {
                    Log.e(TAG, "API error: " + response.code() + " - " + response.message());
                    showError("Failed to load brands: " + response.message());
                    displayFallbackBrands();
                }
            }

            @Override
            public void onFailure(Call<BrandResponse> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                showError("Network error: " + t.getMessage());
                displayFallbackBrands();
            }
        });
    }
    
    /**
     * Display brands in the grid layout.
     * @param brands List of brands to display
     */
    private void displayBrands(List<Brand> brands) {
        if (allBrandsGrid == null || brands == null || brands.isEmpty()) {
            Log.w(TAG, "Cannot display brands: grid or brands list is null/empty");
            return;
        }

        allBrandsGrid.removeAllViews();
        
        for (Brand brand : brands) {
            // Skip inactive brands
            if (!brand.isActive()) {
                continue;
            }
            
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.item_brand_square, allBrandsGrid, false);

            ImageView brandIcon = view.findViewById(R.id.brandIcon);
            TextView iconText = view.findViewById(R.id.brandIconText);
            TextView nameText = view.findViewById(R.id.brandName);

            // Since logos might be null, show brand name initial
            if (brand.getLogo() != null && !brand.getLogo().isEmpty()) {
                // TODO: Load logo using Glide when logo URLs are available
                // For now, show initial
                showBrandInitial(brandIcon, iconText, brand.getName());
            } else {
                showBrandInitial(brandIcon, iconText, brand.getName());
            }
            
            nameText.setText(brand.getName());

            // Add product count if available
            if (brand.getProductsCount() > 0) {
                nameText.setText(brand.getName() + " (" + brand.getProductsCount() + ")");
            }

            // Open CategoryDetailActivity filtered to this brand's products
            view.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(
                        this, CategoryDetailActivity.class);
                intent.putExtra(CategoryDetailActivity.EXTRA_BRAND_FILTER, brand.getName());
                intent.putExtra("BRAND_ID", brand.getId());
                intent.putExtra("BRAND_SLUG", brand.getSlug());
                startActivity(intent);
            });

            allBrandsGrid.addView(view);
        }
        
        Log.d(TAG, "Displayed " + brands.size() + " brands");
    }
    
    /**
     * Show brand initial letter when logo is not available.
     */
    private void showBrandInitial(ImageView brandIcon, TextView iconText, String brandName) {
        brandIcon.setVisibility(View.GONE);
        iconText.setVisibility(View.VISIBLE);
        
        if (brandName != null && !brandName.isEmpty()) {
            iconText.setText(String.valueOf(brandName.charAt(0)).toUpperCase());
        } else {
            iconText.setText("?");
        }
    }
    
    /**
     * Display fallback/hardcoded brands when API fails.
     */
    private void displayFallbackBrands() {
        Log.d(TAG, "Displaying fallback brands");
        
        List<FallbackBrand> brands = new ArrayList<>();
        brands.add(new FallbackBrand("ASUS", R.drawable.ic_brand_asus));
        brands.add(new FallbackBrand("ROG", R.drawable.ic_brand_rog));
        brands.add(new FallbackBrand("Apple", R.drawable.ic_brand_apple));
        brands.add(new FallbackBrand("Dell", R.drawable.ic_brand_dell));
        brands.add(new FallbackBrand("HP", R.drawable.ic_brand_hp));
        brands.add(new FallbackBrand("MSI", R.drawable.ic_brand_msi));
        brands.add(new FallbackBrand("Samsung", R.drawable.ic_brand_samsung));
        brands.add(new FallbackBrand("Lenovo", R.drawable.ic_brand_lenovo));
        brands.add(new FallbackBrand("Acer", R.drawable.ic_brand_acer));
        brands.add(new FallbackBrand("Razer", R.drawable.ic_brand_razer));
        brands.add(new FallbackBrand("Microsoft", R.drawable.ic_brand_microsoft));

        if (allBrandsGrid == null) return;
        
        allBrandsGrid.removeAllViews();
        for (FallbackBrand brand : brands) {
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.item_brand_square, allBrandsGrid, false);

            ImageView brandIcon = view.findViewById(R.id.brandIcon);
            TextView iconText = view.findViewById(R.id.brandIconText);
            TextView nameText = view.findViewById(R.id.brandName);

            brandIcon.setVisibility(View.VISIBLE);
            iconText.setVisibility(View.GONE);
            brandIcon.setImageResource(brand.imageResId);
            brandIcon.clearColorFilter();
            nameText.setText(brand.name);

            view.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(
                        this, CategoryDetailActivity.class);
                String cat = brandToCategory(brand.name);
                intent.putExtra(CategoryDetailActivity.EXTRA_CATEGORY, cat);
                intent.putExtra(CategoryDetailActivity.EXTRA_BRAND_FILTER, brand.name);
                startActivity(intent);
            });

            allBrandsGrid.addView(view);
        }
    }
    
    /**
     * Map brand name to category (fallback logic).
     */
    private String brandToCategory(String brand) {
        switch (brand) {
            case "ASUS":
            case "ROG":
            case "Apple":
            case "Dell":
            case "HP":
            case "Lenovo":
            case "Acer":
            case "Microsoft":
                return "Laptops";
            case "MSI":
            case "Razer":
                return "Gaming";
            case "Samsung":
                return "Smartphones";
            default:
                return "Laptops";
        }
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
     * Show error message to user.
     */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Simple fallback brand class for hardcoded data.
     */
    private static class FallbackBrand {
        String name;
        int imageResId;
        
        FallbackBrand(String name, int imageResId) {
            this.name = name;
            this.imageResId = imageResId;
        }
    }
}

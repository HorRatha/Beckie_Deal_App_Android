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
    private TextView brandCountTextView;
    private BrandApiService brandApiService;
    private List<Brand> brandsList = new ArrayList<>();
    private int totalBrandsCount = 0;

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
        brandCountTextView = findViewById(R.id.brandCount);
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
                        
                        // Get total count from pagination
                        if (brandResponse.getData().getPagination() != null) {
                            totalBrandsCount = brandResponse.getData().getPagination().getTotal();
                        } else {
                            totalBrandsCount = brandsList.size();
                        }
                        
                        Log.d(TAG, "Brands fetched successfully: " + brandsList.size() + " items");
                        Log.d(TAG, "Total brands available: " + totalBrandsCount);
                        
                        // Update brand count display
                        updateBrandCount(totalBrandsCount);
                        
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
            TextView nameText = view.findViewById(R.id.brandName);
            TextView productCount = view.findViewById(R.id.brandProductCount);

            // Display brand name
            nameText.setText(brand.getName());

            // Display product count
            if (productCount != null && brand.getProductsCount() > 0) {
                int count = brand.getProductsCount();
                productCount.setText(count + " product" + (count != 1 ? "s" : ""));
                productCount.setVisibility(View.VISIBLE);
            } else if (productCount != null) {
                productCount.setVisibility(View.GONE);
            }

            // Load logo from drawable resources
            try {
                int logoResId = brand.getLogoResourceId();
                if (logoResId != 0) {
                    brandIcon.setImageResource(logoResId);
                    brandIcon.setVisibility(View.VISIBLE);
                } else {
                    brandIcon.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Log.w(TAG, "Failed to load logo for " + brand.getName(), e);
                brandIcon.setVisibility(View.GONE);
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
     * Update brand count display in the header.
     * @param count Number of brands to display
     */
    private void updateBrandCount(int count) {
        if (brandCountTextView != null) {
            String text = count + (count == 1 ? " brand" : " brands");
            brandCountTextView.setText(text);
            Log.d(TAG, "Updated brand count: " + text);
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

        // Update brand count for fallback brands
        updateBrandCount(brands.size());

        if (allBrandsGrid == null) return;
        
        allBrandsGrid.removeAllViews();
        for (FallbackBrand brand : brands) {
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.item_brand_square, allBrandsGrid, false);

            ImageView brandIcon = view.findViewById(R.id.brandIcon);
            TextView nameText = view.findViewById(R.id.brandName);
            TextView productCount = view.findViewById(R.id.brandProductCount);

            brandIcon.setImageResource(brand.imageResId);
            nameText.setText(brand.name);
            
            // Hide product count for fallback brands
            if (productCount != null) {
                productCount.setVisibility(View.GONE);
            }

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

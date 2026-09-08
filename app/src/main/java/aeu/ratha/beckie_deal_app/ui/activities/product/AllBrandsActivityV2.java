package aeu.ratha.beckie_deal_app.ui.activities.product;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Brand;
import aeu.ratha.beckie_deal_app.data.repository.BrandRepository;
import aeu.ratha.beckie_deal_app.ui.adapters.BrandAdapter;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;

/**
 * Enhanced AllBrandsActivity using RecyclerView, Repository pattern, and search.
 * This is an improved version with better architecture and features.
 */
public class AllBrandsActivityV2 extends BaseActivity implements BrandAdapter.OnBrandClickListener {
    
    private static final String TAG = "AllBrandsActivityV2";
    private static final int GRID_COLUMNS = 3;
    
    // UI Components
    private RecyclerView brandsRecyclerView;
    private BrandAdapter brandAdapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText searchEditText;
    private TextView emptyStateText;
    private ImageView clearSearchButton;
    
    // Data
    private BrandRepository brandRepository;
    private List<Brand> allBrands = new ArrayList<>();
    private int currentOffset = 0;
    private int pageSize = 20;
    private int totalBrands = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_brands_v2);
        
        initViews();
        setupTopBar();
        setupRecyclerView();
        setupSearch();
        setupSwipeRefresh();
        initRepository();
        loadBrands(false);
        
        UiAnimations.fadeSlideDown(findViewById(R.id.topAppBar), 0);
    }
    
    /**
     * Initialize view references.
     */
    private void initViews() {
        brandsRecyclerView = findViewById(R.id.brandsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        searchEditText = findViewById(R.id.searchEditText);
        emptyStateText = findViewById(R.id.emptyStateText);
        clearSearchButton = findViewById(R.id.clearSearchButton);
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
     * Setup RecyclerView with adapter and layout manager.
     */
    private void setupRecyclerView() {
        // Setup layout manager
        GridLayoutManager layoutManager = new GridLayoutManager(this, GRID_COLUMNS);
        brandsRecyclerView.setLayoutManager(layoutManager);
        
        // Setup adapter
        brandAdapter = new BrandAdapter(this, allBrands, this);
        brandsRecyclerView.setAdapter(brandAdapter);
        
        // Setup scroll listener for pagination
        brandsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && !isLoading && !isLastPage) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    
                    // Load more when scrolled to bottom
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        loadMoreBrands();
                    }
                }
            }
        });
    }
    
    /**
     * Setup search functionality.
     */
    private void setupSearch() {
        if (searchEditText != null) {
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterBrands(s.toString());
                    updateClearButton(s.length() > 0);
                }
                
                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
        
        if (clearSearchButton != null) {
            clearSearchButton.setOnClickListener(v -> {
                searchEditText.setText("");
                clearSearchButton.setVisibility(View.GONE);
            });
        }
    }
    
    /**
     * Setup swipe to refresh.
     */
    private void setupSwipeRefresh() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> {
                refreshBrands();
            });
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
        currentOffset = 0;
        isLastPage = false;
        
        brandRepository.fetchBrands(pageSize, currentOffset, forceRefresh, 
            new BrandRepository.BrandCallback() {
                @Override
                public void onSuccess(List<Brand> brands, int total) {
                    isLoading = false;
                    showLoading(false);
                    
                    totalBrands = total;
                    allBrands.clear();
                    
                    // Filter active brands
                    List<Brand> activeBrands = brandRepository.filterActiveBrands(brands);
                    allBrands.addAll(activeBrands);
                    
                    brandAdapter.updateBrands(allBrands);
                    currentOffset += brands.size();
                    
                    updateEmptyState();
                    
                    Log.d(TAG, "Loaded " + allBrands.size() + " active brands (total: " + total + ")");
                }
                
                @Override
                public void onError(String errorMessage) {
                    isLoading = false;
                    showLoading(false);
                    showError(errorMessage);
                    updateEmptyState();
                    
                    Log.e(TAG, "Error loading brands: " + errorMessage);
                }
            });
    }
    
    /**
     * Load more brands (pagination).
     */
    private void loadMoreBrands() {
        if (isLoading || isLastPage) return;
        
        isLoading = true;
        
        brandRepository.fetchBrands(pageSize, currentOffset, false,
            new BrandRepository.BrandCallback() {
                @Override
                public void onSuccess(List<Brand> brands, int total) {
                    isLoading = false;
                    
                    if (brands.isEmpty()) {
                        isLastPage = true;
                    } else {
                        List<Brand> activeBrands = brandRepository.filterActiveBrands(brands);
                        allBrands.addAll(activeBrands);
                        brandAdapter.addBrands(activeBrands);
                        currentOffset += brands.size();
                    }
                    
                    Log.d(TAG, "Loaded " + brands.size() + " more brands");
                }
                
                @Override
                public void onError(String errorMessage) {
                    isLoading = false;
                    showError("Failed to load more brands");
                }
            });
    }
    
    /**
     * Refresh brands (pull to refresh).
     */
    private void refreshBrands() {
        brandRepository.clearCache();
        loadBrands(true);
        
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    
    /**
     * Filter brands by search query.
     */
    private void filterBrands(String query) {
        brandAdapter.filter(query);
        updateEmptyState();
    }
    
    /**
     * Show or hide loading indicator.
     */
    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (brandsRecyclerView != null) {
            brandsRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    
    /**
     * Update empty state visibility.
     */
    private void updateEmptyState() {
        if (emptyStateText != null && brandAdapter != null) {
            boolean isEmpty = brandAdapter.getItemCount() == 0;
            emptyStateText.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            brandsRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            
            // Update message based on search
            if (searchEditText != null && searchEditText.getText().length() > 0) {
                emptyStateText.setText("No brands found for \"" + searchEditText.getText() + "\"");
            } else {
                emptyStateText.setText("No brands available");
            }
        }
    }
    
    /**
     * Update clear search button visibility.
     */
    private void updateClearButton(boolean show) {
        if (clearSearchButton != null) {
            clearSearchButton.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
    
    /**
     * Show error message.
     */
    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Handle brand item click.
     */
    @Override
    public void onBrandClick(Brand brand, int position) {
        Log.d(TAG, "Brand clicked: " + brand.getName() + " (ID: " + brand.getId() + ")");
        
        // Navigate to category detail filtered by brand
        Intent intent = new Intent(this, CategoryDetailActivity.class);
        intent.putExtra(CategoryDetailActivity.EXTRA_BRAND_FILTER, brand.getName());
        intent.putExtra("BRAND_ID", brand.getId());
        intent.putExtra("BRAND_SLUG", brand.getSlug());
        intent.putExtra("BRAND_WEBSITE", brand.getWebsite());
        startActivity(intent);
    }
}

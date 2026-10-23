package aeu.ratha.beckie_deal_app.ui.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Brand;

/**
 * RecyclerView adapter for displaying brands with logo and colored borders.
 * Shows brand logo images (webp) with gradient-colored borders.
 * Logos are loaded statically from res/drawable since backend doesn't provide them.
 */
public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder> {
    
    private Context context;
    private List<Brand> brands;
    private List<Brand> brandsFiltered;
    private OnBrandClickListener listener;
    
    /**
     * Interface for handling brand item clicks.
     */
    public interface OnBrandClickListener {
        void onBrandClick(Brand brand, int position);
    }
    
    /**
     * Constructor with click listener.
     */
    public BrandAdapter(Context context, List<Brand> brands, OnBrandClickListener listener) {
        this.context = context;
        this.brands = brands != null ? brands : new ArrayList<>();
        this.brandsFiltered = new ArrayList<>(this.brands);
        this.listener = listener;
    }
    
    /**
     * Constructor without click listener.
     */
    public BrandAdapter(Context context, List<Brand> brands) {
        this(context, brands, null);
    }
    
    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_brand_square, parent, false);
        return new BrandViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        Brand brand = brandsFiltered.get(position);
        holder.bind(brand, listener, position);
    }
    
    @Override
    public int getItemCount() {
        return brandsFiltered.size();
    }
    
    /**
     * Update the brands list and refresh the adapter.
     */
    public void updateBrands(List<Brand> newBrands) {
        if (newBrands != null) {
            this.brands = new ArrayList<>(newBrands);
            this.brandsFiltered = new ArrayList<>(newBrands);
            notifyDataSetChanged();
        }
    }
    
    /**
     * Add more brands to the existing list (for pagination).
     */
    public void addBrands(List<Brand> newBrands) {
        if (newBrands != null && !newBrands.isEmpty()) {
            int startPosition = brandsFiltered.size();
            this.brands.addAll(newBrands);
            this.brandsFiltered.addAll(newBrands);
            notifyItemRangeInserted(startPosition, newBrands.size());
        }
    }
    
    /**
     * Filter brands by name.
     */
    public void filter(String query) {
        brandsFiltered.clear();
        
        if (query == null || query.trim().isEmpty()) {
            brandsFiltered.addAll(brands);
        } else {
            String lowerQuery = query.toLowerCase().trim();
            for (Brand brand : brands) {
                if (brand.getName().toLowerCase().contains(lowerQuery) ||
                    (brand.getSlug() != null && brand.getSlug().toLowerCase().contains(lowerQuery))) {
                    brandsFiltered.add(brand);
                }
            }
        }
        
        notifyDataSetChanged();
    }
    
    /**
     * Get brand at specific position.
     */
    public Brand getBrandAt(int position) {
        if (position >= 0 && position < brandsFiltered.size()) {
            return brandsFiltered.get(position);
        }
        return null;
    }
    
    /**
     * Clear all brands.
     */
    public void clear() {
        brands.clear();
        brandsFiltered.clear();
        notifyDataSetChanged();
    }
    
    /**
     * ViewHolder for brand items.
     */
    static class BrandViewHolder extends RecyclerView.ViewHolder {
        
        private ImageView brandIcon;
        private TextView brandName;
        
        BrandViewHolder(@NonNull View itemView) {
            super(itemView);
            brandIcon = itemView.findViewById(R.id.brandIcon);
            brandName = itemView.findViewById(R.id.brandName);
        }
        
        void bind(Brand brand, OnBrandClickListener listener, int position) {
            // Set brand name
            brandName.setText(brand.getName());
            
            // Display brand logo from static drawable resources
            try {
                int logoResId = brand.getLogoResourceId();
                if (logoResId != 0) {  // 0 means no logo found
                    // Check if dark mode is enabled and use white apple logo if needed
                    if (brand.getSlug() != null && brand.getSlug().toLowerCase().equals("apple") && isDarkModeEnabled(itemView.getContext())) {
                        brandIcon.setImageResource(R.drawable.apple_white);
                    } else {
                        brandIcon.setImageResource(logoResId);
                    }
                    brandIcon.setVisibility(View.VISIBLE);
                } else {
                    // No logo found, show placeholder with brand initial
                    brandIcon.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                android.util.Log.e("BrandAdapter", "Error loading brand logo for: " + brand.getName(), e);
                brandIcon.setVisibility(View.GONE);
            }
            
            // Apply gradient colored border to card
            View cardView = itemView.findViewById(R.id.brandCard);
            if (cardView != null) {
                applyBrandBorderColor(cardView, position);
            }
            
            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int adapterPosition = getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        listener.onBrandClick(brand, adapterPosition);
                    }
                }
            });
        }
        
        /**
         * Check if dark mode is enabled.
         */
        private boolean isDarkModeEnabled(Context context) {
            int nightMode = context.getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            return nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        }
        
        /**
         * Apply rotating gradient color palette to the card border.
         */
        private void applyBrandBorderColor(View cardView, int position) {
            int[] borderColors = {
                    R.color.brand_tint_fg_1, R.color.brand_tint_fg_2, R.color.brand_tint_fg_3,
                    R.color.brand_tint_fg_4, R.color.brand_tint_fg_5, R.color.brand_tint_fg_6
            };
            
            int colorIndex = position % borderColors.length;
            int borderColor = ContextCompat.getColor(itemView.getContext(), borderColors[colorIndex]);
            
            // Find MaterialCardView and apply stroke color
            MaterialCardView card = (MaterialCardView) cardView;
            card.setStrokeColor(borderColor);
        }
    }
}

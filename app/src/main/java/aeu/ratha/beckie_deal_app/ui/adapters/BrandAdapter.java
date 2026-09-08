package aeu.ratha.beckie_deal_app.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Brand;

/**
 * RecyclerView adapter for displaying brands in a grid or list.
 * Handles brand item clicks and displays brand information.
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
        holder.bind(brand, listener);
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
        private TextView brandIconText;
        private TextView brandName;
        
        BrandViewHolder(@NonNull View itemView) {
            super(itemView);
            brandIcon = itemView.findViewById(R.id.brandIcon);
            brandIconText = itemView.findViewById(R.id.brandIconText);
            brandName = itemView.findViewById(R.id.brandName);
        }
        
        void bind(Brand brand, OnBrandClickListener listener) {
            // Set brand name
            brandName.setText(brand.getName());
            
            // Load brand logo or show initial
            if (brand.getLogo() != null && !brand.getLogo().isEmpty()) {
                // Load logo with Glide
                brandIcon.setVisibility(View.VISIBLE);
                brandIconText.setVisibility(View.GONE);
                
                Glide.with(itemView.getContext())
                        .load(brand.getLogo())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .into(brandIcon);
            } else {
                // Show brand initial letter
                brandIcon.setVisibility(View.GONE);
                brandIconText.setVisibility(View.VISIBLE);
                
                String initial = brand.getName() != null && !brand.getName().isEmpty()
                        ? String.valueOf(brand.getName().charAt(0)).toUpperCase()
                        : "?";
                brandIconText.setText(initial);
            }
            
            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onBrandClick(brand, position);
                    }
                }
            });
        }
    }
}

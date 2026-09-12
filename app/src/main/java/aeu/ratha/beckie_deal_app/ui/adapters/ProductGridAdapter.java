package aeu.ratha.beckie_deal_app.ui.adapters;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.repository.WishlistRepository;
import aeu.ratha.beckie_deal_app.data.repository.CartRepository;
import aeu.ratha.beckie_deal_app.data.models.WishlistItemResponse;
import aeu.ratha.beckie_deal_app.data.models.CartItemResponse;
import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.ProductDetailActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecyclerView adapter for displaying products in a 2-column grid.
 * ViewHolder pattern ensures smooth scrolling by recycling card views
 * instead of inflating new ones for every item.
 */
public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.ProductViewHolder> {

    private static final String TAG = "ProductGridAdapter";
    private final Context context;
    private final List<MainActivity.Product> products;
    private final WishlistRepository wishlistRepository;
    private final CartRepository cartRepository;
    private Toast lastToast;
    private boolean listMode = false;

    public ProductGridAdapter(Context context, List<MainActivity.Product> products, WishlistRepository wishlistRepository, CartRepository cartRepository) {
        this.context = context;
        this.products = products;
        this.wishlistRepository = wishlistRepository;
        this.cartRepository = cartRepository;
    }
    
    // Legacy constructor for backward compatibility (creates its own repositories)
    public ProductGridAdapter(Context context, List<MainActivity.Product> products) {
        this(context, products, new WishlistRepository(context), new CartRepository(context));
    }

    /**
     * Switch between the 2-column grid card ({@code product_card.xml}) and the
     * 1-column list row ({@code product_card_list.xml}). Triggers a full rebind
     * since every visible item needs to be re-inflated with the new layout.
     */
    public void setListMode(boolean listMode) {
        if (this.listMode == listMode) return;
        this.listMode = listMode;
        notifyDataSetChanged();
    }

    public boolean isListMode() {
        return listMode;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = listMode ? R.layout.product_card_list : R.layout.product_card;
        View view = LayoutInflater.from(context)
                .inflate(layoutRes, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        // Distinct view types for grid vs list so RecyclerView doesn't try to
        // recycle a grid card into a list row (or vice versa) after a mode switch.
        return listMode ? 1 : 0;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        MainActivity.Product product = products.get(position);

        // Wishlist state will be checked via API if needed
        // For now, use the cached state from the product object
        
        // Use the shared binder to fill all card fields (name, price, rating, image, etc.)
        ProductCardBinder.bind(context, holder.itemView, product);

        // Card tap → ProductDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", product.id);
            intent.putExtra("product_name", product.name);
            intent.putExtra("product_price", product.price);
            intent.putExtra("product_rating", product.rating);
            intent.putExtra("product_image", product.imageUrl);
            intent.putExtra("product_sku", product.sku);
            context.startActivity(intent);
        });

        // Wishlist toggle
        ImageView favoriteIcon = holder.itemView.findViewById(R.id.favoriteIcon);
        if (favoriteIcon != null) {
            favoriteIcon.setOnClickListener(v -> {
                // Check authentication before modifying wishlist
                if (!aeu.ratha.beckie_deal_app.utils.helpers.AuthHelper.requireAuthForWishlist((android.app.Activity) context)) {
                    return; // Prompt shown, operation cancelled
                }
                
                if (product.id == null || product.id.isEmpty()) {
                    Toast.makeText(context, "Product ID not available", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Disable icon to prevent double-clicks
                favoriteIcon.setEnabled(false);
                
                if (!product.isInWishlist) {
                    // Add to wishlist via API
                    wishlistRepository.addToWishlist(product.id, 1, new WishlistRepository.WishlistItemCallback() {
                        @Override
                        public void onSuccess(WishlistItemResponse response) {
                            ((android.app.Activity) context).runOnUiThread(() -> {
                                product.isInWishlist = true;
                                if (response.getData() != null) {
                                    product.wishlistItemId = response.getData().getId();
                                }
                                ProductCardBinder.refreshFavIcon(context, favoriteIcon, true);
                                if (lastToast != null) lastToast.cancel();
                                lastToast = Toast.makeText(context, "Added to Wishlist", Toast.LENGTH_SHORT);
                                lastToast.show();
                                favoriteIcon.setEnabled(true);
                            });
                        }
                        
                        @Override
                        public void onError(String error) {
                            ((android.app.Activity) context).runOnUiThread(() -> {
                                Log.e(TAG, "Failed to add to wishlist: " + error);
                                Toast.makeText(context, "Failed to add to wishlist", Toast.LENGTH_SHORT).show();
                                favoriteIcon.setEnabled(true);
                            });
                        }
                    });
                } else {
                    // Remove from wishlist via API
                    if (product.wishlistItemId > 0) {
                        wishlistRepository.removeFromWishlist(product.wishlistItemId, new WishlistRepository.SimpleCallback() {
                            @Override
                            public void onSuccess() {
                                ((android.app.Activity) context).runOnUiThread(() -> {
                                    product.isInWishlist = false;
                                    product.wishlistItemId = 0;
                                    ProductCardBinder.refreshFavIcon(context, favoriteIcon, false);
                                    if (lastToast != null) lastToast.cancel();
                                    lastToast = Toast.makeText(context, "Removed from Wishlist", Toast.LENGTH_SHORT);
                                    lastToast.show();
                                    favoriteIcon.setEnabled(true);
                                });
                            }
                            
                            @Override
                            public void onError(String error) {
                                ((android.app.Activity) context).runOnUiThread(() -> {
                                    Log.e(TAG, "Failed to remove from wishlist: " + error);
                                    Toast.makeText(context, "Failed to remove from wishlist", Toast.LENGTH_SHORT).show();
                                    favoriteIcon.setEnabled(true);
                                });
                            }
                        });
                    } else {
                        // No wishlist item ID, just toggle locally
                        product.isInWishlist = false;
                        ProductCardBinder.refreshFavIcon(context, favoriteIcon, false);
                        favoriteIcon.setEnabled(true);
                    }
                }
            });
        }

        // Quick Add to Cart button
        View quickAddBtn = holder.itemView.findViewById(R.id.quickAddToCartButton);
        if (quickAddBtn != null) {
            quickAddBtn.setOnClickListener(v -> {
                // Check authentication before adding to cart
                if (!aeu.ratha.beckie_deal_app.utils.helpers.AuthHelper.requireAuthForCart((android.app.Activity) context)) {
                    return; // Prompt shown, operation cancelled
                }
                
                // Validate product ID
                if (product.id == null || product.id.isEmpty()) {
                    Log.e(TAG, "Cannot add to cart - product ID is null for: " + product.name);
                    if (lastToast != null) lastToast.cancel();
                    lastToast = Toast.makeText(context, "Product information missing", Toast.LENGTH_SHORT);
                    lastToast.show();
                    return;
                }
                
                // Stop click from propagating to parent card
                v.setPressed(true);
                
                Log.d(TAG, "Quick add to cart - Product ID: " + product.id + ", Name: " + product.name);
                
                // Add 1 quantity to cart via API
                cartRepository.addToCart(product.id, 1, new CartRepository.CartItemCallback() {
                    @Override
                    public void onSuccess(CartItemResponse response) {
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            // Show feedback toast
                            if (lastToast != null) lastToast.cancel();
                            lastToast = Toast.makeText(context, "Added to cart!", Toast.LENGTH_SHORT);
                            lastToast.show();
                            
                            // Update cart badge
                            if (context instanceof aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity) {
                                aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper.updateCartBadge(
                                        (android.app.Activity) context);
                            }
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            Log.e(TAG, "Failed to add to cart: " + error);
                            if (lastToast != null) lastToast.cancel();
                            lastToast = Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT);
                            lastToast.show();
                        });
                    }
                });
            });
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    /** Refresh a single item after wishlist toggle from outside the adapter */
    public void notifyItemChanged(MainActivity.Product product) {
        int idx = products.indexOf(product);
        if (idx >= 0) notifyItemChanged(idx);
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

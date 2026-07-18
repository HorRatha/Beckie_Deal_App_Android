package aeu.ratha.beckie_deal_app;

import android.content.Context;
import android.content.Intent;
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

    private final Context context;
    private final List<MainActivity.Product> products;
    private Toast lastToast;
    private boolean listMode = false;

    public ProductGridAdapter(Context context, List<MainActivity.Product> products) {
        this.context  = context;
        this.products = products;
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

        // Sync favorite state before binding
        product.isFavorite = FavoriteManager.getInstance().isFavorite(product);

        // Use the shared binder to fill all card fields (name, price, rating, image, etc.)
        ProductCardBinder.bind(context, holder.itemView, product);

        // Card tap → ProductDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_name",   product.name);
            intent.putExtra("product_price",  product.price);
            intent.putExtra("product_image",  product.imageUrl);
            intent.putExtra("product_rating", product.rating);
            intent.putExtra("is_favorite",    product.isFavorite);
            context.startActivity(intent);
        });

        // Favorite toggle
        ImageView favoriteIcon = holder.itemView.findViewById(R.id.favoriteIcon);
        if (favoriteIcon != null) {
            favoriteIcon.setOnClickListener(v -> {
                product.isFavorite = !product.isFavorite;
                if (product.isFavorite) FavoriteManager.getInstance().addFavorite(product);
                else                    FavoriteManager.getInstance().removeFavorite(product);
                ProductCardBinder.refreshFavIcon(context, favoriteIcon, product.isFavorite);
                if (lastToast != null) lastToast.cancel();
                lastToast = Toast.makeText(context,
                        product.isFavorite ? "Added to Favorites" : "Removed from Favorites",
                        Toast.LENGTH_SHORT);
                lastToast.show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    /** Refresh a single item after favorite toggle from outside the adapter */
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

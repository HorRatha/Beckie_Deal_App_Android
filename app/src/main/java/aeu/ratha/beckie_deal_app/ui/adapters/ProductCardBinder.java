package aeu.ratha.beckie_deal_app.ui.adapters;

import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;

import aeu.ratha.beckie_deal_app.R;


import aeu.ratha.beckie_deal_app.ui.adapters.ProductCardBinder;

import android.content.Context;

import aeu.ratha.beckie_deal_app.ui.adapters.ProductCardBinder;

import android.view.View;

import aeu.ratha.beckie_deal_app.ui.adapters.ProductCardBinder;

import android.widget.ImageView;

import aeu.ratha.beckie_deal_app.ui.adapters.ProductCardBinder;

import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;

/**
 * Binds a {@link MainActivity.Product} to the views inside {@code product_card.xml}.
 * Centralises all field binding so every Activity that inflates product_card.xml
 * stays in sync without duplicating logic.
 */
public class ProductCardBinder {

    /**
     * Shared image options. Reused across every card so Glide doesn't rebuild
     * the request settings each bind.
     *  - PREFER_RGB_565 halves decode memory & CPU vs ARGB_8888
     *  - override(400) caps decode resolution so a large source image can't
     *    block the main thread while being decoded/scaled
     *  - dontAnimate() skips the crossfade
     *  - error/placeholder settle cleanly on the 404 image URLs
     */
    private static final RequestOptions CARD_IMAGE_OPTIONS = new RequestOptions()
            .format(DecodeFormat.PREFER_RGB_565)
            .override(400)
            .centerInside()
            .dontAnimate()
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.placeholder_image);

    /**
     * Bind all visible fields of a product card.
     *
     * @param context    Activity/Fragment context
     * @param cardView   The root view inflated from product_card.xml
     * @param product    The product data to display
     */
    public static void bind(Context context, View cardView, MainActivity.Product product) {

        // ── Product name (header) ─────────────────────────────────────────
        TextView nameView = cardView.findViewById(R.id.productName);
        if (nameView != null) nameView.setText(product.name);

        // ── Product image ─────────────────────────────────────────────────
        ImageView imgView = cardView.findViewById(R.id.productImage);
        if (imgView != null) {
            Glide.with(context)
                 .load(product.imageUrl)
                 .apply(CARD_IMAGE_OPTIONS)
                 .into(imgView);
        }

        // ── Variant / full model line (same as name if no variant set) ────
        TextView variantView = cardView.findViewById(R.id.productVariant);
        if (variantView != null) {
            String variant = product.specs != null && !product.specs.isEmpty()
                    ? extractVariantLine(product) : "";
            if (!variant.isEmpty()) {
                variantView.setText(variant);
                variantView.setVisibility(View.VISIBLE);
            } else {
                variantView.setVisibility(View.GONE);
            }
        }

        // ── Stock status ──────────────────────────────────────────────────
        TextView stockView = cardView.findViewById(R.id.stockStatus);
        if (stockView != null) {
            stockView.setText(product.inStock ? "Available In Stock" : "Out Of Stock");
            stockView.setTextColor(product.inStock
                    ? 0xFF1565C0   // blue
                    : 0xFFB71C1C); // dark red
        }

        // ── Warranty ──────────────────────────────────────────────────────
        TextView warrantyView = cardView.findViewById(R.id.warrantyText);
        if (warrantyView != null) {
            if (product.warrantyYears > 0) {
                warrantyView.setText(product.warrantyYears + " Year Warranty");
                warrantyView.setVisibility(View.VISIBLE);
            } else {
                warrantyView.setVisibility(View.GONE);
            }
        }

        // ── SKU ───────────────────────────────────────────────────────────
        TextView skuView = cardView.findViewById(R.id.skuText);
        if (skuView != null) {
            if (product.sku != null && !product.sku.isEmpty()) {
                skuView.setText("SKU " + product.sku);
                skuView.setVisibility(View.VISIBLE);
            } else {
                skuView.setVisibility(View.GONE);
            }
        }

        // ── Price ─────────────────────────────────────────────────────────
        TextView priceView = cardView.findViewById(R.id.productPrice);
        if (priceView != null) priceView.setText(product.price);

        // ── Specs ─────────────────────────────────────────────────────────
        TextView specsView = cardView.findViewById(R.id.productSpecs);
        if (specsView != null) {
            if (product.specs != null && !product.specs.isEmpty()) {
                specsView.setText(product.specs);
                specsView.setVisibility(View.VISIBLE);
            } else {
                specsView.setVisibility(View.GONE);
            }
        }

        // ── Free items ────────────────────────────────────────────────────
        TextView freeView = cardView.findViewById(R.id.freeItemsText);
        if (freeView != null) {
            if (product.freeItems != null && !product.freeItems.isEmpty()) {
                freeView.setText("-" + product.freeItems);
                freeView.setVisibility(View.VISIBLE);
            } else {
                freeView.setVisibility(View.GONE);
            }
        }

        // ── Favorite icon ─────────────────────────────────────────────────
        ImageView favIcon = cardView.findViewById(R.id.favoriteIcon);
        if (favIcon != null) {
            refreshFavIcon(context, favIcon, product.isFavorite);
        }
    }

    /** Update just the favourite icon state (called on toggle). */
    public static void refreshFavIcon(Context context, ImageView icon, boolean isFavorite) {
        icon.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        icon.setColorFilter(ContextCompat.getColor(context,
                isFavorite ? R.color.red_error : R.color.text_secondary));
    }

    /**
     * Extract a short variant line from specs for the card sub-header.
     * Looks for the last bullet that mentions GB/TB (memory/storage) line.
     */
    private static String extractVariantLine(MainActivity.Product product) {
        if (product.specs == null || product.specs.isEmpty()) return "";
        // Return the last spec line which typically holds memory/storage/colour
        String[] lines = product.specs.split("\n");
        return lines[lines.length - 1].replaceFirst("^-", "").trim();
    }
}

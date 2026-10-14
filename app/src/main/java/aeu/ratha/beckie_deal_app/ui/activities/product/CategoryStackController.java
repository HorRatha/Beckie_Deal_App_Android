package aeu.ratha.beckie_deal_app.ui.activities.product;

import android.content.Context;
import android.content.res.ColorStateList;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.card.MaterialCardView;

import aeu.ratha.beckie_deal_app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds the stacked "wallet" category cards and expands a card to show its products.
 * Only touches the container you give it. Your brands grid, search bar, drawer and
 * bottom nav are not affected.
 */
public class CategoryStackController {

    private static final int MAX_PREVIEW = 5;      // products shown before "View all"
    private static final int OVERLAP_DP = 30;      // how much each card overlaps the previous
    private static final int EXPANDED_GAP_DP = 38; // space under an open card
    private static final int CORNER_DP = 26;

    // Card colors cycle through your own palette
    @ColorRes private static final int[] CARD_BG =
            {R.color.cat_yellow, R.color.dark_brown, R.color.brand_orange, R.color.badge_bg};

    @ColorRes private static final int[] CARD_TEXT =
            {R.color.dark_brown, R.color.white_fixed, R.color.white_fixed, R.color.dark_brown};

    @ColorRes private static final int[] CARD_ICON =
            {R.color.dark_brown, R.color.cat_yellow, R.color.white_fixed, R.color.primary};

    // ---------- Models: map your own classes into these ----------

    public static class CategoryUi {

        public final String id;

        public final String name;

        public final int itemCount;

        @DrawableRes public final int iconRes;

        public CategoryUi(String id, String name, int itemCount, @DrawableRes int iconRes) {

            this.id = id;

            this.name = name;

            this.itemCount = itemCount;

            this.iconRes = iconRes;

        }

    }

    public static class ProductUi {

        public final String id;

        public final String name;

        @Nullable public final String subtitle; // e.g. price

        @Nullable public final String imageUrl;

        public ProductUi(String id, String name, @Nullable String subtitle, @Nullable String imageUrl) {

            this.id = id;

            this.name = name;

            this.subtitle = subtitle;

            this.imageUrl = imageUrl;

        }

    }

    public interface ProductsCallback {

        void onLoaded(@NonNull List<ProductUi> products);

        void onError();

    }

    /** Load products for a category. Can call the callback from any thread. */

    public interface ProductLoader {

        void load(@NonNull CategoryUi category, @NonNull ProductsCallback callback);

    }

    /** Plug in Glide / Coil / Picasso here. */

    public interface ImageLoader {

        void load(@NonNull ImageView target, @NonNull String url);

    }

    public interface Listener {

        void onOpenCategory(@NonNull CategoryUi category);

        void onProductClick(@NonNull CategoryUi category, @NonNull ProductUi product);

    }

    // ---------- Internals ----------

    private static class Section {

        CategoryUi data;

        View root;

        MaterialCardView card;

        View panel;

        View loading;

        TextView empty;

        LinearLayout products;

        MaterialCardView viewAll;

        TextView viewAllText;

        ImageView chevron;

        boolean expanded;

        List<ProductUi> cache;

    }

    private final Context context;

    private final LinearLayout container;

    private final NestedScrollView scrollView;

    private final ProductLoader loader;

    private final Listener listener;

    private final List<Section> sections = new ArrayList<>();

    private ImageLoader imageLoader;

    private Section open;

    public CategoryStackController(@NonNull Context context,

                                   @NonNull LinearLayout container,

                                   @NonNull NestedScrollView scrollView,

                                   @Nullable ProductLoader loader,

                                   @Nullable Listener listener) {

        this.context = context;

        this.container = container;

        this.scrollView = scrollView;

        this.loader = loader;

        this.listener = listener;

    }

    public void setImageLoader(@Nullable ImageLoader imageLoader) {

        this.imageLoader = imageLoader;

    }

    public void bind(@NonNull List<CategoryUi> categories) {

        container.removeAllViews();

        sections.clear();

        open = null;

        LayoutInflater inflater = LayoutInflater.from(context);

        for (int i = 0; i < categories.size(); i++) {

            final CategoryUi c = categories.get(i);

            final Section s = new Section();

            s.data = c;

            s.root = inflater.inflate(R.layout.item_category_stack, container, false);

            s.card = s.root.findViewById(R.id.stackCard);

            s.panel = s.root.findViewById(R.id.stackPanel);

            s.loading = s.root.findViewById(R.id.stackLoading);

            s.empty = s.root.findViewById(R.id.stackEmpty);

            s.products = s.root.findViewById(R.id.stackProducts);

            s.viewAll = s.root.findViewById(R.id.stackViewAll);

            s.viewAllText = s.root.findViewById(R.id.stackViewAllText);

            s.chevron = s.root.findViewById(R.id.stackChevron);

            int p = i % CARD_BG.length;

            int textColor = ContextCompat.getColor(context, CARD_TEXT[p]);

            int iconColor = ContextCompat.getColor(context, CARD_ICON[p]);

            s.card.setCardBackgroundColor(ContextCompat.getColor(context, CARD_BG[p]));

            TextView name = s.root.findViewById(R.id.stackName);

            TextView count = s.root.findViewById(R.id.stackCount);

            ImageView icon = s.root.findViewById(R.id.stackIcon);

            name.setText(c.name);

            name.setTextColor(textColor);

            count.setText(countLabel(c.itemCount));

            count.setTextColor(textColor);

            icon.setImageResource(c.iconRes);

            ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(iconColor));

            ImageViewCompat.setImageTintList(s.chevron, ColorStateList.valueOf(textColor));

            s.card.setAlpha(c.itemCount == 0 ? 0.65f : 1f);

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) s.root.getLayoutParams();

            lp.topMargin = (i == 0) ? 0 : -dp(OVERLAP_DP);

            s.root.setLayoutParams(lp);

            // Disable interaction for empty categories
            if (c.itemCount > 0) {
                s.card.setClickable(true);
                s.card.setFocusable(true);
                s.card.setOnClickListener(v -> toggle(s));
            } else {
                s.card.setClickable(false);
                s.card.setFocusable(false);
                s.card.setOnClickListener(null);
            }

            // Set Open category button colors based on category header color
            s.viewAll.setCardBackgroundColor(ContextCompat.getColor(context, CARD_BG[p]));
            s.viewAllText.setTextColor(textColor);

            s.viewAll.setOnClickListener(v -> {

                if (listener != null) listener.onOpenCategory(c);

            });

            container.addView(s.root);

            sections.add(s);

        }

    }

    /** Collapse whatever is open (e.g. when the user leaves the screen). */

    public void collapseAll() {

        if (open != null) {

            TransitionManager.beginDelayedTransition(container, new AutoTransition().setDuration(200));

            setExpanded(open, false);

            open = null;

        }

    }

    // ---------- Expand / collapse ----------

    private void toggle(Section s) {

        boolean willExpand = !s.expanded;

        if (willExpand && open != null && open != s) {

            setExpanded(open, false); // accordion: one open at a time

        }

        TransitionManager.beginDelayedTransition(container, new AutoTransition().setDuration(220));

        setExpanded(s, willExpand);

        open = willExpand ? s : null;

        if (willExpand) {

            loadProducts(s);

            scrollToSection(s);

        }

    }

    private void setExpanded(Section s, boolean expanded) {

        s.expanded = expanded;

        s.panel.setVisibility(expanded ? View.VISIBLE : View.GONE);

        s.chevron.animate().rotation(expanded ? 180f : 0f).setDuration(200).start();

        if (expanded) {

            // card + panel read as one shape

            s.card.setShapeAppearanceModel(s.card.getShapeAppearanceModel().toBuilder()

                    .setBottomLeftCornerSize(0f)

                    .setBottomRightCornerSize(0f)

                    .build());

        } else {

            s.card.setRadius(dp(CORNER_DP));

        }

        // cancel the next card's overlap so it doesn't cover the open panel

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) s.root.getLayoutParams();

        lp.bottomMargin = expanded ? dp(EXPANDED_GAP_DP) : 0;

        s.root.setLayoutParams(lp);

    }

    private void scrollToSection(final Section s) {

        scrollView.postDelayed(() -> {

            int y = 0;

            View v = s.root;

            while (v != scrollView && v.getParent() instanceof View) {

                y += v.getTop();

                v = (View) v.getParent();

            }

            scrollView.smoothScrollTo(0, Math.max(0, y - dp(12)));

        }, 260);

    }

    // ---------- Products ----------

    private void loadProducts(final Section s) {

        if (s.cache != null) {

            render(s, s.cache);

            return;

        }

        if (s.data.itemCount == 0 || loader == null) {

            s.cache = new ArrayList<>();

            render(s, s.cache);

            return;

        }

        s.loading.setVisibility(View.VISIBLE);

        s.empty.setVisibility(View.GONE);

        s.viewAll.setVisibility(View.GONE);

        s.products.removeAllViews();

        loader.load(s.data, new ProductsCallback() {

            @Override

            public void onLoaded(@NonNull List<ProductUi> products) {

                final List<ProductUi> copy = new ArrayList<>(products);

                container.post(() -> {

                    s.cache = copy;

                    render(s, copy);

                });

            }

            @Override

            public void onError() {

                container.post(() -> showError(s));

            }

        });

    }

    private void render(Section s, List<ProductUi> list) {

        s.loading.setVisibility(View.GONE);

        s.products.removeAllViews();

        s.empty.setOnClickListener(null);

        s.empty.setClickable(false);

        if (list.isEmpty()) {

            // Show appropriate empty message based on context
            if (s.data.itemCount == 0) {
                s.empty.setText("Coming soon. Check back later.");
            } else {
                s.empty.setText("No products available.");
            }

            s.empty.setVisibility(View.VISIBLE);

            s.viewAll.setVisibility(View.VISIBLE);
            s.viewAllText.setText("Browse category");

            return;

        }

        s.empty.setVisibility(View.GONE);

        LayoutInflater inflater = LayoutInflater.from(context);

        int shown = Math.min(list.size(), MAX_PREVIEW);

        for (int i = 0; i < shown; i++) {

            final ProductUi p = list.get(i);

            View row = inflater.inflate(R.layout.item_category_wallet_product, s.products, false);

            ((TextView) row.findViewById(R.id.productName)).setText(p.name);

            // Bind price from subtitle (formatted as "$X.XX")
            TextView priceView = row.findViewById(R.id.productPrice);
            if (p.subtitle == null || p.subtitle.isEmpty()) {
                priceView.setText("$0.00");
            } else {
                priceView.setText(p.subtitle);
            }

            // Dynamically show/hide status badges based on product data
            TextView stockStatus = row.findViewById(R.id.stockStatus);
            TextView warrantyText = row.findViewById(R.id.warrantyText);
            
            // Only show status badges if we have actual status data
            // For now, hide them if no subtitle or if status is not available
            if (stockStatus != null) {
                stockStatus.setVisibility(
                    (p.subtitle != null && !p.subtitle.isEmpty()) ? View.VISIBLE : View.GONE
                );
            }
            if (warrantyText != null) {
                warrantyText.setVisibility(
                    (p.subtitle != null && !p.subtitle.isEmpty()) ? View.VISIBLE : View.GONE
                );
            }

            bindProductImage((ImageView) row.findViewById(R.id.productImage), p.imageUrl);

            row.setOnClickListener(v -> {

                if (listener != null) listener.onProductClick(s.data, p);

            });

            s.products.addView(row);

        }

        int total = Math.max(s.data.itemCount, list.size());

        s.viewAllText.setText(total > shown ? "View all " + total + " items" : "Open category");

        s.viewAll.setVisibility(View.VISIBLE);

    }

    private void showError(final Section s) {

        s.loading.setVisibility(View.GONE);

        s.viewAll.setVisibility(View.GONE);

        s.empty.setText("Couldn't load products. Tap to retry.");

        s.empty.setVisibility(View.VISIBLE);

        s.empty.setOnClickListener(v -> {

            s.empty.setOnClickListener(null);

            loadProducts(s);

        });

    }

    private void bindProductImage(ImageView img, @Nullable String url) {

        if (imageLoader != null && url != null && !url.isEmpty()) {

            img.setScaleType(ImageView.ScaleType.CENTER_CROP);

            imageLoader.load(img, url);

        } else {

            img.setImageResource(R.drawable.ic_category_default);

            int pad = dp(12);

            img.setPadding(pad, pad, pad, pad);

            img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        }

    }

    // ---------- Helpers ----------

    private static String countLabel(int n) {

        if (n == 0) return "Coming soon";

        return n == 1 ? "1 item" : n + " items";

    }

    private int dp(int value) {

        return Math.round(value * context.getResources().getDisplayMetrics().density);

    }

}

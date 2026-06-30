package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CategoryDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY   = "category_name";
    public static final String EXTRA_HERO_IMAGE = "category_image_url";
    public static final String EXTRA_ITEM_COUNT = "category_item_count";
    public static final String EXTRA_BRAND_FILTER = "brand_filter";

    private String currentBrandFilter = "All";
    private String currentCategoryName = "";
    private GridLayout productsGrid;
    private TextView resultsLabel;
    private TextView cartBadge;
    private LinearLayout brandFilterContainer;

    // Filter state (from FilterBottomSheet)
    private String currentSort     = FilterBottomSheet.SORT_DEFAULT;
    private String currentYear     = "All";
    private int    currentMaxPrice = 3000;

    // Full catalogue: category → brand → product list (public so ProductListActivity can reuse it)
    public static final Map<String, Map<String, List<MainActivity.Product>>> CATALOGUE = buildCatalogue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        currentCategoryName = getIntent().getStringExtra(EXTRA_CATEGORY);
        String heroImageUrl  = getIntent().getStringExtra(EXTRA_HERO_IMAGE);
        String itemCount     = getIntent().getStringExtra(EXTRA_ITEM_COUNT);
        String brandFilter   = getIntent().getStringExtra(EXTRA_BRAND_FILTER);
        if (currentCategoryName == null) currentCategoryName = "Category";
        if (brandFilter != null) currentBrandFilter = brandFilter;

        productsGrid         = findViewById(R.id.detailProductsGrid);
        resultsLabel         = findViewById(R.id.resultsLabel);
        brandFilterContainer = findViewById(R.id.brandFilterContainer);

        setupTopBar();
        setupHero(heroImageUrl, itemCount);
        setupBrandFilters();

        // Wire Filter & Sort button
        View sortBtn = findViewById(R.id.sortButton);
        if (sortBtn != null) sortBtn.setOnClickListener(v -> openFilterSheet());

        loadProducts(currentBrandFilter);
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    // ── Top bar ───────────────────────────────────────────────────────────────

    private void setupTopBar() {
        TextView titleView = findViewById(R.id.categoryTitleText);
        if (titleView != null) titleView.setText(currentCategoryName);

        ImageView backBtn = findViewById(R.id.backButton);
        if (backBtn != null) backBtn.setOnClickListener(v -> finish());

        cartBadge = findViewById(R.id.cartBadge);
        View cartContainer = findViewById(R.id.cartContainer);
        if (cartContainer != null)
            cartContainer.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));

        updateCartBadge();
    }

    private void updateCartBadge() {
        if (cartBadge == null) return;
        int count = CartManager.getInstance().getCartCount();
        cartBadge.setText(String.valueOf(count));
        cartBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
    }

    // ── Hero banner ───────────────────────────────────────────────────────────

    private void setupHero(String heroImageUrl, String itemCount) {
        ImageView heroImage = findViewById(R.id.categoryHeroImage);
        TextView heroTitle  = findViewById(R.id.heroTitle);
        TextView heroSub    = findViewById(R.id.heroSubtitle);

        if (heroTitle != null) heroTitle.setText(currentCategoryName);
        if (heroSub   != null) heroSub.setText(itemCount != null ? itemCount : "");

        if (heroImage != null && heroImageUrl != null && !heroImageUrl.isEmpty()) {
            Glide.with(this).load(heroImageUrl).centerCrop()
                 .placeholder(R.drawable.placeholder_image).into(heroImage);
        }
    }

    // ── Brand filter chips ────────────────────────────────────────────────────

    private void setupBrandFilters() {
        if (brandFilterContainer == null) return;

        List<String> chips = new ArrayList<>();
        chips.add("All");
        Map<String, List<MainActivity.Product>> brandMap = CATALOGUE.get(currentCategoryName);
        if (brandMap != null) chips.addAll(brandMap.keySet());

        brandFilterContainer.removeAllViews();
        for (String brand : chips) {
            View chip = LayoutInflater.from(this)
                    .inflate(R.layout.item_brand_chip, brandFilterContainer, false);
            TextView chipText = chip.findViewById(R.id.chipText);
            if (chipText != null) chipText.setText(brand);

            applyChipStyle((MaterialCardView) chip, chipText, brand.equals(currentBrandFilter));

            chip.setOnClickListener(v -> {
                currentBrandFilter = brand;
                setupBrandFilters();
                loadProducts(brand);
            });
            brandFilterContainer.addView(chip);
        }
    }

    private void applyChipStyle(MaterialCardView card, TextView text, boolean selected) {
        if (selected) {
            card.setCardBackgroundColor(Color.parseColor("#F97316"));
            card.setStrokeWidth(0);
            if (text != null) text.setTextColor(Color.WHITE);
        } else {
            card.setCardBackgroundColor(Color.WHITE);
            card.setStrokeWidth(1);
            card.setStrokeColor(Color.parseColor("#EEEEEE"));
            if (text != null) text.setTextColor(Color.parseColor("#555555"));
        }
    }

    // ── Products grid ─────────────────────────────────────────────────────────

    private void loadProducts(String brandFilter) {
        List<MainActivity.Product> filtered = new ArrayList<>();
        Map<String, List<MainActivity.Product>> brandMap = CATALOGUE.get(currentCategoryName);

        if (brandMap != null) {
            if ("All".equals(brandFilter)) {
                for (List<MainActivity.Product> list : brandMap.values()) filtered.addAll(list);
            } else {
                List<MainActivity.Product> list = brandMap.get(brandFilter);
                if (list != null) filtered.addAll(list);
            }
        }

        // Apply year filter (price-band simulation since products have no real year field)
        if (!"All".equals(currentYear)) {
            int y = Integer.parseInt(currentYear);
            int minP = (y - 2010) * 100;
            int maxP = minP + 400;
            List<MainActivity.Product> yearFiltered = new ArrayList<>();
            for (MainActivity.Product p : filtered) {
                double price = parsePrice(p.price);
                if (price >= minP && price <= maxP) yearFiltered.add(p);
            }
            if (!yearFiltered.isEmpty()) filtered = yearFiltered;
        }

        // Apply max price filter
        List<MainActivity.Product> priceFiltered = new ArrayList<>();
        for (MainActivity.Product p : filtered) {
            if (parsePrice(p.price) <= currentMaxPrice) priceFiltered.add(p);
        }
        filtered = priceFiltered;

        // Apply sort
        switch (currentSort) {
            case FilterBottomSheet.SORT_PRICE_LOW:
                Collections.sort(filtered, (a, b) ->
                        Double.compare(parsePrice(a.price), parsePrice(b.price)));
                break;
            case FilterBottomSheet.SORT_PRICE_HIGH:
                Collections.sort(filtered, (a, b) ->
                        Double.compare(parsePrice(b.price), parsePrice(a.price)));
                break;
            case FilterBottomSheet.SORT_TOP_RATED:
                Collections.sort(filtered, (a, b) ->
                        Double.compare(Double.parseDouble(b.rating), Double.parseDouble(a.rating)));
                break;
            default:
                break;
        }

        // Sync favorite state from FavoriteManager
        for (MainActivity.Product p : filtered)
            p.isFavorite = FavoriteManager.getInstance().isFavorite(p);

        // Build results label
        if (resultsLabel != null) {
            StringBuilder label = new StringBuilder();
            label.append(filtered.size()).append(" products");
            if (!"All".equals(brandFilter)) label.append(" · ").append(brandFilter);
            if (!currentSort.equals(FilterBottomSheet.SORT_DEFAULT))
                label.append(" · ").append(currentSort);
            if (!currentYear.equals("All")) label.append(" · ").append(currentYear);
            if (currentMaxPrice < 3000) label.append(" · ≤$").append(currentMaxPrice);
            resultsLabel.setText(label.toString());
        }

        renderProducts(filtered);
    }

    /** Open the filter bottom sheet. */
    private void openFilterSheet() {
        FilterBottomSheet sheet = FilterBottomSheet.newInstance(
                currentSort, currentYear, currentMaxPrice);
        sheet.setFilterListener((sortBy, year, maxPrice) -> {
            currentSort     = sortBy;
            currentYear     = year;
            currentMaxPrice = maxPrice;
            loadProducts(currentBrandFilter);
        });
        sheet.show(getSupportFragmentManager(), "filter");
    }

    private double parsePrice(String price) {
        try {
            return Double.parseDouble(price.replace("$", "").replace(",", ""));
        } catch (NumberFormatException e) { return 0; }
    }

    private void renderProducts(List<MainActivity.Product> products) {
        if (productsGrid == null) return;
        productsGrid.removeAllViews();

        if (products.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No products found.");
            empty.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            empty.setPadding(32, 64, 32, 64);
            productsGrid.addView(empty);
            return;
        }

        for (int i = 0; i < products.size(); i++) {
            final MainActivity.Product product = products.get(i);
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.product_card, productsGrid, false);

            ImageView imgView  = view.findViewById(R.id.productImage);
            ImageView favIcon  = view.findViewById(R.id.favoriteIcon);
            TextView  nameView = view.findViewById(R.id.productName);
            TextView  priceView= view.findViewById(R.id.productPrice);
            TextView  ratingView=view.findViewById(R.id.productRating);

            nameView.setText(product.name);
            priceView.setText(product.price);
            ratingView.setText(product.rating + " ★");

            Glide.with(this).load(product.imageUrl).centerCrop()
                 .placeholder(R.drawable.placeholder_image).into(imgView);

            refreshFavIcon(favIcon, product.isFavorite);

            // Open product detail on card tap
            view.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProductDetailActivity.class);
                intent.putExtra("product_name",   product.name);
                intent.putExtra("product_price",  product.price);
                intent.putExtra("product_image",  product.imageUrl);
                intent.putExtra("product_rating", product.rating);
                startActivity(intent);
            });

            // Toggle favorite on heart tap
            favIcon.setOnClickListener(v -> {
                product.isFavorite = !product.isFavorite;
                if (product.isFavorite) {
                    FavoriteManager.getInstance().addFavorite(product);
                    Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                } else {
                    FavoriteManager.getInstance().removeFavorite(product);
                    Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                }
                refreshFavIcon(favIcon, product.isFavorite);
            });

            productsGrid.addView(view);
        }
    }

    private void refreshFavIcon(ImageView icon, boolean fav) {
        icon.setImageResource(fav ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        icon.setColorFilter(ContextCompat.getColor(this,
                fav ? R.color.red_error : R.color.text_secondary));
    }

    // ── Bottom navigation ─────────────────────────────────────────────────────

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;

        bottomNav.setBackgroundColor(Color.TRANSPARENT);
        MaterialCardView container = findViewById(R.id.bottomNavigationContainer);
        if (container != null)
            container.setCardBackgroundColor(ContextCompat.getColor(this, R.color.surface));

        bottomNav.setSelectedItemId(R.id.nav_categories);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if      (id == R.id.nav_home)       startActivity(new Intent(this, MainActivity.class));
            else if (id == R.id.nav_categories) startActivity(new Intent(this, CategoriesActivity.class));
            else if (id == R.id.nav_favorites)  startActivity(new Intent(this, FavoritesActivity.class));
            else if (id == R.id.nav_about)      startActivity(new Intent(this, AboutActivity.class));
            else if (id == R.id.nav_account)    startActivity(new Intent(this, ProfileActivity.class));
            else return true;
            overridePendingTransition(0, 0);
            finish();
            return true;
        });
    }

    // ── Static catalogue (category → brand → products) ────────────────────────

    private static Map<String, Map<String, List<MainActivity.Product>>> buildCatalogue() {
        Map<String, Map<String, List<MainActivity.Product>>> cat = new LinkedHashMap<>();

        // ── LAPTOPS — Apple, ASUS, Dell, HP, Lenovo, Acer, Microsoft ─────────
        Map<String, List<MainActivity.Product>> laptops = new LinkedHashMap<>();
        laptops.put("Apple", list(
                p("MacBook Air M2",            "$1,199.00","4.9","https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=500&h=500&fit=crop"),
                p("MacBook Pro 14\"",           "$1,999.00","4.8","https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?w=500&h=500&fit=crop"),
                p("MacBook Pro M3 Max",         "$2,499.00","5.0","https://images.unsplash.com/photo-1542393545-10f5cde2c810?w=500&h=500&fit=crop")
        ));
        laptops.put("ASUS", list(
                p("ASUS ZenBook 14 OLED",       "$999.00", "4.7","https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=500&h=500&fit=crop"),
                p("ASUS VivoBook 16X",           "$749.00", "4.5","https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=500&h=500&fit=crop"),
                p("ASUS ProArt Studiobook",      "$1,799.00","4.8","https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500&h=500&fit=crop")
        ));
        laptops.put("Dell", list(
                p("Dell XPS 15",                "$1,749.00","4.7","https://images.unsplash.com/photo-1593642634367-d91a135587b5?w=500&h=500&fit=crop"),
                p("Dell Inspiron 15 3000",       "$599.00", "4.3","https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500&h=500&fit=crop"),
                p("Dell Latitude 5540",          "$1,299.00","4.6","https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?w=500&h=500&fit=crop")
        ));
        laptops.put("HP", list(
                p("HP Spectre x360 14",          "$1,449.00","4.7","https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=500&h=500&fit=crop"),
                p("HP Pavilion 15",              "$679.00", "4.4","https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?w=500&h=500&fit=crop"),
                p("HP EliteBook 840 G10",        "$1,599.00","4.6","https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=500&h=500&fit=crop")
        ));
        laptops.put("Lenovo", list(
                p("ThinkPad X1 Carbon Gen 11",   "$1,849.00","4.8","https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?w=500&h=500&fit=crop"),
                p("IdeaPad Slim 5 Gen 8",        "$699.00", "4.5","https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500&h=500&fit=crop"),
                p("Lenovo Yoga 9i Gen 8",        "$1,299.00","4.7","https://images.unsplash.com/photo-1542393545-10f5cde2c810?w=500&h=500&fit=crop")
        ));
        laptops.put("Acer", list(
                p("Acer Swift X 14",             "$999.00", "4.5","https://images.unsplash.com/photo-1593642634367-d91a135587b5?w=500&h=500&fit=crop"),
                p("Acer Aspire 5",               "$549.00", "4.3","https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=500&h=500&fit=crop"),
                p("Acer Predator Helios 16",     "$1,599.00","4.7","https://images.unsplash.com/photo-1527814050087-3793815479db?w=500&h=500&fit=crop")
        ));
        laptops.put("Microsoft", list(
                p("Surface Laptop 5",            "$1,299.00","4.7","https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?w=500&h=500&fit=crop"),
                p("Surface Pro 9",               "$1,599.00","4.6","https://images.unsplash.com/photo-1542393545-10f5cde2c810?w=500&h=500&fit=crop"),
                p("Surface Laptop Studio 2",     "$1,999.00","4.8","https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?w=500&h=500&fit=crop")
        ));
        cat.put("Laptops", laptops);

        // ── SMARTPHONES — Apple, Samsung, ASUS ───────────────────────────────
        Map<String, List<MainActivity.Product>> phones = new LinkedHashMap<>();
        phones.put("Apple", list(
                p("iPhone 15 Pro Max",          "$1,199.00","4.9","https://images.unsplash.com/photo-1592286927505-1fed6c3d2c16?w=500&h=500&fit=crop"),
                p("iPhone 15 Pro",              "$999.00",  "4.9","https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?w=500&h=500&fit=crop"),
                p("iPhone 14",                  "$699.00",  "4.7","https://images.unsplash.com/photo-1574944985070-8f3ebc6b79d2?w=500&h=500&fit=crop")
        ));
        phones.put("Samsung", list(
                p("Samsung S24 Ultra",           "$1,299.00","4.8","https://images.unsplash.com/photo-1511707267537-b85faf00021e?w=500&h=500&fit=crop"),
                p("Samsung Galaxy Z Fold 5",     "$1,799.00","4.7","https://images.unsplash.com/photo-1585060544812-6b45742d762f?w=500&h=500&fit=crop"),
                p("Samsung Galaxy A55",          "$449.00",  "4.5","https://images.unsplash.com/photo-1580910051074-3eb694886505?w=500&h=500&fit=crop")
        ));
        phones.put("ASUS", list(
                p("ASUS ROG Phone 8 Pro",        "$1,099.00","4.7","https://images.unsplash.com/photo-1605236453806-6ff36851218e?w=500&h=500&fit=crop"),
                p("ASUS Zenfone 10",             "$699.00",  "4.6","https://images.unsplash.com/photo-1550399105-c4db5fb85c18?w=500&h=500&fit=crop")
        ));
        cat.put("Smartphones", phones);

        // ── ACCESSORIES — Microsoft, Dell, HP ────────────────────────────────
        Map<String, List<MainActivity.Product>> acc = new LinkedHashMap<>();
        acc.put("Microsoft", list(
                p("Microsoft Arc Mouse",        "$79.00",  "4.7","https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500&h=500&fit=crop"),
                p("Microsoft Ergonomic Kbd",    "$59.00",  "4.6","https://images.unsplash.com/photo-1541140532154-b024d705b90a?w=500&h=500&fit=crop"),
                p("Surface Dock 2",             "$259.00", "4.5","https://images.unsplash.com/photo-1625595339169-07bc2c10bb81?w=500&h=500&fit=crop")
        ));
        acc.put("Dell", list(
                p("Dell 27 USB-C Monitor",      "$499.00", "4.6","https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=500&h=500&fit=crop"),
                p("Dell Pro Webcam WB5023",     "$99.00",  "4.5","https://images.unsplash.com/photo-1596558450268-9c27524ba856?w=500&h=500&fit=crop"),
                p("Dell WD19S USB-C Dock",      "$189.00", "4.4","https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=500&h=500&fit=crop")
        ));
        acc.put("HP", list(
                p("HP 27f 4K Display",          "$449.00", "4.5","https://images.unsplash.com/photo-1593305841991-05c297ba4575?w=500&h=500&fit=crop"),
                p("HP Thunderbolt Dock G4",     "$219.00", "4.4","https://images.unsplash.com/photo-1583394838336-acd977736f90?w=500&h=500&fit=crop")
        ));
        cat.put("Accessories", acc);

        // ── AUDIO — Apple, Samsung, ASUS ─────────────────────────────────────
        Map<String, List<MainActivity.Product>> audio = new LinkedHashMap<>();
        audio.put("Apple", list(
                p("AirPods Pro 2nd Gen",        "$249.00", "4.9","https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=500&h=500&fit=crop"),
                p("AirPods Max",                "$549.00", "4.8","https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&h=500&fit=crop"),
                p("AirPods 3rd Gen",            "$169.00", "4.7","https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=500&h=500&fit=crop")
        ));
        audio.put("Samsung", list(
                p("Galaxy Buds 2 Pro",          "$229.00", "4.7","https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=500&h=500&fit=crop"),
                p("Galaxy Buds FE",             "$99.00",  "4.5","https://images.unsplash.com/photo-1484704849700-f032a568e944?w=500&h=500&fit=crop")
        ));
        audio.put("ASUS", list(
                p("ROG Cetra True Wireless",    "$149.00", "4.6","https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&h=500&fit=crop"),
                p("ROG Delta S Wireless",       "$199.00", "4.7","https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=500&h=500&fit=crop")
        ));
        cat.put("Audio", audio);

        // ── GAMING — ROG, MSI, Razer, Acer ───────────────────────────────────
        Map<String, List<MainActivity.Product>> gaming = new LinkedHashMap<>();
        gaming.put("ROG", list(
                p("ROG Strix G16 2024",         "$1,799.00","4.9","https://images.unsplash.com/photo-1593305841991-05c297ba4575?w=500&h=500&fit=crop"),
                p("ROG Ally X Handheld",         "$799.00",  "4.8","https://images.unsplash.com/photo-1580234811497-9df7fd2f357e?w=500&h=500&fit=crop"),
                p("ROG Swift OLED 27\"",          "$899.00",  "4.8","https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=500&h=500&fit=crop")
        ));
        gaming.put("MSI", list(
                p("MSI Titan GT77 HX",           "$2,299.00","4.7","https://images.unsplash.com/photo-1612287230202-1ff1d85d1bdf?w=500&h=500&fit=crop"),
                p("MSI Raider GE78 HX",          "$1,899.00","4.6","https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=500&h=500&fit=crop"),
                p("MSI Optix MAG274QRF",         "$399.00",  "4.5","https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=500&h=500&fit=crop")
        ));
        gaming.put("Razer", list(
                p("Razer Blade 16 2024",         "$2,799.00","4.8","https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=500&h=500&fit=crop"),
                p("Razer DeathAdder V3 Pro",     "$149.00",  "4.9","https://images.unsplash.com/photo-1585792180666-f7347c490ee2?w=500&h=500&fit=crop"),
                p("Razer BlackWidow V4 Pro",     "$229.00",  "4.7","https://images.unsplash.com/photo-1541140532154-b024d705b90a?w=500&h=500&fit=crop")
        ));
        gaming.put("Acer", list(
                p("Acer Predator Helios 16",     "$1,599.00","4.7","https://images.unsplash.com/photo-1527814050087-3793815479db?w=500&h=500&fit=crop"),
                p("Acer Nitro 5 AN515",          "$899.00",  "4.5","https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=500&h=500&fit=crop")
        ));
        cat.put("Gaming", gaming);

        // ── WEARABLES — Apple, Samsung, Microsoft ────────────────────────────
        Map<String, List<MainActivity.Product>> wear = new LinkedHashMap<>();
        wear.put("Apple", list(
                p("Apple Watch Ultra 2",        "$799.00",  "4.9","https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&h=500&fit=crop"),
                p("Apple Watch Series 9",       "$399.00",  "4.8","https://images.unsplash.com/photo-1434494878577-86c23bcb06b9?w=500&h=500&fit=crop"),
                p("Apple Watch SE Gen 2",       "$249.00",  "4.6","https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=500&h=500&fit=crop")
        ));
        wear.put("Samsung", list(
                p("Galaxy Watch 6 Classic",     "$399.00",  "4.7","https://images.unsplash.com/photo-1579586337278-3befd40fd17a?w=500&h=500&fit=crop"),
                p("Galaxy Watch 6",             "$299.00",  "4.6","https://images.unsplash.com/photo-1617043786394-f977fa12eddf?w=500&h=500&fit=crop"),
                p("Samsung Galaxy Fit 3",       "$99.00",   "4.4","https://images.unsplash.com/photo-1575311373937-040b8e1fd5b6?w=500&h=500&fit=crop")
        ));
        wear.put("Microsoft", list(
                p("Surface Duo 2",              "$799.00",  "4.3","https://images.unsplash.com/photo-1605236453806-6ff36851218e?w=500&h=500&fit=crop"),
                p("Microsoft Band 2",           "$149.00",  "4.2","https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=500&h=500&fit=crop")
        ));
        cat.put("Wearables", wear);

        return cat;
    }

    // Shorthand helpers to keep the catalogue readable
    private static MainActivity.Product p(String name, String price, String rating, String img) {
        return new MainActivity.Product(name, price, rating, false, img);
    }

    @SafeVarargs
    private static <T> List<T> list(T... items) {
        List<T> l = new ArrayList<>();
        for (T t : items) l.add(t);
        return l;
    }
}

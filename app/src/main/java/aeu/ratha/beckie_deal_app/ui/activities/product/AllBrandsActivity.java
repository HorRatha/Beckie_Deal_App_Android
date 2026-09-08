package aeu.ratha.beckie_deal_app.ui.activities.product;

import aeu.ratha.beckie_deal_app.R;


import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.ui.activities.product.CategoryDetailActivity;

import android.os.Bundle;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.ui.activities.product.CategoryDetailActivity;

import android.view.LayoutInflater;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.ui.activities.product.CategoryDetailActivity;

import android.view.View;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.ui.activities.product.CategoryDetailActivity;

import android.widget.GridLayout;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.ui.activities.product.CategoryDetailActivity;

import android.widget.ImageView;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.ui.activities.product.CategoryDetailActivity;

import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class AllBrandsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_brands);

        setupTopBar();
        displayAllBrands();
        UiAnimations.fadeSlideDown(findViewById(R.id.topAppBar), 0);
    }

    private void setupTopBar() {
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
        UiAnimations.pressBounce(backButton);
    }

    private void displayAllBrands() {
        GridLayout allBrandsGrid = findViewById(R.id.allBrandsGrid);
        if (allBrandsGrid == null) return;

        List<CategoriesActivity.Brand> brands = new ArrayList<>();
        brands.add(new CategoriesActivity.Brand("ASUS",      R.drawable.ic_brand_asus,      false));
        brands.add(new CategoriesActivity.Brand("ROG",       R.drawable.ic_brand_rog,       false));
        brands.add(new CategoriesActivity.Brand("Apple",     R.drawable.ic_brand_apple,     false));
        brands.add(new CategoriesActivity.Brand("Dell",      R.drawable.ic_brand_dell,      false));
        brands.add(new CategoriesActivity.Brand("HP",        R.drawable.ic_brand_hp,        false));
        brands.add(new CategoriesActivity.Brand("MSI",       R.drawable.ic_brand_msi,       false));
        brands.add(new CategoriesActivity.Brand("Samsung",   R.drawable.ic_brand_samsung,   false));
        brands.add(new CategoriesActivity.Brand("Lenovo",    R.drawable.ic_brand_lenovo,    false));
        brands.add(new CategoriesActivity.Brand("Acer",      R.drawable.ic_brand_acer,      false));
        brands.add(new CategoriesActivity.Brand("Razer",     R.drawable.ic_brand_razer,     false));
        brands.add(new CategoriesActivity.Brand("Microsoft", R.drawable.ic_brand_microsoft, false));

        allBrandsGrid.removeAllViews();
        for (CategoriesActivity.Brand brand : brands) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_brand_square, allBrandsGrid, false);

            ImageView brandIcon = view.findViewById(R.id.brandIcon);
            TextView  iconText  = view.findViewById(R.id.brandIconText);
            TextView  nameText  = view.findViewById(R.id.brandName);

            brandIcon.setVisibility(View.VISIBLE);
            iconText.setVisibility(View.GONE);
            brandIcon.setImageResource(brand.imageResId);
            brandIcon.clearColorFilter();
            nameText.setText(brand.name);

            // Open CategoryDetailActivity filtered to this brand's products
            view.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(this, CategoryDetailActivity.class);
                // Pick the most relevant category per brand
                String cat = brandToCategory(brand.name);
                intent.putExtra(CategoryDetailActivity.EXTRA_CATEGORY, cat);
                intent.putExtra(CategoryDetailActivity.EXTRA_BRAND_FILTER, brand.name);
                startActivity(intent);
            });

            allBrandsGrid.addView(view);
        }
    }

    private String brandToCategory(String brand) {
        switch (brand) {
            case "ASUS":      return "Laptops";
            case "ROG":       return "Gaming";
            case "Apple":     return "Laptops";
            case "Dell":      return "Laptops";
            case "HP":        return "Laptops";
            case "MSI":       return "Gaming";
            case "Samsung":   return "Smartphones";
            case "Lenovo":    return "Laptops";
            case "Acer":      return "Laptops";
            case "Razer":     return "Gaming";
            case "Microsoft": return "Laptops";
            default:          return "Laptops";
        }
    }
}

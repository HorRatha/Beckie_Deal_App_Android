package aeu.ratha.beckie_deal_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

public class AllBrandsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_brands);

        setupTopBar();
        displayAllBrands();
    }

    private void setupTopBar() {
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }

    private void displayAllBrands() {
        GridLayout allBrandsGrid = findViewById(R.id.allBrandsGrid);
        if (allBrandsGrid == null) return;

        List<CategoriesActivity.Brand> brands = new ArrayList<>();
        // Updated to use local Vector drawables
        brands.add(new CategoriesActivity.Brand("ASUS", R.drawable.ic_brand_asus, false));
        brands.add(new CategoriesActivity.Brand("ROG", R.drawable.ic_brand_rog, false));
        brands.add(new CategoriesActivity.Brand("Apple", R.drawable.ic_brand_apple, false));
        brands.add(new CategoriesActivity.Brand("Dell", R.drawable.ic_brand_dell, false));
        brands.add(new CategoriesActivity.Brand("HP", R.drawable.ic_brand_hp, false));
        brands.add(new CategoriesActivity.Brand("MSI", R.drawable.ic_brand_msi, false));
        brands.add(new CategoriesActivity.Brand("Samsung", R.drawable.ic_brand_samsung, false));
        
        // These still use URLs or placeholders as vectors aren't available yet for them
        brands.add(new CategoriesActivity.Brand("Lenovo", "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b8/Lenovo_logo_2015.svg/2560px-Lenovo_logo_2015.svg.png", false));
        brands.add(new CategoriesActivity.Brand("Acer", "https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/Acer_2011.svg/2560px-Acer_2011.svg.png", false));
        brands.add(new CategoriesActivity.Brand("Razer", "https://upload.wikimedia.org/wikipedia/en/thumb/4/40/Razer_snake_logo.svg/1200px-Razer_snake_logo.svg.png", false));
        brands.add(new CategoriesActivity.Brand("Microsoft", "https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/Microsoft_logo.svg/2048px-Microsoft_logo.svg.png", false));

        allBrandsGrid.removeAllViews();
        for (CategoriesActivity.Brand brand : brands) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_brand_square, allBrandsGrid, false);
            
            MaterialCardView card = view.findViewById(R.id.brandCard);
            ImageView brandIcon = view.findViewById(R.id.brandIcon);
            TextView iconText = view.findViewById(R.id.brandIconText);
            TextView nameText = view.findViewById(R.id.brandName);

            if (brand.imageResId != 0) {
                brandIcon.setVisibility(View.VISIBLE);
                iconText.setVisibility(View.GONE);
                brandIcon.setImageResource(brand.imageResId);
                // Clear color filter if it was set elsewhere, or set it to primary if needed
                brandIcon.clearColorFilter();
            } else if (brand.imageUrl != null && !brand.imageUrl.isEmpty()) {
                brandIcon.setVisibility(View.VISIBLE);
                iconText.setVisibility(View.GONE);
                Glide.with(this)
                        .load(brand.imageUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .into(brandIcon);
            } else {
                brandIcon.setVisibility(View.GONE);
                iconText.setVisibility(View.VISIBLE);
                iconText.setText(brand.name.substring(0, Math.min(brand.name.length(), 2)).toUpperCase());
            }
            
            nameText.setText(brand.name);
            allBrandsGrid.addView(view);
        }
    }
}

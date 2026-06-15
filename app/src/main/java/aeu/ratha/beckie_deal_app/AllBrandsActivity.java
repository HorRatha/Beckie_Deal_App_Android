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
        brands.add(new CategoriesActivity.Brand("ASUS", "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2e/ASUS_Logo.svg/2560px-ASUS_Logo.svg.png", false));
        brands.add(new CategoriesActivity.Brand("ROG", "https://logos-world.net/wp-content/uploads/2020/11/ROG-Logo.png", false));
        brands.add(new CategoriesActivity.Brand("Apple", "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fa/Apple_logo_black.svg/1665px-Apple_logo_black.svg.png", false));
        brands.add(new CategoriesActivity.Brand("Dell", "https://upload.wikimedia.org/wikipedia/commons/thumb/1/18/Dell_logo_2016.svg/1200px-Dell_logo_2016.svg.png", false));
        brands.add(new CategoriesActivity.Brand("HP", "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ad/HP_logo_2012.svg/1200px-HP_logo_2012.svg.png", false));
        brands.add(new CategoriesActivity.Brand("MSI", "https://upload.wikimedia.org/wikipedia/en/thumb/f/ff/MSI_Dragon_Logo.svg/1200px-MSI_Dragon_Logo.svg.png", false));
        brands.add(new CategoriesActivity.Brand("Lenovo", "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b8/Lenovo_logo_2015.svg/2560px-Lenovo_logo_2015.svg.png", false));
        brands.add(new CategoriesActivity.Brand("Acer", "https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/Acer_2011.svg/2560px-Acer_2011.svg.png", false));
        brands.add(new CategoriesActivity.Brand("Razer", "https://upload.wikimedia.org/wikipedia/en/thumb/4/40/Razer_snake_logo.svg/1200px-Razer_snake_logo.svg.png", false));
        brands.add(new CategoriesActivity.Brand("Microsoft", "https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/Microsoft_logo.svg/2048px-Microsoft_logo.svg.png", false));
        brands.add(new CategoriesActivity.Brand("Samsung", "https://upload.wikimedia.org/wikipedia/commons/thumb/2/24/Samsung_Logo.svg/2560px-Samsung_Logo.svg.png", false));

        allBrandsGrid.removeAllViews();
        for (CategoriesActivity.Brand brand : brands) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_brand_square, allBrandsGrid, false);
            
            MaterialCardView card = view.findViewById(R.id.brandCard);
            ImageView brandIcon = view.findViewById(R.id.brandIcon);
            TextView iconText = view.findViewById(R.id.brandIconText);
            TextView nameText = view.findViewById(R.id.brandName);

            if (brand.imageUrl != null && !brand.imageUrl.isEmpty()) {
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

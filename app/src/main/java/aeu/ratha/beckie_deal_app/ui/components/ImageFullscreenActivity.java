package aeu.ratha.beckie_deal_app;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

/**
 * Full-screen immersive image viewer.
 * Launched from ProductDetailActivity with:
 *   - "image_urls"   ArrayList<String>
 *   - "start_index"  int (which image to show first)
 */
public class ImageFullscreenActivity extends AppCompatActivity {

    private ArrayList<String> imageUrls;
    private int currentIndex = 0;

    private ImageView fullscreenImage;
    private TextView  fullscreenCounter;
    private View[]    dots = new View[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // True full-screen: hide status bar & nav bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Draw behind system bars (edge-to-edge)
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        setContentView(R.layout.activity_image_fullscreen);

        imageUrls    = getIntent().getStringArrayListExtra("image_urls");
        currentIndex = getIntent().getIntExtra("start_index", 0);
        if (imageUrls == null || imageUrls.isEmpty()) { finish(); return; }

        fullscreenImage   = findViewById(R.id.fullscreenImage);
        fullscreenCounter = findViewById(R.id.fullscreenCounter);

        dots[0] = findViewById(R.id.fsDot1);
        dots[1] = findViewById(R.id.fsDot2);
        dots[2] = findViewById(R.id.fsDot3);
        dots[3] = findViewById(R.id.fsDot4);

        // Close button
        View close = findViewById(R.id.fullscreenClose);
        if (close != null) close.setOnClickListener(v -> finish());

        // Prev / Next
        View prev = findViewById(R.id.fullscreenPrev);
        View next = findViewById(R.id.fullscreenNext);
        if (prev != null) prev.setOnClickListener(v -> navigate(-1));
        if (next != null) next.setOnClickListener(v -> navigate(+1));

        showImage(currentIndex);
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    private void navigate(int delta) {
        int size = imageUrls.size();
        currentIndex = (currentIndex + delta + size) % size;
        showImage(currentIndex);
    }

    private void showImage(int index) {
        String url = imageUrls.get(index);
        Glide.with(this)
             .load(url)
             .placeholder(R.drawable.placeholder_image)
             .into(fullscreenImage);

        // Counter label
        if (fullscreenCounter != null) {
            fullscreenCounter.setText((index + 1) + " / " + imageUrls.size());
        }

        // Dots
        for (int i = 0; i < 4; i++) {
            if (dots[i] == null) continue;
            android.view.ViewGroup.LayoutParams lp = dots[i].getLayoutParams();
            if (i == index) {
                lp.width = dpToPx(20);
                dots[i].setLayoutParams(lp);
                dots[i].setBackgroundResource(R.drawable.dot_active);
            } else {
                lp.width = dpToPx(6);
                dots[i].setLayoutParams(lp);
                dots[i].setBackgroundResource(R.drawable.dot_inactive);
            }
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}

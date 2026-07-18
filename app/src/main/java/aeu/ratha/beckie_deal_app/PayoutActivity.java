package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class PayoutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payout);

        setupTopBar();
        setupOrderSummary();
        setupConfirmButton();
    }

    private void setupTopBar() {
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }

    private void setupOrderSummary() {
        double subtotal = CartManager.getInstance().getTotalPrice();
        double shipping = 15.00;
        double total = subtotal + shipping;

        TextView subtotalText = findViewById(R.id.summarySubtotal);
        TextView totalText = findViewById(R.id.summaryTotal);

        if (subtotalText != null) subtotalText.setText(String.format(Locale.US, "$%.2f", subtotal));
        if (totalText != null) totalText.setText(String.format(Locale.US, "$%.2f", total));
    }

    private void setupConfirmButton() {
        Button confirmButton = findViewById(R.id.confirmPaymentButton);
        View processingOverlay = findViewById(R.id.processingOverlay);
        View successView = findViewById(R.id.successView);
        Button backToHomeButton = findViewById(R.id.backToHomeButton);
        Button viewOrdersButton = findViewById(R.id.viewOrdersButton);
        TextView successOrderId = findViewById(R.id.successOrderId);
        TextView successAmountPaid = findViewById(R.id.successAmountPaid);

        if (confirmButton != null) {
            confirmButton.setOnClickListener(v -> {
                // Show processing overlay
                if (processingOverlay != null) {
                    processingOverlay.setVisibility(View.VISIBLE);
                }

                // Simulate processing for 3 seconds
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    if (processingOverlay != null) {
                        processingOverlay.setVisibility(View.GONE);
                    }

                    // Save the order before clearing cart
                    double subtotal = CartManager.getInstance().getTotalPrice();
                    double total = subtotal + 15.00; // shipping
                    OrderManager.Order order = OrderManager.getInstance().placeOrder(
                            CartManager.getInstance().getCartItems(), total);

                    if (successOrderId != null) successOrderId.setText(order.orderId);
                    if (successAmountPaid != null) {
                        successAmountPaid.setText(String.format(Locale.US, "$%.2f", total));
                    }

                    if (successView != null) {
                        successView.setVisibility(View.VISIBLE);
                    }

                    // Clear cart after successful "payment"
                    CartManager.getInstance().getCartItems().clear();
                }, 3000);
            });
        }

        if (backToHomeButton != null) {
            backToHomeButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }

        if (viewOrdersButton != null) {
            viewOrdersButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, MyOrdersActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }
    }
}

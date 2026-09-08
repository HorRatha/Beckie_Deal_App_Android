package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import java.util.Locale;

public class PayoutActivity extends BaseActivity {

    private TextView summarySubtotalText;
    private TextView summaryDiscountText;
    private TextView summaryTotalText;
    private LinearLayout discountRow;
    private EditText couponInput;
    private MaterialCardView appliedCouponCard;
    private TextView appliedCouponCode;
    private TextView appliedCouponDiscount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payout);

        setupTopBar();
        setupCouponSection();
        setupOrderSummary();
        setupConfirmButton();
        setupEntranceAnimation();
    }

    private void setupTopBar() {
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
        UiAnimations.pressBounce(backButton);
    }

    /** Staggers the shipping/payment/summary cards into place, and fades in the header. */
    private void setupEntranceAnimation() {
        UiAnimations.fadeSlideDown(findViewById(R.id.appBarLayout), 0);
        androidx.core.widget.NestedScrollView scroll = findViewById(R.id.payoutContentScroll);
        if (scroll != null && scroll.getChildCount() > 0) {
            View listRoot = scroll.getChildAt(0);
            if (listRoot instanceof android.view.ViewGroup) {
                UiAnimations.staggerChildrenIn((android.view.ViewGroup) listRoot, 80L);
            }
        }
        UiAnimations.fadeSlideUp(findViewById(R.id.bottomActionLayout), 200);
    }

    private void setupCouponSection() {
        couponInput = findViewById(R.id.couponInput);
        Button applyCouponButton = findViewById(R.id.applyCouponButton);
        appliedCouponCard = findViewById(R.id.appliedCouponCard);
        appliedCouponCode = findViewById(R.id.appliedCouponCode);
        appliedCouponDiscount = findViewById(R.id.appliedCouponDiscount);
        ImageView removeCouponButton = findViewById(R.id.removeCouponButton);

        UiAnimations.pressBounce(applyCouponButton, removeCouponButton);

        if (applyCouponButton != null) {
            applyCouponButton.setOnClickListener(v -> {
                String code = couponInput.getText().toString();
                double subtotal = CartManager.getInstance().getTotalPrice();
                
                CouponManager.CouponResult result = 
                    CouponManager.getInstance().applyCoupon(code, subtotal);
                
                if (result.success) {
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show();
                    updateCouponUI();
                    updateOrderSummary();
                } else {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (removeCouponButton != null) {
            removeCouponButton.setOnClickListener(v -> {
                CouponManager.getInstance().removeCoupon();
                Toast.makeText(this, "Coupon removed", Toast.LENGTH_SHORT).show();
                updateCouponUI();
                updateOrderSummary();
            });
        }

        updateCouponUI();
    }

    private void updateCouponUI() {
        if (CouponManager.getInstance().hasCoupon()) {
            CouponManager.Coupon coupon = CouponManager.getInstance().getAppliedCoupon();
            if (appliedCouponCard != null) {
                appliedCouponCard.setVisibility(View.VISIBLE);
                UiAnimations.fadeSlideDown(appliedCouponCard, 0);
            }
            if (appliedCouponCode != null) {
                appliedCouponCode.setText(coupon.code);
            }
            if (appliedCouponDiscount != null) {
                appliedCouponDiscount.setText(coupon.getDiscountLabel());
            }
            if (couponInput != null) {
                couponInput.setText("");
                couponInput.clearFocus();
            }
        } else {
            if (appliedCouponCard != null) {
                appliedCouponCard.setVisibility(View.GONE);
            }
        }
    }

    private void setupOrderSummary() {
        summarySubtotalText = findViewById(R.id.summarySubtotal);
        summaryDiscountText = findViewById(R.id.summaryDiscount);
        summaryTotalText = findViewById(R.id.summaryTotal);
        discountRow = findViewById(R.id.discountRow);

        updateOrderSummary();
    }

    private void updateOrderSummary() {
        double subtotal = CartManager.getInstance().getTotalPrice();
        double discount = CouponManager.getInstance().calculateDiscount(subtotal);
        double shipping = 15.00;
        double total = subtotal - discount + shipping;

        if (summarySubtotalText != null) {
            summarySubtotalText.setText(String.format(Locale.US, "$%.2f", subtotal));
        }
        
        if (discountRow != null && summaryDiscountText != null) {
            if (discount > 0) {
                discountRow.setVisibility(View.VISIBLE);
                summaryDiscountText.setText(String.format(Locale.US, "-$%.2f", discount));
                UiAnimations.fadeSlideDown(discountRow, 0);
            } else {
                discountRow.setVisibility(View.GONE);
            }
        }

        if (summaryTotalText != null) {
            summaryTotalText.setText(String.format(Locale.US, "$%.2f", total));
        }
    }

    private void setupConfirmButton() {
        Button confirmButton = findViewById(R.id.confirmPaymentButton);
        View processingOverlay = findViewById(R.id.processingOverlay);
        View successView = findViewById(R.id.successView);
        Button backToHomeButton = findViewById(R.id.backToHomeButton);
        Button viewOrdersButton = findViewById(R.id.viewOrdersButton);
        TextView successOrderId = findViewById(R.id.successOrderId);
        TextView successAmountPaid = findViewById(R.id.successAmountPaid);

        UiAnimations.pressBounce(confirmButton, backToHomeButton, viewOrdersButton);

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
                    double discount = CouponManager.getInstance().calculateDiscount(subtotal);
                    double total = subtotal - discount + 15.00; // shipping
                    OrderManager.Order order = OrderManager.getInstance().placeOrder(
                            CartManager.getInstance().getCartItems(), total);

                    if (successOrderId != null) successOrderId.setText(order.orderId);
                    if (successAmountPaid != null) {
                        successAmountPaid.setText(String.format(Locale.US, "$%.2f", total));
                    }

                    if (successView != null) {
                        successView.setVisibility(View.VISIBLE);
                        UiAnimations.popIn(successView, 0);
                    }

                    // Clear cart and coupon after successful "payment"
                    CartManager.getInstance().getCartItems().clear();
                    CouponManager.getInstance().removeCoupon();
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

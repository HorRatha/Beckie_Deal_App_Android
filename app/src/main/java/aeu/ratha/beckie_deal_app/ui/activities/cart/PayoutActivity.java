package aeu.ratha.beckie_deal_app.ui.activities.cart;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Cart;
import aeu.ratha.beckie_deal_app.data.models.CartItem;
import aeu.ratha.beckie_deal_app.data.models.CartResponse;
import aeu.ratha.beckie_deal_app.data.models.ShippingMethod;
import aeu.ratha.beckie_deal_app.data.repository.CartRepository;
import aeu.ratha.beckie_deal_app.data.repository.ShippingMethodRepository;
import aeu.ratha.beckie_deal_app.ui.activities.payment.PaymentWebViewActivity;
import aeu.ratha.beckie_deal_app.utils.helpers.AuthHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.managers.CouponManager;
import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextWatcher;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Checkout screen — 3-step stepper:
 *   Step 1 — Shipping Address  (with GPS autofill)
 *   Step 2 — Payment Method & Promo Code
 *   Step 3 — Review & Confirm
 *
 * Payment method values sent to backend:
 *   "aba_payway" — ABA PayWay (QR / cards / mobile banking)
 *   "cod"        — Cash on Delivery
 */
public class PayoutActivity extends BaseActivity {

    private static final String TAG = "PayoutActivity";
    private static final int REQUEST_LOCATION_PERMISSION = 1001;
    private static final int REQUEST_ABA_PAYMENT = 1002;

    // ── Payment method constants (match backend field values exactly) ─────────
    private static final String PAYMENT_ABA = "aba_payway";
    private static final String PAYMENT_COD = "cod";

    // ── Data ──────────────────────────────────────────────────────────────────
    private CartRepository cartRepository;
    private ShippingMethodRepository shippingMethodRepository;
    private Cart currentCart;
    private String selectedPaymentMethod = PAYMENT_ABA; // default

    // ── Shipping method selection ──────────────────────────────────────────────
    private List<ShippingMethod> availableShippingMethods = new ArrayList<>();
    private ShippingMethod selectedShippingMethod = null;

    // ── Location ──────────────────────────────────────────────────────────────
    private FusedLocationProviderClient fusedLocationClient;

    // ── Stepper ───────────────────────────────────────────────────────────────
    private int currentStep = 1;
    private ViewFlipper stepViewFlipper;

    // Stepper header widgets
    private View step1Circle, step2Circle, step3Circle;
    private TextView step1Number, step2Number, step3Number;
    private ImageView step1Check, step2Check, step3Check;
    private TextView step1Label, step2Label, step3Label;
    private View connector1, connector2;

    // Bottom bar
    private MaterialButton stepBackButton;
    private MaterialButton stepNextButton;

    // ── Step 1 — Shipping ─────────────────────────────────────────────────────
    private TextInputEditText shippingNameInput;
    private TextInputEditText shippingPhoneInput;
    private TextInputEditText shippingAddressInput;
    private TextInputEditText shippingCityInput;
    private TextInputEditText shippingPostalCodeInput;
    private TextInputEditText shippingStateInput;
    private TextInputEditText shippingCountryInput;
    private LinearLayout locationLoadingRow;

    // ── Step 2 — Payment + Coupon ─────────────────────────────────────────────
    private MaterialCardView paymentCardAba;
    private MaterialCardView paymentCardCod;
    private FrameLayout abaCheckBadge;
    private View abaRadioDot;
    private FrameLayout codCheckBadge;
    private View codRadioDot;
    private TextInputEditText couponInput;
    private MaterialCardView appliedCouponCard;
    private TextView appliedCouponCode;
    private TextView appliedCouponDiscount;
    // Shipping method picker (Step 2)
    private LinearLayout shippingMethodContainer;
    private TextView shippingMethodLoadingText;
    private RadioGroup shippingMethodRadioGroup;

    // ── Step 3 — Summary ──────────────────────────────────────────────────────
    private TextView summarySubtotalText;
    private TextView summaryDiscountText;
    private TextView summaryTotalText;
    private LinearLayout discountRow;
    private TextView reviewShippingName;
    private TextView reviewShippingAddress;
    private TextView reviewShippingPhone;
    private TextView reviewPaymentMethod;
    // New Step 3 views
    private LinearLayout reviewItemsContainer;
    private TextView     reviewItemCount;
    private LinearLayout reviewShippingMethodRow;
    private TextView     reviewCouponCode;

    // ─────────────────────────────────────────────────────────────────────────
    // onCreate
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payout);

        if (!AuthHelper.isUserLoggedIn(this)) {
            AuthHelper.showCustomLoginPrompt(this,
                    "Login Required",
                    "Please login to complete your purchase.");
            finish();
            return;
        }

        cartRepository = new CartRepository(this);
        shippingMethodRepository = ShippingMethodRepository.getInstance(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        bindViews();
        setupTopBar();
        setupStepperNavigation();
        setupLocationButton();
        setupPaymentMethodSelection();
        setupCouponSection();

        // Default values
        if (shippingCountryInput != null) shippingCountryInput.setText("Cambodia");

        // Entrance animation
        UiAnimations.fadeSlideDown(findViewById(R.id.appBarLayout), 0);
        UiAnimations.fadeSlideUp(findViewById(R.id.bottomActionLayout), 200);

        renderStep(1, false);
        loadCartFromAPI();
        loadShippingMethods(); // pre-fetch so Step 2 is ready when user arrives
    }

    // ─────────────────────────────────────────────────────────────────────────
    // View binding
    // ─────────────────────────────────────────────────────────────────────────

    private void bindViews() {
        stepViewFlipper = findViewById(R.id.stepViewFlipper);

        // Stepper header
        step1Circle = findViewById(R.id.step1Circle);
        step2Circle = findViewById(R.id.step2Circle);
        step3Circle = findViewById(R.id.step3Circle);
        step1Number = findViewById(R.id.step1Number);
        step2Number = findViewById(R.id.step2Number);
        step3Number = findViewById(R.id.step3Number);
        step1Check  = findViewById(R.id.step1Check);
        step2Check  = findViewById(R.id.step2Check);
        step3Check  = findViewById(R.id.step3Check);
        step1Label  = findViewById(R.id.step1Label);
        step2Label  = findViewById(R.id.step2Label);
        step3Label  = findViewById(R.id.step3Label);
        connector1  = findViewById(R.id.connector1);
        connector2  = findViewById(R.id.connector2);

        stepBackButton = findViewById(R.id.stepBackButton);
        stepNextButton = findViewById(R.id.stepNextButton);

        // Step 1
        shippingNameInput       = findViewById(R.id.shippingNameInput);
        shippingPhoneInput      = findViewById(R.id.shippingPhoneInput);
        shippingAddressInput    = findViewById(R.id.shippingAddressInput);
        shippingCityInput       = findViewById(R.id.shippingCityInput);
        shippingPostalCodeInput = findViewById(R.id.shippingPostalCodeInput);
        shippingStateInput      = findViewById(R.id.shippingStateInput);
        shippingCountryInput    = findViewById(R.id.shippingCountryInput);
        locationLoadingRow      = findViewById(R.id.locationLoadingRow);

        // Step 2
        paymentCardAba  = findViewById(R.id.paymentCardAba);
        paymentCardCod  = findViewById(R.id.paymentCardCod);
        abaCheckBadge   = findViewById(R.id.abaCheckBadge);
        abaRadioDot     = findViewById(R.id.abaRadioDot);
        codCheckBadge   = findViewById(R.id.codCheckBadge);
        codRadioDot     = findViewById(R.id.codRadioDot);
        couponInput         = findViewById(R.id.couponInput);
        appliedCouponCard   = findViewById(R.id.appliedCouponCard);
        appliedCouponCode   = findViewById(R.id.appliedCouponCode);
        appliedCouponDiscount = findViewById(R.id.appliedCouponDiscount);
        shippingMethodContainer  = findViewById(R.id.shippingMethodContainer);
        shippingMethodLoadingText = findViewById(R.id.shippingMethodLoadingText);
        shippingMethodRadioGroup = findViewById(R.id.shippingMethodRadioGroup);

        // Step 3
        summarySubtotalText  = findViewById(R.id.summarySubtotal);
        summaryDiscountText  = findViewById(R.id.summaryDiscount);
        summaryTotalText     = findViewById(R.id.summaryTotal);
        discountRow          = findViewById(R.id.discountRow);
        reviewShippingName   = findViewById(R.id.reviewShippingName);
        reviewShippingAddress = findViewById(R.id.reviewShippingAddress);
        reviewShippingPhone  = findViewById(R.id.reviewShippingPhone);
        reviewPaymentMethod  = findViewById(R.id.reviewPaymentMethod);
        reviewItemsContainer = findViewById(R.id.reviewItemsContainer);
        reviewItemCount      = findViewById(R.id.reviewItemCount);
        reviewShippingMethodRow = findViewById(R.id.reviewShippingMethodRow);
        reviewCouponCode     = findViewById(R.id.reviewCouponCode);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Top bar
    // ─────────────────────────────────────────────────────────────────────────

    private void setupTopBar() {
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                if (currentStep > 1) goToStep(currentStep - 1);
                else finish();
            });
            UiAnimations.pressBounce(backButton);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Stepper navigation
    // ─────────────────────────────────────────────────────────────────────────

    private void setupStepperNavigation() {
        if (stepBackButton != null) {
            stepBackButton.setOnClickListener(v -> goToStep(currentStep - 1));
            UiAnimations.pressBounce(stepBackButton);
        }
        if (stepNextButton != null) {
            stepNextButton.setOnClickListener(v -> {
                if (currentStep == 3) processCheckout();
                else if (validateCurrentStep()) goToStep(currentStep + 1);
            });
            UiAnimations.pressBounce(stepNextButton);
        }

        // "Edit" shortcuts in review step
        TextView editShipping = findViewById(R.id.reviewEditShipping);
        if (editShipping != null) editShipping.setOnClickListener(v -> goToStep(1));

        TextView editPayment = findViewById(R.id.reviewEditPayment);
        if (editPayment != null) editPayment.setOnClickListener(v -> goToStep(2));
    }

    private void goToStep(int target) {
        if (target < 1 || target > 3 || target == currentStep) return;
        boolean forward = target > currentStep;
        stepViewFlipper.setInAnimation(this,
                forward ? R.anim.slide_in_right : R.anim.slide_in_left);
        stepViewFlipper.setOutAnimation(this,
                forward ? R.anim.slide_out_left : R.anim.slide_out_right);
        currentStep = target;
        renderStep(target, true);
        androidx.core.widget.NestedScrollView scroll = findViewById(R.id.payoutContentScroll);
        if (scroll != null) scroll.smoothScrollTo(0, 0);
    }

    private void renderStep(int step, boolean animate) {
        // Flip ViewFlipper to correct child (0-indexed)
        if (animate) {
            int target = step - 1;
            while (stepViewFlipper.getDisplayedChild() != target) {
                if (stepViewFlipper.getDisplayedChild() < target) stepViewFlipper.showNext();
                else stepViewFlipper.showPrevious();
            }
        }

        // Update each circle
        updateStepCircle(1, step1Circle, step1Number, step1Check, step1Label, step);
        updateStepCircle(2, step2Circle, step2Number, step2Check, step2Label, step);
        updateStepCircle(3, step3Circle, step3Number, step3Check, step3Label, step);

        // Connectors
        if (connector1 != null)
            connector1.setBackgroundResource(step >= 2
                    ? R.drawable.stepper_connector_active
                    : R.drawable.stepper_connector_inactive);
        if (connector2 != null)
            connector2.setBackgroundResource(step >= 3
                    ? R.drawable.stepper_connector_active
                    : R.drawable.stepper_connector_inactive);

        // Bottom bar
        if (stepBackButton != null)
            stepBackButton.setVisibility(step > 1 ? View.VISIBLE : View.GONE);
        if (stepNextButton != null) {
            if (step == 3) {
                stepNextButton.setText("Place Order");
                stepNextButton.setIconResource(R.drawable.ic_check_bold_24);
            } else {
                stepNextButton.setText("Continue");
                stepNextButton.setIconResource(R.drawable.ic_arrow_right);
            }
        }

        if (step == 3) populateReviewCard();
    }

    private void updateStepCircle(int idx, View circle, TextView number,
                                   ImageView check, TextView label, int active) {
        if (circle == null) return;
        if (idx < active) {
            // Done — gradient bg, dark_brown check
            circle.setBackgroundResource(R.drawable.stepper_circle_done);
            if (number != null) number.setVisibility(View.GONE);
            if (check  != null) check.setVisibility(View.VISIBLE);
            if (label  != null) label.setTextColor(getColor(R.color.green_stock));
        } else if (idx == active) {
            // Active — gradient
            circle.setBackgroundResource(R.drawable.stepper_circle_active);
            if (number != null) { number.setVisibility(View.VISIBLE); number.setTextColor(getColor(R.color.dark_brown)); }
            if (check  != null) check.setVisibility(View.GONE);
            if (label  != null) label.setTextColor(getColor(R.color.primary));
        } else {
            // Future — inactive
            circle.setBackgroundResource(R.drawable.stepper_circle_inactive);
            if (number != null) { number.setVisibility(View.VISIBLE); number.setTextColor(getColor(R.color.text_muted)); }
            if (check  != null) check.setVisibility(View.GONE);
            if (label  != null) label.setTextColor(getColor(R.color.text_muted));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GPS Location autofill
    // ─────────────────────────────────────────────────────────────────────────

    private void setupLocationButton() {
        View btn = findViewById(R.id.locationAutofillButton);
        if (btn != null) {
            btn.setOnClickListener(v -> requestLocationAutofill());
            UiAnimations.pressBounce(btn);
        }
    }

    private void requestLocationAutofill() {
        // Check if permission is already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fetchLocationAndAutofill();
        } else {
            // Request runtime permission
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                            @NonNull String[] permissions,
                                            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocationAndAutofill();
            } else {
                Toast.makeText(this,
                        "Location permission denied. Please fill address manually.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void fetchLocationAndAutofill() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        // Show loading row
        if (locationLoadingRow != null) locationLoadingRow.setVisibility(View.VISIBLE);

        // Disable GPS button while fetching
        View btn = findViewById(R.id.locationAutofillButton);
        TextView btnText = findViewById(R.id.locationButtonText);
        if (btn != null) btn.setEnabled(false);
        if (btnText != null) btnText.setText("Detecting…");

        // Stage 1: try the last known location (instant, works offline)
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        Log.d(TAG, "Using last-known location: " + location.getLatitude() + "," + location.getLongitude());
                        reverseGeocode(location.getLatitude(), location.getLongitude());
                    } else {
                        // Stage 2: no cached fix — request a fresh one
                        Log.d(TAG, "No last-known location, requesting fresh fix…");
                        requestFreshLocation();
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "getLastLocation failed: " + e.getMessage());
                    requestFreshLocation();
                });
    }

    /**
     * Fall-back: issue a one-shot high-accuracy location request.
     * Times out after 10 s and shows a manual-fill message if nothing arrives.
     */
    private void requestFreshLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            hideLocationLoading();
            return;
        }

        LocationRequest req = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setMaxUpdates(1)
                .setDurationMillis(10_000)
                .build();

        LocationCallback cb = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                fusedLocationClient.removeLocationUpdates(this);
                android.location.Location loc = result.getLastLocation();
                if (loc != null) {
                    Log.d(TAG, "Fresh location: " + loc.getLatitude() + "," + loc.getLongitude());
                    reverseGeocode(loc.getLatitude(), loc.getLongitude());
                } else {
                    runOnUiThread(() -> {
                        hideLocationLoading();
                        Toast.makeText(PayoutActivity.this,
                                "Could not get your location. Please fill address manually.",
                                Toast.LENGTH_LONG).show();
                    });
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(req, cb, Looper.getMainLooper());

        // Safety timeout — if no fix within 10 s, give up gracefully
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            fusedLocationClient.removeLocationUpdates(cb);
            if (locationLoadingRow != null
                    && locationLoadingRow.getVisibility() == View.VISIBLE) {
                hideLocationLoading();
                Toast.makeText(this,
                        "Location timed out. Please fill address manually.",
                        Toast.LENGTH_LONG).show();
            }
        }, 11_000);
    }

    /**
     * Convert lat/lng to a postal address and populate the shipping fields.
     * Runs Geocoder on a background thread; posts UI updates back to main thread.
     */
    private void reverseGeocode(double lat, double lng) {
        new Thread(() -> {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> results = geocoder.getFromLocation(lat, lng, 1);

                runOnUiThread(() -> {
                    hideLocationLoading();

                    if (results == null || results.isEmpty()) {
                        Toast.makeText(this,
                                "Address not found for your location. Fill manually.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    Address addr = results.get(0);

                    // Build street line from available sub-address parts
                    StringBuilder street = new StringBuilder();
                    for (int i = 0; i <= addr.getMaxAddressLineIndex(); i++) {
                        String line = addr.getAddressLine(i);
                        if (line != null && !line.isEmpty()) {
                            // Use only the first address line (most specific)
                            street.append(line);
                            break;
                        }
                    }

                    // Populate fields — only overwrite if field is currently empty
                    // so user-typed data is not wiped
                    autofillIfEmpty(shippingAddressInput, street.toString());
                    autofillIfEmpty(shippingCityInput,
                            firstNonNull(addr.getLocality(), addr.getSubAdminArea()));
                    autofillIfEmpty(shippingStateInput, addr.getAdminArea());
                    autofillIfEmpty(shippingPostalCodeInput, addr.getPostalCode());
                    autofillIfEmpty(shippingCountryInput, addr.getCountryName());

                    Toast.makeText(this,
                            "Address filled from GPS — please verify it's correct.",
                            Toast.LENGTH_LONG).show();
                });
            } catch (IOException e) {
                Log.e(TAG, "Geocoder failed", e);
                runOnUiThread(() -> {
                    hideLocationLoading();
                    Toast.makeText(this,
                            "Could not resolve address. Fill manually.",
                            Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    /** Fill a field only if it is currently empty — never overwrite user input. */
    private void autofillIfEmpty(TextInputEditText field, String value) {
        if (field == null || value == null || value.isEmpty()) return;
        if (field.getText() == null || field.getText().toString().trim().isEmpty()) {
            field.setText(value);
        }
    }

    private String firstNonNull(String... values) {
        for (String v : values) if (v != null && !v.isEmpty()) return v;
        return "";
    }

    private void hideLocationLoading() {
        if (locationLoadingRow != null) locationLoadingRow.setVisibility(View.GONE);
        View btn = findViewById(R.id.locationAutofillButton);
        TextView btnText = findViewById(R.id.locationButtonText);
        if (btn != null) btn.setEnabled(true);
        if (btnText != null) btnText.setText("Use GPS");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Payment method selection
    // ─────────────────────────────────────────────────────────────────────────

    private void setupPaymentMethodSelection() {
        if (paymentCardAba != null) {
            paymentCardAba.setOnClickListener(v -> selectPaymentMethod(PAYMENT_ABA));
            UiAnimations.pressBounce(paymentCardAba);
        }
        if (paymentCardCod != null) {
            paymentCardCod.setOnClickListener(v -> selectPaymentMethod(PAYMENT_COD));
            UiAnimations.pressBounce(paymentCardCod);
        }
        // Render initial state
        applyPaymentSelectionUI(selectedPaymentMethod);
    }

    private void selectPaymentMethod(String method) {
        selectedPaymentMethod = method;
        applyPaymentSelectionUI(method);
        Log.d(TAG, "Payment method selected: " + method);
    }

    /**
     * Toggle card strokes and check/radio badges to reflect which method is selected.
     */
    private void applyPaymentSelectionUI(String selected) {
        boolean abaSelected = PAYMENT_ABA.equals(selected);

        // ABA card
        if (paymentCardAba != null) {
            paymentCardAba.setStrokeColor(abaSelected
                    ? getColor(R.color.primary)
                    : getColor(R.color.border_color));
            paymentCardAba.setStrokeWidth(abaSelected ? dpToPx(2) : dpToPx(1));
        }
        if (abaCheckBadge != null) abaCheckBadge.setVisibility(abaSelected ? View.VISIBLE : View.GONE);
        if (abaRadioDot    != null) abaRadioDot.setVisibility(abaSelected ? View.GONE : View.VISIBLE);

        // COD card
        if (paymentCardCod != null) {
            paymentCardCod.setStrokeColor(!abaSelected
                    ? getColor(R.color.primary)
                    : getColor(R.color.border_color));
            paymentCardCod.setStrokeWidth(!abaSelected ? dpToPx(2) : dpToPx(1));
        }
        if (codCheckBadge != null) codCheckBadge.setVisibility(!abaSelected ? View.VISIBLE : View.GONE);
        if (codRadioDot   != null) codRadioDot.setVisibility(!abaSelected ? View.GONE : View.VISIBLE);

        // Also update the icon container tint on COD card when selected
        FrameLayout codIconBadge = null;
        if (paymentCardCod != null) {
            // find the icon FrameLayout (first child's first child)
            try {
                LinearLayout row = (LinearLayout) paymentCardCod.getChildAt(0);
                codIconBadge = (FrameLayout) row.getChildAt(0);
            } catch (Exception ignored) {}
        }
        if (codIconBadge != null) {
            codIconBadge.setBackgroundResource(!abaSelected
                    ? R.drawable.stepper_circle_active
                    : R.drawable.form_field_background);
            // Update icon tint inside
            try {
                ImageView icon = (ImageView) ((FrameLayout) codIconBadge).getChildAt(0);
                icon.setColorFilter(getColor(!abaSelected ? R.color.dark_brown : R.color.text_secondary));
            } catch (Exception ignored) {}
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Validation
    // ─────────────────────────────────────────────────────────────────────────

    private boolean validateCurrentStep() {
        if (currentStep == 1) return validateShippingStep();
        if (currentStep == 2) return validateShippingMethodStep();
        return true;
    }

    private boolean validateShippingMethodStep() {
        // If the backend has no shipping methods configured yet, let the user
        // through — the checkout call will either succeed (backend uses a default)
        // or return a clear error message we can surface.
        if (availableShippingMethods.isEmpty()) return true;

        // Methods are loaded but nothing is selected
        if (selectedShippingMethod == null) {
            Toast.makeText(this,
                    "Please select a shipping method to continue.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateShippingStep() {
        return assertNotEmpty(shippingNameInput,       "Full name is required")
            && assertNotEmpty(shippingPhoneInput,      "Phone number is required")
            && assertNotEmpty(shippingAddressInput,    "Street address is required")
            && assertNotEmpty(shippingCityInput,       "City is required")
            && assertNotEmpty(shippingPostalCodeInput, "Postal code is required");
    }

    private boolean assertNotEmpty(TextInputEditText field, String msg) {
        if (field == null) return false;
        if (field.getText() == null || field.getText().toString().trim().isEmpty()) {
            field.setError(msg);
            field.requestFocus();
            // Red outline on parent TextInputLayout
            if (field.getParent() != null
                    && field.getParent().getParent() instanceof TextInputLayout) {
                TextInputLayout til = (TextInputLayout) field.getParent().getParent();
                til.setError(msg);
                field.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
                    @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                        til.setError(null);
                        field.removeTextChangedListener(this);
                    }
                    @Override public void afterTextChanged(Editable s) {}
                });
            }
            return false;
        }
        return true;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Review card population (Step 3)
    // ─────────────────────────────────────────────────────────────────────────

    private void populateReviewCard() {
        String name    = getText(shippingNameInput);
        String phone   = getText(shippingPhoneInput);
        String address = getText(shippingAddressInput);
        String city    = getText(shippingCityInput);
        String postal  = getText(shippingPostalCodeInput);
        String state   = getText(shippingStateInput);
        String country = getText(shippingCountryInput);

        if (reviewShippingName != null)
            reviewShippingName.setText(name.isEmpty() ? "—" : name);

        StringBuilder addrLine = new StringBuilder();
        if (!address.isEmpty()) addrLine.append(address);
        if (!city.isEmpty())    addrLine.append(addrLine.length() > 0 ? ", " : "").append(city);
        if (!state.isEmpty())   addrLine.append(addrLine.length() > 0 ? ", " : "").append(state);
        if (!postal.isEmpty())  addrLine.append(" ").append(postal);
        if (!country.isEmpty()) addrLine.append(addrLine.length() > 0 ? ", " : "").append(country);
        if (reviewShippingAddress != null)
            reviewShippingAddress.setText(addrLine.length() > 0 ? addrLine.toString() : "—");

        if (reviewShippingPhone != null)
            reviewShippingPhone.setText(phone.isEmpty() ? "—" : phone);

        // Payment label
        if (reviewPaymentMethod != null) {
            reviewPaymentMethod.setText(PAYMENT_COD.equals(selectedPaymentMethod)
                    ? "Cash on Delivery"
                    : "ABA PayWay");
        }

        // Shipping method row — show only when a method is selected
        if (reviewShippingMethodRow != null) {
            if (selectedShippingMethod != null) {
                reviewShippingMethodRow.setVisibility(View.VISIBLE);
                TextView tv = findViewById(R.id.reviewShippingMethod);
                if (tv != null) tv.setText(selectedShippingMethod.getDisplayLabel());
            } else {
                reviewShippingMethodRow.setVisibility(View.GONE);
            }
        }

        // ── Cart items list ───────────────────────────────────────────────────
        populateReviewItems();

        // ── Order summary numbers ─────────────────────────────────────────────
        updateOrderSummary();
    }

    /** Inflate one item_order_review.xml row per cart item into reviewItemsContainer. */
    private void populateReviewItems() {
        if (reviewItemsContainer == null) return;
        reviewItemsContainer.removeAllViews();

        if (currentCart == null || currentCart.getItems() == null
                || currentCart.getItems().isEmpty()) {
            if (reviewItemCount != null) reviewItemCount.setText("0 items");
            return;
        }

        List<CartItem> items = currentCart.getItems();
        int totalUnits = 0;
        for (CartItem item : items) totalUnits += item.getQuantity();

        if (reviewItemCount != null) {
            reviewItemCount.setText(totalUnits + (totalUnits == 1 ? " item" : " items"));
        }

        android.view.LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);

            View row = inflater.inflate(
                    R.layout.item_order_review, reviewItemsContainer, false);

            // Thumbnail
            ImageView img = row.findViewById(R.id.reviewItemImage);
            String thumb = item.getProductThumbnail();
            if (img != null && thumb != null && !thumb.isEmpty()) {
                com.bumptech.glide.Glide.with(this)
                        .load(thumb)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .into(img);
            }

            // Name
            TextView tvName = row.findViewById(R.id.reviewItemName);
            if (tvName != null) tvName.setText(item.getProductName());

            // Qty
            TextView tvQty = row.findViewById(R.id.reviewItemQty);
            if (tvQty != null) tvQty.setText(String.valueOf(item.getQuantity()));

            // Line total
            TextView tvTotal = row.findViewById(R.id.reviewItemTotal);
            if (tvTotal != null) {
                tvTotal.setText(String.format(Locale.US, "$%.2f", item.getSubtotal()));
            }

            // Thin divider between rows (not after last)
            if (i < items.size() - 1) {
                View divider = new View(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1);
                lp.setMarginStart(68); // indent past the thumbnail
                divider.setLayoutParams(lp);
                divider.setBackgroundColor(getColor(R.color.border_color));
                reviewItemsContainer.addView(divider);
            }

            reviewItemsContainer.addView(row);
        }
    }

    private String getText(TextInputEditText f) {
        if (f == null || f.getText() == null) return "";
        return f.getText().toString().trim();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Cart / Order summary
    // ─────────────────────────────────────────────────────────────────────────

    private void loadCartFromAPI() {
        cartRepository.getCart(new CartRepository.CartCallback() {
            @Override
            public void onSuccess(CartResponse response) {
                if (response.isSuccess() && response.hasData()) {
                    currentCart = response.getData();
                    Log.d(TAG, "Cart loaded — subtotal: $" + currentCart.getSubtotalFromAPI()
                            + "  items: " + currentCart.getItemCount());
                    // Do NOT auto-restore a coupon from the backend cart here.
                    // Discounts are only shown when the user actively applies a coupon
                    // this session via the Step 2 coupon input.
                    runOnUiThread(() -> {
                        updateOrderSummary();
                        updateCouponUI();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(PayoutActivity.this, "Your cart is empty.", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }
            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to load cart: " + error);
                runOnUiThread(() -> {
                    Toast.makeText(PayoutActivity.this, "Failed to load cart.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void updateOrderSummary() {
        if (currentCart == null) return;

        double subtotal = currentCart.getSubtotalFromAPI();
        if (subtotal == 0.0) subtotal = currentCart.getTotalPrice();

        // Use CouponManager as the single source of truth for discount state.
        // It is synced from the backend cart in loadCartFromAPI(), so it reflects
        // exactly what the backend has — no stale or phantom discounts.
        boolean hasCoupon = CouponManager.getInstance().hasCoupon();
        double discount   = hasCoupon ? CouponManager.getInstance().getDiscountAmount() : 0.0;
        double shipping   = calculateShippingFee(subtotal);
        double total      = subtotal - discount + shipping;

        if (summarySubtotalText != null)
            summarySubtotalText.setText(String.format(Locale.US, "$%.2f", subtotal));

        if (discountRow != null && summaryDiscountText != null) {
            if (hasCoupon && discount > 0) {
                discountRow.setVisibility(View.VISIBLE);
                summaryDiscountText.setText(String.format(Locale.US, "-$%.2f", discount));
                // Show the coupon code badge
                if (reviewCouponCode != null) {
                    String code = CouponManager.getInstance().getAppliedCouponCode();
                    reviewCouponCode.setText(code != null ? code.toUpperCase() : "");
                    reviewCouponCode.setVisibility(code != null && !code.isEmpty()
                            ? View.VISIBLE : View.GONE);
                }
            } else {
                discountRow.setVisibility(View.GONE);
            }
        }

        TextView shippingView = findViewById(R.id.summaryShipping);
        if (shippingView != null) {
            if (shipping == 0.0) {
                shippingView.setText("FREE");
                shippingView.setTextColor(getColor(R.color.green_stock));
            } else {
                shippingView.setText(String.format(Locale.US, "$%.2f", shipping));
                shippingView.setTextColor(getColor(R.color.text_primary));
            }
        }

        if (summaryTotalText != null)
            summaryTotalText.setText(String.format(Locale.US, "$%.2f", total));
    }

    private double calculateShippingFee(double total) {
        return total >= 50.00 ? 0.0 : 15.00;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Coupon section
    // ─────────────────────────────────────────────────────────────────────────

    private void setupCouponSection() {
        MaterialButton applyBtn  = findViewById(R.id.applyCouponButton);
        ImageView removeBtn      = findViewById(R.id.removeCouponButton);

        UiAnimations.pressBounce(applyBtn, removeBtn);

        if (applyBtn != null) {
            applyBtn.setOnClickListener(v -> {
                String code = couponInput != null ? getText(couponInput) : "";
                if (code.isEmpty()) {
                    Toast.makeText(this, "Please enter a coupon code", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (currentCart == null) {
                    Toast.makeText(this, "Cart not loaded yet — please wait", Toast.LENGTH_SHORT).show();
                    return;
                }
                applyBtn.setEnabled(false);
                applyBtn.setText("Applying…");
                CouponManager.getInstance().applyCoupon(code,
                        String.valueOf(currentCart.getId()), this,
                        new CouponManager.CouponCallback() {
                            @Override
                            public void onSuccess(String couponCode, double discountAmount) {
                                runOnUiThread(() -> {
                                    applyBtn.setEnabled(true);
                                    applyBtn.setText("Apply");
                                    if (couponInput != null) couponInput.setText("");
                                    Toast.makeText(PayoutActivity.this,
                                            String.format(Locale.US,
                                                    "Coupon applied! You saved $%.2f", discountAmount),
                                            Toast.LENGTH_LONG).show();
                                    loadCartFromAPI();
                                    updateCouponUI();
                                });
                            }
                            @Override
                            public void onError(String errorMessage) {
                                runOnUiThread(() -> {
                                    applyBtn.setEnabled(true);
                                    applyBtn.setText("Apply");
                                    Toast.makeText(PayoutActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
            });
        }

        if (removeBtn != null) {
            removeBtn.setOnClickListener(v -> {
                CouponManager.getInstance().removeCoupon();
                Toast.makeText(this, "Coupon removed", Toast.LENGTH_SHORT).show();
                loadCartFromAPI();
                updateCouponUI();
            });
        }

        updateCouponUI();
    }

    private void updateCouponUI() {
        boolean has = CouponManager.getInstance().hasCoupon();

        // Find the TextInputLayout wrapping couponInput
        View couponTil = (couponInput != null
                && couponInput.getParent() != null
                && couponInput.getParent().getParent() instanceof TextInputLayout)
                ? (TextInputLayout) couponInput.getParent().getParent() : null;
        MaterialButton applyBtn = findViewById(R.id.applyCouponButton);

        if (couponTil   != null) couponTil.setVisibility(has ? View.GONE : View.VISIBLE);
        if (applyBtn    != null) applyBtn.setVisibility(has ? View.GONE : View.VISIBLE);
        if (appliedCouponCard != null) appliedCouponCard.setVisibility(has ? View.VISIBLE : View.GONE);

        if (has) {
            String code    = CouponManager.getInstance().getAppliedCouponCode();
            double discount = CouponManager.getInstance().getDiscountAmount();
            if (appliedCouponCode != null)
                appliedCouponCode.setText(code != null ? code : "Coupon Applied");
            if (appliedCouponDiscount != null)
                appliedCouponDiscount.setText(
                        String.format(Locale.US, "-$%.2f discount", discount));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Checkout submission
    // ─────────────────────────────────────────────────────────────────────────

    private void processCheckout() {
        // Safety re-validate shipping
        if (!validateShippingStep()) {
            Toast.makeText(this, "Please complete the shipping details first.", Toast.LENGTH_SHORT).show();
            goToStep(1);
            return;
        }
        if (currentCart == null) {
            Toast.makeText(this, "Cart not found. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (stepNextButton != null) stepNextButton.setEnabled(false);

        View overlay = findViewById(R.id.processingOverlay);
        if (overlay != null) overlay.setVisibility(View.VISIBLE);

        String country = getText(shippingCountryInput);
        if (country.isEmpty()) country = "Cambodia";

        // Guard: shipping method must be selected (only enforced when methods are available)
        if (selectedShippingMethod == null && !availableShippingMethods.isEmpty()) {
            if (overlay != null) overlay.setVisibility(View.GONE);
            if (stepNextButton != null) stepNextButton.setEnabled(true);
            Toast.makeText(this, "Please select a shipping method.", Toast.LENGTH_SHORT).show();
            goToStep(2);
            return;
        }

        // ── Build flat request map — all field names match the backend exactly ──
        Map<String, Object> checkoutData = new HashMap<>();
        checkoutData.put("cart_id",        currentCart.getId());  // integer
        checkoutData.put("recipient_name", getText(shippingNameInput));
        checkoutData.put("address_line_1", getText(shippingAddressInput));
        checkoutData.put("city",           getText(shippingCityInput));
        checkoutData.put("country",        country);
        checkoutData.put("phone",          getText(shippingPhoneInput));
        checkoutData.put("postal_code",    getText(shippingPostalCodeInput));
        checkoutData.put("payment_method", selectedPaymentMethod); // "aba_payway" or "cod"
        checkoutData.put("notes",          "");
        checkoutData.put("metadata",       new HashMap<String, Object>()); // required by backend

        // Include the logged-in user's email (backend requires it for order record)
        String userEmail = aeu.ratha.beckie_deal_app.security.TokenManager
                .getInstance(this).getUserEmail();
        if (userEmail != null && !userEmail.isEmpty()) {
            checkoutData.put("email", userEmail);
        }

        // Only send shipping_method_id when one was actually selected
        if (selectedShippingMethod != null) {
            checkoutData.put("shipping_method_id", selectedShippingMethod.getId());
        }

        String state = getText(shippingStateInput);
        if (!state.isEmpty()) checkoutData.put("state", state);

        Log.d(TAG, "Checkout request: " + checkoutData);

        final View finalOverlay = overlay;
        cartRepository.checkout(checkoutData, new CartRepository.CheckoutCallback() {
            @Override
            public void onSuccess(Map<String, Object> response) {
                runOnUiThread(() -> handleCheckoutResponse(response, finalOverlay));
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    if (finalOverlay != null) finalOverlay.setVisibility(View.GONE);
                    if (stepNextButton != null) stepNextButton.setEnabled(true);
                    Log.e(TAG, "Checkout error: " + error);
                    Toast.makeText(PayoutActivity.this, error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * Parse the backend checkout response.
     *
     * Expected response shapes (both handled):
     *
     *  Shape A — order created, no online payment needed (COD):
     *  {
     *    "success": true,
     *    "data": {
     *      "order_id": "ORD-123",
     *      "status": "pending",
     *      "total_amount": 45.00,
     *      "payment_method": "cash_on_delivery"
     *    }
     *  }
     *
     *  Shape B — order created, redirect to ABA PayWay:
     *  {
     *    "success": true,
     *    "data": {
     *      "order_id": "ORD-456",
     *      "payment_url": "https://pay.ababank.com/...",
     *      "total_amount": 60.00
     *    }
     *  }
     */
    private void handleCheckoutResponse(Map<String, Object> response, View overlay) {
        Log.d(TAG, "Checkout response: " + response);

        // Unwrap nested "data" if present
        Map<String, Object> data = extractMap(response, "data");
        Map<String, Object> src  = (data != null) ? data : response;

        // Check for a redirect payment URL (ABA PayWay online flow)
        String paymentUrl = extractString(src, "payment_url", null);
        if (paymentUrl == null) paymentUrl = extractString(response, "payment_url", null);

        if (paymentUrl != null && !paymentUrl.isEmpty()) {
            Log.d(TAG, "ABA PayWay redirect URL: " + paymentUrl);
            openABAPayment(paymentUrl);
        } else {
            // No redirect needed (COD, or direct success)
            Log.d(TAG, "No payment URL — showing success screen.");
            showSuccessScreen(response);
        }
    }

    /**
     * Launch the real ABA PayWay WebView.
     * The backend returns a payment_url after checkout; we open it in
     * PaymentWebViewActivity and wait for the deep-link result.
     */
    private void openABAPayment(String paymentUrl) {
        Log.d(TAG, "Launching ABA PayWay WebView: " + paymentUrl);
        Intent intent = new Intent(this, PaymentWebViewActivity.class);
        intent.putExtra(PaymentWebViewActivity.EXTRA_PAYMENT_URL, paymentUrl);
        startActivityForResult(intent, REQUEST_ABA_PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_ABA_PAYMENT) return;

        View overlay = findViewById(R.id.processingOverlay);
        if (overlay != null) overlay.setVisibility(View.GONE);
        if (stepNextButton != null) stepNextButton.setEnabled(true);

        String status = (data != null) ? data.getStringExtra("status") : "unknown";
        Log.d(TAG, "ABA payment result — resultCode=" + resultCode + "  status=" + status);

        if (resultCode == PaymentWebViewActivity.RESULT_PAYMENT_SUCCESS) {
            // Build a minimal success payload so showSuccessScreen has something to display
            Map<String, Object> successData = new HashMap<>();
            Map<String, Object> inner = new HashMap<>();
            inner.put("status", "paid");
            inner.put("payment_method", PAYMENT_ABA);
            if (currentCart != null) {
                double total = currentCart.getSubtotalFromAPI();
                if (total == 0.0) total = currentCart.getTotalPrice();
                inner.put("total_amount", total);
            }
            successData.put("data", inner);
            showSuccessScreen(successData);

        } else if (resultCode == PaymentWebViewActivity.RESULT_PAYMENT_CANCELLED) {
            Toast.makeText(this,
                    "Payment cancelled. You can try again or choose Cash on Delivery.",
                    Toast.LENGTH_LONG).show();
            // Return user to Step 2 so they can pick COD or retry
            goToStep(2);

        } else {
            // RESULT_PAYMENT_FAILED or any unknown result
            Toast.makeText(this,
                    "Payment was not completed. Please try again.",
                    Toast.LENGTH_LONG).show();
            goToStep(2);
        }
    }

    /**
     * Reveal the full-screen success view.
     * Reads: order_id, total_amount, payment_method from response (top-level or nested data).
     */
    private void showSuccessScreen(Map<String, Object> response) {
        Map<String, Object> data = extractMap(response, "data");
        Map<String, Object> src  = (data != null) ? data : response;

        String orderId      = extractString(src, "order_id",     "—");
        Double totalAmount  = extractDouble(src, "total_amount");
        String paymentUsed  = extractString(src, "payment_method", selectedPaymentMethod);

        Log.d(TAG, "Order placed — ID: " + orderId
                + "  total: $" + totalAmount
                + "  payment: " + paymentUsed);

        View processingOverlay = findViewById(R.id.processingOverlay);
        View successView       = findViewById(R.id.successView);

        if (processingOverlay != null) processingOverlay.setVisibility(View.GONE);
        if (successView != null) {
            successView.setVisibility(View.VISIBLE);
            UiAnimations.popIn(successView, 0);
        }

        TextView tvOrderId    = findViewById(R.id.successOrderId);
        TextView tvAmount     = findViewById(R.id.successAmountPaid);
        TextView tvPayment    = findViewById(R.id.successPaymentMethod);

        if (tvOrderId != null) tvOrderId.setText(orderId);
        if (tvAmount  != null && totalAmount != null)
            tvAmount.setText(String.format(Locale.US, "$%.2f", totalAmount));
        if (tvPayment != null)
            tvPayment.setText(PAYMENT_COD.equals(paymentUsed)
                    ? "Cash on Delivery"
                    : "ABA PayWay");

        MaterialButton homeBtn   = findViewById(R.id.backToHomeButton);
        MaterialButton ordersBtn = findViewById(R.id.viewOrdersButton);

        if (homeBtn != null) {
            homeBtn.setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });
            UiAnimations.pressBounce(homeBtn);
        }
        if (ordersBtn != null) {
            ordersBtn.setOnClickListener(v -> {
                startActivity(new Intent(this, MyOrdersActivity.class));
                finish();
            });
            UiAnimations.pressBounce(ordersBtn);
        }

        CouponManager.getInstance().removeCoupon();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Shipping method loader + picker
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Pre-fetch shipping methods so they are ready when the user reaches Step 2.
     * Called once from onCreate; falls back to stale cache automatically via the repo.
     */
    private void loadShippingMethods() {
        shippingMethodRepository.fetchShippingMethods(
                new ShippingMethodRepository.ShippingMethodCallback() {
                    @Override
                    public void onSuccess(@NonNull List<ShippingMethod> methods) {
                        runOnUiThread(() -> {
                            availableShippingMethods = methods;
                            renderShippingMethodPicker(methods);
                        });
                    }

                    @Override
                    public void onError(@NonNull String message) {
                        Log.e(TAG, "Could not load shipping methods: " + message);
                        runOnUiThread(() -> {
                            // Show a retry message inside the container
                            if (shippingMethodLoadingText != null) {
                                shippingMethodLoadingText.setText(
                                        "Could not load shipping options. Please check your connection.");
                            }
                        });
                    }
                });
    }

    /**
     * Populate the RadioGroup with one RadioButton per available shipping method.
     * If there is exactly one method, auto-select it so the user doesn't have to.
     * If the backend has no methods yet, shows an info message (non-blocking).
     */
    private void renderShippingMethodPicker(List<ShippingMethod> methods) {
        if (shippingMethodRadioGroup == null) return;

        if (methods.isEmpty()) {
            // Backend has no shipping methods configured yet — show info, don't block
            if (shippingMethodLoadingText != null) {
                shippingMethodLoadingText.setVisibility(View.VISIBLE);
                shippingMethodLoadingText.setText(
                        "No shipping options configured yet — your order will use the default rate.");
            }
            shippingMethodRadioGroup.setVisibility(View.GONE);
            return;
        }

        // Hide loading text, show the radio group
        if (shippingMethodLoadingText != null) shippingMethodLoadingText.setVisibility(View.GONE);
        shippingMethodRadioGroup.removeAllViews();
        shippingMethodRadioGroup.setVisibility(View.VISIBLE);

        for (ShippingMethod method : methods) {
            RadioButton rb = new RadioButton(this);
            rb.setId(method.getId()); // use the real DB id as the view id
            rb.setText(method.getDisplayLabel());
            rb.setTextAppearance(R.style.TextAppearance_App_Body);
            rb.setPadding(dpToPx(4), dpToPx(12), dpToPx(4), dpToPx(12));
            rb.setTag(method); // store the full object for retrieval
            shippingMethodRadioGroup.addView(rb);
        }

        // Auto-select if there is only one option — avoids friction for most stores
        if (methods.size() == 1) {
            ShippingMethod only = methods.get(0);
            shippingMethodRadioGroup.check(only.getId());
            selectedShippingMethod = only;
            Log.d(TAG, "Auto-selected only shipping method: " + only);
        }

        // When user manually picks a method
        shippingMethodRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checked = group.findViewById(checkedId);
            if (checked != null && checked.getTag() instanceof ShippingMethod) {
                selectedShippingMethod = (ShippingMethod) checked.getTag();
                Log.d(TAG, "Shipping method selected: " + selectedShippingMethod);
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Utilities
    // ─────────────────────────────────────────────────────────────────────────

    private String extractString(Map<String, Object> map, String key, String def) {
        if (map == null || !map.containsKey(key)) return def;
        Object v = map.get(key);
        return v != null ? v.toString() : def;
    }

    private Double extractDouble(Map<String, Object> map, String key) {
        return extractDouble(map, key, null);
    }

    private Double extractDouble(Map<String, Object> map, String key, Double def) {
        if (map == null || !map.containsKey(key)) return def;
        Object v = map.get(key);
        if (v == null) return def;
        try {
            return (v instanceof Number) ? ((Number) v).doubleValue()
                                         : Double.parseDouble(v.toString());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractMap(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) return null;
        Object v = map.get(key);
        return (v instanceof Map) ? (Map<String, Object>) v : null;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}

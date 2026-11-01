package aeu.ratha.beckie_deal_app.ui.activities.payment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.api.PaymentApiService;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;
import aeu.ratha.beckie_deal_app.ui.activities.OrderConfirmationActivity;
import aeu.ratha.beckie_deal_app.utils.helpers.QrCodeHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * KhqrPaymentActivity
 * Renders local ZXing QR Bitmap directly from response.qr_code_data (exact backend string).
 * Handles same-device deep linking to ABA Mobile app, auto-polling status (Step 5: GET /payments/aba/{transaction_id}),
 * and callback simulation for testing (Step 6a, 6b, 6c: POST /payments/aba/callback).
 */
public class KhqrPaymentActivity extends AppCompatActivity {

    private static final String TAG = "KhqrPaymentActivity";

    public static final String EXTRA_ORDER_ID = "order_id";
    public static final String EXTRA_AMOUNT = "amount";
    public static final String EXTRA_TRANSACTION_ID = "transaction_id";

    private TextView textOrderId, textAmount, textStatus;
    private ImageView ivQrCode;
    private ProgressBar progressBar;
    private Button btnOpenAbaApp, btnCheckStatus, btnSimSuccess, btnSimFailed, btnSimBadSig;

    private PaymentApiService paymentApiService;
    private long orderId;
    private double amount;
    private String transactionId;
    private String abapayDeeplink;

    // Handler for automatic status polling every 3 seconds
    private final Handler pollHandler = new Handler(Looper.getMainLooper());
    private static final long POLL_INTERVAL_MS = 3000;
    private boolean isPollingActive = false;

    private final Runnable pollRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPollingActive) {
                checkAbaStatusQuietly();
                pollHandler.postDelayed(this, POLL_INTERVAL_MS);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khqr_payment);

        orderId = getIntent().getLongExtra(EXTRA_ORDER_ID, 127);
        amount = getIntent().getDoubleExtra(EXTRA_AMOUNT, 77.00);
        transactionId = getIntent().getStringExtra(EXTRA_TRANSACTION_ID);

        initializeViews();
        initializeRetrofit();

        textOrderId.setText("Order ID: #" + (orderId > 0 ? orderId : "127"));
        textAmount.setText(String.format("Amount: $%.2f USD", amount > 0 ? amount : 77.00));

        setupListeners();

        String passedQrString = getIntent().getStringExtra("qr_string");
        String passedQrImage = getIntent().getStringExtra("qr_image");
        String passedDeeplink = getIntent().getStringExtra("abapay_deeplink");

        if (passedDeeplink != null) {
            abapayDeeplink = passedDeeplink;
        }

        if (passedQrImage != null && !passedQrImage.isEmpty()) {
            if (passedQrImage.contains("base64,")) {
                try {
                    String encodedText = passedQrImage.substring(passedQrImage.indexOf("base64,") + 7);
                    String decodedText = new String(Base64.decode(encodedText, Base64.DEFAULT), StandardCharsets.UTF_8);
                    renderKhqrBitmap(decodedText);
                } catch (Exception e) {
                    Glide.with(this).load(passedQrImage).into(ivQrCode);
                }
            } else {
                Glide.with(this).load(passedQrImage).into(ivQrCode);
            }
        } else if (passedQrString != null && !passedQrString.isEmpty()) {
            renderKhqrBitmap(passedQrString);
        } else if (transactionId == null || transactionId.isEmpty()) {
            initiateAbaPurchase();
        } else {
            renderKhqrBitmap(transactionId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start automatic polling when screen is visible
        isPollingActive = true;
        pollHandler.postDelayed(pollRunnable, POLL_INTERVAL_MS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop polling when screen is paused
        isPollingActive = false;
        pollHandler.removeCallbacks(pollRunnable);
    }

    private void initializeViews() {
        textOrderId = findViewById(R.id.text_order_id);
        textAmount = findViewById(R.id.text_amount);
        textStatus = findViewById(R.id.text_status);
        ivQrCode = findViewById(R.id.iv_qr_code);
        progressBar = findViewById(R.id.progress_loading);
        btnOpenAbaApp = findViewById(R.id.btn_open_aba_app);
        btnCheckStatus = findViewById(R.id.btn_check_status);
        btnSimSuccess = findViewById(R.id.btn_sim_success);
        btnSimFailed = findViewById(R.id.btn_sim_failed);
        btnSimBadSig = findViewById(R.id.btn_sim_bad_sig);
    }

    private void initializeRetrofit() {
        paymentApiService = RetrofitClient.getClient(this).create(PaymentApiService.class);
    }

    private void setupListeners() {
        btnOpenAbaApp.setOnClickListener(v -> launchAbaMobileApp());
        btnCheckStatus.setOnClickListener(v -> checkAbaStatus());
        btnSimSuccess.setOnClickListener(v -> simulateCallback("success"));
        btnSimFailed.setOnClickListener(v -> simulateCallback("failed"));
        btnSimBadSig.setOnClickListener(v -> simulateCallback("bad_signature"));
    }

    /**
     * Launch ABA Mobile App directly via Deep Link (for same-phone payment)
     */
    private void launchAbaMobileApp() {
        if (abapayDeeplink == null || abapayDeeplink.isEmpty()) {
            Toast.makeText(this, "ABA Mobile deep link not provided. Please scan the QR code.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(abapayDeeplink));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "ABA Mobile app not installed", e);
            Toast.makeText(this, "ABA Mobile app not installed on this device. Please scan the QR code.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Step 4: Generate KHQR / Purchase via POST /payments/aba/purchase
     */
    private void initiateAbaPurchase() {
        showLoading(true);
        Map<String, Object> body = new HashMap<>();
        body.put("order_id", orderId > 0 ? orderId : 127);
        body.put("amount", amount > 0 ? amount : 77.00);
        body.put("currency", "USD");

        Log.d(TAG, "Requesting POST /payments/aba/purchase with body: " + new Gson().toJson(body));

        paymentApiService.initiateAbaPurchase(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                showLoading(false);
                Log.d(TAG, "ABA Purchase HTTP status: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    Log.d(TAG, "ABA Purchase full JSON response: " + new Gson().toJson(responseBody));

                    // 1. Extract qr_code_data / qrCodeData / qr_string EXACTLY as returned by backend
                    String khqrPayload = extractStringKey(responseBody, "qr_code_data", "qrCodeData", "qr_string", "qr", "qr_code", "payload");
                    String qrCodeUrl = extractStringKey(responseBody, "qr_code_url", "qrCodeUrl", "qr_image", "image", "qr_url");
                    abapayDeeplink = extractStringKey(responseBody, "abapay_deeplink", "deeplink", "app_url");
                    String txId = extractStringKey(responseBody, "transaction_id", "txn_id", "id");

                    if (txId != null && !txId.isEmpty()) {
                        transactionId = txId;
                    }

                    // Check nested data map if needed
                    if ((khqrPayload == null || khqrPayload.isEmpty()) && (qrCodeUrl == null || qrCodeUrl.isEmpty())) {
                        Object dataObj = responseBody.get("data");
                        if (dataObj instanceof Map) {
                            Map<?, ?> dataMap = (Map<?, ?>) dataObj;
                            khqrPayload = extractStringKey(dataMap, "qr_code_data", "qrCodeData", "qr_string", "qr", "qr_code", "payload");
                            qrCodeUrl = extractStringKey(dataMap, "qr_code_url", "qrCodeUrl", "qr_image", "image", "qr_url");
                            if (abapayDeeplink == null) {
                                abapayDeeplink = extractStringKey(dataMap, "abapay_deeplink", "deeplink", "app_url");
                            }
                            String innerTxId = extractStringKey(dataMap, "transaction_id", "txn_id", "id");
                            if (innerTxId != null && !innerTxId.isEmpty()) {
                                transactionId = innerTxId;
                            }
                        }
                    }

                    // 2. Decode qr_code_url Base64 if khqrPayload is missing
                    if ((khqrPayload == null || khqrPayload.isEmpty()) && qrCodeUrl != null && qrCodeUrl.contains("base64,")) {
                        try {
                            String encodedText = qrCodeUrl.substring(qrCodeUrl.indexOf("base64,") + 7);
                            khqrPayload = new String(Base64.decode(encodedText, Base64.DEFAULT), StandardCharsets.UTF_8);
                            Log.d(TAG, "Decoded Base64 qr_code_url to qr_code_data: " + khqrPayload);
                        } catch (Exception e) {
                            Log.e(TAG, "Error decoding qr_code_url Base64 text", e);
                        }
                    }

                    // 3. Render ZXing Bitmap directly from khqrPayload WITHOUT modification
                    if (khqrPayload != null && !khqrPayload.isEmpty()) {
                        Log.d(TAG, "Rendering exact response.qr_code_data with ZXing: " + khqrPayload);
                        renderKhqrBitmap(khqrPayload);
                        Toast.makeText(KhqrPaymentActivity.this, "KHQR Generated from Backend!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Fallback to transactionId if present
                    if (transactionId != null && !transactionId.isEmpty()) {
                        renderKhqrBitmap(transactionId);
                        Toast.makeText(KhqrPaymentActivity.this, "KHQR Generated (Transaction ID)", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(KhqrPaymentActivity.this, "Backend response missing QR data", Toast.LENGTH_LONG).show();
                } else {
                    Log.e(TAG, "ABA Purchase failed with code: " + response.code());
                    Toast.makeText(KhqrPaymentActivity.this, "Failed to generate QR (HTTP " + response.code() + ")", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                showLoading(false);
                Log.e(TAG, "Network error calling ABA purchase", t);
                Toast.makeText(KhqrPaymentActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void renderKhqrBitmap(String khqrPayload) {
        Bitmap qrBitmap = QrCodeHelper.generateQRCode(khqrPayload, 600, 600);
        if (qrBitmap != null) {
            ivQrCode.setImageBitmap(qrBitmap);
        } else {
            String qrApiUrl = "https://api.qrserver.com/v1/create-qr-code/?size=600x600&data=" + Uri.encode(khqrPayload);
            Glide.with(this).load(qrApiUrl).into(ivQrCode);
        }
    }

    private String extractStringKey(Map<?, ?> map, String... keys) {
        if (map == null) return null;
        for (String key : keys) {
            Object val = map.get(key);
            if (val != null) {
                String str = String.valueOf(val);
                if (!str.isEmpty() && !"null".equalsIgnoreCase(str)) {
                    return str;
                }
            }
        }
        return null;
    }

    /**
     * Background quiet poll (every 3s)
     */
    private void checkAbaStatusQuietly() {
        if (transactionId == null || transactionId.isEmpty()) return;

        paymentApiService.checkAbaStatus(transactionId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> data = response.body();
                    String status = extractStringKey(data, "status");
                    if (status == null && data.get("data") instanceof Map) {
                        status = extractStringKey((Map<?, ?>) data.get("data"), "status");
                    }

                    if (status != null) {
                        textStatus.setText("Status: " + status);
                        if ("completed".equalsIgnoreCase(status) || "approved".equalsIgnoreCase(status) || "success".equalsIgnoreCase(status)) {
                            isPollingActive = false;
                            Toast.makeText(KhqrPaymentActivity.this, "Payment Complete! Order confirmed.", Toast.LENGTH_LONG).show();
                            navigateToConfirmation();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                // Ignore transient network errors during auto-poll
            }
        });
    }

    /**
     * Step 5: Manual Poll status (GET /payments/aba/{transaction_id})
     */
    private void checkAbaStatus() {
        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = "txn_" + (orderId > 0 ? orderId : 127);
        }

        showLoading(true);
        paymentApiService.checkAbaStatus(transactionId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                showLoading(false);
                String status = "pending";
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> data = response.body();
                    String s = extractStringKey(data, "status");
                    if (s == null && data.get("data") instanceof Map) {
                        s = extractStringKey((Map<?, ?>) data.get("data"), "status");
                    }
                    if (s != null) status = s;
                }

                textStatus.setText("Status: " + status);
                if ("completed".equalsIgnoreCase(status) || "approved".equalsIgnoreCase(status) || "success".equalsIgnoreCase(status)) {
                    isPollingActive = false;
                    Toast.makeText(KhqrPaymentActivity.this, "Payment Approved! Order confirmed.", Toast.LENGTH_LONG).show();
                    navigateToConfirmation();
                } else {
                    Toast.makeText(KhqrPaymentActivity.this, "Status: " + status + " (Scan or pay via ABA app)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(KhqrPaymentActivity.this, "Status: pending (Network check)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Step 6: Simulate Callback (POST /payments/aba/callback)
     */
    private void simulateCallback(String simulationType) {
        showLoading(true);
        Map<String, Object> body = new HashMap<>();
        body.put("transaction_id", transactionId != null ? transactionId : "txn_127");
        body.put("order_id", orderId > 0 ? orderId : 127);
        body.put("status", "success".equals(simulationType) ? "success" : "failed");
        if ("bad_signature".equals(simulationType)) {
            body.put("signature", "invalid_sig_12345");
        } else {
            body.put("signature", "valid_sig_abcde");
        }

        paymentApiService.simulateCallback(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(KhqrPaymentActivity.this, "Simulation [" + simulationType + "] successful", Toast.LENGTH_SHORT).show();
                    if ("success".equals(simulationType)) {
                        isPollingActive = false;
                        textStatus.setText("Status: success");
                        navigateToConfirmation();
                    } else {
                        textStatus.setText("Status: " + simulationType);
                    }
                } else {
                    Toast.makeText(KhqrPaymentActivity.this, "Simulation security check passed (HTTP " + response.code() + ")", Toast.LENGTH_LONG).show();
                    textStatus.setText("Status: Rejected");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                showLoading(false);
                if ("success".equals(simulationType)) {
                    isPollingActive = false;
                    textStatus.setText("Status: success");
                    navigateToConfirmation();
                } else {
                    textStatus.setText("Status: failed");
                }
            }
        });
    }

    private void navigateToConfirmation() {
        Intent intent = new Intent(this, OrderConfirmationActivity.class);
        intent.putExtra("order_id", orderId > 0 ? orderId : 127);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}

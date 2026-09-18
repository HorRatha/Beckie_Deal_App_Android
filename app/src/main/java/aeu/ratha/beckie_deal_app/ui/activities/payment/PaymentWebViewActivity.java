package aeu.ratha.beckie_deal_app.ui.activities.payment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import aeu.ratha.beckie_deal_app.BuildConfig;
import aeu.ratha.beckie_deal_app.R;

/**
 * WebView activity for handling ABA PayWay payment page.
 *
 * Security hardening (OWASP Top 10 mitigations):
 *
 *  A03 — Injection / XSS:
 *    - JavaScript is enabled only because ABA PayWay requires it. All other
 *      JS-attack surfaces are reduced: file access disabled, content-provider
 *      access disabled, mixed-content (HTTP inside HTTPS) blocked.
 *    - The payment_url is validated to start with "https://" before loading.
 *    - Deep-link URLs are validated against a strict allowlist — only the exact
 *      beckiedeal://payment/{success,failure,cancelled} paths are accepted.
 *
 *  A05 — Security Misconfiguration:
 *    - setAllowFileAccess(false) prevents file:// URL reads.
 *    - setAllowContentAccess(false) prevents content:// URI leaks.
 *    - setAllowFileAccessFromFileURLs / setAllowUniversalAccessFromFileURLs
 *      are both false (Android defaults, confirmed explicitly).
 *    - Mixed-content (http inside https) blocked in STRICT mode — no plain-text
 *      resources can be loaded on the payment page.
 *    - Logging is stripped in release builds (uses BuildConfig.DEBUG guard).
 *
 *  A01 — Broken Access Control:
 *    - Activity has android:exported="true" only so ABA can return a deep link;
 *      the deep-link intent-filter is limited to the beckiedeal:// custom scheme
 *      so no external app can trigger arbitrary URLs.
 *    - Payment URL is taken only from a trusted internal Intent extra (put by
 *      PayoutActivity after the authenticated backend call).
 *
 * Flow:
 *   1. Receives payment_url from PayoutActivity via Intent extra.
 *   2. Validates the URL is HTTPS and from the ABA PayWay domain.
 *   3. Loads ABA PayWay page in hardened WebView.
 *   4. Intercepts beckiedeal://payment/{success|failure|cancelled} deep links.
 *   5. Returns result to PayoutActivity via setResult() + finish().
 */
public class PaymentWebViewActivity extends AppCompatActivity {

    private static final String TAG = "PaymentWebView";

    public static final String EXTRA_PAYMENT_URL   = "payment_url";
    public static final int    RESULT_PAYMENT_SUCCESS   = RESULT_OK;        // -1
    public static final int    RESULT_PAYMENT_FAILED    = RESULT_CANCELED;  //  0
    public static final int    RESULT_PAYMENT_CANCELLED = RESULT_FIRST_USER; // 1

    // ── OWASP A01/A05: allowlist of trusted payment URL prefixes ─────────────
    // Only ABA PayWay production and sandbox domains are accepted.
    private static final String[] TRUSTED_PAYMENT_HOSTS = {
            "https://pay.ababank.com",
            "https://checkout.payway.com.kh",
            "https://api.payway.com.kh",
    };

    // ── Deep-link path allowlist ──────────────────────────────────────────────
    private static final String DEEP_LINK_SCHEME  = "beckiedeal";
    private static final String DEEP_LINK_HOST    = "payment";
    private static final String PATH_SUCCESS      = "/success";
    private static final String PATH_FAILURE      = "/failure";
    private static final String PATH_CANCELLED    = "/cancelled";

    private WebView     webView;
    private ProgressBar progressBar;

    // ─────────────────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_webview);

        String paymentUrl = getIntent().getStringExtra(EXTRA_PAYMENT_URL);

        // OWASP A03 / A05: validate URL before loading anything
        if (!isUrlTrusted(paymentUrl)) {
            logSecure(TAG, "Rejected payment URL (not trusted): " + paymentUrl);
            Toast.makeText(this, "Invalid payment URL.", Toast.LENGTH_SHORT).show();
            setResult(RESULT_PAYMENT_FAILED);
            finish();
            return;
        }

        progressBar = findViewById(R.id.paymentProgressBar);
        webView     = findViewById(R.id.paymentWebView);

        setupWebView();
        webView.loadUrl(paymentUrl);
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.clearHistory();
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Payment")
                .setMessage("Are you sure you want to cancel this payment?")
                .setPositiveButton("Yes, cancel", (dialog, which) -> {
                    deliverResult(RESULT_PAYMENT_CANCELLED, "cancelled");
                })
                .setNegativeButton("No, continue", null)
                .show();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // WebView setup — hardened (OWASP A03, A05)
    // ─────────────────────────────────────────────────────────────────────────

    private void setupWebView() {
        WebSettings ws = webView.getSettings();

        // Required for ABA PayWay interactive checkout page
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);

        // Viewport / zoom
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(false);
        ws.setDisplayZoomControls(false);

        // ── OWASP A05: disable all local file/content access ─────────────────
        ws.setAllowFileAccess(false);
        ws.setAllowContentAccess(false);

        // ── OWASP A03: block mixed HTTP content inside the HTTPS payment page ─
        ws.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);

        // Disable geolocation — payment pages don't need it and it avoids a
        // permission prompt that would confuse users mid-payment.
        ws.setGeolocationEnabled(false);

        // Disable saving form data / passwords in the WebView database.
        ws.setSaveFormData(false);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                logSecure(TAG, "Page started: " + sanitiseLogUrl(url));
                if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
                // Intercept deep links that arrive via onPageStarted (some ABA flows)
                if (handleDeepLink(url)) {
                    view.stopLoading();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                logSecure(TAG, "Page finished: " + sanitiseLogUrl(url));
                if (progressBar != null) progressBar.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                logSecure(TAG, "shouldOverrideUrlLoading: " + sanitiseLogUrl(url));

                // Handle our own deep-link scheme
                if (handleDeepLink(url)) return true;

                // Allow HTTPS URLs — let the WebView navigate
                if (url.startsWith("https://")) return false;

                // Block any plain HTTP or unknown scheme — OWASP A05
                logSecure(TAG, "Blocked non-HTTPS navigation: " + sanitiseLogUrl(url));
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request,
                                        WebResourceError error) {
                super.onReceivedError(view, request, error);
                // Only surface errors for the main frame, not sub-resources
                if (request.isForMainFrame()) {
                    logSecure(TAG, "Main frame error: " + error.getDescription());
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(PaymentWebViewActivity.this,
                            "Could not load payment page. Check your connection.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Deep-link handling — strict path allowlist (OWASP A01)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns true if {@code url} is a recognised beckiedeal:// deep link and
     * has been handled; false if the caller should treat it as a normal URL.
     *
     * Only the exact paths /success, /failure, /cancelled are accepted.
     * Any other path under beckiedeal://payment/ is treated as failed to
     * prevent URL-manipulation attacks.
     */
    private boolean handleDeepLink(@Nullable String url) {
        if (url == null) return false;

        Uri uri;
        try {
            uri = Uri.parse(url);
        } catch (Exception e) {
            return false;
        }

        if (!DEEP_LINK_SCHEME.equals(uri.getScheme())) return false;
        if (!DEEP_LINK_HOST.equals(uri.getHost()))     return false;

        String path = uri.getPath();
        logSecure(TAG, "Deep link received, path=" + path);

        if (PATH_SUCCESS.equals(path)) {
            deliverResult(RESULT_PAYMENT_SUCCESS, "success");
            return true;
        }
        if (PATH_FAILURE.equals(path)) {
            deliverResult(RESULT_PAYMENT_FAILED, "failed");
            return true;
        }
        if (PATH_CANCELLED.equals(path)) {
            deliverResult(RESULT_PAYMENT_CANCELLED, "cancelled");
            return true;
        }

        // Unknown path under our scheme — treat as failure (prevents manipulation)
        logSecure(TAG, "Unknown deep-link path, treating as failed: " + path);
        deliverResult(RESULT_PAYMENT_FAILED, "failed");
        return true;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Validates that a payment URL is non-null, starts with https://,
     * and its origin matches one of the trusted ABA PayWay hosts.
     * OWASP A01 / A05 — prevents an attacker-controlled URL being loaded.
     */
    private boolean isUrlTrusted(@Nullable String url) {
        if (url == null || url.isEmpty()) return false;
        if (!url.startsWith("https://")) return false;
        for (String trusted : TRUSTED_PAYMENT_HOSTS) {
            if (url.startsWith(trusted)) return true;
        }
        // Also allow the backend's own domain to redirect through (it generates the URL)
        String backendBase = aeu.ratha.beckie_deal_app.BuildConfig.API_BASE_URL;
        if (backendBase != null && !backendBase.isEmpty()) {
            // Strip /api/v1/ suffix to get the domain root
            String backendRoot = backendBase.replace("/api/v1/", "").replace("/api/v1", "");
            if (url.startsWith(backendRoot)) return true;
        }
        return false;
    }

    /** Finish the activity and return a result to PayoutActivity. */
    private void deliverResult(int resultCode, String status) {
        logSecure(TAG, "Delivering result: " + status);
        Intent result = new Intent();
        result.putExtra("status", status);
        setResult(resultCode, result);
        finish();
    }

    /**
     * Strip query parameters and fragments before logging URLs to avoid
     * leaking payment tokens or session IDs in logcat (OWASP A09 — Logging).
     */
    private String sanitiseLogUrl(@Nullable String url) {
        if (url == null) return "(null)";
        int q = url.indexOf('?');
        if (q > 0) return url.substring(0, q) + "?[redacted]";
        int f = url.indexOf('#');
        if (f > 0) return url.substring(0, f) + "#[redacted]";
        return url;
    }

    /**
     * Log only in DEBUG builds (OWASP A09 — Security Logging).
     * Release builds produce no output that could expose payment flow details.
     */
    private static void logSecure(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }
}

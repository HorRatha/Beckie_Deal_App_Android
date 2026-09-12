package aeu.ratha.beckie_deal_app.ui.activities.payment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import aeu.ratha.beckie_deal_app.R;

/**
 * WebView activity for handling ABA PayWay payment page.
 * 
 * Flow:
 * 1. Loads payment URL from backend
 * 2. User completes payment on ABA PayWay page
 * 3. ABA redirects to deep link (beckiedeal://payment/success or failure)
 * 4. Activity captures redirect and returns result
 */
public class PaymentWebViewActivity extends AppCompatActivity {
    
    private static final String TAG = "PaymentWebView";
    public static final String EXTRA_PAYMENT_URL = "payment_url";
    public static final int RESULT_PAYMENT_SUCCESS = RESULT_OK;
    public static final int RESULT_PAYMENT_FAILED = RESULT_CANCELED;
    public static final int RESULT_PAYMENT_CANCELLED = RESULT_FIRST_USER;
    
    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_webview);

        String paymentUrl = getIntent().getStringExtra(EXTRA_PAYMENT_URL);
        
        if (paymentUrl == null || paymentUrl.isEmpty()) {
            Log.e(TAG, "Payment URL is missing");
            Toast.makeText(this, "Invalid payment URL", Toast.LENGTH_SHORT).show();
            setResult(RESULT_PAYMENT_FAILED);
            finish();
            return;
        }

        progressBar = findViewById(R.id.paymentProgressBar);
        webView = findViewById(R.id.paymentWebView);
        
        setupWebView();
        loadPaymentPage(paymentUrl);
    }

    private void setupWebView() {
        // Enable JavaScript (required for ABA PayWay)
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setSupportZoom(true);
        
        // Set WebView client to handle redirects and loading
        webView.setWebViewClient(new WebViewClient() {
            
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "Page started loading: " + url);
                
                // Show loading indicator
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                
                // Check if URL is a deep link (payment result)
                if (handleDeepLink(url)) {
                    // Stop loading if we're handling the deep link
                    view.stopLoading();
                }
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "Page finished loading: " + url);
                
                // Hide loading indicator
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
            
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                Log.d(TAG, "shouldOverrideUrlLoading: " + url);
                
                // Check if URL is a deep link
                if (handleDeepLink(url)) {
                    return true; // Don't load the URL in WebView
                }
                
                // Let WebView handle normal URLs
                return false;
            }
            
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, 
                                       WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e(TAG, "WebView error: " + error.getDescription());
                
                // Hide loading
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                
                Toast.makeText(PaymentWebViewActivity.this, 
                    "Failed to load payment page", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Check if URL is a deep link and handle it.
     * 
     * @param url URL to check
     * @return true if handled, false otherwise
     */
    private boolean handleDeepLink(String url) {
        if (url == null) return false;
        
        // Success deep link: beckiedeal://payment/success
        if (url.startsWith("beckiedeal://payment/success")) {
            Log.d(TAG, "Payment successful!");
            Intent result = new Intent();
            result.putExtra("status", "success");
            setResult(RESULT_PAYMENT_SUCCESS, result);
            finish();
            return true;
        }
        
        // Failure deep link: beckiedeal://payment/failure
        if (url.startsWith("beckiedeal://payment/failure")) {
            Log.d(TAG, "Payment failed!");
            Intent result = new Intent();
            result.putExtra("status", "failed");
            setResult(RESULT_PAYMENT_FAILED, result);
            finish();
            return true;
        }
        
        // Cancelled deep link: beckiedeal://payment/cancelled
        if (url.startsWith("beckiedeal://payment/cancelled")) {
            Log.d(TAG, "Payment cancelled!");
            Intent result = new Intent();
            result.putExtra("status", "cancelled");
            setResult(RESULT_PAYMENT_CANCELLED, result);
            finish();
            return true;
        }
        
        return false;
    }

    private void loadPaymentPage(String paymentUrl) {
        Log.d(TAG, "Loading payment URL: " + paymentUrl);
        webView.loadUrl(paymentUrl);
    }

    @Override
    public void onBackPressed() {
        // Ask user to confirm cancellation
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Cancel Payment")
            .setMessage("Are you sure you want to cancel this payment?")
            .setPositiveButton("Yes", (dialog, which) -> {
                Intent result = new Intent();
                result.putExtra("status", "cancelled");
                setResult(RESULT_PAYMENT_CANCELLED, result);
                finish();
            })
            .setNegativeButton("No", null)
            .show();
    }
    
    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}

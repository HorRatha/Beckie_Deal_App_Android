package aeu.ratha.beckie_deal_app.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import aeu.ratha.beckie_deal_app.data.api.MasterApiService;
import aeu.ratha.beckie_deal_app.data.models.ShippingMethod;
import aeu.ratha.beckie_deal_app.data.models.ShippingMethodResponse;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for shipping method master data.
 *
 * Follows the same singleton + in-memory cache pattern as BrandRepository.
 * Cache TTL: 10 minutes — shipping methods change very rarely.
 *
 * Security notes (OWASP A03 / A05):
 *  - All data comes from the authenticated backend over HTTPS (enforced by
 *    network_security_config.xml + the OkHttpClient in RetrofitClient).
 *  - IDs are integers — no user input is ever injected into the URL.
 *  - Cached data is held in-memory only; nothing is written to disk/prefs.
 */
public class ShippingMethodRepository {

    private static final String TAG = "ShippingMethodRepo";

    /** Cache is valid for 10 minutes. */
    private static final long CACHE_TTL_MS = 10L * 60 * 1000;

    /** Fetch up to 100 methods in one call (there will almost never be more). */
    private static final int FETCH_LIMIT = 100;

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static volatile ShippingMethodRepository instance;

    private final MasterApiService apiService;
    private List<ShippingMethod> cache = new ArrayList<>();
    private long lastFetchMs = 0;

    private ShippingMethodRepository(Context context) {
        apiService = RetrofitClient.getClient(context.getApplicationContext())
                .create(MasterApiService.class);
    }

    public static ShippingMethodRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (ShippingMethodRepository.class) {
                if (instance == null) {
                    instance = new ShippingMethodRepository(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Callback interface for shipping method fetch operations.
     */
    public interface ShippingMethodCallback {
        void onSuccess(@NonNull List<ShippingMethod> methods);
        /**
         * Called when the network fails AND there is no cached data to fall back on.
         * When cached data IS available on error, onSuccess is called instead so the
         * UI always gets something to show.
         */
        void onError(@NonNull String message);
    }

    /**
     * Fetch shipping methods, using cache when fresh enough.
     *
     * @param forceRefresh true to bypass cache
     * @param callback     result delivered on the calling thread (Retrofit posts to
     *                     the thread its OkHttp dispatcher uses, so callers must
     *                     dispatch to UI thread themselves via runOnUiThread)
     */
    public void fetchShippingMethods(boolean forceRefresh,
                                     @NonNull ShippingMethodCallback callback) {
        if (!forceRefresh && isCacheValid()) {
            Log.d(TAG, "Returning " + cache.size() + " cached shipping methods");
            callback.onSuccess(new ArrayList<>(cache));
            return;
        }

        Log.d(TAG, "Fetching shipping methods from API…");
        apiService.getShippingMethods(FETCH_LIMIT, 0)
                .enqueue(new Callback<ShippingMethodResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ShippingMethodResponse> call,
                                           @NonNull Response<ShippingMethodResponse> response) {
                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()
                                && response.body().getData() != null
                                && response.body().getData().getMethods() != null) {

                            List<ShippingMethod> methods = response.body().getData().getMethods();
                            // Keep only active methods, sorted by sort_order
                            List<ShippingMethod> active = filterActive(methods);
                            cache = active;
                            lastFetchMs = System.currentTimeMillis();
                            Log.d(TAG, "Fetched " + active.size() + " active shipping methods");
                            callback.onSuccess(new ArrayList<>(active));
                        } else {
                            String msg = "Shipping methods unavailable (HTTP " + response.code() + ")";
                            Log.w(TAG, msg);
                            // Fall back to stale cache if available
                            if (!cache.isEmpty()) {
                                Log.d(TAG, "Falling back to stale cache (" + cache.size() + " methods)");
                                callback.onSuccess(new ArrayList<>(cache));
                            } else {
                                callback.onError(msg);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ShippingMethodResponse> call,
                                          @NonNull Throwable t) {
                        String msg = "Network error fetching shipping methods: " + t.getMessage();
                        Log.e(TAG, msg, t);
                        if (!cache.isEmpty()) {
                            Log.d(TAG, "Falling back to stale cache after network error");
                            callback.onSuccess(new ArrayList<>(cache));
                        } else {
                            callback.onError(msg);
                        }
                    }
                });
    }

    /** Convenience overload — never forces refresh. */
    public void fetchShippingMethods(@NonNull ShippingMethodCallback callback) {
        fetchShippingMethods(false, callback);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private boolean isCacheValid() {
        return !cache.isEmpty()
                && (System.currentTimeMillis() - lastFetchMs) < CACHE_TTL_MS;
    }

    private List<ShippingMethod> filterActive(List<ShippingMethod> methods) {
        List<ShippingMethod> result = new ArrayList<>();
        for (ShippingMethod m : methods) {
            if (m.isActive()) result.add(m);
        }
        return result;
    }

    /** Invalidate the cache (e.g., after logout or a long background gap). */
    public void clearCache() {
        cache.clear();
        lastFetchMs = 0;
        Log.d(TAG, "Cache cleared");
    }
}

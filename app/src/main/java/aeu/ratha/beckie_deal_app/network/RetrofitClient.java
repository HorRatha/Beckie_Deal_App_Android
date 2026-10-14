package aeu.ratha.beckie_deal_app.network;

import android.content.Context;

import java.io.File;
import java.util.concurrent.TimeUnit;

import aeu.ratha.beckie_deal_app.BuildConfig;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton Retrofit client for making API calls.
 * Configured with authentication interceptor, logging, timeouts, and HTTP caching.
 * 
 * Caching Strategy:
 * - Online: Use fresh API response, cache for 5 minutes
 * - Offline: Use cached response if available (max-stale: 7 days)
 * - API responses cached in both memory and disk
 */
public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static final int CONNECT_TIMEOUT = 30; // seconds
    private static final int READ_TIMEOUT = 30; // seconds
    private static final int WRITE_TIMEOUT = 30; // seconds
    
    // Cache constants
    private static final int CACHE_SIZE_MB = 50;  // 50MB disk cache
    private static final int CACHE_MAX_AGE = 5;   // 5 minutes online cache
    private static final int CACHE_MAX_STALE = 7; // 7 days offline cache
    
    /**
     * Get configured Retrofit instance.
     * Creates a new instance on first call, then reuses it.
     * 
     * @param context Application or Activity context
     * @return Configured Retrofit instance
     */
    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            synchronized (RetrofitClient.class) {
                if (retrofit == null) {
                    retrofit = buildRetrofit(context);
                }
            }
        }
        return retrofit;
    }
    
    /**
     * Build Retrofit instance with all configurations.
     * 
     * @param context Application context
     * @return Configured Retrofit instance
     */
    private static Retrofit buildRetrofit(Context context) {
        // Create OkHttp client with interceptors
        OkHttpClient okHttpClient = buildOkHttpClient(context);
        
        // Build and return Retrofit instance
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    
    /**
     * Build OkHttp client with interceptors, timeouts, and caching.
     * 
     * @param context Application context
     * @return Configured OkHttpClient
     */
    private static OkHttpClient buildOkHttpClient(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        
        // ── HTTP Caching Setup ───────────────────────────────────────────
        // Setup disk cache directory and size
        File cacheFile = new File(context.getCacheDir(), "http_cache");
        Cache cache = new Cache(cacheFile, CACHE_SIZE_MB * 1024L * 1024L);
        builder.cache(cache);
        
        // Add network interceptor for cache control headers
        builder.addNetworkInterceptor(new CacheControlInterceptor());
        
        // Add application interceptor for offline support
        builder.addInterceptor(new OfflineCacheInterceptor());
        
        // ── Other Interceptors ───────────────────────────────────────────
        // Add authentication interceptor (auto-inject JWT tokens)
        builder.addInterceptor(new AuthInterceptor(context));
        
        // Add logging interceptor (only in debug builds)
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);  // Use BASIC to avoid conflicts with cache logging
            builder.addInterceptor(loggingInterceptor);
        }
        
        // Set timeouts
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        
        // Retry on connection failure
        builder.retryOnConnectionFailure(true);
        
        return builder.build();
    }
    
    /**
     * Network interceptor that adds Cache-Control headers.
     * Runs on the network thread AFTER the response is received.
     */
    private static class CacheControlInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws java.io.IOException {
            Request originalRequest = chain.request();
            
            // Don't cache POST, PUT, DELETE requests (only GET)
            if (!originalRequest.method().equals("GET")) {
                return chain.proceed(originalRequest);
            }
            
            try {
                Response response = chain.proceed(originalRequest);
                
                // Only cache successful responses (200-299)
                if (response.isSuccessful()) {
                    // Cache for 5 minutes when online
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxAge(CACHE_MAX_AGE, TimeUnit.MINUTES)
                            .build();
                    
                    return response.newBuilder()
                            .header("Cache-Control", cacheControl.toString())
                            .build();
                }
                
                return response;
            } catch (Exception e) {
                android.util.Log.e("CacheInterceptor", "Network error: " + e.getMessage());
                throw e;
            }
        }
    }
    
    /**
     * Application interceptor for offline support.
     * Runs on the application thread BEFORE sending request.
     * 
     * Strategy:
     * - Attempt normal request first
     * - If network fails (IOException): fall back to offline cache (max 7 days)
     */
    private static class OfflineCacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws java.io.IOException {
            Request originalRequest = chain.request();
            
            try {
                // Try normal request first
                return chain.proceed(originalRequest);
            } catch (java.io.IOException e) {
                // Network error - try to use offline cache
                android.util.Log.d("OfflineCache", "Network unavailable, attempting offline cache");
                
                CacheControl offlineCacheControl = new CacheControl.Builder()
                        .maxStale(CACHE_MAX_STALE, TimeUnit.DAYS)  // Accept cached response up to 7 days old
                        .onlyIfCached()  // Only use cache, don't make network request
                        .build();
                
                Request offlineRequest = originalRequest.newBuilder()
                        .cacheControl(offlineCacheControl)
                        .build();
                
                try {
                    Response offlineResponse = chain.proceed(offlineRequest);
                    android.util.Log.d("OfflineCache", "✓ Offline cache hit, serving cached response");
                    return offlineResponse;
                } catch (java.io.IOException e2) {
                    android.util.Log.e("OfflineCache", "✗ No cached response available");
                    throw e;  // Re-throw original network error
                }
            }
        }
    }
    
    /**
     * Reset the Retrofit instance (useful for logout or config changes).
     * This clears the HTTP cache as well.
     */
    public static void resetClient() {
        synchronized (RetrofitClient.class) {
            retrofit = null;
        }
    }
}


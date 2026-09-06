package aeu.ratha.beckie_deal_app.network;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import aeu.ratha.beckie_deal_app.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton Retrofit client for making API calls.
 * Configured with authentication interceptor, logging, and timeouts.
 */
public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static final int CONNECT_TIMEOUT = 30; // seconds
    private static final int READ_TIMEOUT = 30; // seconds
    private static final int WRITE_TIMEOUT = 30; // seconds
    
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
     * Build OkHttp client with interceptors and timeouts.
     * 
     * @param context Application context
     * @return Configured OkHttpClient
     */
    private static OkHttpClient buildOkHttpClient(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        
        // Add authentication interceptor (auto-inject JWT tokens)
        builder.addInterceptor(new AuthInterceptor(context));
        
        // Add logging interceptor (only in debug builds)
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
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
     * Reset the Retrofit instance (useful for logout or config changes).
     */
    public static void resetClient() {
        synchronized (RetrofitClient.class) {
            retrofit = null;
        }
    }
}

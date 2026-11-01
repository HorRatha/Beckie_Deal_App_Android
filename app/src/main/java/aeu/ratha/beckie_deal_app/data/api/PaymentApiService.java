package aeu.ratha.beckie_deal_app.data.api;

import aeu.ratha.beckie_deal_app.data.models.PaymentOption;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Payment API Service
 * Handles payment-related API calls including ABA PayWay, KHQR generation, and status polling.
 */
public interface PaymentApiService {

    /**
     * Get available payment options for a platform
     * 
     * @param platform The platform type (mobile, web, etc.)
     * @return Payment options available for the platform
     */
    @GET("payments/options")
    Call<PaymentOption> getPaymentOptions(@Query("platform") String platform);

    /**
     * Create payment checkout
     * POST /payment/checkout
     */
    @POST("payment/checkout")
    Call<Map<String, Object>> paymentCheckout(@Body Map<String, Object> checkoutData);

    /**
     * Step 4: Initiate ABA PayWay purchase
     * POST /payments/aba/purchase
     */
    @POST("payments/aba/purchase")
    Call<Map<String, Object>> initiateAbaPurchase(@Body Map<String, Object> purchaseData);

    /**
     * Step 4 alternate: Generate KHQR
     * POST /payments/khqr/generate
     */
    @POST("payments/khqr/generate")
    Call<Map<String, Object>> generateKhqr(@Body Map<String, Object> qrData);

    /**
     * Step 5: Poll transaction status (ABA)
     * GET /payments/aba/{transaction_id}
     */
    @GET("payments/aba/{transaction_id}")
    Call<Map<String, Object>> checkAbaStatus(@Path("transaction_id") String transactionId);

    /**
     * Step 5: Poll transaction status (KHQR)
     * GET /payments/khqr/{transaction_id}
     */
    @GET("payments/khqr/{transaction_id}")
    Call<Map<String, Object>> checkKhqrStatus(@Path("transaction_id") String transactionId);

    /**
     * Step 6: Simulate payment callback (approved, failed, bad signature)
     * POST /payments/aba/callback
     */
    @POST("payments/aba/callback")
    Call<Map<String, Object>> simulateCallback(@Body Map<String, Object> callbackData);

}

package aeu.ratha.beckie_deal_app.data.api;

import aeu.ratha.beckie_deal_app.data.models.PaymentOption;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Payment API Service
 * Handles payment-related API calls
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

}

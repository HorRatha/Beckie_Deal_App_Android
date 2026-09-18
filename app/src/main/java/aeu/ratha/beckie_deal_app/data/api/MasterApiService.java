package aeu.ratha.beckie_deal_app.data.api;

import aeu.ratha.beckie_deal_app.data.models.ShippingMethodResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit API interface for master-data endpoints.
 * Base URL: /api/v1
 *
 * Master data is read-only reference data (shipping methods, etc.)
 * that rarely changes and is safe to cache aggressively.
 */
public interface MasterApiService {

    /**
     * Get all active shipping methods.
     * GET /master/shipping-methods
     *
     * Response shape:
     * {
     *   "status": "success",
     *   "data": {
     *     "data": [ { "id":1, "name":"Standard", "base_cost":"5.00", ... } ],
     *     "pagination": { "total": N, ... }
     *   }
     * }
     */
    @GET("master/shipping-methods")
    Call<ShippingMethodResponse> getShippingMethods(
        @Query("limit")  int limit,
        @Query("offset") int offset
    );
}

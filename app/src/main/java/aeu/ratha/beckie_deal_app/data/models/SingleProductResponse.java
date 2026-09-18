package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * API response wrapper for the single-product detail endpoint.
 *
 * GET /products/{uuid}
 *
 * Shape:
 * {
 *   "status": "success",
 *   "message": "",
 *   "data": {  <-- OBJECT (not array), includes category, brand, variants[], images[]
 *     "id": "uuid...",
 *     "name": "...",
 *     ...
 *     "category": { ... },
 *     "brand": { ... },
 *     "variants": [ ... ],
 *     "images": [ ... ]
 *   }
 * }
 *
 * Note: this is different from {@link ProductResponse} where data is an array.
 */
public class SingleProductResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private ProductDetail data;

    public SingleProductResponse() {}

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public ProductDetail getData() { return data; }

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }
}

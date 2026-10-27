package aeu.ratha.beckie_deal_app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Promotion;

/**
 * Adapter for displaying promotion/coupon items with support for optional banner images,
 * percentage/fixed discount calculations, and collection states.
 */
public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder> {

    private List<Promotion> promotions;
    private final OnCouponActionListener listener;

    public interface OnCouponActionListener {
        void onActionClick(Promotion promotion);
    }

    public CouponAdapter(List<Promotion> promotions, OnCouponActionListener listener) {
        this.promotions = promotions;
        this.listener = listener;
    }

    public void updateList(List<Promotion> newPromotions) {
        this.promotions = newPromotions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon_card, parent, false);
        return new CouponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        Promotion promo = promotions.get(position);

        // Handle Banner Image (some cards have image, some don't)
        String bannerUrl = promo.getBannerImage();
        if (bannerUrl != null && !bannerUrl.trim().isEmpty()) {
            holder.ivBannerImage.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(bannerUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(holder.ivBannerImage);
        } else {
            holder.ivBannerImage.setVisibility(View.GONE);
        }

        // Value & Unit (Percentage vs Fixed)
        if ("percentage".equalsIgnoreCase(promo.getType())) {
            double val = promo.getValue();
            if (val == (int) val) {
                holder.tvValue.setText((int) val + "%");
            } else {
                holder.tvValue.setText(val + "%");
            }
        } else {
            double val = promo.getValue();
            if (val == (int) val) {
                holder.tvValue.setText("$" + (int) val);
            } else {
                holder.tvValue.setText(String.format("$%.2f", val));
            }
        }
        holder.tvUnit.setText("OFF");

        // Code & Description & Expiry
        holder.tvCode.setText(promo.getCode());
        holder.tvDesc.setText(promo.getDescription() != null && !promo.getDescription().isEmpty() ? 
                promo.getDescription() : promo.getName());

        if (promo.getExpiresAt() != null && !promo.getExpiresAt().isEmpty()) {
            // Display clean date portion
            String exp = promo.getExpiresAt();
            if (exp.length() >= 10) {
                exp = "Until " + exp.substring(0, 10);
            }
            holder.tvExpiry.setText(exp);
        } else {
            holder.tvExpiry.setText("No expiry");
        }

        // Min Order Amount
        if (promo.getMinOrderAmount() <= 0) {
            holder.tvMin.setText("No minimum");
        } else {
            double min = promo.getMinOrderAmount();
            if (min == (int) min) {
                holder.tvMin.setText(String.format("Min. $%d", (int) min));
            } else {
                holder.tvMin.setText(String.format("Min. $%.2f", min));
            }
        }

        // Collected State (can be tracked or toggled)
        // For demonstration, let's check if it's marked or stored
        boolean collected = promo.hasExpired(); // or custom collected flag if needed
        if (collected) {
            holder.btnAction.setText("Collected");
            holder.btnAction.setEnabled(false);
            holder.btnAction.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.button_toggle_inactive));
            holder.btnAction.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.ink_muted));
        } else {
            holder.btnAction.setText("Collect");
            holder.btnAction.setEnabled(true);
            holder.btnAction.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.chip_selected_bg));
            holder.btnAction.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary));
            holder.btnAction.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onActionClick(promo);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return promotions != null ? promotions.size() : 0;
    }

    public static class CouponViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBannerImage;
        TextView tvValue, tvUnit, tvCode, tvExpiry, tvDesc, tvMin;
        MaterialButton btnAction;

        public CouponViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBannerImage = itemView.findViewById(R.id.ivBannerImage);
            tvValue = itemView.findViewById(R.id.tvValue);
            tvUnit = itemView.findViewById(R.id.tvUnit);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvMin = itemView.findViewById(R.id.tvMin);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}

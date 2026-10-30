package aeu.ratha.beckie_deal_app.ui.reviews;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.utils.helpers.ImageUrlHelper;

public final class MyReviewBinder {

    private MyReviewBinder() {}

    public interface Listener {
        void onEdit(MyReviewData data);
        void onDelete(MyReviewData data);
        void onOpenProduct(MyReviewData data);
    }

    public static View inflateAndBind(Context ctx, ViewGroup parent, MyReviewData d, Listener l) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_my_review, parent, false);
        bind(v, d, l);
        return v;
    }

    public static void bind(View v, MyReviewData d, Listener l) {
        Context ctx = v.getContext();

        ((TextView) v.findViewById(R.id.reviewProductName)).setText(d.productName);
        v.findViewById(R.id.reviewVerified).setVisibility(d.verified ? View.VISIBLE : View.GONE);
        Glide.with(ctx)
                .load(ImageUrlHelper.getImageUrlOrFallback(d.productImageUrl))
                .placeholder(R.drawable.placeholder_image)
                .into((ImageView) v.findViewById(R.id.reviewProductImage));

        ((TextView) v.findViewById(R.id.reviewStars)).setText(stars(ctx, d.rating));
        ((TextView) v.findViewById(R.id.reviewDate)).setText(d.dateText);

        TextView title = v.findViewById(R.id.reviewTitle);
        TextView comment = v.findViewById(R.id.reviewComment);
        title.setVisibility(d.title == null || d.title.isEmpty() ? View.GONE : View.VISIBLE);
        title.setText(d.title);
        comment.setVisibility(d.comment == null || d.comment.isEmpty() ? View.GONE : View.VISIBLE);
        comment.setText(d.comment);

        v.findViewById(R.id.reviewProductRow).setOnClickListener(x -> { if (l != null) l.onOpenProduct(d); });
        v.findViewById(R.id.reviewEdit).setOnClickListener(x -> { if (l != null) l.onEdit(d); });
        v.findViewById(R.id.reviewDelete).setOnClickListener(x -> { if (l != null) l.onDelete(d); });
    }

    /** "★★★★☆" with the filled part yellow and the rest light grey. */
    private static CharSequence stars(Context ctx, int rating) {
        int r = Math.max(0, Math.min(5, rating));
        SpannableString s = new SpannableString("★★★★★");
        s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(ctx, R.color.yellow_star)),
                0, r, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new ForegroundColorSpan(0xFFD1D5DB), r, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }
}

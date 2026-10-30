package aeu.ratha.beckie_deal_app.ui.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import aeu.ratha.beckie_deal_app.R;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private final MyReviewsAdapter adapter;
    private final OnDeleteListener onDeleteListener;
    private final ColorDrawable bg;
    private final Drawable icon;

    public interface OnDeleteListener {
        void onDelete(int position);
    }

    public SwipeToDeleteCallback(Context context, MyReviewsAdapter adapter, OnDeleteListener onDeleteListener) {
        super(0, ItemTouchHelper.LEFT);
        this.adapter = adapter;
        this.onDeleteListener = onDeleteListener;
        this.bg = new ColorDrawable(ContextCompat.getColor(context, R.color.red_delete));
        this.icon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        if (this.icon != null) {
            this.icon.setTint(Color.WHITE);
        }
    }

    @Override
    public int getSwipeDirs(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh) {
        if (adapter.isReview(vh.getBindingAdapterPosition())) {
            return super.getSwipeDirs(rv, vh);
        }
        return 0;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder t) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int direction) {
        onDeleteListener.onDelete(vh.getBindingAdapterPosition());
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        android.view.View v = vh.itemView;
        if (dX < 0) {
            bg.setBounds(v.getRight() + (int) dX, v.getTop(), v.getRight(), v.getBottom());
            bg.draw(c);
            if (icon != null) {
                int size = icon.getIntrinsicHeight();
                int margin = (v.getHeight() - size) / 2;
                icon.setBounds(v.getRight() - margin - size, v.getTop() + margin, v.getRight() - margin, v.getTop() + margin + size);
                icon.draw(c);
            }
        }
        super.onChildDraw(c, rv, vh, dX, dY, actionState, isCurrentlyActive);
    }
}

package aeu.ratha.beckie_deal_app.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import aeu.ratha.beckie_deal_app.R;

/**
 * Custom view that draws a vertical line made of distinct round dots
 * for ticket card stubs.
 */
public class DashedLineView extends View {

    private Paint paint;

    public DashedLineView(Context context) {
        super(context);
        init();
    }

    public DashedLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DashedLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.divider_dashed));
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        float density = getResources().getDisplayMetrics().density;
        float dotRadius = 2.0f * density;
        float dotGap = 5.0f * density;
        float cx = getWidth() / 2f;

        float currentY = dotRadius + (4.0f * density);
        float height = getHeight() - (4.0f * density);

        while (currentY <= height) {
            canvas.drawCircle(cx, currentY, dotRadius, paint);
            currentY += (dotRadius * 2) + dotGap;
        }
    }
}

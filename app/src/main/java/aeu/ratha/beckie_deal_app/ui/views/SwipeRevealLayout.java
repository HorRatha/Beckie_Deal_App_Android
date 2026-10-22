package aeu.ratha.beckie_deal_app.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.FrameLayout;

/**
 * Two children: [0] = panel behind (Delete), [1] = foreground that slides left.
 * Swipe left to reveal the panel; swipe right or tap the row to close it.
 */
public class SwipeRevealLayout extends FrameLayout {

    private final int touchSlop;
    private float downX, downY, startTx;
    private boolean dragging;

    public SwipeRevealLayout(Context context) {
        this(context, null);
    }

    public SwipeRevealLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeRevealLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private View foreground() {
        return getChildAt(getChildCount() - 1);
    }

    private float maxReveal() {
        return getChildCount() > 1 ? getChildAt(0).getWidth() : 0f;
    }

    private boolean isHorizontalDrag(MotionEvent ev) {
        float dx = ev.getX() - downX;
        float dy = ev.getY() - downY;
        return Math.abs(dx) > touchSlop && Math.abs(dx) > Math.abs(dy) * 1.5f;
    }

    private void claimTouch() {
        dragging = true;
        ViewParent p = getParent();
        if (p != null) p.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                startTx = foreground().getTranslationX();
                dragging = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!dragging && isHorizontalDrag(ev)) claimTouch();
                break;
            default:
                break;
        }
        return dragging;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!dragging && isHorizontalDrag(ev)) claimTouch();
                if (dragging) {
                    float t = startTx + (ev.getX() - downX);
                    foreground().setTranslationX(Math.max(-maxReveal(), Math.min(0f, t)));
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (dragging) settle();
                else if (foreground().getTranslationX() != 0f) close();
                dragging = false;
                return true;
            default:
                return super.onTouchEvent(ev);
        }
    }

    private void settle() {
        float open = -maxReveal();
        float target = foreground().getTranslationX() < open / 2f ? open : 0f;
        foreground().animate().translationX(target).setDuration(160).start();
    }

    public void close() {
        foreground().animate().translationX(0f).setDuration(160).start();
    }
}

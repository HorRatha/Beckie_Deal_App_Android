package aeu.ratha.beckie_deal_app;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * UiAnimations
 *
 * Small shared helpers for the two animation patterns used across the app's
 * static pages (header/forms/cards/buttons):
 *
 *   1. Entrance — fade + slide/scale a view (or stagger a group of views)
 *      into place when a screen first appears.
 *   2. Press bounce — a subtle scale-down/overshoot-back on touch, layered
 *      on top of whatever ripple/click behaviour a view already has.
 *
 * IMPORTANT: Per product requirements, product cards / product grids
 * (ProductGridAdapter, ProductCardBinder, any RecyclerView/GridLayout that
 * renders product listings) must NEVER be passed to these helpers. Only
 * apply these to static chrome around them — headers, toolbars, forms,
 * summary cards, empty states, filter rows, and standalone buttons.
 */
public final class UiAnimations {

    private UiAnimations() {}

    private static final long DEFAULT_DURATION_MS = 280L;
    private static final long STAGGER_STEP_MS      = 40L;

    // ── Press bounce ──────────────────────────────────────────────────────

    /** Adds a bouncy scale-down/overshoot-back press effect to each view. */
    public static void pressBounce(View... views) {
        for (View v : views) {
            if (v == null) continue;
            v.setOnTouchListener((view, event) -> {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        view.animate()
                                .scaleX(0.96f)
                                .scaleY(0.96f)
                                .setDuration(100)
                                .setInterpolator(new DecelerateInterpolator())
                                .start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(220)
                                .setInterpolator(new OvershootInterpolator(3f))
                                .start();
                        break;
                    default:
                        break;
                }
                return false; // let the click listener / ripple still fire
            });
        }
    }

    // ── Entrance: fade + slide up ─────────────────────────────────────────

    /** Fades a single view in while sliding it up from below, after an optional delay. */
    public static void fadeSlideUp(View view, long startDelayMs) {
        if (view == null) return;
        float density = view.getResources().getDisplayMetrics().density;
        view.setAlpha(0f);
        view.setTranslationY(28f * density);
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(startDelayMs)
                .setDuration(DEFAULT_DURATION_MS)
                .setInterpolator(new DecelerateInterpolator(1.6f))
                .start();
    }

    /** Fades a single view in while sliding it down from above, after an optional delay. */
    public static void fadeSlideDown(View view, long startDelayMs) {
        if (view == null) return;
        float density = view.getResources().getDisplayMetrics().density;
        view.setAlpha(0f);
        view.setTranslationY(-24f * density);
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(startDelayMs)
                .setDuration(DEFAULT_DURATION_MS)
                .setInterpolator(new DecelerateInterpolator(1.6f))
                .start();
    }

    /** Staggers a list of views in (fade + slide up), each starting a bit after the last. */
    public static void staggerIn(long baseDelayMs, View... views) {
        for (int i = 0; i < views.length; i++) {
            fadeSlideUp(views[i], baseDelayMs + i * STAGGER_STEP_MS);
        }
    }

    /**
     * Staggers every direct child of a container in, top to bottom.
     * Use for a section's card list (settings rows, order cards, etc.) —
     * NEVER pass a RecyclerView/GridLayout that renders product cards.
     */
    public static void staggerChildrenIn(ViewGroup container, long baseDelayMs) {
        if (container == null) return;
        for (int i = 0; i < container.getChildCount(); i++) {
            fadeSlideUp(container.getChildAt(i), baseDelayMs + i * STAGGER_STEP_MS);
        }
    }

    // ── Entrance: pop (scale + fade, with overshoot) ──────────────────────

    /** Pops a view in with a scale + fade and a slight overshoot — good for avatars/icons. */
    public static void popIn(View view, long startDelayMs) {
        if (view == null) return;
        view.setAlpha(0f);
        view.setScaleX(0.4f);
        view.setScaleY(0.4f);
        view.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(startDelayMs)
                .setDuration(320)
                .setInterpolator(new OvershootInterpolator(2.2f))
                .start();
    }
}

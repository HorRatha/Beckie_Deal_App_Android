package aeu.ratha.beckie_deal_app.utils.animations;

import android.animation.ValueAnimator;
import android.os.Build;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/** Smoothly swaps product containers between grid and list layouts. */
public final class ViewModeAnimator {
    private ViewModeAnimator() {}

    public static void transform(View container, View toggle, View icon, Runnable swapLayout) {
        if (container == null || !container.isLaidOut() || !container.isShown()
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && !ValueAnimator.areAnimatorsEnabled())) {
            swapLayout.run();
            return;
        }

        if (toggle != null) toggle.setEnabled(false);
        container.animate().cancel();
        if (icon != null) {
            icon.animate().cancel();
            icon.animate().alpha(0f).rotation(-70f).scaleX(0.75f).scaleY(0.75f)
                    .setDuration(120L).setInterpolator(new AccelerateInterpolator()).start();
        }

        container.animate().alpha(0.08f).scaleX(0.985f).scaleY(0.985f)
                .setDuration(120L).setInterpolator(new AccelerateInterpolator()).withLayer()
                .withEndAction(() -> {
                    swapLayout.run();
                    float offset = 6f * container.getResources().getDisplayMetrics().density;
                    container.setAlpha(0.08f);
                    container.setScaleX(1.015f);
                    container.setScaleY(1.015f);
                    container.setTranslationY(offset);
                    if (icon != null) {
                        icon.animate().cancel();
                        icon.setAlpha(0f);
                        icon.setRotation(70f);
                        icon.setScaleX(0.75f);
                        icon.setScaleY(0.75f);
                        icon.animate().alpha(1f).rotation(0f).scaleX(1f).scaleY(1f)
                                .setDuration(260L).setInterpolator(new OvershootInterpolator(1.4f)).start();
                    }
                    container.post(() -> container.animate().alpha(1f).scaleX(1f).scaleY(1f)
                            .translationY(0f).setDuration(260L)
                            .setInterpolator(new DecelerateInterpolator(1.8f)).withLayer()
                            .withEndAction(() -> { if (toggle != null) toggle.setEnabled(true); }).start());
                }).start();
    }
}

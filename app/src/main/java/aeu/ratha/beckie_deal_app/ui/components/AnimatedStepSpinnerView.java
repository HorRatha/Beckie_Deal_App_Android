package aeu.ratha.beckie_deal_app.ui.components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Reusable Spinning Gradient Ring component for active step indicators.
 * 
 * Features:
 * - Continuous 360° spinning animation with smooth gradient
 * - Smoothly tracks and animates to different step circles
 * - Customizable gradient colors
 * - Low overhead, efficient rendering
 * 
 * Usage:
 * 1. Add to layout: <aeu.ratha.beckie_deal_app.ui.components.AnimatedStepSpinnerView ... />
 * 2. In Activity: stepSpinner.attachToStep(stepCircleView, true);
 */
public class AnimatedStepSpinnerView extends View {

    private Paint ringPaint;
    private RectF rectF = new RectF();
    private float currentRotation = 0f;
    private ValueAnimator rotationAnimator;

    // Gradient colors matching the sample (Pink to Orange / Gold)
    private int startColor = Color.parseColor("#F700A8");
    private int endColor = Color.parseColor("#FF8000");

    public AnimatedStepSpinnerView(Context context) {
        super(context);
        init();
    }

    public AnimatedStepSpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimatedStepSpinnerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initialize paint and start rotation animation
     */
    private void init() {
        setElevation(dpToPx(10f)); // High Z-index to stay on top of step circles

        ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(dpToPx(3.5f)); // Ring thickness
        ringPaint.setStrokeCap(Paint.Cap.ROUND);

        // Continuous spin animation (2.5 seconds per rotation)
        rotationAnimator = ValueAnimator.ofFloat(0f, 360f);
        rotationAnimator.setDuration(2500);
        rotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnimator.setInterpolator(new LinearInterpolator());

        rotationAnimator.addUpdateListener(animator -> {
            currentRotation = (float) animator.getAnimatedValue();
            invalidate(); // Triggers canvas redraw
        });

        rotationAnimator.start();
    }

    /**
     * Setup gradient shader when view size changes
     * Calculates stroke padding to ensure arc stays fully within view bounds
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Half stroke + 1dp padding so the stroke is fully drawn inside view bounds
        float strokePadding = (ringPaint.getStrokeWidth() / 2f) + dpToPx(1f);

        rectF.set(strokePadding, strokePadding, w - strokePadding, h - strokePadding);

        LinearGradient shader = new LinearGradient(
                0, 0, w, h,
                new int[]{startColor, endColor},
                null, Shader.TileMode.CLAMP
        );

        ringPaint.setShader(shader);
    }

    /**
     * Draw the spinning gradient arc
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        // Rotate around view center
        canvas.rotate(currentRotation, getWidth() / 2f, getHeight() / 2f);

        // Draw 260-degree arc for spinning glow effect
        canvas.drawArc(rectF, 0, 260, false, ringPaint);

        canvas.restore();
    }

    /**
     * Attaches and aligns this spinner around any target step circle view.
     * Smoothly animates to the new position if animate is true.
     *
     * Uses getLocationOnScreen() for accurate positioning regardless of parent margins
     * or nested layout groups. Converts absolute screen coords to parent-relative coords.
     *
     * @param targetView The step circle view (e.g., step1Circle or step2Circle)
     * @param animate    If true, smoothly slides to the new target step circle
     */
    public void attachToStep(final View targetView, boolean animate) {
        if (targetView == null) return;

        // Forces spinner to be drawn ON TOP of all other sibling views
        bringToFront();

        targetView.post(() -> {
            View parentView = (View) getParent();
            if (parentView == null) return;

            int[] targetLocation = new int[2];
            int[] parentLocation = new int[2];

            // Get exact screen coordinates (accounts for ALL parent margins/nesting)
            targetView.getLocationOnScreen(targetLocation);
            parentView.getLocationOnScreen(parentLocation);

            // Calculate relative position inside the parent container
            float relativeX = targetLocation[0] - parentLocation[0];
            float relativeY = targetLocation[1] - parentLocation[1];

            // Center the spinner perfectly over the target step circle
            float targetX = relativeX + (targetView.getWidth() - getWidth()) / 2f;
            float targetY = relativeY + (targetView.getHeight() - getHeight()) / 2f;

            if (animate) {
                animate().x(targetX)
                        .y(targetY)
                        .setDuration(350)
                        .start();
            } else {
                setX(targetX);
                setY(targetY);
            }
        });
    }

    /**
     * Change gradient colors programmatically for different screens if needed
     *
     * @param startColor The starting color of the gradient
     * @param endColor   The ending color of the gradient
     */
    public void setGradientColors(int startColor, int endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
        onSizeChanged(getWidth(), getHeight(), 0, 0);
        invalidate();
    }

    /**
     * Stop the spinning animation (useful for cleanup)
     */
    public void stopAnimation() {
        if (rotationAnimator != null && rotationAnimator.isRunning()) {
            rotationAnimator.cancel();
        }
    }

    /**
     * Resume the spinning animation if it was stopped
     */
    public void startAnimation() {
        if (rotationAnimator != null && !rotationAnimator.isRunning()) {
            rotationAnimator.start();
        }
    }

    /**
     * Convert dp to pixels
     */
    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }
}

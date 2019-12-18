package com.dev.mffa.mynote.Lib;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Chatikyan on 13.10.2016.
 */
class SpaceNavigationViewBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {

    public SpaceNavigationViewBehavior(Context context, AttributeSet attrs) {
        super();
    }

    public SpaceNavigationViewBehavior() {
        super();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, V child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, final V child, View dependency) {
        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        child.setTranslationY(translationY);
        return true;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V child, View directTargetChild, View target, int nestedScrollAxes) {
        // Ensure we react to vertical scrolling
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final V child,
                               final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyConsumed > 0) {
            Utils.makeTranslationYAnimation(child, child.getHeight());
        } else if (dyConsumed < 0) {
            Utils.makeTranslationYAnimation(child, 0);
        }
    }

    static class Utils {

        /**
         * Change given image view tint
         *
         * @param imageView target image view
         * @param color     tint color
         */
        static void changeImageViewTint(ImageView imageView, int color) {
            imageView.setColorFilter(color);
        }

        /**
         * Change view visibility
         *
         * @param view target view
         */
        static void changeViewVisibilityGone(View view) {
            if (view != null && view.getVisibility() == View.VISIBLE)
                view.setVisibility(View.GONE);
        }

        /**
         * Change view visibility
         *
         * @param view target view
         */
        static void changeViewVisibilityVisible(View view) {
            if (view != null && view.getVisibility() == View.GONE)
                view.setVisibility(View.VISIBLE);
        }

        /**
         * Change given image view tint with animation
         *
         * @param image     target image view
         * @param fromColor start animation from color
         * @param toColor   final color
         */
        static void changeImageViewTintWithAnimation(final ImageView image, int fromColor, int toColor) {
            ValueAnimator imageTintChangeAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), fromColor, toColor);
            imageTintChangeAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    image.setColorFilter((Integer) animator.getAnimatedValue());
                }
            });
            imageTintChangeAnimation.setDuration(150);
            imageTintChangeAnimation.start();
        }

        static void makeTranslationYAnimation(View view, float value) {
            view.animate()
                    .translationY(value)
                    .setDuration(150)
                    .start();
        }

        // TODO: 15.08.2016 add ripple effect programmatically
        @TargetApi(21)
        static RippleDrawable getPressedColorRippleDrawable(int normalColor, int pressedColor) {
            return new RippleDrawable(getPressedColorSelector(normalColor, pressedColor), new ColorDrawable(normalColor), null);
        }

        private static ColorStateList getPressedColorSelector(int normalColor, int pressedColor) {
            return new ColorStateList(
                    new int[][]
                            {
                                    new int[]{android.R.attr.state_pressed},
                                    new int[]{android.R.attr.state_focused},
                                    new int[]{android.R.attr.state_activated},
                                    new int[]{}
                            },
                    new int[]
                            {
                                    pressedColor,
                                    pressedColor,
                                    pressedColor,
                                    normalColor
                            }
            );
        }
    }
}

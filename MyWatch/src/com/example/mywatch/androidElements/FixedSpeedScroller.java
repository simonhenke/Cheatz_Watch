package com.example.mywatch.androidElements;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * This class is used to overwrite the Scroller of the ViewPager (switches Pages very fast)
 * A duration of 1 second is better to be aware of a chapter change
 * @author Simon
 *
 */
public class FixedSpeedScroller extends Scroller {

   private int mDuration = 500;

    public FixedSpeedScroller(Context context) {
        super(context);
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }


    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
}
package com.simonk.project.prettyrss.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * Created by simon on 13.10.2017.
 */

public class RevealFrameLayout extends FrameLayout {

    private double mAnimationDuration = 300.0;
    private long mFirstTimestamp = -1;
    private float mMaxRadius;

    private int mLatency = 0;

    private final Path mClippingPath = new Path();

    private int mLayerTypeBeforeAnimating;

    private boolean mOpenAnimation = false;
    private boolean mHideAnimation = false;
    private boolean mAnimating;
    private boolean mNeedTurnAround;
    private boolean mNeedAnimation = false;
    private boolean mNeedAnimationHideOnly = false;
    private Interpolator mInterpolator;
    private boolean isOpenAnimationChild;

    public RevealFrameLayout(@NonNull Context context) {
        super(context);
    }

    public RevealFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RevealFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RevealFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void shouldAnimate(boolean needAnimation) {
        mNeedAnimation = needAnimation;
    }

    public void shouldAnimateHideOnly(boolean needAnimateHideOnly) {
        mNeedAnimationHideOnly = needAnimateHideOnly;
    }

    public void open() {
        if (mLatency == 0) {
            openInternal();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLatency = 0;
                    openInternal();
                }
            }, mLatency);
        }
    }

    public void openInternal() {
        if (mNeedAnimation && !mNeedAnimationHideOnly) {
            if (mAnimating && mOpenAnimation)
                return;

            if (mAnimating && mHideAnimation)
                mNeedTurnAround = true;

            mOpenAnimation = true;
            mHideAnimation = false;
            isOpenAnimationChild = true;
        }

        setVisibility(VISIBLE);
    }

    public void setLatency(int latency) {
        mLatency = latency;
    }

    public int getOpenLatency() {
        return mLatency;
    }

    public void hide() {
        if (mLatency == 0) {
            hideInternal();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLatency = 0;
                    hideInternal();
                }
            }, mLatency);
        }
    }

    private void hideInternal() {
        if (mNeedAnimation || mNeedAnimationHideOnly) {
            if (mAnimating && mHideAnimation)
                return;

            if (mAnimating && mOpenAnimation)
                mNeedTurnAround = true;

            mHideAnimation = true;
            mOpenAnimation = false;
            isOpenAnimationChild = true;

            invalidate();
        } else {
            setVisibility(GONE);
        }
    }

    private void prepareBeforeAnimating() {
        if (mOpenAnimation) {
            mInterpolator = new AccelerateDecelerateInterpolator();
        } else {
            mInterpolator = new AccelerateDecelerateInterpolator();
        }

        mMaxRadius = (float) Math.sqrt(Math.pow((getWidth() / 2), 2) + Math.pow(getHeight(), 2));

        mLayerTypeBeforeAnimating = getChildAt(0).getLayerType();
        getChildAt(0).setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {

        if (!mOpenAnimation && !mHideAnimation) {
            return super.drawChild(canvas, child, drawingTime);
        }

        if (!isOpenAnimationChild) {
            return super.drawChild(canvas, child, drawingTime);
        }

        mAnimating = true;

        if (mFirstTimestamp == -1) {
            prepareBeforeAnimating();

            mFirstTimestamp = SystemClock.uptimeMillis();
            postInvalidate();

            if (mHideAnimation) {
                return super.drawChild(canvas, child, drawingTime);
            } else {
                return false;
            }
        }

        long animationTime = SystemClock.uptimeMillis() - mFirstTimestamp;

        if (mNeedTurnAround) {
            mFirstTimestamp = SystemClock.uptimeMillis() - ((long) mAnimationDuration - animationTime);
            animationTime = SystemClock.uptimeMillis() - mFirstTimestamp;
            mNeedTurnAround = false;
        }

        if (animationTime < mAnimationDuration) {
            float animationProgress = (float) (animationTime / mAnimationDuration);
            animationProgress = mInterpolator.getInterpolation(animationProgress);
            if (mHideAnimation) {
                animationProgress = 1 - animationProgress;
            }

            clipCanvas(canvas, animationProgress);
        } else {
            mFirstTimestamp = -1;

            isOpenAnimationChild = false;
            getChildAt(0).setLayerType(mLayerTypeBeforeAnimating, null);

            if (mHideAnimation) {
                mHideAnimation = false;
                mOpenAnimation = false;
                mAnimating = false;

                setVisibility(GONE);
                return false;
            }

            mHideAnimation = false;
            mOpenAnimation = false;
            mAnimating = false;
        }

        return super.drawChild(canvas, child, drawingTime);
    }

    private void clipCanvas(Canvas canvas, float animationProgress) {
        float centerX = getWidth() / 2;
        float centerY = getHeight();
        float radius = mMaxRadius * animationProgress;

        mClippingPath.reset();

        mClippingPath.addCircle(centerX, centerY, radius, Path.Direction.CW);
        canvas.clipPath(mClippingPath);

        postInvalidate();
    }
}

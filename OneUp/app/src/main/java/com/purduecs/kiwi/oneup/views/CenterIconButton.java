package com.purduecs.kiwi.oneup.views;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import com.purduecs.kiwi.oneup.R;

public class CenterIconButton extends ToggleButton {

    private static final int[] STATE_PAST_LIKED = {R.attr.state_past_liked};

    private static final int LEFT = 0, TOP = 1, RIGHT = 2, BOTTOM = 3;

    // Pre-allocate objects for layout measuring
    private Rect textBounds = new Rect();
    private Rect drawableBounds = new Rect();

    private boolean mIsPastLiked = false;

    public CenterIconButton(Context context) {
        this(context, null);
    }

    public CenterIconButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.buttonStyle);
    }

    public CenterIconButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPastLiked(boolean isPastLiked) {
        mIsPastLiked = isPastLiked;
        refreshDrawableState();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (mIsPastLiked) {
            mergeDrawableStates(drawableState, STATE_PAST_LIKED);
        }
        return drawableState;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (!changed) return;

        final CharSequence text = getText();
        if (!TextUtils.isEmpty(text)) {
            TextPaint textPaint = getPaint();
            textPaint.getTextBounds(text.toString(), 0, text.length(), textBounds);
        } else {
            textBounds.setEmpty();
        }

        final int width = getWidth() - (getPaddingLeft() + getPaddingRight());

        final Drawable[] drawables = getCompoundDrawables();

        if (drawables[LEFT] != null) {
            drawables[LEFT].copyBounds(drawableBounds);
            int leftOffset =
                    (width - (textBounds.width() + drawableBounds.width()) + getRightPaddingOffset()) / 2 - getCompoundDrawablePadding();
            drawableBounds.offset(leftOffset, 0);
            drawables[LEFT].setBounds(drawableBounds);
        }

        if (drawables[RIGHT] != null) {
            drawables[RIGHT].copyBounds(drawableBounds);
            int rightOffset =
                    ((textBounds.width() + drawableBounds.width()) - width + getLeftPaddingOffset()) / 2 + getCompoundDrawablePadding();
            drawableBounds.offset(rightOffset, 0);
            drawables[RIGHT].setBounds(drawableBounds);
        }
    }
}
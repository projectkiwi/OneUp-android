package com.purduecs.kiwi.oneup.views;

import android.content.Context;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
        import android.view.View;

import com.purduecs.kiwi.oneup.R;

public class WinnerWedgeView extends View {

    int mColor;

    public WinnerWedgeView(Context context) {
        super(context);
        init();
    }

    public WinnerWedgeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public WinnerWedgeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mColor = ((ColorDrawable)this.getBackground()).getColor();
        this.setBackground(new ColorDrawable(Color.TRANSPARENT));
    }

    public void setColor(int place) {
        switch (place) {
            case 1:
                mColor = getContext().getResources().getColor(R.color.firstGold);
                break;
            case 2:
                mColor = getContext().getResources().getColor(R.color.secondSilver);
                break;
            case 3:
                mColor = getContext().getResources().getColor(R.color.thirdBronze);
                break;
            default:
                mColor = Color.TRANSPARENT;
                break;
        }
        this.invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mColor == Color.TRANSPARENT) return;

        int w = getWidth();

        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(w, 0);
        path.lineTo(0, w);
        path.lineTo(0, 0);
        path.close();

        Paint p = new Paint();
        p.setColor(mColor);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTextSize(30);

        canvas.drawPath(path, p);

        float t = (float)w/16.0f;
        float r = (float)w/4.0f - t;
        float x = (float)w/4.0f + t;
        float y = x;
        int count = 0;
        Path med = new Path();
        med.moveTo(w/2, w/4);
        for (float i = 0; i < 2*Math.PI; i+=.28889) {
            count++;
            float real_r = (r+ (count%2)*t);
            med.lineTo(x+(float)(real_r*Math.cos(i)), y+(float)(real_r*Math.sin(i)));
        }
        med.lineTo(w / 2, w / 4);

        Paint p2 = new Paint();

        float[] hsv = new float[3];
        int color = mColor;
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // value component
        hsv[2] = 1.0f - 0.8f * (1.0f - hsv[2]);
        color = Color.HSVToColor(hsv);

        p2.setColor(color);

        canvas.drawPath(med, p2);
    }
}

package com.example.roleta_mobile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import androidx.annotation.Nullable;

public class RouletteView extends View {

    private Paint paint;
    private int[] colors = new int[] { Color.parseColor("#05DBF3"), Color.parseColor("#F21A05")};
    private int sections = 6;

    public RouletteView(Context context) {
        super(context);
        init();
    }

    public RouletteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2;
        int cx = width / 2;
        int cy = height / 2;

        for (int i = 0; i < sections; i++) {
            paint.setColor(colors[i % colors.length]);
            canvas.drawArc(cx - radius, cy - radius, cx + radius, cy + radius,
                    (i * (360 / sections)), (360 / sections), true, paint);
        }
    }
    public void rotateRoulette(int duration) {
        RotateAnimation rotateAnim = new RotateAnimation(0f, 360f * 5,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setInterpolator(new LinearInterpolator());
        rotateAnim.setDuration(duration);
        rotateAnim.setRepeatCount(0);
        rotateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        startAnimation(rotateAnim);
    }
}

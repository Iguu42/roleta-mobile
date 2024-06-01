package com.example.roleta;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;

import androidx.annotation.Nullable;

public class RouletteView extends View {

    private Paint paint;
    private int[] colors = new int[] { Color.parseColor("#05DBF3"), Color.parseColor("#6805F3"), Color.parseColor("#FFD700")};
    private int sections = 6;
    private String[] sectionNames = {"", "", ""};
    private int finalAngle;
    private RouletteListener listener;

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
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2;
        int cx = width / 2;
        int cy = height / 2;

        float angleStep = 360f / sections;
        float textAngleDegrees;

        for (int i = 0; i < sections; i++) {
            paint.setColor(colors[i % colors.length]);
            float startAngle = i * angleStep;
            canvas.drawArc(cx - radius, cy - radius, cx + radius, cy + radius,
                    startAngle, angleStep, true, paint);

            textAngleDegrees = startAngle + angleStep / 2;
            paint.setColor(Color.WHITE);

            canvas.save();
            canvas.translate(cx + radius * 0.75f * (float) Math.cos(Math.toRadians(textAngleDegrees)),
                    cy + radius * 0.75f * (float) Math.sin(Math.toRadians(textAngleDegrees)));
            canvas.rotate(textAngleDegrees + 90);
            canvas.drawText(sectionNames[i % sectionNames.length], 0, 0, paint);
            canvas.restore();
        }
    }

    public void setSections(String[] sections) {
        this.sectionNames = sections;
        this.sections = sections.length * 2;
    }

    public void rotateRoulette(int duration) {
        finalAngle = (int) (360f * 5 + Math.random() * 360);
        RotateAnimation rotateAnim = new RotateAnimation(0f, finalAngle,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setInterpolator(new DecelerateInterpolator());
        rotateAnim.setDuration(duration);
        rotateAnim.setFillAfter(true);
        rotateAnim.setRepeatCount(0);

        rotateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                displaySelectedSection();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        startAnimation(rotateAnim);
    }

    private void displaySelectedSection() {
        int anglePerSection = 360 / sections;
        int normalizedAngle = finalAngle % 360;
        int selectedSection = (normalizedAngle + anglePerSection / 2) / anglePerSection;
        selectedSection = sections + 1 - selectedSection;

        if (selectedSection >= sectionNames.length) {
            selectedSection = selectedSection % sectionNames.length;
        }

        String selectedText = sectionNames[selectedSection];
        if (listener != null) {
            listener.onSectionSelected(selectedText);
        }
    }

    public void setRouletteListener(RouletteListener listener) {
        this.listener = listener;
    }

    public interface RouletteListener {
        void onSectionSelected(String selectedSection);
    }
}

package com.example.roleta;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import androidx.annotation.Nullable;

public class RouletteView extends View {

    private Paint paint;
    private int[] colors = new int[] { Color.parseColor("#05DBF3"), Color.parseColor("#F21A05")}; // Cores das seções
    private int sections = 6; // Número de seções na roleta

    public RouletteView(Context context) {
        super(context);
        init();
    }

    public RouletteView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private String[] sectionNames = {"Filme 1", "Filme 2"}; // Adicione mais nomes conforme necessário

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(30); // Ajuste o tamanho do texto conforme necessário
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

        float angleStep = 360 / sections;
        float textAngleDegrees;

        for (int i = 0; i < sections; i++) {
            paint.setColor(colors[i % colors.length]);
            float startAngle = i * angleStep;
            canvas.drawArc(cx - radius, cy - radius, cx + radius, cy + radius,
                    startAngle, angleStep, true, paint);

            // Ajustar para o ângulo de desenho do texto
            textAngleDegrees = startAngle + angleStep / 2;
            paint.setColor(Color.WHITE); // Define a cor do texto

            // Salvar o estado do canvas
            canvas.save();
            // Translate para o ponto onde o texto será desenhado
            canvas.translate(cx + radius * 0.75f * (float) Math.cos(Math.toRadians(textAngleDegrees)),
                    cy + radius * 0.75f * (float) Math.sin(Math.toRadians(textAngleDegrees)));
            // Rotacionar o canvas para alinhar o texto verticalmente
            canvas.rotate(textAngleDegrees + 90); // +90 para alinhar verticalmente
            // Desenhar o texto
            canvas.drawText(sectionNames[i % sectionNames.length], 0, 0, paint);
            // Restaurar o estado do canvas
            canvas.restore();
        }
    }


    public void rotateRoulette(int duration) {
        // Gera um número aleatório para o ângulo de parada
        int finalAngle = (int) (360f * 5 + Math.random() * 360);
        RotateAnimation rotateAnim = new RotateAnimation(0f, finalAngle,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setInterpolator(new DecelerateInterpolator());
        rotateAnim.setDuration(duration);
        rotateAnim.setFillAfter(true); // Importante para manter a vista na posição final
        rotateAnim.setRepeatCount(0);

        rotateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Código que será executado quando a rotação terminar
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        startAnimation(rotateAnim);
    }

}

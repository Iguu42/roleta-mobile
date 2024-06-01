package com.example.roleta;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PlayRouletteFragment extends Fragment implements SensorEventListener {

    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long shakeTimestamp;
    private int shakeCount;

    private RouletteView rouletteView;
    private ImageView spinButton;
    private ImageView shareButton;
    private ImageView editButton;
    private TextView titleTextView;
    private Vibrator vibrator;
    private String rouletteId;

    public PlayRouletteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_roulette, container, false);

        rouletteView = view.findViewById(R.id.rvHome1);
        spinButton = view.findViewById(R.id.imageViewPlay);
        shareButton = view.findViewById(R.id.imageViewShare);
        editButton = view.findViewById(R.id.imageViewEditRoulette);
        titleTextView = view.findViewById(R.id.textViewTitlePlay);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        spinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateRoulette();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareScreenshot();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditRouletteFragment();
            }
        });

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        fetchLatestRoulette();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        fetchLatestRoulette(); // Atualiza as informações da roleta quando o fragmento volta a ser visível
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement.
            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                final long now = System.currentTimeMillis();
                // ignore shake events too close to each other (500ms)
                if (shakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return;
                }

                // reset the shake count after 3 seconds of no shakes
                if (shakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    shakeCount = 0;
                }

                shakeTimestamp = now;
                shakeCount++;

                rotateRoulette();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore
    }

    private void rotateRoulette() {
        // Gira a roleta
        int finalAngle = (int) (360f * 5 + Math.random() * 360);
        RotateAnimation rotateAnim = new RotateAnimation(0f, finalAngle,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setInterpolator(new DecelerateInterpolator());
        rotateAnim.setDuration(6000);
        rotateAnim.setFillAfter(true); // Importante para manter a vista na posição final
        rotateAnim.setRepeatCount(0);

        rotateAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Nada
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Vibrar ao terminar a rotação
                if (vibrator != null) {
                    vibrator.vibrate(500); // Vibrar por 500 milissegundos
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Nada
            }
        });

        rouletteView.startAnimation(rotateAnim);
    }

    private void fetchLatestRoulette() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("roletas");
        query.addDescendingOrder("createdAt");
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && !objects.isEmpty()) {
                    ParseObject latestRoulette = objects.get(0);
                    rouletteId = latestRoulette.getObjectId();
                    String title = latestRoulette.getString("titulo");
                    titleTextView.setText(title); // Atualiza o título dinamicamente
                    fetchOptions(latestRoulette);
                } else {
                    Toast.makeText(getActivity(), "Erro ao buscar roleta: " + (e != null ? e.getMessage() : "Nenhuma roleta encontrada"), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void fetchOptions(ParseObject roulette) {
        ParseRelation<ParseObject> relation = roulette.getRelation("opcoes");
        ParseQuery<ParseObject> query = relation.getQuery();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    String[] options = new String[objects.size()];
                    for (int i = 0; i < objects.size(); i++) {
                        options[i] = objects.get(i).getString("opcao");
                    }
                    updateRouletteView(options);
                } else {
                    Toast.makeText(getActivity(), "Erro ao buscar opções: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateRouletteView(String[] options) {
        rouletteView.setSections(options);
        rouletteView.invalidate(); // Redesenha a roleta com as novas opções
    }

    private void openEditRouletteFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("ROULETTE_ID", rouletteId);
        EditRouletteFragment editRouletteFragment = new EditRouletteFragment();
        editRouletteFragment.setArguments(bundle);
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.replaceFragment(editRouletteFragment);
        }
    }

    private void shareScreenshot() {
        // Capturar a tela do fragmento
        View rootView = getView();
        if (rootView == null) return;
        rootView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);

        // Salvar a captura de tela
        File cachePath = new File(getContext().getCacheDir(), "images");
        cachePath.mkdirs();
        File file = new File(cachePath, "image.png");
        try (FileOutputStream stream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (IOException e) {
            Log.e("PlayRouletteFragment", "Erro ao salvar a captura de tela", e);
            return;
        }

        // Compartilhar a captura de tela
        Uri contentUri = FileProvider.getUriForFile(getContext(), "com.example.roleta.fileprovider", file);
        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, getContext().getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(shareIntent, "Compartilhar via"));
        }
    }
}

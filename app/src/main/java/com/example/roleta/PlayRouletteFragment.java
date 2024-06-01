package com.example.roleta;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PlayRouletteFragment extends Fragment implements SensorEventListener, RouletteView.RouletteListener {

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

        rouletteView.setRouletteListener(this);

        spinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rouletteView.rotateRoulette(6000);
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

        Bundle args = getArguments();
        if (args != null && args.containsKey("ROULETTE_ID")) {
            rouletteId = args.getString("ROULETTE_ID");
            fetchRouletteById(rouletteId);
        } else {
            fetchLatestRoulette();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        if (rouletteId != null) {
            fetchRouletteById(rouletteId);
        } else {
            fetchLatestRoulette();
        }
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

            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                final long now = System.currentTimeMillis();
                if (shakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return;
                }

                if (shakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    shakeCount = 0;
                }

                shakeTimestamp = now;
                shakeCount++;

                rouletteView.rotateRoulette(6000);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
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
                    titleTextView.setText(title);
                    fetchOptions(latestRoulette);
                } else {
                    Toast.makeText(getActivity(), "Erro ao buscar roleta: " + (e != null ? e.getMessage() : "Nenhuma roleta encontrada"), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void fetchRouletteById(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("roletas");
        query.getInBackground(id, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject roulette, ParseException e) {
                if (e == null) {
                    String title = roulette.getString("titulo");
                    titleTextView.setText(title);
                    fetchOptions(roulette);
                } else {
                    Toast.makeText(getActivity(), "Erro ao buscar roleta: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
        rouletteView.invalidate();
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
        View rootView = getView();
        if (rootView == null) return;
        rootView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);

        File cachePath = new File(getContext().getCacheDir(), "images");
        cachePath.mkdirs();
        File file = new File(cachePath, "image.png");
        try (FileOutputStream stream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (IOException e) {
            Log.e("PlayRouletteFragment", "Erro ao salvar a captura de tela", e);
            return;
        }

        Uri contentUri = FileProvider.getUriForFile(getContext(), "com.example.roleta.fileprovider", file);
        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setDataAndType(contentUri, getContext().getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(shareIntent, "Compartilhar via"));
        }
    }

    private void showResultDialog(String selectedSection) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Resultado");
        builder.setMessage(selectedSection);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onSectionSelected(String selectedSection) {
        showResultDialog(selectedSection);
    }
}

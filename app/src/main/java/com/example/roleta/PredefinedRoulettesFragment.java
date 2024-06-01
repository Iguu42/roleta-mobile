package com.example.roleta;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.List;

public class PredefinedRoulettesFragment extends Fragment {

    private GridLayout roulettesContainer;
    private static final String TAG = "PredefinedRoulettesFragment";

    public PredefinedRoulettesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_predefined_roulettes, container, false);

        roulettesContainer = view.findViewById(R.id.predefinedRoulettesContainer);

        fetchRoulettes();

        return view;
    }

    private void fetchRoulettes() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("roletas");
        query.addDescendingOrder("createdAt");
        query.setLimit(8);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && !objects.isEmpty()) {
                    for (ParseObject roulette : objects) {
                        addRouletteToView(roulette);
                    }
                } else {
                    Log.e(TAG, "Erro ao buscar roletas: " + (e != null ? e.getMessage() : "Nenhuma roleta encontrada"));
                    Toast.makeText(getActivity(), "Erro ao buscar roletas: " + (e != null ? e.getMessage() : "Nenhuma roleta encontrada"), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @SuppressLint("NewApi")
    private void addRouletteToView(ParseObject roulette) {
        String title = roulette.getString("titulo");

        RouletteView rouletteView = new RouletteView(getContext());
        GridLayout.LayoutParams rouletteParams = new GridLayout.LayoutParams();
        rouletteParams.width = 200;
        rouletteParams.height = 200;
        rouletteParams.setMargins(16, 16, 16, 8);
        rouletteView.setLayoutParams(rouletteParams);
        roulettesContainer.addView(rouletteView);

        TextView titleView = new TextView(getContext());
        titleView.setText(title);
        titleView.setTextColor(Color.WHITE);
        titleView.setTextSize(16);
        titleView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        try {
            titleView.setTypeface(getResources().getFont(R.font.inter_semibold));
        } catch (Exception e) {
            Log.e(TAG, "Erro ao carregar a fonte: " + e.getMessage());
        }
        GridLayout.LayoutParams titleParams = new GridLayout.LayoutParams();
        titleParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
        titleParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        titleParams.setMargins(16, 8, 16, 16);
        titleParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
        titleParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
        titleView.setLayoutParams(titleParams);
        roulettesContainer.addView(titleView);

        rouletteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlayRouletteFragment(roulette);
            }
        });

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
                    rouletteView.setSections(options);
                    rouletteView.invalidate();
                } else {
                    Log.e(TAG, "Erro ao buscar opções: " + e.getMessage());
                    Toast.makeText(getActivity(), "Erro ao buscar opções: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void openPlayRouletteFragment(ParseObject roulette) {
        PlayRouletteFragment playRouletteFragment = new PlayRouletteFragment();
        Bundle args = new Bundle();
        args.putString("ROULETTE_ID", roulette.getObjectId());
        playRouletteFragment.setArguments(args);
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.replaceFragment(playRouletteFragment);
        }
    }
}

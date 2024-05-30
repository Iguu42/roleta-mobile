package com.example.roleta;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.List;

public class PlayRouletteFragment extends Fragment {

    private RouletteView rouletteView;
    private ImageView spinButton;

    public PlayRouletteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_roulette, container, false);

        rouletteView = view.findViewById(R.id.rouletteView4);
        spinButton = view.findViewById(R.id.imageView);

        spinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rouletteView.rotateRoulette(6000);
            }
        });

        fetchLatestRoulette();

        return view;
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
}

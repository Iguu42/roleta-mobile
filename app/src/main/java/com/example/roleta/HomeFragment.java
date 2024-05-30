package com.example.roleta;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.List;

public class HomeFragment extends Fragment {

    private RouletteView rouletteView1;
    private RouletteView rouletteView2;
    private RouletteView rouletteView3;
    private RouletteView rouletteView4;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button btPredRoulettes = view.findViewById(R.id.btPredRoulettes);
        Button btCreateRoulettes = view.findViewById(R.id.btCreateRoulettes);

        btPredRoulettes.setOnClickListener(view1 -> handleButtonClick(R.id.roulletes));
        btCreateRoulettes.setOnClickListener(view1 -> handleButtonClick(R.id.store));

        rouletteView1 = view.findViewById(R.id.rvHome1);
        rouletteView2 = view.findViewById(R.id.rvHome2);
        rouletteView3 = view.findViewById(R.id.rvHome3);
        rouletteView4 = view.findViewById(R.id.rvHome4);

        textView1 = view.findViewById(R.id.textVRHome1);
        textView2 = view.findViewById(R.id.textVRHome2);
        textView3 = view.findViewById(R.id.textVRHome3);
        textView4 = view.findViewById(R.id.textVRHome4);

        fetchRoulettes();

        return view;
    }

    private void fetchRoulettes() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("roletas");
        query.addDescendingOrder("createdAt");
        query.setLimit(4);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && !objects.isEmpty()) {
                    if (objects.size() > 0) updateRouletteView(rouletteView1, textView1, objects.get(0));
                    if (objects.size() > 1) updateRouletteView(rouletteView2, textView2, objects.get(1));
                    if (objects.size() > 2) updateRouletteView(rouletteView3, textView3, objects.get(2));
                    if (objects.size() > 3) updateRouletteView(rouletteView4, textView4, objects.get(3));
                } else {
                    Toast.makeText(getActivity(), "Erro ao buscar roletas: " + (e != null ? e.getMessage() : "Nenhuma roleta encontrada"), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateRouletteView(RouletteView rouletteView, TextView textView, ParseObject roulette) {
        String title = roulette.getString("titulo");
        textView.setText(title);

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
                    Toast.makeText(getActivity(), "Erro ao buscar opções: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void handleButtonClick(int menuItemId) {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.replaceFragment(new HomeFragment());
            mainActivity.binding.bottomNavigationView.setSelectedItemId(menuItemId);
        }
    }
}

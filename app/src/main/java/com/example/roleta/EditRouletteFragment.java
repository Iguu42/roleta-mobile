package com.example.roleta;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.SaveCallback;

public class EditRouletteFragment extends Fragment {

    private EditText editTextTitle;
    private EditText editTextOption1;
    private EditText editTextOption2;
    private EditText editTextOption3;
    private Button updateButton;
    private String rouletteId;

    public EditRouletteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rouletteId = getArguments().getString("ROULETTE_ID");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_roulette, container, false);

        editTextTitle = view.findViewById(R.id.editTextTituloEdit);
        editTextOption1 = view.findViewById(R.id.editTextOpcao1Edit);
        editTextOption2 = view.findViewById(R.id.editTextOpcao2Edit);
        editTextOption3 = view.findViewById(R.id.editTextOpcao3Edit);
        updateButton = view.findViewById(R.id.buttonAtualizar);

        fetchRouletteDetails();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRouletteInBack4App();
            }
        });

        return view;
    }

    private void fetchRouletteDetails() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("roletas");
        query.getInBackground(rouletteId, (object, e) -> {
            if (e == null) {
                editTextTitle.setText(object.getString("titulo"));
                ParseRelation<ParseObject> relation = object.getRelation("opcoes");
                ParseQuery<ParseObject> optionsQuery = relation.getQuery();
                optionsQuery.findInBackground((options, ex) -> {
                    if (ex == null && options.size() >= 3) {
                        editTextOption1.setText(options.get(0).getString("opcao"));
                        editTextOption2.setText(options.get(1).getString("opcao"));
                        editTextOption3.setText(options.get(2).getString("opcao"));
                    } else {
                        Toast.makeText(getActivity(), "Erro ao buscar opções: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Erro ao buscar roleta: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateRouletteInBack4App() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("roletas");
        query.getInBackground(rouletteId, (object, e) -> {
            if (e == null) {
                object.put("titulo", editTextTitle.getText().toString());
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            updateOptions(object);
                        } else {
                            Toast.makeText(getActivity(), "Erro ao atualizar roleta: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Erro ao buscar roleta: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void updateOptions(ParseObject roulette) {
        ParseRelation<ParseObject> relation = roulette.getRelation("opcoes");
        ParseQuery<ParseObject> optionsQuery = relation.getQuery();
        optionsQuery.findInBackground((options, e) -> {
            if (e == null && options.size() >= 3) {
                options.get(0).put("opcao", editTextOption1.getText().toString());
                options.get(1).put("opcao", editTextOption2.getText().toString());
                options.get(2).put("opcao", editTextOption3.getText().toString());
                options.get(0).saveInBackground();
                options.get(1).saveInBackground();
                options.get(2).saveInBackground();
                Toast.makeText(getActivity(), "Roleta atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(getActivity(), "Erro ao atualizar opções: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

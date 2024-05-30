package com.example.roleta;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.SaveCallback;

public class CreateRouletteFragment extends Fragment {

    private EditText editTextTitle;
    private EditText editTextOption1;
    private EditText editTextOption2;
    private EditText editTextOption3;
    private Button createButton;

    public CreateRouletteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_roulette, container, false);

        editTextTitle = view.findViewById(R.id.editTextTitulo);
        editTextOption1 = view.findViewById(R.id.editTextOpcao1);
        editTextOption2 = view.findViewById(R.id.editTextOpcao2);
        editTextOption3 = view.findViewById(R.id.editTextOpcao3);
        createButton = view.findViewById(R.id.buttonCriar);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString();
                String option1 = editTextOption1.getText().toString();
                String option2 = editTextOption2.getText().toString();
                String option3 = editTextOption3.getText().toString();

                if (title.isEmpty() || option1.isEmpty() || option2.isEmpty() || option3.isEmpty()) {
                    Toast.makeText(getActivity(), "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
                } else {
                    saveRouletteToBack4App(title, option1, option2, option3);
                }
            }
        });

        return view;
    }

    private void saveRouletteToBack4App(String title, String option1, String option2, String option3) {
        ParseObject roulette = new ParseObject("roletas");
        roulette.put("titulo", title);

        roulette.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    saveOptionsToBack4App(roulette, option1, option2, option3);
                } else {
                    Toast.makeText(getActivity(), "Erro ao criar roleta: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveOptionsToBack4App(ParseObject roulette, String option1, String option2, String option3) {
        ParseRelation<ParseObject> relation = roulette.getRelation("opcoes");

        saveOption(option1, relation, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    saveOption(option2, relation, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                saveOption(option3, relation, new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            roulette.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        Toast.makeText(getActivity(), "Roleta e opções criadas com sucesso!", Toast.LENGTH_SHORT).show();
                                                        handleButtonClick(R.id.play);
                                                    } else {
                                                        Toast.makeText(getActivity(), "Erro ao salvar roleta: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(getActivity(), "Erro ao salvar opção 3: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getActivity(), "Erro ao salvar opção 2: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Erro ao salvar opção 1: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveOption(String option, ParseRelation<ParseObject> relation, SaveCallback callback) {
        ParseObject optionObject = new ParseObject("opcoes");
        optionObject.put("opcao", option);
        optionObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    relation.add(optionObject);
                    callback.done(null);
                } else {
                    callback.done(e);
                }
            }
        });
    }

    private void handleButtonClick(int menuItemId) {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.replaceFragment(new PlayRouletteFragment());
            mainActivity.binding.bottomNavigationView.setSelectedItemId(menuItemId);
        }
    }
}

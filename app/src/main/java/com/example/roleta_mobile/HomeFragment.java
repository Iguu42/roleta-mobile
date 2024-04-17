package com.example.roleta_mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import com.example.roleta_mobile.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Button btPredRoulettes = view.findViewById(R.id.btPredRoulettes);
        Button btCreateRoulettes = view.findViewById(R.id.btCreateRoulettes);

        btPredRoulettes.setOnClickListener(view1 -> handleButtonClick(R.id.roulletes));
        btCreateRoulettes.setOnClickListener(view1 -> handleButtonClick(R.id.store));

        return view;
    }
    private void handleButtonClick(int menuItemId){
        MainActivity mainActivity = (MainActivity) getActivity();
        if(mainActivity != null) {
            mainActivity.replaceFragment(new HomeFragment());
            mainActivity.binding.bottomNavigationView.setSelectedItemId(menuItemId);
        }
    }
}
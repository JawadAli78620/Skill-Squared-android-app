package com.example.skillsquared2.ui.saved;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.skillsquared2.R;

public class SavedFragment extends Fragment {

    private SavedViewModel savedViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        savedViewModel =
                ViewModelProviders.of(this).get(SavedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_saved, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        savedViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
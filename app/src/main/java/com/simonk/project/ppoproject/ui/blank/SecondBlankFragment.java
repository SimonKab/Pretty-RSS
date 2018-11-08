package com.simonk.project.ppoproject.ui.blank;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simonk.project.ppoproject.R;

import androidx.fragment.app.Fragment;

public class SecondBlankFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.second_blank_fragment, parent, false);
        return root;
    }

}

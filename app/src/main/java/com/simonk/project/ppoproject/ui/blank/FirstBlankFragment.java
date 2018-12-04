package com.simonk.project.ppoproject.ui.blank;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simonk.project.ppoproject.R;
import com.simonk.project.ppoproject.databinding.FirstBlankFragmentBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

public class FirstBlankFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        FirstBlankFragmentBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.first_blank_fragment, parent, false);
        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.appBarInclude.toolbarLayout.toolbar);
        return binding.getRoot();
    }

}

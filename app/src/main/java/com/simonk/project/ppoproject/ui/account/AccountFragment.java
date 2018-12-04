package com.simonk.project.ppoproject.ui.account;

import android.app.AppComponentFactory;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simonk.project.ppoproject.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.simonk.project.ppoproject.databinding.AccountFragmentBinding;
import com.simonk.project.ppoproject.model.Account;
import com.simonk.project.ppoproject.viewmodels.AccountViewModel;

public class AccountFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        AccountFragmentBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.account_fragment, parent, false);
        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.toolbarLayout.toolbar);

        binding.accountEditFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NavController navController =
                        Navigation.findNavController(v);
                navController.navigate(R.id.action_account_to_edit_account);
            }
        });

        final AccountViewModel model = ViewModelProviders.of(this).get(AccountViewModel.class);
        model.getCurrentAccount().observe(this, (result) -> {
            if (result == null) {
                return;
            }

            if (result.data != null) {
                setDate(result.data, binding);
            }

            if (result.complete) {
                Toast.makeText(requireContext(), "Complete", Toast.LENGTH_LONG).show();
            }
            if (result.networkError) {
                Snackbar.make(getView(), "Network error", Snackbar.LENGTH_LONG).show();
            }
            if (result.disconnect) {
                Snackbar.make(getView(), "Disconnected", Snackbar.LENGTH_LONG).show();
            }
            if (result.error != null) {
                if (result.error instanceof FirebaseException) {
                    Snackbar.make(getView(), result.error.getMessage(), Snackbar.LENGTH_LONG).show();
                } else {
                    throw new RuntimeException(result.error);
                }
            }
        });

        return binding.getRoot();
    }

    private void setDate(Account account, AccountFragmentBinding binding) {
        binding.accountName.setText(getString(R.string.name_template,
                account.getFirstName() == null ? "" : account.getFirstName(),
                account.getLastName() == null ? "" : account.getLastName()));
        binding.accountEmail.setText(account.getEmail());
        binding.accountAddress.setText(account.getAddress());
        binding.accountTelephone.setText(account.getTelephone());

        if (account.getPicture() != null && account.getPicture().getPath() != null &&
                !account.getPicture().getPath().isEmpty()) {
            Glide.with(this).load(account.getPicture().getPath()).into(binding.accountPicture);
        }
    }
}

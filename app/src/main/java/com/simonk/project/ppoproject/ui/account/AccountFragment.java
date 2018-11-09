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

import com.bumptech.glide.Glide;
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
        model.getAccount().observe(this, (account) -> {
            if (account != null) {
                setDate(account, binding);
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

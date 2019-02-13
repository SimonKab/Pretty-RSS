package com.simonk.project.prettyrss.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simonk.project.prettyrss.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.simonk.project.prettyrss.auth.AuthFactory;
import com.simonk.project.prettyrss.databinding.AccountFragmentBinding;
import com.simonk.project.prettyrss.error.ErrorInterceptor;
import com.simonk.project.prettyrss.error.ErrorLayout;
import com.simonk.project.prettyrss.error.GeneralFirebaseErrorInterceptor;
import com.simonk.project.prettyrss.error.NetworkErrorInterceptor;
import com.simonk.project.prettyrss.error.NotAuthenticatedErrorLayout;
import com.simonk.project.prettyrss.model.Account;
import com.simonk.project.prettyrss.ui.login.LoginActivity;
import com.simonk.project.prettyrss.viewmodels.AccountViewModel;
import com.simonk.project.prettyrss.viewmodels.GalleryViewModel;

public class AccountFragment extends Fragment {

    private ViewGroup mImageNameViewGroup;
    private ImageView mAccountPicture;
    private TextView mAccountName;
    private ProgressBar mProgress;
    private ViewGroup mContent;
    private TextView mTelephone;
    private TextView mEmail;
    private TextView mAddress;
    private FloatingActionButton mEditFab;
    private ErrorLayout mErrorLayout;
    private Button mSignOutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        AccountFragmentBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.account_fragment, parent, false);
        init(binding);

        binding.toolbarLayout.toolbar.setTitle("");
        ((AppCompatActivity)requireActivity()).setSupportActionBar(binding.toolbarLayout.toolbar);

        mEditFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NavController navController =
                        Navigation.findNavController(v);
                navController.navigate(R.id.action_account_to_edit_account);
            }
        });

        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthFactory.getAuthManager().signOut();

                Intent intent = new Intent(requireContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        mErrorLayout.addErrorInterceptor(new NetworkErrorInterceptor());
        mErrorLayout.addErrorInterceptor(new GeneralFirebaseErrorInterceptor());
        mErrorLayout.addErrorInterceptor(new NotAuthenticatedErrorLayout());
        mErrorLayout.setOnRetryListener(new ErrorLayout.OnRetryListener() {
            @Override
            public void onRetry() {
                updateUi();
            }
        });

        if (!AuthFactory.getAuthManager().isSignIn()) {
            setErrorView();
            mErrorLayout.setErrorButtonText(getString(R.string.sign_in));
            mErrorLayout.handleError(ErrorInterceptor.ErrorType.NOT_AUTHENTICATED);
            mErrorLayout.setOnRetryListener(new ErrorLayout.OnRetryListener() {
                @Override
                public void onRetry() {
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });
            return binding.getRoot();
        }

        updateUi();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewModelProviders.of(getActivity()).get(GalleryViewModel.class).setPath(null);
    }

    private void init(AccountFragmentBinding binding) {
        mImageNameViewGroup = binding.accountFragmentImageName;
        mAccountPicture = binding.accountPicture;
        mAccountName = binding.accountName;
        mProgress = binding.accountFragmentProgress;
        mContent = binding.accountFragmentContent;
        mTelephone = binding.accountTelephone;
        mEmail = binding.accountEmail;
        mAddress = binding.accountAddress;
        mEditFab = binding.accountEditFab;
        mErrorLayout = binding.accountFragmentError;
        mSignOutButton = binding.accountFragmentSignOut;
    }

    private void updateUi() {
        setProgressView();

        final AccountViewModel model = ViewModelProviders.of(this).get(AccountViewModel.class);
        model.getCurrentAccount().observe(this, (result) -> {
            if (result == null) {
                setProgressView();
                return;
            }

            if (result.complete) {
                setContentView();
                if (result.data != null) {
                    setData(result.data);
                }
            }

            if (result.networkError) {
                setErrorView();
                mErrorLayout.handleError(ErrorInterceptor.ErrorType.NETWORK_ERROR);
            }
            if (result.disconnect) {
                setErrorView();
                mErrorLayout.handleError(ErrorInterceptor.ErrorType.NETWORK_ERROR);
            }
            if (result.error != null) {
                setErrorView();
                if (result.error instanceof FirebaseException) {
                    mErrorLayout.handleError(result.error);
                } else {
                    throw new RuntimeException(result.error);
                }
            }
        });
    }

    private void setProgressView() {
        mProgress.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);
        mImageNameViewGroup.setVisibility(View.GONE);
        mEditFab.hide();
        mErrorLayout.setVisibility(View.GONE);
    }

    private void setContentView() {
        mProgress.setVisibility(View.GONE);
        mContent.setVisibility(View.VISIBLE);
        mImageNameViewGroup.setVisibility(View.VISIBLE);
        mEditFab.show();
        mErrorLayout.setVisibility(View.GONE);
    }

    private void setErrorView() {
        mProgress.setVisibility(View.GONE);
        mContent.setVisibility(View.GONE);
        mImageNameViewGroup.setVisibility(View.GONE);
        mEditFab.hide();
        mErrorLayout.setVisibility(View.VISIBLE);
    }

    private void setData(Account account) {
        mAccountName.setText(getString(R.string.name_template,
                account.getFirstName() == null ? "" : account.getFirstName(),
                account.getLastName() == null ? "" : account.getLastName()));
        mEmail.setText(account.getEmail());
        mAddress.setText(account.getAddress());
        mTelephone.setText(account.getTelephone());

        if (account.getPicture() != null && account.getPicture().getPath() != null &&
                !account.getPicture().getPath().isEmpty()) {
            Glide.with(this).load(account.getPicture().getPath()).into(mAccountPicture);
        }
    }
}

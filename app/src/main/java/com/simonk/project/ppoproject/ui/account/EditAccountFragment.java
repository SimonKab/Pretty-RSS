package com.simonk.project.ppoproject.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.simonk.project.ppoproject.R;
import com.simonk.project.ppoproject.databinding.EditAccountFragmentBinding;
import com.simonk.project.ppoproject.error.ErrorInterceptor;
import com.simonk.project.ppoproject.error.ErrorLayout;
import com.simonk.project.ppoproject.error.GeneralFirebaseErrorInterceptor;
import com.simonk.project.ppoproject.error.NetworkErrorInterceptor;
import com.simonk.project.ppoproject.model.Account;
import com.simonk.project.ppoproject.model.Picture;
import com.simonk.project.ppoproject.repository.AccountRepository;
import com.simonk.project.ppoproject.viewmodels.EditAccountViewModel;
import com.simonk.project.ppoproject.viewmodels.GalleryViewModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class EditAccountFragment extends Fragment {

    private ImageView mEditAccountPicture;
    private EditText mEditFirstName;
    private EditText mEditSecondName;
    private EditText mEditTelephone;
    private EditText mEditAddress;

    private ViewGroup mEditImageNameViewGroup;
    private ViewGroup mContent;
    private ProgressBar mProgress;
    private ErrorLayout mErrorLayout;

    private FloatingActionButton mSaveFab;

    private GalleryViewModel mGallerViewModel;
    private EditAccountViewModel mEditAccountViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        EditAccountFragmentBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.edit_account_fragment, parent, false);
        init(binding);

        mGallerViewModel = ViewModelProviders.of(getActivity()).get(GalleryViewModel.class);
        mGallerViewModel.getPath().observe(this, path -> {
            if (path == null) {
                return;
            }

            Glide.with(EditAccountFragment.this)
                    .load(path)
                    .into(mEditAccountPicture);

            mEditAccountViewModel.setImagePath(path);
        });

        mEditAccountViewModel = ViewModelProviders.of(this).get(EditAccountViewModel.class);

        mEditAccountPicture.setOnClickListener(v -> {
            final NavController navController =
                    Navigation.findNavController(v);
            navController.navigate(R.id.action_edit_account_to_gallery);
        });

        mErrorLayout.addErrorInterceptor(new NetworkErrorInterceptor());
        mErrorLayout.addErrorInterceptor(new GeneralFirebaseErrorInterceptor());
        mErrorLayout.setOnRetryListener(new ErrorLayout.OnRetryListener() {
            @Override
            public void onRetry() {
                updateUi();
            }
        });

        mEditAccountViewModel = ViewModelProviders.of(this).get(EditAccountViewModel.class);

        updateUi();

        return binding.getRoot();
    }

    private void init(EditAccountFragmentBinding binding) {
        mEditAccountPicture = binding.editAccountPicture;
        mEditFirstName = binding.editAccountFirstName;
        mEditSecondName = binding.editAccountSecondName;
        mEditTelephone = binding.editAccountTelephone;
        mEditAddress = binding.editAccountAddress;
        mSaveFab = binding.editAccountSaveFab;
        mContent = binding.editAccountContent;
        mEditImageNameViewGroup = binding.editAccountImageName;
        mProgress = binding.editAccountFragmentProgress;
        mErrorLayout = binding.editAccountFragmentError;
    }

    private void updateUi() {
        setProgressView();
        mEditAccountViewModel.fetchInitialAccount().observe(this, new Observer<AccountRepository.DatabaseResult<Account>>() {
            @Override
            public void onChanged(AccountRepository.DatabaseResult<Account> databaseResult) {
                if (databaseResult == null) {
                    setProgressView();
                    return;
                }

                if (databaseResult.complete) {
                    setContentView();
                    Account initialAccount;
                    if (databaseResult.data != null) {
                        initialAccount = databaseResult.data;
                        if (mGallerViewModel.getPath().getValue() == null && initialAccount.getPicture() != null) {
                            mGallerViewModel.setPath(initialAccount.getPicture().getPath());
                        }
                        setData(initialAccount);
                    }

                    mSaveFab.setOnClickListener(v -> {
                        saveData();
                    });
                }

                if (databaseResult.networkError) {
                    setErrorView();
                    mErrorLayout.handleError(ErrorInterceptor.ErrorType.NETWORK_ERROR);
                }
                if (databaseResult.disconnect) {
                    setErrorView();
                    mErrorLayout.handleError(ErrorInterceptor.ErrorType.NETWORK_ERROR);
                }
                if (databaseResult.error != null) {
                    setErrorView();
                    if (databaseResult.error instanceof FirebaseException) {
                        mErrorLayout.handleError(databaseResult.error);
                    } else {
                        throw new RuntimeException(databaseResult.error);
                    }
                }
            }
        });
    }

    private void setProgressView() {
        mContent.setVisibility(View.GONE);
        mEditImageNameViewGroup.setVisibility(View.GONE);
        mErrorLayout.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
    }

    private void setContentView() {
        mContent.setVisibility(View.VISIBLE);
        mEditImageNameViewGroup.setVisibility(View.VISIBLE);
        mErrorLayout.setVisibility(View.GONE);
        mProgress.setVisibility(View.GONE);
    }

    private void setErrorView() {
        mContent.setVisibility(View.GONE);
        mEditImageNameViewGroup.setVisibility(View.GONE);
        mErrorLayout.setVisibility(View.VISIBLE);
        mProgress.setVisibility(View.GONE);
    }

    private void setData(Account account) {
        mEditFirstName.setText(account.getFirstName());
        mEditSecondName.setText(account.getLastName());
        mEditAddress.setText(account.getAddress());
        mEditTelephone.setText(account.getTelephone());
    }

    private void saveData() {

        String firstName = mEditFirstName.getText().toString();
        String lastName = mEditSecondName.getText().toString();
        String telephone = mEditTelephone.getText().toString();
        String location = mEditAddress.getText().toString();
        String photo = mGallerViewModel.getPath().getValue();

        Account account = new Account();
        if (mEditAccountViewModel.getInitialAccount().getValue() == null) {
            account.setMain(true);
        } else {
            account = mEditAccountViewModel.getInitialAccount().getValue();
        }

        if (!firstName.isEmpty()) {
            account.setFirstName(firstName);
        }
        if (!lastName.isEmpty()) {
            account.setLastName(lastName);
        }
        if (!telephone.isEmpty()) {
            account.setTelephone(telephone);
        }
        if (!location.isEmpty()) {
            account.setAddress(location);
        }
        if (photo != null && !photo.isEmpty()) {
            Picture picture = new Picture();
            picture.setPath(photo);
            account.setPicrute(picture);
        }

        mEditAccountViewModel.setEditedAccount(account.getFirstName(), account.getLastName(),
                account.getTelephone(), account.getAddress());

        LiveData<AccountRepository.DatabaseResult> resultLiveData = mEditAccountViewModel.saveEditedAccount();
        resultLiveData.observe(this, new Observer<AccountRepository.DatabaseResult>() {
            @Override
            public void onChanged(AccountRepository.DatabaseResult databaseResult) {
                if (databaseResult == null) {
                    return;
                }

                if (databaseResult.complete) {
                    final NavController navController =
                            Navigation.findNavController(getView());
                    navController.navigateUp();
                }

                if (databaseResult.networkError) {
                    Snackbar.make(getView(), "Network error", Snackbar.LENGTH_LONG).show();
                }
                if (databaseResult.disconnect) {
                    Snackbar.make(getView(), "Disconnected", Snackbar.LENGTH_LONG).show();
                }
                if (databaseResult.error != null) {
                    if (databaseResult.error instanceof FirebaseException) {
                        Snackbar.make(getView(), databaseResult.error.getMessage(), Snackbar.LENGTH_LONG).show();
                    } else {
                        throw new RuntimeException(databaseResult.error);
                    }
                }
            }
        });
    }

}

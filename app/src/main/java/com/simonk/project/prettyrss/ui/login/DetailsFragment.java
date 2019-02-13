package com.simonk.project.prettyrss.ui.login;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.firebase.FirebaseException;
import com.simonk.project.prettyrss.R;
import com.simonk.project.prettyrss.databinding.LoginDetailsFragmentBinding;
import com.simonk.project.prettyrss.repository.AccountRepository;
import com.simonk.project.prettyrss.viewmodels.RegisterDetailsViewModel;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

public class DetailsFragment extends Fragment {

    private CardView mCardView;

    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;
    private EditText mAddressEditText;
    private EditText mTelephoneEditText;

    private TextView mErrorTextView;

    private FrameLayout mFinishFrame;
    private Button mFinishButton;

    private RegisterDetailsViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        LoginDetailsFragmentBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.login_details_fragment, parent, false);
        View root = init(binding);

        setFinishButtonAnimation();

        mFinishButton.setOnClickListener(this::onFinishButtonClicked);

        mCardView.setVisibility(View.INVISIBLE);
        mFinishButton.setVisibility(View.INVISIBLE);
        mFinishFrame.setVisibility(View.INVISIBLE);

        mViewModel = ViewModelProviders.of(this).get(RegisterDetailsViewModel.class);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        new Handler().post(() -> {
            requireLoginActivity().new LoginAnimator()
                    .animateCardAndButtons(mCardView, mFinishButton, mFinishFrame, null,
                            true, null);
        });
    }

    private View init(LoginDetailsFragmentBinding binding) {
        mCardView = binding.loginDetailsFragmentCard;
        mFirstNameEditText = binding.loginDetailsFragmentFirstName;
        mLastNameEditText = binding.loginDetailsFragmentLastName;
        mAddressEditText = binding.loginDetailsFragmentAddress;
        mTelephoneEditText = binding.loginDetailsFragmentTelephone;

        mErrorTextView = binding.loginDetailsFragmentError;

        mFinishFrame = binding.loginDetailsFragmentFinishFrame;
        mFinishButton = binding.loginDetailsFragmentFinishButton;

        return binding.getRoot();
    }

    private void onFinishButtonClicked(View v) {
        String firstName = mFirstNameEditText.getText().toString();
        String lastName = mLastNameEditText.getText().toString();
        String address = mAddressEditText.getText().toString();
        String telephone = mTelephoneEditText.getText().toString();

        if (Strings.isNullOrEmpty(firstName)
                || Strings.isNullOrEmpty(lastName)) {
            mErrorTextView.setText(getString(R.string.please_first_and_last_name));
            return;
        }

        startFinishButtonAnimation();

        LiveData<AccountRepository.DatabaseResult> resultLiveData =
                mViewModel.saveData(firstName, lastName, mViewModel.getCurrentUserEmail(),
                        address, telephone);
        resultLiveData.observe(this, new Observer<AccountRepository.DatabaseResult>() {
            @Override
            public void onChanged(AccountRepository.DatabaseResult saveDataResult) {
                if (saveDataResult == null) {
                    return;
                }

                if (saveDataResult.complete) {
                    requireLoginActivity().startMainActivity();
                }
                if (saveDataResult.networkError) {
                    mErrorTextView.setText(getString(R.string.error_network));
                }
                if (saveDataResult.error != null) {
                    if (saveDataResult.error instanceof FirebaseException) {
                        mErrorTextView.setText(saveDataResult.error.getMessage());
                    } else {
                        throw new RuntimeException(saveDataResult.error);
                    }
                }

                resultLiveData.removeObserver(this);
                stopFinishButtonAnimation();
            }
        });
    }

    private void setFinishButtonAnimation() {
        AnimatedVectorDrawableCompat drawable =
                AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.process_animation);
        drawable.setTint(ContextCompat.getColor(requireContext(), android.R.color.white));
        mFinishFrame.setBackground(drawable);
    }

    private void startFinishButtonAnimation() {
        AnimatedVectorDrawableCompat background = (AnimatedVectorDrawableCompat) mFinishFrame.getBackground();
        background.start();
    }

    private void stopFinishButtonAnimation() {
        AnimatedVectorDrawableCompat background = (AnimatedVectorDrawableCompat) mFinishFrame.getBackground();
        background.stop();
        setFinishButtonAnimation();
    }

    private LoginActivity requireLoginActivity() {
        return (LoginActivity) requireActivity();
    }

}
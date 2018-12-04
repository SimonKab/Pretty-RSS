package com.simonk.project.ppoproject.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.firebase.FirebaseException;
import com.simonk.project.ppoproject.R;
import com.simonk.project.ppoproject.databinding.RegisterFragmentBinding;
import com.simonk.project.ppoproject.repository.LoginRepository;
import com.simonk.project.ppoproject.viewmodels.RegisterViewModel;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

public class RegisterFragment extends Fragment {

    private CardView mCardView;
    private FrameLayout mRegisterFrame;
    private Button mSignInButton;
    private Button mRegisterButton;

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mPasswordConfirmEditText;

    private TextView mErrorTextView;

    private RegisterViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        RegisterFragmentBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.register_fragment, parent, false);
        View root = init(binding);

        setRegisterButtonAnimation();

        mRegisterButton.setOnClickListener(this::onRegisterButtonClicked);
        mSignInButton.setOnClickListener(this::onSignInButtonClicked);

        mCardView.setVisibility(View.INVISIBLE);
        mRegisterFrame.setVisibility(View.INVISIBLE);
        mSignInButton.setVisibility(View.INVISIBLE);

        mViewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        new Handler().post(() -> {
            requireLoginActivity().new LoginAnimator()
                    .animateCardAndButtons(mCardView, mRegisterFrame, mSignInButton,
                            true, null);
        });
    }

    private View init(RegisterFragmentBinding binding) {
        mCardView = binding.loginLayoutCard;
        mRegisterFrame = binding.loginLayoutSignInFrame;
        mRegisterButton = binding.loginLayoutSignInButton;
        mSignInButton = binding.registerFragmentSignIn;

        mEmailEditText = binding.registerFragmentEmail;
        mPasswordEditText = binding.registerFragmentPassword;
        mPasswordConfirmEditText = binding.registerFragmentPasswordConfirm;

        mErrorTextView = binding.registerFragmentError;

        return binding.getRoot();
    }

    private void onRegisterButtonClicked(View v) {
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String passwordConfirm = mPasswordConfirmEditText.getText().toString();

        if (Strings.isNullOrEmpty(email)
                || Strings.isNullOrEmpty(password)
                || Strings.isNullOrEmpty(passwordConfirm)) {
            return;
        }

        if (!password.equals(passwordConfirm)) {
            mErrorTextView.setText("Confirmation password does not match");
            return;
        }

        startRegisterButtonAnimation();

        mRegisterButton.setText("Working...");

        LiveData<LoginRepository.RegisterResult> resultLiveData = mViewModel.register(
                mEmailEditText.getText().toString(),
                mPasswordEditText.getText().toString()
        );
        resultLiveData.observe(this, new Observer<LoginRepository.RegisterResult>() {
            @Override
            public void onChanged(LoginRepository.RegisterResult registerResult) {
                if (registerResult == null) {
                    return;
                }

                if (registerResult.complete) {
                    onDetailsFragmentStart();
                }
                if (registerResult.invalidCredentials) {
                    mErrorTextView.setText("Wrong email or password");
                }
                if (registerResult.weakPassword) {
                    mErrorTextView.setText("Your password is too bad");
                }
                if (registerResult.userCollision) {
                    mErrorTextView.setText("User with same email already exists");
                }
                if (registerResult.error != null) {
                    if (registerResult.error instanceof FirebaseException) {
                        mErrorTextView.setText(registerResult.error.getMessage());
                    } else {
                        mErrorTextView.setText("Unknown error");
                    }
                }

                resultLiveData.removeObserver(this);
                stopRegisterButtonAnimation();
                mRegisterButton.setText("Register");
            }
        });
    }

    private void setRegisterButtonAnimation() {
        AnimatedVectorDrawableCompat drawable =
                AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.process_animation);
        drawable.setTint(ContextCompat.getColor(requireContext(), android.R.color.white));
        mRegisterFrame.setBackground(drawable);
    }

    private void startRegisterButtonAnimation() {
        AnimatedVectorDrawableCompat background = (AnimatedVectorDrawableCompat) mRegisterFrame.getBackground();
        background.start();
    }

    private void stopRegisterButtonAnimation() {
        AnimatedVectorDrawableCompat background = (AnimatedVectorDrawableCompat) mRegisterFrame.getBackground();
        background.stop();
        setRegisterButtonAnimation();
    }

    private void onSignInButtonClicked(View v) {
        onOtherFragmentStart(R.id.action_register_to_login);
    }

    private void onDetailsFragmentStart() {
        onOtherFragmentStart(R.id.action_register_to_details);
    }

    private void onOtherFragmentStart(int navigationId) {
        NavController navController = Navigation.findNavController(getView());
        requireLoginActivity().new LoginAnimator()
                .animateCardAndButtons(mCardView, mRegisterFrame, mSignInButton, false,
                        new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                navController.navigate(navigationId);
                            }
                        });
    }

    private LoginActivity requireLoginActivity() {
        return (LoginActivity) requireActivity();
    }
}

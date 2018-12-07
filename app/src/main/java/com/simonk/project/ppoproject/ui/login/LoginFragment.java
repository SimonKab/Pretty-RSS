package com.simonk.project.ppoproject.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.firebase.FirebaseError;
import com.google.firebase.FirebaseException;
import com.simonk.project.ppoproject.R;
import com.simonk.project.ppoproject.databinding.AccountFragmentBinding;
import com.simonk.project.ppoproject.databinding.LoginFragmentBinding;
import com.simonk.project.ppoproject.repository.LoginRepository;
import com.simonk.project.ppoproject.ui.about.AboutActivity;
import com.simonk.project.ppoproject.viewmodels.LoginViewModel;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionValues;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

public class LoginFragment extends Fragment {

    private CardView mCardView;
    private FrameLayout mSignInFrame;
    private Button mSignInButton;
    private Button mRegisterButton;

    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    private TextView mErrorTextView;

    private Button mAboutButton;

    private boolean mProcessingRequest;

    private boolean mAnimateCardAndButtons = true;

    private LoginViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        LoginFragmentBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.login_fragment, parent, false);
        View root = init(binding);

        setSignInButtonAnimation();

        mAboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AboutActivity.getIntent(requireContext()));
            }
        });

        mRegisterButton.setOnClickListener(this::onRegisterButtonClicked);
        mSignInButton.setOnClickListener(this::onSignInButtonClicked);

        mCardView.setVisibility(View.INVISIBLE);
        mRegisterButton.setVisibility(View.INVISIBLE);
        mSignInFrame.setVisibility(View.INVISIBLE);

        mViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAnimateCardAndButtons) {
            new Handler().postDelayed(() -> {
                requireLoginActivity().new LoginAnimator()
                        .animateCardAndButtons(mCardView, mSignInFrame, mRegisterButton, mAboutButton,
                                true, null);
            }, getResources().getInteger(R.integer.show_login_view_delay));

            mAnimateCardAndButtons = false;
        }
    }

    private View init(LoginFragmentBinding binding) {
        mCardView = binding.loginLayoutCard;
        mSignInFrame = binding.loginLayoutSignInFrame;
        mSignInButton = binding.loginLayoutSignInButton;
        mRegisterButton = binding.loginFragmentRegister;
        mEmailEditText = binding.loginFragmentEmail;
        mPasswordEditText = binding.loginFragmentPassword;
        mErrorTextView = binding.loginFragmentError;
        mAboutButton = binding.loginLayoutAbout;

        return binding.getRoot();
    }

    private void onSignInButtonClicked(View v) {
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        if (Strings.isNullOrEmpty(email)
                || Strings.isNullOrEmpty(password)) {
            return;
        }

        startSignInButtonAnimation();

        mSignInButton.setText("Working...");

        LiveData<LoginRepository.SignInResult> resultLiveData = mViewModel.signIn(
                mEmailEditText.getText().toString(),
                mPasswordEditText.getText().toString()
        );
        resultLiveData.observe(this, new Observer<LoginRepository.SignInResult>() {
            @Override
            public void onChanged(LoginRepository.SignInResult signInResult) {
                if (signInResult == null) {
                    return;
                }

                if (signInResult.complete) {
                    requireLoginActivity().startMainActivity();
                }
                if (signInResult.invalidCredentials) {
                    mErrorTextView.setText("Wrong email or password");
                }
                if (signInResult.error != null) {
                    if (signInResult.error instanceof FirebaseException) {
                        mErrorTextView.setText(signInResult.error.getMessage());
                    } else {
                        mErrorTextView.setText("Unknown error");
                    }
                }

                resultLiveData.removeObserver(this);
                mSignInButton.setText("Sign in");
                stopSignInButtonAnimation();
            }
        });
    }

    private void setSignInButtonAnimation() {
        AnimatedVectorDrawableCompat drawable =
                AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.process_animation);
        drawable.setTint(ContextCompat.getColor(requireContext(), android.R.color.white));
        mSignInFrame.setBackground(drawable);
    }

    private void startSignInButtonAnimation() {
        AnimatedVectorDrawableCompat background = (AnimatedVectorDrawableCompat) mSignInFrame.getBackground();
        background.start();
    }

    private void stopSignInButtonAnimation() {
        AnimatedVectorDrawableCompat background = (AnimatedVectorDrawableCompat) mSignInFrame.getBackground();
        background.stop();
        setSignInButtonAnimation();
    }

    private void onRegisterButtonClicked(View v) {
        requireLoginActivity().new LoginAnimator()
                .animateCardAndButtons(mCardView, mSignInFrame, mRegisterButton, mAboutButton, false,
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        View view = LoginFragment.this.getView();
                        Navigation.findNavController(view).navigate(R.id.register_fragment);
                    }
                });
    }

    private LoginActivity requireLoginActivity() {
        return (LoginActivity) requireActivity();
    }
}

package com.simonk.project.ppoproject.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.simonk.project.ppoproject.R;
import com.simonk.project.ppoproject.auth.AuthFactory;
import com.simonk.project.ppoproject.repository.LoginRepository;
import com.simonk.project.ppoproject.ui.MainActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class LoginActivity extends AppCompatActivity {

    private boolean mAnimateAppName = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        if (LoginRepository.getInstance().isSignIn()) {
            startMainActivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAnimateAppName) {
            new Handler().postDelayed(this::animateAppNameTranslationUp,
                    getResources().getInteger(R.integer.show_login_view_delay));
            mAnimateAppName = false;
        }
    }

    void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    void animateAppNameTranslationUp() {
        View decorView = getWindow().getDecorView();
        LayerDrawable layerDrawable = (LayerDrawable) decorView.getBackground();
        BitmapDrawable appNameDrawable =
                (BitmapDrawable) layerDrawable.findDrawableByLayerId(R.id.splash_screen_logo);

        int quadOfWindow = decorView.getMeasuredHeight() / 4;
        int halfOfWindow = decorView.getMeasuredHeight() / 2;

        int shiftOfTranslation = halfOfWindow - quadOfWindow;

        Rect appNameRect = appNameDrawable.copyBounds();

        ValueAnimator translateAnimator = ValueAnimator.ofInt(shiftOfTranslation);
        translateAnimator.setDuration(getResources().getInteger(R.integer.login_animation_duration));
        translateAnimator.setInterpolator(new LinearOutSlowInInterpolator());
        translateAnimator.addUpdateListener(animation -> {
            int offset = (int) animation.getAnimatedValue();

            appNameDrawable.setBounds(appNameRect.left, appNameRect.top - offset,
                    appNameRect.right, appNameRect.bottom - offset);
            appNameDrawable.invalidateSelf();
        });
        translateAnimator.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    class LoginAnimator {

        void animateCardAndButtons(View card, View button, View registerButton, boolean appearance,
                                           @Nullable AnimatorListenerAdapter finishAnimatorListener) {
            int cardDelay = 0, buttonsDelay = 0;
            if (appearance) {
                buttonsDelay = getResources().getInteger(R.integer.login_animation_view_delay);
            } else {
                cardDelay = getResources().getInteger(R.integer.login_animation_view_delay);
            }
            animateAppearanceDisappearance(card, cardDelay, appearance, null);
            animateAppearanceDisappearance(button, buttonsDelay, appearance, null);
            animateAppearanceDisappearance(registerButton, buttonsDelay, appearance, finishAnimatorListener);
        }

        private void animateAppearanceDisappearance(View view, int startDelay, boolean appearance,
                                                    @Nullable AnimatorListenerAdapter animatorListenerAdapter) {
            View decorView = getWindow().getDecorView();

            int windowHeight = decorView.getMeasuredHeight();

            int[] viewLocation = new int[2];
            view.getLocationInWindow(viewLocation);

            int shift = windowHeight - viewLocation[1];

            ValueAnimator shiftAnimator;
            if (appearance) {
                shiftAnimator = ValueAnimator.ofInt(shift, 0);
            } else {
                shiftAnimator = ValueAnimator.ofInt(0, shift + 100);
            }
            shiftAnimator.setDuration(getResources().getInteger(R.integer.login_animation_duration));
            shiftAnimator.setInterpolator(new LinearOutSlowInInterpolator());
            shiftAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    view.setVisibility(View.VISIBLE);
                }
            });
            if (animatorListenerAdapter != null) {
                shiftAnimator.addListener(animatorListenerAdapter);
            }
            shiftAnimator.addUpdateListener(animation -> {
                int offset = (int) animation.getAnimatedValue();
                view.setTranslationY(offset);
            });
            shiftAnimator.setStartDelay(startDelay);
            shiftAnimator.start();
        }

    }
}

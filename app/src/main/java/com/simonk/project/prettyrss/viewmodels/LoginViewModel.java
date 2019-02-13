package com.simonk.project.prettyrss.viewmodels;

import android.app.Application;

import com.simonk.project.prettyrss.repository.LoginRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class LoginViewModel extends AndroidViewModel {

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<LoginRepository.SignInResult> signIn(String email, String password) {
        return LoginRepository.getInstance().signIn(email, password);
    }

}

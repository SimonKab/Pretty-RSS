package com.simonk.project.prettyrss.viewmodels;

import android.app.Application;

import com.simonk.project.prettyrss.repository.LoginRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class RegisterViewModel extends AndroidViewModel {

    public RegisterViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<LoginRepository.RegisterResult> register(String email, String password) {
        return LoginRepository.getInstance().register(email, password);
    }

}

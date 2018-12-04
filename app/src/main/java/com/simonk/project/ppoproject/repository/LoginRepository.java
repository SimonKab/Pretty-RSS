package com.simonk.project.ppoproject.repository;

import com.simonk.project.ppoproject.auth.AuthFactory;
import com.simonk.project.ppoproject.auth.AuthManager;
import com.simonk.project.ppoproject.database.DatabaseFactory;
import com.simonk.project.ppoproject.database.UserManager;
import com.simonk.project.ppoproject.model.Account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class LoginRepository {

    private static LoginRepository sInstance;

    private LoginRepository() {

    }

    public static LoginRepository getInstance() {
        if (sInstance == null) {
            synchronized (AccountRepository.class) {
                if (sInstance == null) {
                    sInstance = new LoginRepository();
                }
            }
        }
        return sInstance;
    }

    public boolean isSignIn() {
        return AuthFactory.getAuthManager().isSignIn();
    }

    public LiveData<SignInResult> signIn(String email, String password) {
        MutableLiveData<SignInResult> resultLiveData = new MutableLiveData<>();
        AuthFactory.getAuthManager().signInUser(email, password, new AuthManager.AuthListener() {
            @Override
            public void onComplete() {
                SignInResult result = new SignInResult();
                result.complete = true;
                resultLiveData.setValue(result);
            }

            @Override
            public void onError(Exception error) {
                SignInResult result = new SignInResult();
                result.error = error;
                resultLiveData.setValue(result);
            }

            @Override
            public void onWeakPassword() {
                // never called in sign in
            }

            @Override
            public void onInvalidCredentials() {
                SignInResult result = new SignInResult();
                result.invalidCredentials = true;
                resultLiveData.setValue(result);
            }

            @Override
            public void onUserCollision() {
                // never called in sign in
            }
        });

        return resultLiveData;
    }

    public LiveData<RegisterResult> register(String email, String password) {
        MutableLiveData<RegisterResult> resultLiveData = new MutableLiveData<>();
        AuthFactory.getAuthManager().registerUser(email, password, new AuthManager.AuthListener() {
            @Override
            public void onComplete() {
                RegisterResult result = new RegisterResult();
                result.complete = true;
                resultLiveData.setValue(result);
            }

            @Override
            public void onError(Exception error) {
                RegisterResult result = new RegisterResult();
                result.error = error;
                resultLiveData.setValue(result);
            }

            @Override
            public void onWeakPassword() {
                RegisterResult result = new RegisterResult();
                result.weakPassword = true;
                resultLiveData.setValue(result);
            }

            @Override
            public void onInvalidCredentials() {
                RegisterResult result = new RegisterResult();
                result.invalidCredentials = true;
                resultLiveData.setValue(result);
            }

            @Override
            public void onUserCollision() {
                RegisterResult result = new RegisterResult();
                result.userCollision = true;
                resultLiveData.setValue(result);
            }
        });

        return resultLiveData;
    }

    public static class SignInResult {
        public boolean complete;
        public Exception error;
        public boolean invalidCredentials;
    }

    public static class RegisterResult {
        public boolean complete;
        public Exception error;
        public boolean weakPassword;
        public boolean invalidCredentials;
        public boolean userCollision;
    }
}

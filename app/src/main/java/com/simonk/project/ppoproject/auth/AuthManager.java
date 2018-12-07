package com.simonk.project.ppoproject.auth;

public interface AuthManager {

    interface AuthListener {

        void onComplete();

        void onError(Exception error);

        void onWeakPassword();

        void onInvalidCredentials();

        void onUserCollision();
    }

    boolean isSignIn();

    String getCurrentUserId();
    String getCurrentUserEmail();

    void signInUser(String email, String password, AuthListener listener);
    void registerUser(String email, String password, AuthListener listener);

    void signOut();
}

package com.simonk.project.prettyrss.auth;

import com.simonk.project.prettyrss.auth.firebase.FirebaseAuthManager;

public class AuthFactory {

    public static AuthManager getAuthManager() {
        return new FirebaseAuthManager();
    }

}

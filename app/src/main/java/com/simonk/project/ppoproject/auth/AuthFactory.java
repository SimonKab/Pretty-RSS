package com.simonk.project.ppoproject.auth;

import com.simonk.project.ppoproject.auth.firebase.FirebaseAuthManager;

public class AuthFactory {

    public static AuthManager getAuthManager() {
        return new FirebaseAuthManager();
    }

}

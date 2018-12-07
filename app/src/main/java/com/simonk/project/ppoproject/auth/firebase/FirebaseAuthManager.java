package com.simonk.project.ppoproject.auth.firebase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.simonk.project.ppoproject.auth.AuthManager;

import java.util.Objects;

import androidx.annotation.NonNull;

public class FirebaseAuthManager implements AuthManager {

    @Override
    public boolean isSignIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    @Override
    public String getCurrentUserId() {
        if (!isSignIn()) {
            return null;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return Objects.requireNonNull(user).getUid();
    }

    @Override
    public String getCurrentUserEmail() {
        if (!isSignIn()) {
            return null;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return Objects.requireNonNull(user).getEmail();
    }

    @Override
    public void signInUser(String email, String password, AuthListener listener) {
        Task<AuthResult> task = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password);
        setOnTaskCompleteListener(task, listener);
    }

    @Override
    public void registerUser(String email, String password, AuthListener listener) {
        Task<AuthResult> task = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password);
        setOnTaskCompleteListener(task, listener);
    }

    @Override
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    private void setOnTaskCompleteListener(Task<AuthResult> task, AuthListener listener) {
        task.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (listener == null) {
                    return;
                }

                if (task.isSuccessful()) {
                    listener.onComplete();
                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseAuthWeakPasswordException e) {
                        listener.onWeakPassword();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        listener.onInvalidCredentials();
                    } catch(FirebaseAuthUserCollisionException e) {
                        listener.onUserCollision();
                    } catch (Exception e) {
                        listener.onError(e);
                    }
                }
            }
        });
    }
}

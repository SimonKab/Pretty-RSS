package com.simonk.project.ppoproject;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class PpoProjectApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        configureFirebase();
    }

    private void configureFirebase() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

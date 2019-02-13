package com.simonk.project.prettyrss;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class PrettyRssApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        configureFirebase();
    }

    private void configureFirebase() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

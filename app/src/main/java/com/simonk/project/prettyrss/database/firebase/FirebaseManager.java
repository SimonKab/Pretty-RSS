package com.simonk.project.prettyrss.database.firebase;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseManager {

    protected FirebaseDatabase getFirebase() {
        return FirebaseDatabase.getInstance();
    }

}

package com.simonk.project.ppoproject.database.firebase;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseManager {

    protected FirebaseDatabase getFirebase() {
        return FirebaseDatabase.getInstance();
    }

}

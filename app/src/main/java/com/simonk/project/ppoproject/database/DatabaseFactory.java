package com.simonk.project.ppoproject.database;

import com.simonk.project.ppoproject.database.firebase.FirebaseUserManager;

public class DatabaseFactory {

    public static UserManager getUserManager() {
        return new FirebaseUserManager();
    }

}

package com.simonk.project.prettyrss.database;

import com.simonk.project.prettyrss.database.firebase.FirebaseHistoryManager;
import com.simonk.project.prettyrss.database.firebase.FirebaseUserManager;
import com.simonk.project.prettyrss.database.local.LocalRssCacheManager;
import com.simonk.project.prettyrss.database.local.LocalUserManagerImpl;

public class DatabaseFactory {

    public static UserManager getUserManager() {
        return new FirebaseUserManager();
    }

    public static LocalUserManager getLocalUserManager() {
        return new LocalUserManagerImpl();
    }

    public static HistoryManager getHistoryManager() {
        return new FirebaseHistoryManager();
    }

    public static RssCacheManager getRssCacheManager() {
        return new LocalRssCacheManager();
    }
}

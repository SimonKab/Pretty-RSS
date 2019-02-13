package com.simonk.project.prettyrss.database.local;

import android.content.Context;

import com.simonk.project.prettyrss.database.local.entyties.AccountEntity;
import com.simonk.project.prettyrss.database.local.entyties.RssCacheEntity;
import com.simonk.project.prettyrss.database.local.providers.AccountProvider;
import com.simonk.project.prettyrss.database.local.providers.RssCacheProvider;

import androidx.room.Room;
import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {AccountEntity.class, RssCacheEntity.class}, version = 1, exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase {

    private static LocalDatabase sInstance;

    private static final String DATABASE_NAME = "PpoDatabase";

    public abstract AccountProvider accountProvider();

    public abstract RssCacheProvider rssCacheProvider();

    public static LocalDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (LocalDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(), LocalDatabase.class, DATABASE_NAME)
                            .build();
                }
            }
        }
        return sInstance;
    }
}

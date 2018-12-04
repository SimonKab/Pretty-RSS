package com.simonk.project.ppoproject.database.local;

import android.content.Context;

import com.simonk.project.ppoproject.database.local.entyties.AccountEntity;
import com.simonk.project.ppoproject.database.local.providers.AccountProvider;

import androidx.room.Room;
import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {AccountEntity.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {

    private static Database sInstance;

    private static final String DATABASE_NAME = "PpoDatabase";

    public abstract AccountProvider accountProvider();

    public static Database getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (Database.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            Database.class, DATABASE_NAME)
                            .build();
                }
            }
        }
        return sInstance;
    }
}

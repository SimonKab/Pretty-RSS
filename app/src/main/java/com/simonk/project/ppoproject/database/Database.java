package com.simonk.project.ppoproject.database;

import android.content.Context;

import com.simonk.project.ppoproject.database.entyties.AccountEntity;
import com.simonk.project.ppoproject.database.providers.AccountProvider;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

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

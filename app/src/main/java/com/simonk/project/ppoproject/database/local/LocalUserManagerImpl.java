package com.simonk.project.ppoproject.database.local;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.simonk.project.ppoproject.database.LocalUserManager;
import com.simonk.project.ppoproject.database.local.entyties.AccountEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

public class LocalUserManagerImpl implements LocalUserManager {

    @Override
    public LiveData<String> getAccountImage(Context context, String id) {
        LiveData<AccountEntity> accountLiveData
                = LocalDatabase.getInstance(context).accountProvider().loadAccount(id);
        return Transformations.map(accountLiveData, entity -> {
            if (entity != null) {
                return entity.picture;
            }

            return null;
        });
    }

    @Override
    public void saveAccountImage(Context context, String id, String path) {
        AccountEntity entity = new AccountEntity();
        entity.id = id;
        entity.picture = path;
        new SaveAccountAsyncTask(context, entity).execute();
    }

    private static class SaveAccountAsyncTask extends AsyncTask<Void, Void, Void> {

        private AccountEntity mEntity;

        @SuppressLint("StaticFieldLeak")
        private Context mApplicationContext;

        SaveAccountAsyncTask(Context context, AccountEntity entity) {
            mApplicationContext = context.getApplicationContext();
            mEntity = entity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AccountEntity entity
                    = LocalDatabase.getInstance(mApplicationContext).accountProvider().getAccount(mEntity.id);
            if (entity == null) {
                LocalDatabase.getInstance(mApplicationContext).accountProvider().insertAccount(mEntity);
            } else {
                LocalDatabase.getInstance(mApplicationContext).accountProvider().updateAccount(mEntity);
            }
            return null;
        }
    }

}

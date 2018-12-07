package com.simonk.project.ppoproject.repository;

import android.content.Context;

import com.simonk.project.ppoproject.auth.AuthFactory;
import com.simonk.project.ppoproject.database.DatabaseFactory;
import com.simonk.project.ppoproject.database.UserManager;
import com.simonk.project.ppoproject.model.Account;
import com.simonk.project.ppoproject.model.Picture;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class AccountRepository {

    private static AccountRepository sInstance;

    private AccountRepository() {
    }

    public static AccountRepository getInstance() {
        if (sInstance == null) {
            synchronized (AccountRepository.class) {
                if (sInstance == null) {
                    sInstance = new AccountRepository();
                }
            }
        }
        return sInstance;
    }

    public String getCurrentUserEmail() {
        return AuthFactory.getAuthManager().getCurrentUserEmail();
    }

    public String getCurrentUserId() {
        return AuthFactory.getAuthManager().getCurrentUserId();
    }

    public LiveData<String> getCurrentUserImage(Context context) {
        String userId = AuthFactory.getAuthManager().getCurrentUserId();
        return DatabaseFactory.getLocalUserManager().getAccountImage(context, userId);
    }

    public LiveData<DatabaseResult<Account>> getCurrentUser(Context context) {
        MutableLiveData<DatabaseResult<Account>> resultLiveData = new MutableLiveData<>();

        String userId = AuthFactory.getAuthManager().getCurrentUserId();
        DatabaseFactory.getUserManager().getUserById(userId, new UserManager.RetrieveDataListener<Account>() {
            @Override
            public void onComplete(Account data) {
                DatabaseResult<Account> result = new DatabaseResult<>();
                result.data = data;
                result.complete = true;

                DatabaseFactory.getLocalUserManager().getAccountImage(context, userId).observeForever(new Observer<String>() {
                    @Override
                    public void onChanged(String path) {
                        Picture picture = new Picture();
                        picture.setPath(path);
                        result.data.setPicrute(picture);

                        resultLiveData.setValue(result);
                    }
                });

                resultLiveData.setValue(result);
            }

            @Override
            public void onError(Exception error) {
                DatabaseResult<Account> result = new DatabaseResult<>();
                result.error = error;
                resultLiveData.setValue(result);
            }

            @Override
            public void onDisconnected() {
                DatabaseResult<Account> result = new DatabaseResult<>();
                result.disconnect = true;
                resultLiveData.setValue(result);
            }

            @Override
            public void onNetworkError() {
                DatabaseResult<Account> result = new DatabaseResult<>();
                result.networkError = true;
                resultLiveData.setValue(result);
            }
        });

        return resultLiveData;
    }

    public LiveData<DatabaseResult> saveUser(Context context, Account account) {
        MutableLiveData<DatabaseResult> resultLiveData = new MutableLiveData<>();
        UserManager.SaveDataListener listener = new UserManager.SaveDataListener() {
            @Override
            public void onComplete() {
                DatabaseResult result = new DatabaseResult();
                result.complete = true;
                resultLiveData.setValue(result);
            }

            @Override
            public void onNetworkError() {
                DatabaseResult result = new DatabaseResult();
                result.networkError = true;
                resultLiveData.setValue(result);
            }

            @Override
            public void onError(Exception error) {
                DatabaseResult result = new DatabaseResult();
                result.error = error;
                resultLiveData.setValue(result);
            }
        };
        if (account.getId() == null) {
            DatabaseFactory.getUserManager().saveAccount(account, listener);
        } else {
            DatabaseFactory.getUserManager().saveAccount(account.getId(), account, listener);
        }
        if (account.getPicture() != null) {
            DatabaseFactory.getLocalUserManager().saveAccountImage(context, account.getId(), account.getPicture().getPath());
        }
        return resultLiveData;
    }

    public static class DatabaseResult<T> {
        public T data;
        public boolean complete;
        public Exception error;

        public boolean disconnect;
        public boolean networkError;
    }
}

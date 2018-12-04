package com.simonk.project.ppoproject.repository;

import com.simonk.project.ppoproject.auth.AuthFactory;
import com.simonk.project.ppoproject.database.DatabaseFactory;
import com.simonk.project.ppoproject.database.UserManager;
import com.simonk.project.ppoproject.model.Account;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

    public LiveData<DatabaseResult<Account>> getCurrentUser() {
        MutableLiveData<DatabaseResult<Account>> resultLiveData = new MutableLiveData<>();

        String userId = AuthFactory.getAuthManager().getCurrentUserId();
        DatabaseFactory.getUserManager().getUserById(userId, new UserManager.RetrieveDataListener<Account>() {
            @Override
            public void onComplete(Account data) {
                DatabaseResult<Account> result = new DatabaseResult<>();
                result.data = data;
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

    public LiveData<DatabaseResult> saveUser(Account account) {
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

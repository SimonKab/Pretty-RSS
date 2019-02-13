package com.simonk.project.prettyrss.database;

import com.simonk.project.prettyrss.model.Account;

public interface UserManager {

    interface RetrieveDataListener<T> {
        void onComplete(T data);
        void onError(Exception error);
        void onDisconnected();
        void onNetworkError();
    }

    interface SaveDataListener {
        void onComplete();
        void onNetworkError();
        void onError(Exception error);
    }

    void getUserById(String userId, RetrieveDataListener<Account> dataListener);

    void saveAccount(Account account, SaveDataListener listener);
    void saveAccount(String userId, Account account, SaveDataListener listener);
}

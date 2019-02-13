package com.simonk.project.prettyrss.database.local.providers;

import com.simonk.project.prettyrss.database.local.entyties.AccountEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface AccountProvider {

    @Query("SELECT * FROM accounts WHERE id=:id")
    LiveData<AccountEntity> loadAccount(String id);

    @Query("SELECT * FROM accounts WHERE id=:id")
    AccountEntity getAccount(String id);

    @Query("SELECT * FROM accounts")
    LiveData<List<AccountEntity>> loadAccounts();

    @Insert
    void insertAccount(AccountEntity entity);

    @Update
    void updateAccount(AccountEntity entity);

    @Delete
    void deleteAccount(AccountEntity entity);

}

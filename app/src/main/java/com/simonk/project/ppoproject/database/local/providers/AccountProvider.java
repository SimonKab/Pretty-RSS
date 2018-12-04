package com.simonk.project.ppoproject.database.local.providers;

import com.simonk.project.ppoproject.database.local.entyties.AccountEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface AccountProvider {

    @Query("SELECT * FROM accounts")
    LiveData<List<AccountEntity>> loadAllAccounts();

    @Query("SELECT * FROM accounts WHERE id=:id")
    LiveData<AccountEntity> loadAccount(int id);

    @Query("SELECT * FROM accounts WHERE main!=0")
    LiveData<AccountEntity> loadMainAccount();

    @Insert
    void insertAccount(AccountEntity entity);

    @Update
    void updateAccount(AccountEntity entity);

    @Delete
    void deleteAccount(AccountEntity entity);

}

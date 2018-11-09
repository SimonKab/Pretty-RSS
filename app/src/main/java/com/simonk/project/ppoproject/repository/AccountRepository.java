package com.simonk.project.ppoproject.repository;

import android.content.Context;
import android.os.AsyncTask;

import com.simonk.project.ppoproject.database.Database;
import com.simonk.project.ppoproject.database.adapters.AccountAdapter;
import com.simonk.project.ppoproject.database.entyties.AccountEntity;
import com.simonk.project.ppoproject.model.Account;

import java.lang.ref.WeakReference;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

public class AccountRepository {

    private static AccountRepository sInstance;

    private final Database mDatabase;

    private LiveData<Account> mMainAccount;

    private AccountRepository(Context context) {
        mDatabase = Database.getInstance(context);

        mMainAccount = transformAccountEntityToAccount(
                Database.getInstance(context).accountProvider().loadMainAccount());
    }

    public static AccountRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AccountRepository.class) {
                if (sInstance == null) {
                    sInstance = new AccountRepository(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    public LiveData<Account> getAccount(int id) {
        return transformAccountEntityToAccount(mDatabase.accountProvider().loadAccount(id));
    }

    public LiveData<Account> getMainAccount() {
        return mMainAccount;
    }

    public void saveAccount(Account account) {
        new ChangeDatabase(() -> {
            mDatabase.accountProvider().insertAccount(AccountAdapter.convertAccountToAccountEntity(account));
        }).execute();
    }

    public void updateAccount(Account account) {
        new ChangeDatabase(() -> {
            mDatabase.accountProvider().updateAccount(AccountAdapter.convertAccountToAccountEntity(account));
        }).execute();
    }

    private LiveData<Account> transformAccountEntityToAccount(LiveData<AccountEntity> entity) {
        return Transformations.map(entity,
                AccountAdapter::convertAccountEntityToAccount);
    }

    private static class ChangeDatabase extends AsyncTask<Void, Void, Void> {

        private WeakReference<ChangeAction> mChangeActionWeakReference;

        public interface ChangeAction {
            void change();
        }

        ChangeDatabase(ChangeAction action) {
            mChangeActionWeakReference = new WeakReference<>(action);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (mChangeActionWeakReference != null) {
                mChangeActionWeakReference.get().change();
            }
            return null;
        }
    }
}

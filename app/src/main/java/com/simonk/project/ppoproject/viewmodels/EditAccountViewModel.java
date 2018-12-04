package com.simonk.project.ppoproject.viewmodels;

import android.app.Application;

import com.simonk.project.ppoproject.model.Account;
import com.simonk.project.ppoproject.repository.AccountRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

public class EditAccountViewModel extends AndroidViewModel {

    private final MutableLiveData<Account> mEditedAccount;

    public EditAccountViewModel(@NonNull Application application) {
        super(application);

        mEditedAccount = new MutableLiveData<>();
    }

    public LiveData<AccountRepository.DatabaseResult<Account>> getInitialAccount() {
        LiveData<AccountRepository.DatabaseResult<Account>> result =
                AccountRepository.getInstance().getCurrentUser();
        result.observeForever(new Observer<AccountRepository.DatabaseResult<Account>>() {
            @Override
            public void onChanged(AccountRepository.DatabaseResult<Account> accountDatabaseResult) {
                if (accountDatabaseResult.data != null) {
                    mEditedAccount.setValue(accountDatabaseResult.data);
                }
            }
        });
        return result;
    }

    public LiveData<Account> getAccount() {
        return mEditedAccount;
    }

    public void setAccount(String firstName, String lastName,
                           String telephone, String address) {
        Account account = new Account();
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setAddress(address);
        account.setTelephone(telephone);
        mEditedAccount.setValue(account);
    }

    public LiveData<AccountRepository.DatabaseResult> saveAccount() {
        Account account = mEditedAccount.getValue();
        if (account == null) {
            throw new IllegalStateException("Account to save is null");
        }
        account.setEmail(AccountRepository.getInstance().getCurrentUserEmail());
        account.setId(AccountRepository.getInstance().getCurrentUserId());
        return AccountRepository.getInstance().saveUser(account);
    }
}

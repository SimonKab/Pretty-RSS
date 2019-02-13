package com.simonk.project.prettyrss.viewmodels;

import android.app.Application;

import com.simonk.project.prettyrss.model.Account;
import com.simonk.project.prettyrss.model.Picture;
import com.simonk.project.prettyrss.repository.AccountRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

public class EditAccountViewModel extends AndroidViewModel {

    private final MutableLiveData<Account> mEditedAccount;
    private final MutableLiveData<Account> mInitialAccount;

    public EditAccountViewModel(@NonNull Application application) {
        super(application);

        mEditedAccount = new MutableLiveData<>();
        mInitialAccount = new MutableLiveData<>();
    }

    public LiveData<AccountRepository.DatabaseResult<Account>> fetchInitialAccount() {
        LiveData<AccountRepository.DatabaseResult<Account>> result =
                AccountRepository.getInstance().getCurrentUser(getApplication().getApplicationContext());
        result.observeForever(new Observer<AccountRepository.DatabaseResult<Account>>() {
            @Override
            public void onChanged(AccountRepository.DatabaseResult<Account> accountDatabaseResult) {
                if (accountDatabaseResult.data != null) {
                    mInitialAccount.setValue(accountDatabaseResult.data);
                }
            }
        });
        return result;
    }

    public LiveData<String> getCurrentUserImage() {
        return AccountRepository.getInstance().getCurrentUserImage(getApplication().getApplicationContext());
    }

    public LiveData<Account> getInitialAccount() {
        return mInitialAccount;
    }

    public LiveData<Account> getEditedAccount() {
        return mEditedAccount;
    }

    public void setEditedAccount(String firstName, String lastName,
                           String telephone, String address) {
        Account account = mEditedAccount.getValue();
        if (account == null) {
            account = new Account();
        }

        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setAddress(address);
        account.setTelephone(telephone);
        mEditedAccount.setValue(account);
    }

    public void setImagePath(String path) {
        Picture picture = new Picture();
        picture.setPath(path);

        Account account = mEditedAccount.getValue();
        if (account == null) {
            account = new Account();
        }

        account.setPicrute(picture);
        mEditedAccount.setValue(account);
    }

    public LiveData<AccountRepository.DatabaseResult> saveEditedAccount() {
        Account account = mEditedAccount.getValue();
        if (account == null) {
            throw new IllegalStateException("Account to save is null");
        }
        account.setEmail(AccountRepository.getInstance().getCurrentUserEmail());
        account.setId(AccountRepository.getInstance().getCurrentUserId());
        return AccountRepository.getInstance().saveUser(getApplication().getApplicationContext(), account);
    }
}

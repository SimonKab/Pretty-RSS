package com.simonk.project.ppoproject.viewmodels;

import android.app.Application;

import com.simonk.project.ppoproject.model.Account;
import com.simonk.project.ppoproject.repository.AccountRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EditAccountViewModel extends AndroidViewModel {

    private final LiveData<Account> mAccount;

    private final MutableLiveData<Account> mEditedAccount;

    private AccountRepository mRepository;

    public EditAccountViewModel(@NonNull Application application) {
        super(application);

        mRepository = AccountRepository.getInstance(application.getApplicationContext());

        mAccount = mRepository.getMainAccount();
        mEditedAccount = new MutableLiveData<>();
        mEditedAccount.setValue(mAccount.getValue());
    }

    public LiveData<Account> getAccount() {
        return mEditedAccount;
    }

    public void setAccount(Account account) {
        mEditedAccount.setValue(account);
    }

    public void saveAccount() {
        if (mAccount.getValue() == null) {
            mRepository.saveAccount(mEditedAccount.getValue());
        } else {
            mRepository.updateAccount(mEditedAccount.getValue());
        }
    }
}

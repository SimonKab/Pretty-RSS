package com.simonk.project.ppoproject.viewmodels;

import android.app.Application;

import com.simonk.project.ppoproject.model.Account;
import com.simonk.project.ppoproject.repository.AccountRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AccountViewModel extends AndroidViewModel {

    private final LiveData<Account> mAccount;

    public AccountViewModel(@NonNull Application application) {
        super(application);

        mAccount = AccountRepository.getInstance(application.getApplicationContext())
                .getMainAccount();
    }

    public LiveData<Account> getAccount() {
        return mAccount;
    }


}

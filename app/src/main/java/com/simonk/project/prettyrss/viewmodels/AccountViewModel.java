package com.simonk.project.prettyrss.viewmodels;

import android.app.Application;

import com.simonk.project.prettyrss.model.Account;
import com.simonk.project.prettyrss.repository.AccountRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AccountViewModel extends AndroidViewModel {

    public AccountViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<AccountRepository.DatabaseResult<Account>> getCurrentAccount() {
        return AccountRepository.getInstance().getCurrentUser(getApplication().getApplicationContext());
    }

    public LiveData<String> getCurrentUserImage() {
        return AccountRepository.getInstance().getCurrentUserImage(getApplication().getApplicationContext());
    }
}

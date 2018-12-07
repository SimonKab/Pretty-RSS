package com.simonk.project.ppoproject.viewmodels;

import android.app.Application;

import com.simonk.project.ppoproject.model.Account;
import com.simonk.project.ppoproject.repository.AccountRepository;
import com.simonk.project.ppoproject.repository.LoginRepository;

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

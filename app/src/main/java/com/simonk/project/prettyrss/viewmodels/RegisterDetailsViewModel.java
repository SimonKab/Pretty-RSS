package com.simonk.project.prettyrss.viewmodels;

import android.app.Application;

import com.simonk.project.prettyrss.model.Account;
import com.simonk.project.prettyrss.repository.AccountRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class RegisterDetailsViewModel extends AndroidViewModel {

    public RegisterDetailsViewModel(@NonNull Application application) {
        super(application);
    }

    public String getCurrentUserEmail() {
        return AccountRepository.getInstance().getCurrentUserEmail();
    }

    public LiveData<AccountRepository.DatabaseResult> saveData(String firstName, String lastName,
                                                             String email, String address,
                                                             String telephone) {
        Account account = new Account();
        account.setId(AccountRepository.getInstance().getCurrentUserId());
        account.setFirstName(firstName);
        account.setLastName(lastName);
        account.setAddress(address);
        account.setEmail(email);
        account.setTelephone(telephone);
        return AccountRepository.getInstance().saveUser(getApplication().getApplicationContext(), account);
    }

}

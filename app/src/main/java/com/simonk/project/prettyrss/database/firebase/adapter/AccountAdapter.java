package com.simonk.project.prettyrss.database.firebase.adapter;

import com.simonk.project.prettyrss.database.firebase.model.AccountFirebaseModel;
import com.simonk.project.prettyrss.model.Account;

public class AccountAdapter {

    public static Account convertFirebaseToModelAccount(AccountFirebaseModel model) {
        if (model == null) {
            return null;
        }

        Account account = new Account();
        account.setFirstName(model.firstName);
        account.setLastName(model.lastName);
        account.setTelephone(model.telephone);
        account.setEmail(model.email);
        account.setAddress(model.address);

        return account;
    }

    public static AccountFirebaseModel convertModelToFirebaseAccount(Account account) {
        if (account == null) {
            return null;
        }

        AccountFirebaseModel model = new AccountFirebaseModel();
        model.firstName = account.getFirstName();
        model.lastName = account.getLastName();
        model.telephone = account.getTelephone();
        model.email = account.getEmail();
        model.address = account.getAddress();

        return model;
    }
}

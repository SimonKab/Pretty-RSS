package com.simonk.project.ppoproject.database.firebase.adapter;

import com.simonk.project.ppoproject.database.firebase.model.AccountFirebaseModel;
import com.simonk.project.ppoproject.model.Account;
import com.simonk.project.ppoproject.model.Picture;

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

        Picture picture = new Picture();
        picture.setPath(model.picture);
        account.setPicrute(picture);

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
        model.picture = account.getPicture() == null ? null : account.getPicture().getPath();

        return model;
    }
}

package com.simonk.project.prettyrss.database.local.adapters;

import com.simonk.project.prettyrss.database.local.entyties.AccountEntity;
import com.simonk.project.prettyrss.model.Account;
import com.simonk.project.prettyrss.model.Picture;

public class AccountAdapter {

    public static Account convertAccountEntityToAccount(AccountEntity entity) {
        if (entity == null) {
            return null;
        }
        Account account = new Account();
        account.setId(String.valueOf(entity.id));

        Picture picture = new Picture();
        picture.setPath(entity.picture);
        account.setPicrute(picture);

        return account;
    }


    public static AccountEntity convertAccountToAccountEntity(Account account) {
        if (account == null) {
            return null;
        }
        AccountEntity entity = new AccountEntity();
        entity.id = account.getId();

        if (account.getPicture() != null) {
            entity.picture = account.getPicture().getPath();
        }

        return entity;
    }

}

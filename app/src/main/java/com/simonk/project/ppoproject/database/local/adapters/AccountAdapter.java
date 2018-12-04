package com.simonk.project.ppoproject.database.local.adapters;

import com.simonk.project.ppoproject.database.local.entyties.AccountEntity;
import com.simonk.project.ppoproject.model.Account;
import com.simonk.project.ppoproject.model.Picture;

public class AccountAdapter {

    public static Account convertAccountEntityToAccount(AccountEntity entity) {
        if (entity == null) {
            return null;
        }
        Account account = new Account();
        account.setId(String.valueOf(entity.id));
        account.setFirstName(entity.firstName);
        account.setLastName(entity.lastName);
        account.setTelephone(entity.telephone);
        account.setAddress(entity.address);
        account.setMain(entity.main);
        account.setEmail(entity.email);

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
        entity.id = Integer.parseInt(account.getId());
        entity.firstName = account.getFirstName();
        entity.lastName = account.getLastName();
        entity.telephone = account.getTelephone();
        entity.address = account.getAddress();
        entity.main = account.isMain();
        entity.email = account.getEmail();
        if (account.getPicture() != null) {
            entity.picture = account.getPicture().getPath();
        }

        return entity;
    }

}

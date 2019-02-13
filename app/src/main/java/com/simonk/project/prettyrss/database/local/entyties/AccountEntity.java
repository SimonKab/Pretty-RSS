package com.simonk.project.prettyrss.database.local.entyties;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts")
public class AccountEntity {

    @PrimaryKey()
    @NonNull
    public String id;

    public String picture;

}

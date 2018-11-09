package com.simonk.project.ppoproject.database.entyties;

import com.simonk.project.ppoproject.model.Picture;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts")
public class AccountEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String picture;
    public String firstName;
    public String lastName;
    public String telephone;
    public String address;
    public String email;
    public boolean main;

}
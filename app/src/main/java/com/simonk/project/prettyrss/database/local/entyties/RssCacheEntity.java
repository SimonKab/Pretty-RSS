package com.simonk.project.prettyrss.database.local.entyties;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "rss_cache")
public class RssCacheEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String userId;
    public String rss;

}

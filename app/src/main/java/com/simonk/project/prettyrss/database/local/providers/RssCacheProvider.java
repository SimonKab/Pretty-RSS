package com.simonk.project.prettyrss.database.local.providers;

import com.simonk.project.prettyrss.database.local.entyties.RssCacheEntity;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface RssCacheProvider {

    @Query("SELECT * FROM rss_cache WHERE userId=:userId")
    RssCacheEntity loadRssCache(String userId);

    @Insert
    void insertRssCache(RssCacheEntity entity);

    @Update
    void updateRssCache(RssCacheEntity entity);

}

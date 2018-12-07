package com.simonk.project.ppoproject.database.local.providers;

import com.simonk.project.ppoproject.database.local.entyties.AccountEntity;
import com.simonk.project.ppoproject.database.local.entyties.RssCacheEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
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

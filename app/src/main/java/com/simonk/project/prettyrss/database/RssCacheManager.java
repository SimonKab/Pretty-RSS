package com.simonk.project.prettyrss.database;

import android.content.Context;

import com.simonk.project.prettyrss.rss.RssChannel;

import androidx.lifecycle.LiveData;

public interface RssCacheManager {

    void saveCache(Context context, String userId, RssChannel rssChannel);
    LiveData<RssChannel> loadCache(Context context, String userId);

}

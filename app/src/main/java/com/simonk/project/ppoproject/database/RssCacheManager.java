package com.simonk.project.ppoproject.database;

import android.content.Context;

import com.simonk.project.ppoproject.rss.RssChannel;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface RssCacheManager {

    void saveCache(Context context, String userId, RssChannel rssChannel);
    LiveData<RssChannel> loadCache(Context context, String userId);

}

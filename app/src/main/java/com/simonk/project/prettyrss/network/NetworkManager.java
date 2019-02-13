package com.simonk.project.prettyrss.network;

import android.net.Uri;

import com.simonk.project.prettyrss.rss.RssChannel;

public interface NetworkManager {

    interface NetworkManagerCallback<T> {
        void onComplete(T data);
        void onError(Throwable error);
    }

    void getRssNews(Uri uri, NetworkManagerCallback<RssChannel> callback);

}

package com.simonk.project.ppoproject.network;

import android.net.Uri;

import com.simonk.project.ppoproject.rss.RssChannel;

public interface NetworkManager {

    interface NetworkManagerCallback<T> {
        void onComplete(T data);
        void onError(Throwable error);
    }

    void getRssNews(Uri uri, NetworkManagerCallback<RssChannel> callback);

}

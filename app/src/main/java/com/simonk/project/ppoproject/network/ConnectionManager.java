package com.simonk.project.ppoproject.network;

import android.content.Context;

public interface ConnectionManager {

    interface ConnectionCallback {
        void onConnected();
        void onInternetAccess();
        void onDisconnected();
        void onError(Throwable error);
    }

    void registerCallback(ConnectionCallback callback);

    void unregisterCallback(ConnectionCallback callback);

    boolean isConnected();

}

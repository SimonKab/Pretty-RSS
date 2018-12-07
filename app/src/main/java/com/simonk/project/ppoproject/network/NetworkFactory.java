package com.simonk.project.ppoproject.network;

import android.content.Context;

import com.simonk.project.ppoproject.network.retrofit.RetrofitNetworkManager;

public class NetworkFactory {

    public static NetworkManager getNeworkManager() {
        return new RetrofitNetworkManager();
    }

    public static ConnectionManager getConnectionManager() {
        return ConnectionManagerImpl.getInstance();
    }
}

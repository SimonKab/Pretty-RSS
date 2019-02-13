package com.simonk.project.prettyrss.network;

import com.simonk.project.prettyrss.network.retrofit.RetrofitNetworkManager;

public class NetworkFactory {

    public static NetworkManager getNeworkManager() {
        return new RetrofitNetworkManager();
    }

    public static ConnectionManager getConnectionManager() {
        return ConnectionManagerImpl.getInstance();
    }
}

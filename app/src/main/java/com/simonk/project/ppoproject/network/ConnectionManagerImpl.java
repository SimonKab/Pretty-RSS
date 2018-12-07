package com.simonk.project.ppoproject.network;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simonk.project.ppoproject.repository.AccountRepository;

import java.util.ArrayList;
import java.util.List;

public class ConnectionManagerImpl implements ConnectionManager {

    private static ConnectionManagerImpl sInstance;

    private List<ConnectionCallback> mCallbacks;

    private boolean mIsConnected;

    private ConnectionManagerImpl() {
        mCallbacks = new ArrayList<>();

        registerFirebaseCallback();
    }

    public static ConnectionManagerImpl getInstance() {
        if (sInstance == null) {
            synchronized (AccountRepository.class) {
                if (sInstance == null) {
                    sInstance = new ConnectionManagerImpl();
                }
            }
        }
        return sInstance;
    }

    public void registerCallback(ConnectionCallback callback) {
        mCallbacks.add(callback);
    }

    public void unregisterCallback(ConnectionCallback callback) {
        mCallbacks.remove(callback);
    }

    @Override
    public boolean isConnected() {
        return mIsConnected;
    }

    private void registerFirebaseCallback() {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    onConnected();
                } else {
                    onDisconnected();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                onError(error.toException());
            }
        });
    }

    private void onConnected() {
        mIsConnected = true;
        for (ConnectionCallback callback : mCallbacks) {
            if (callback != null) {
                callback.onConnected();
            }
        }
    }

    private void onDisconnected() {
        mIsConnected = false;
        for (ConnectionCallback callback : mCallbacks) {
            if (callback != null) {
                callback.onDisconnected();
            }
        }
    }

    private void onInternetAccess() {
        for (ConnectionCallback callback : mCallbacks) {
            if (callback != null) {
                callback.onInternetAccess();
            }
        }
    }

    private void onError(Throwable t) {
        for (ConnectionCallback callback : mCallbacks) {
            if (callback != null) {
                callback.onError(t);
            }
        }
    }
}

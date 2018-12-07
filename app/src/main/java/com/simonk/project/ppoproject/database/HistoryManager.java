package com.simonk.project.ppoproject.database;

import android.content.Context;

import com.simonk.project.ppoproject.model.HistoryEntry;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface HistoryManager {

    int MAX_HISTORY_SIZE = 20;

    interface RetrieveDataListener<T> {
        void onComplete(T data);
        void onError(Exception error);
        void onDisconnected();
        void onNetworkError();
    }

    interface SaveDataListener {
        void onComplete();
        void onNetworkError();
        void onError(Exception error);
    }

    void getHistoryEntries(String userId, RetrieveDataListener<List<HistoryEntry>> listener);

    void getLastEntryInHistory(String userId, RetrieveDataListener<String> listener);

    void getSources(String userId, RetrieveDataListener<List<HistoryEntry>> listener);

    void addHistoryEntry(String userId, HistoryEntry historyEntry, SaveDataListener listener);

    void addSourceEntry(String userId, HistoryEntry historyEntry, SaveDataListener listener);

    void removeHistoryEntry(String userId, HistoryEntry historyEntry);

}

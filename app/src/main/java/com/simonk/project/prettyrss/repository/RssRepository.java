package com.simonk.project.prettyrss.repository;

import android.content.Context;
import android.net.Uri;

import com.bumptech.glide.Glide;
import com.simonk.project.prettyrss.database.DatabaseFactory;
import com.simonk.project.prettyrss.database.HistoryManager;
import com.simonk.project.prettyrss.model.HistoryEntry;
import com.simonk.project.prettyrss.network.NetworkFactory;
import com.simonk.project.prettyrss.network.NetworkManager;
import com.simonk.project.prettyrss.rss.RssChannel;

import java.net.UnknownHostException;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class RssRepository {

    private static RssRepository sInstance;

    private RssRepository() {
    }

    public static RssRepository getInstance() {
        if (sInstance == null) {
            synchronized (RssRepository.class) {
                if (sInstance == null) {
                    sInstance = new RssRepository();
                }
            }
        }
        return sInstance;
    }

    public LiveData<RemoteServerResult<RssChannel>> getRssChannel(Context context, Uri uri) {
        MutableLiveData<RemoteServerResult<RssChannel>> resultLiveData = new MutableLiveData<>();

        if (uri == null) {
            RemoteServerResult<RssChannel> result = new RemoteServerResult<>(null);
            resultLiveData.setValue(result);
        } else {
            NetworkFactory.getNeworkManager().getRssNews(uri, new NetworkManager.NetworkManagerCallback<RssChannel>() {
                @Override
                public void onComplete(RssChannel data) {
                    RemoteServerResult<RssChannel> result = new RemoteServerResult<>(uri);
                    result.data = data;
                    resultLiveData.setValue(result);

                    DatabaseFactory.getRssCacheManager().saveCache(context, AccountRepository.getInstance().getCurrentUserId(), data);
                }

                @Override
                public void onError(Throwable error) {
                    if (error instanceof UnknownHostException) {
                        LiveData<RssChannel> rssChannelLiveData
                                = DatabaseFactory.getRssCacheManager().loadCache(context, AccountRepository.getInstance().getCurrentUserId());
                        rssChannelLiveData.observeForever(new Observer<RssChannel>() {
                                @Override
                                public void onChanged(RssChannel rssChannel) {
                                    if (rssChannel == null) {
                                        return;
                                    }

                                    if (rssChannel.getSourceUri() != null
                                            && rssChannel.getSourceUri().equals(uri)) {
                                        RemoteServerResult<RssChannel> result = new RemoteServerResult<>(uri);
                                        result.data = rssChannel;
                                        resultLiveData.setValue(result);
                                    } else {
                                        RemoteServerResult<RssChannel> result = new RemoteServerResult<>(uri);
                                        result.error = error;
                                        resultLiveData.setValue(result);
                                    }

                                    rssChannelLiveData.removeObserver(this);
                                }
                            });
                    } else {
                        RemoteServerResult<RssChannel> result = new RemoteServerResult<>(uri);
                        result.error = error;
                        resultLiveData.setValue(result);
                    }
                }
            });
        }

        return resultLiveData;
    }

    public LiveData<DatabaseResult<List<HistoryEntry>>> getHistory() {
        MutableLiveData<DatabaseResult<List<HistoryEntry>>> resultLiveData = new MutableLiveData<>();

        String userId = AccountRepository.getInstance().getCurrentUserId();
        DatabaseFactory.getHistoryManager().getHistoryEntries(userId, new HistoryManager.RetrieveDataListener<List<HistoryEntry>>() {
            @Override
            public void onComplete(List<HistoryEntry> data) {
                DatabaseResult<List<HistoryEntry>> result = new DatabaseResult<>();
                result.data = data;
                result.complete = true;
                resultLiveData.setValue(result);
            }

            @Override
            public void onError(Exception error) {
                DatabaseResult<List<HistoryEntry>> result = new DatabaseResult<>();
                result.error = error;
                resultLiveData.setValue(result);
            }

            @Override
            public void onDisconnected() {
                DatabaseResult<List<HistoryEntry>> result = new DatabaseResult<>();
                result.disconnect = true;
                resultLiveData.setValue(result);
            }

            @Override
            public void onNetworkError() {
                DatabaseResult<List<HistoryEntry>> result = new DatabaseResult<>();
                result.networkError = true;
                resultLiveData.setValue(result);
            }
        });

        return resultLiveData;
    }

    public LiveData<DatabaseResult<List<HistoryEntry>>> getSources() {
        MutableLiveData<DatabaseResult<List<HistoryEntry>>> resultLiveData = new MutableLiveData<>();

        String userId = AccountRepository.getInstance().getCurrentUserId();
        DatabaseFactory.getHistoryManager().getSources(userId, new HistoryManager.RetrieveDataListener<List<HistoryEntry>>() {
            @Override
            public void onComplete(List<HistoryEntry> data) {
                DatabaseResult<List<HistoryEntry>> result = new DatabaseResult<>();
                result.data = data;
                result.complete = true;
                resultLiveData.setValue(result);
            }

            @Override
            public void onError(Exception error) {
                DatabaseResult<List<HistoryEntry>> result = new DatabaseResult<>();
                result.error = error;
                resultLiveData.setValue(result);
            }

            @Override
            public void onDisconnected() {
                DatabaseResult<List<HistoryEntry>> result = new DatabaseResult<>();
                result.disconnect = true;
                resultLiveData.setValue(result);
            }

            @Override
            public void onNetworkError() {
                DatabaseResult<List<HistoryEntry>> result = new DatabaseResult<>();
                result.networkError = true;
                resultLiveData.setValue(result);
            }
        });

        return resultLiveData;
    }

    public LiveData<DatabaseResult<String>> getLastEntryInHistory() {
        MutableLiveData<DatabaseResult<String>> resultLiveData = new MutableLiveData<>();

        String userId = AccountRepository.getInstance().getCurrentUserId();

        DatabaseFactory.getHistoryManager().getLastEntryInHistory(userId, new HistoryManager.RetrieveDataListener<String>() {
            @Override
            public void onComplete(String data) {
                DatabaseResult<String> result = new DatabaseResult<>();
                result.data = data;
                result.complete = true;
                resultLiveData.setValue(result);
            }

            @Override
            public void onError(Exception error) {
                DatabaseResult<String> result = new DatabaseResult<>();
                result.error = error;
                resultLiveData.setValue(result);
            }

            @Override
            public void onDisconnected() {
                DatabaseResult<String> result = new DatabaseResult<>();
                result.disconnect = true;
                resultLiveData.setValue(result);
            }

            @Override
            public void onNetworkError() {
                DatabaseResult<String> result = new DatabaseResult<>();
                result.networkError = true;
                resultLiveData.setValue(result);
            }
        });

        return resultLiveData;
    }

    public LiveData<DatabaseResult> saveHistoryEntry(HistoryEntry entry) {
        MutableLiveData<DatabaseResult> resultLiveData = new MutableLiveData<>();

        String userId = AccountRepository.getInstance().getCurrentUserId();
        DatabaseFactory.getHistoryManager().addHistoryEntry(userId, entry, new HistoryManager.SaveDataListener() {
            @Override
            public void onComplete() {
                DatabaseResult result = new DatabaseResult();
                result.complete = true;
                resultLiveData.setValue(result);
            }

            @Override
            public void onNetworkError() {
                DatabaseResult result = new DatabaseResult();
                result.networkError = true;
                resultLiveData.setValue(result);
            }

            @Override
            public void onError(Exception error) {
                DatabaseResult result = new DatabaseResult();
                result.error = error;
                resultLiveData.setValue(result);
            }
        });

        return resultLiveData;
    }

    public LiveData<DatabaseResult> saveSource(HistoryEntry entry) {
        MutableLiveData<DatabaseResult> resultLiveData = new MutableLiveData<>();

        String userId = AccountRepository.getInstance().getCurrentUserId();
        DatabaseFactory.getHistoryManager().addSourceEntry(userId, entry, new HistoryManager.SaveDataListener() {
            @Override
            public void onComplete() {
                DatabaseResult result = new DatabaseResult();
                result.complete = true;
                resultLiveData.setValue(result);
            }

            @Override
            public void onNetworkError() {
                DatabaseResult result = new DatabaseResult();
                result.networkError = true;
                resultLiveData.setValue(result);
            }

            @Override
            public void onError(Exception error) {
                DatabaseResult result = new DatabaseResult();
                result.error = error;
                resultLiveData.setValue(result);
            }
        });

        return resultLiveData;
    }

    public static class RemoteServerResult<T> {
        public T data;
        public Throwable error;
        public Uri sourceUri;

        public RemoteServerResult(Uri uri) {
            sourceUri = uri;
        }
    }

    public static class DatabaseResult<T> {
        public T data;
        public boolean complete;
        public Exception error;

        public boolean disconnect;
        public boolean networkError;
    }
}

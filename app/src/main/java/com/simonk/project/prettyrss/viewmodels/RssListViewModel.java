package com.simonk.project.prettyrss.viewmodels;

import android.app.Application;
import android.net.Uri;

import com.simonk.project.prettyrss.repository.RssRepository;
import com.simonk.project.prettyrss.rss.RssChannel;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

public class RssListViewModel extends AndroidViewModel {

    private MutableLiveData<RssRepository.RemoteServerResult<RssChannel>> mRssChannelResultLiveData;

    private MutableLiveData<Uri> mSourceUri;

    public RssListViewModel(@NonNull Application application) {
        super(application);

        mRssChannelResultLiveData = new MutableLiveData<>();
        mSourceUri = new MutableLiveData<>();

        LiveData<RssRepository.DatabaseResult<String>> history = RssRepository.getInstance().getLastEntryInHistory();
        history.observeForever(new Observer<RssRepository.DatabaseResult<String>>() {
            @Override
            public void onChanged(RssRepository.DatabaseResult<String> stringDatabaseResult) {
                if (stringDatabaseResult == null) {
                    return;
                }

                if (stringDatabaseResult.data != null) {
                    setRssSource(Uri.parse(stringDatabaseResult.data));
                }

                history.removeObserver(this);
            }
        });
    }

    private void fetchRssChannel(Uri uri) {
        RssRepository.getInstance().getRssChannel(getApplication().getApplicationContext(), uri)
                .observeForever(new Observer<RssRepository.RemoteServerResult<RssChannel>>() {
            @Override
            public void onChanged(RssRepository.RemoteServerResult<RssChannel> rssChannelRemoteServerResult) {
                mRssChannelResultLiveData.setValue(rssChannelRemoteServerResult);
            }
        });
    }

    public void forceFetchRssChannel() {
        fetchRssChannel(mSourceUri.getValue());
    }

    public LiveData<RssRepository.RemoteServerResult<RssChannel>> getRssChannel() {
        if (mRssChannelResultLiveData.getValue() == null) {
            fetchRssChannel(mSourceUri.getValue());
        }
        return mRssChannelResultLiveData;
    }


    public void setRssSource(Uri rssSource) {
        if (rssSource == null) {
            if (mSourceUri.getValue() == null) {
                return;
            }
            fetchRssChannel(null);
        } else {
            if (!rssSource.equals(mSourceUri.getValue())) {
                mSourceUri.setValue(rssSource);
                fetchRssChannel(rssSource);
            }
        }
    }

    public LiveData<Uri> getRssSource() {
        return mSourceUri;
    }
}

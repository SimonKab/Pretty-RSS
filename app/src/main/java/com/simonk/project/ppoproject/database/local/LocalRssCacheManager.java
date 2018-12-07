package com.simonk.project.ppoproject.database.local;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.simonk.project.ppoproject.database.RssCacheManager;
import com.simonk.project.ppoproject.database.local.entyties.AccountEntity;
import com.simonk.project.ppoproject.database.local.entyties.RssCacheEntity;
import com.simonk.project.ppoproject.rss.RssChannel;
import com.simonk.project.ppoproject.rss.RssDataProvider;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayOutputStream;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class LocalRssCacheManager implements RssCacheManager {

    @Override
    public void saveCache(Context context, String userId, RssChannel rssChannel) {
        new SaveRssCacheAsyncTask(context, userId, rssChannel).execute();
    }

    @Override
    public LiveData<RssChannel> loadCache(Context context, String userId) {
        MutableLiveData<RssChannel> rssChannelMutableLiveData = new MutableLiveData<>();
        new FetchRssCacheAsyncTask(context, userId, rssChannelMutableLiveData).execute();
        return rssChannelMutableLiveData;
    }

    private static class FetchRssCacheAsyncTask extends AsyncTask<Void, Void, RssChannel> {

        private String userId;

        private MutableLiveData<RssChannel> rssChannelMutableLiveData;

        @SuppressLint("StaticFieldLeak")
        private Context mApplicationContext;

        FetchRssCacheAsyncTask(Context context, String userId, MutableLiveData<RssChannel> rssChannelMutableLiveData) {
            mApplicationContext = context.getApplicationContext();
            this.userId = userId;
            this.rssChannelMutableLiveData = rssChannelMutableLiveData;
        }

        @Override
        protected RssChannel doInBackground(Void... voids) {
            RssCacheEntity entity = LocalDatabase.getInstance(mApplicationContext).rssCacheProvider().loadRssCache(userId);
            if (entity == null) {
                return new RssChannel();
            }

            Serializer serializer = new Persister();
            try {
                RssChannel rssChannel = serializer.read(RssChannel.class, entity.rss);
                RssDataProvider.removeHtmlTagsFromDescription(rssChannel);
                return rssChannel;
            } catch (Exception e) {
                return new RssChannel();
            }
        }

        @Override
        protected void onPostExecute(RssChannel result) {
            if (result != null) {
                rssChannelMutableLiveData.setValue(result);
            }
        }
    }

    private static class SaveRssCacheAsyncTask extends AsyncTask<Void, Void, Void> {

        private RssChannel channel;
        private String userId;

        @SuppressLint("StaticFieldLeak")
        private Context mApplicationContext;

        SaveRssCacheAsyncTask(Context context, String userId, RssChannel channel) {
            mApplicationContext = context.getApplicationContext();
            this.userId = userId;
            this.channel = channel;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RssCacheEntity entity = new RssCacheEntity();
            entity.userId = userId;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Serializer serializer = new Persister();
            try {
                serializer.write(channel, outputStream);
                entity.rss = outputStream.toString();

                RssCacheEntity rssCacheEntityExists
                        = LocalDatabase.getInstance(mApplicationContext).rssCacheProvider().loadRssCache(userId);
                if (rssCacheEntityExists == null) {
                    LocalDatabase.getInstance(mApplicationContext).rssCacheProvider().insertRssCache(entity);
                } else {
                    entity.id = rssCacheEntityExists.id;
                    LocalDatabase.getInstance(mApplicationContext).rssCacheProvider().updateRssCache(entity);
                }
            } catch (Exception e) {
                return null;
            }
            return null;
        }
    }

}

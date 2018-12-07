package com.simonk.project.ppoproject.network.retrofit;

import android.net.Uri;

import com.google.common.base.Strings;
import com.google.firebase.FirebaseException;
import com.simonk.project.ppoproject.network.NetworkManager;
import com.simonk.project.ppoproject.network.exceptions.InvalidRssPathException;
import com.simonk.project.ppoproject.rss.RssChannel;
import com.simonk.project.ppoproject.rss.RssDataProvider;
import com.simonk.project.ppoproject.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import javax.annotation.Nullable;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class RetrofitNetworkManager implements NetworkManager {

    private LoadRssNewsCallback mLoadRssNewsCallback;

    @Override
    public void getRssNews(Uri uri, NetworkManagerCallback<RssChannel> callback) {
        mLoadRssNewsCallback = new LoadRssNewsCallback(callback, uri);

        List<String> pathSegments =  uri.getPathSegments();
        String host = uri.getHost();
        if (Strings.isNullOrEmpty(host) || pathSegments == null
                || pathSegments.size() == 0) {
            callback.onError(new InvalidRssPathException(String.format("Invalid rss url: %s", uri.toString())));
            return;
        }

        String lastPathSegment = uri.getLastPathSegment();

        Uri.Builder builder = new Uri.Builder().scheme(uri.getScheme()).authority(host);
        pathSegments = new ArrayList<>(pathSegments);
        pathSegments.remove(pathSegments.size() - 1);
        for (String pathSegment : pathSegments) {
            builder.appendPath(pathSegment);
        }
        Uri withoutPathUri = builder.build();
        String withoutPathUriString = withoutPathUri.toString();
        if (!withoutPathUriString.endsWith("/")) {
            withoutPathUriString += "/";
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(withoutPathUriString)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        RssChannelApi rssChannelApi = retrofit.create(RssChannelApi.class);

        Call<RssChannel> call = rssChannelApi.loadRssNews(lastPathSegment);
        call.enqueue(mLoadRssNewsCallback);
    }

    private static class LoadRssNewsCallback implements Callback<RssChannel> {

        private NetworkManagerCallback<RssChannel> mCallback;
        private Uri mSourceUri;

        public LoadRssNewsCallback(NetworkManagerCallback<RssChannel> callback, Uri uri) {
            mCallback = callback;
            mSourceUri = uri;
        }

        @Override
        public void onResponse(Call<RssChannel> call, Response<RssChannel> response) {
            if (response.isSuccessful()) {
                RssChannel rssNews = response.body();
                rssNews.setSourceUri(mSourceUri);
                RssDataProvider.removeHtmlTagsFromDescription(rssNews);
                mCallback.onComplete(rssNews);
            } else {
                mCallback.onError(new InvalidRssPathException());
            }
        }

        @Override
        public void onFailure(Call<RssChannel> call, Throwable t) {
            mCallback.onError(t);
        }
    }
}

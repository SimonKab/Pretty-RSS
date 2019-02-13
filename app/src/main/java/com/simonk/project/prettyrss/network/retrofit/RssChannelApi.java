package com.simonk.project.prettyrss.network.retrofit;

import com.simonk.project.prettyrss.rss.RssChannel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RssChannelApi {

    @GET("{path}")
    Call<RssChannel> loadRssNews(@Path("path") String path);

}

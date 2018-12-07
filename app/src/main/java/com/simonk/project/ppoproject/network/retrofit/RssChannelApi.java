package com.simonk.project.ppoproject.network.retrofit;

import com.simonk.project.ppoproject.rss.RssChannel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RssChannelApi {

    @GET("{path}")
    Call<RssChannel> loadRssNews(@Path("path") String path);

}

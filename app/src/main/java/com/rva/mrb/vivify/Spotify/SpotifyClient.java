package com.rva.mrb.vivify.Spotify;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rigo on 7/8/16.
 */
public class SpotifyClient {

    private static final String BASE_URL = "https://api.spotify.com/v1";

    public SpotifyClient() {
//        OkHttpClient okHttpClient = new O
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
                .build();
    }
//    public Retrofit SpotifyClient() {
//        return new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//    }
}

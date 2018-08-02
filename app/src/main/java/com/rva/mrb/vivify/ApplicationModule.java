package com.rva.mrb.vivify;

import android.util.Log;

import com.rva.mrb.vivify.Model.Service.RealmService;
import com.rva.mrb.vivify.Spotify.NodeService;
import com.rva.mrb.vivify.Spotify.SpotifyService;

import java.io.IOException;
import java.util.logging.Level;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Bao on 6/24/16.
 */
@Module
public class ApplicationModule {

    private AlarmApplication mApp;
    final String SPOTIFY_URL = "https://api.spotify.com/v1/";
    final String NODE_URL = "https://5wnjr97ivi.execute-api.us-east-1.amazonaws.com/prod/";
    String accessToken;
    String refreshToken;

    public void setAccessToken(String token) {
        this.accessToken = token;
    }
    public String getAccessToken() { return this.accessToken; }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    public ApplicationModule(AlarmApplication app) {
        mApp = app;
    }

    @Provides
    Realm provideRealm() {
        return Realm.getDefaultInstance();
    }

    @Provides
    RealmService provideRealmService(final Realm realm) { return new RealmService(realm); }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .build();
                Log.d("Request", request.headers() + "");
                return chain.proceed(request);
            }
        });
        OkHttpClient client = builder.build();
        return client;
    }

    @Provides
    @Singleton
    public SpotifyService getSpotifyService(OkHttpClient client){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SPOTIFY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
        return retrofit.create(SpotifyService.class);
    }

    @Provides
    @Singleton
    public NodeService getNodeService(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NODE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(NodeService.class);
    }
}

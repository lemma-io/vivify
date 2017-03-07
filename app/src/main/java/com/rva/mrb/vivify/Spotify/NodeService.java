package com.rva.mrb.vivify.Spotify;

import android.net.Uri;

import com.rva.mrb.vivify.Model.Data.AccessToken;
import com.rva.mrb.vivify.Model.Data.Tokens;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Bao on 9/24/16.
 */
public interface NodeService {
    @GET("getTokens/{code}")
    Call<Tokens> getTokens(@Path("code") String code);

    @GET("refresh_token/{refresh_token}")
    Call<AccessToken> refreshToken(@Path("refresh_token") String refreshToken);
}

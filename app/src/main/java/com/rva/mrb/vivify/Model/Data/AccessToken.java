package com.rva.mrb.vivify.Model.Data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Bao on 9/24/16.
 */
public class AccessToken {
    @SerializedName("access_token")
    @Expose
    private String accessToken;

    @SerializedName("expires_in")
    @Expose
    private int expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }
}

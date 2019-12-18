package com.dev.mffa.mynote.Network.Model;

import com.google.gson.annotations.SerializedName;

public class User extends BaseResponse{
    @SerializedName("api_key")
    String apiKey;

    public String getApiKey() {
        return apiKey;
    }
}

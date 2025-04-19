package com.example.enggo.helpers;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WordRetrofitClient {
    private static final String BASE_URL = "https://api.dictionaryapi.dev/api/v2/";
    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

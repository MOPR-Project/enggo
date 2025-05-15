package com.example.enggo.helpers;

import com.example.enggo.service.TranslateApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TranslateRetrofitClient {
    private static final String BASE_URL = "https://clients5.google.com/";
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

    public static TranslateApiService getApi() {
        return getInstance().create(TranslateApiService.class);
    }
}


package com.example.enggo.service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TranslateApiService {
    @GET("translate_a/t?client=dict-chrome-ex&sl=en&tl=vi")
    Call<List<String>> translate(@Query("q") String word);
}


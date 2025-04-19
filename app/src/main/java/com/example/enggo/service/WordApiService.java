package com.example.enggo.service;

import com.example.enggo.data.WordResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WordApiService {

    @GET("entries/en/{word}")
    Call<List<WordResponse>> getWord(@Path("word") String word);
}

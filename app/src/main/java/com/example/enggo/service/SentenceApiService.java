package com.example.enggo.service;

import com.example.enggo.data.ApiResponse;
import com.example.enggo.data.SentenceResponse;
import com.example.enggo.data.SentenceSubmitRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SentenceApiService {
    @POST("/api/lession/enter-lession")
    Call<SentenceResponse> getSentences(@Query("level") int level);

    @POST("/api/lession/submit-sentences")
    Call<ApiResponse> submitSentence(@Body SentenceSubmitRequest request);

}


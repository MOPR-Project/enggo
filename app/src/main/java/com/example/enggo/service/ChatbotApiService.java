package com.example.enggo.service;

import com.example.enggo.data.ContentRequest;
import com.example.enggo.data.ContentResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ChatbotApiService {
    @POST("/v1beta/models/gemini-2.0-flash:generateContent")
    Call<ContentResponse> generateContent(
            @Query("key") String apiKey,
            @Body ContentRequest request
    );
}

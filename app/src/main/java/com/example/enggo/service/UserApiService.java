package com.example.enggo.service;

import com.example.enggo.data.ApiResponse;
import com.example.enggo.data.LoginRequest;
import com.example.enggo.data.LoginResponse;
import com.example.enggo.data.RegisterRequest;
import com.example.enggo.data.VerifyRegisterRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserApiService {
    @POST("/api/account/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
    @POST("/api/account/register")
    Call<ApiResponse> registerUser(@Body RegisterRequest registerRequest);
    @POST("/api/account/verify-register")
    Call<ApiResponse> verifyRegister(@Body VerifyRegisterRequest verifyRegisterRequest);
    @POST("/api/account/send-otp")
    Call<Map<String, Object>> sendOtp(@Body Map<String, String> body);
    @FormUrlEncoded
    @POST("/api/account/forget")
    Call<ApiResponse> forgetPassword(
            @Field("username") String username,
            @Field("email") String email
    );
    @FormUrlEncoded
    @POST("/api/account/verify-forget")
    Call<ApiResponse> verifyForgetPassword(
            @Field("username") String username,
            @Field("newPassword") String newPassword,
            @Field("email") String email,
            @Field("otpCode") String otpCode
    );
}

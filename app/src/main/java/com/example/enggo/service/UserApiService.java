package com.example.enggo.service;

import com.example.enggo.data.ApiResponse;
import com.example.enggo.data.LoginRequest;
import com.example.enggo.data.LoginResponse;
import com.example.enggo.data.RegisterRequest;
import com.example.enggo.data.UpdateProfileRequest;
import com.example.enggo.data.VerifyRegisterRequest;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UserApiService {
    @POST("/api/account/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);
    @POST("/api/account/login-anonymous")
    Call<LoginResponse> loginUserAnonymous();
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
    @Multipart
    @POST("/api/account/update-avatar")
    Call<ApiResponse> updateAvatar(
            @Part("username") RequestBody username,
            @Part("password") RequestBody password,
            @Part MultipartBody.Part avatar
    );
    @POST("/api/account/update-profile")
    Call<ApiResponse> updateProfile(@Body UpdateProfileRequest request);
    @FormUrlEncoded
    @POST("/api/account/upgrade-anonymous")
    Call<ApiResponse> upgradeAnonymous(
            @Field("oldUsername") String oldUsername,
            @Field("oldPassword") String oldPassword,
            @Field("newEmail") String newEmail
    );
    @FormUrlEncoded
    @POST("/api/account/verify-upgrade-anonymous")
    Call<ApiResponse> verifyUpgradeAnonymous(
            @Field("oldUsername") String oldUsername,
            @Field("oldPassword") String oldPassword,
            @Field("newUsername") String newUsername,
            @Field("newPassword") String newPassword,
            @Field("newEmail") String newEmail,
            @Field("otpCode") String otpCode
    );
    @POST("/api/account/login-google")
    Call<LoginResponse> loginWithGoogle(@Body Map<String, String> request);
}

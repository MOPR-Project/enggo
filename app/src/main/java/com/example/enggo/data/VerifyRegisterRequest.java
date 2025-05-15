package com.example.enggo.data;

public class VerifyRegisterRequest {
    private String username;
    private String email;
    private String password;
    private String otpCode;

    public VerifyRegisterRequest(String username, String email, String password, String otpCode)
    {
        this.username = username;
        this.email = email;
        this.password = password;
        this.otpCode = otpCode;
    }
}

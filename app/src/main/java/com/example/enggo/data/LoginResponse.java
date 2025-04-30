package com.example.enggo.data;

import com.example.enggo.models.Sentence;
import com.example.enggo.models.User;

import java.util.List;

public class LoginResponse {
    private String status;
    private User user;
    private String message;

    public String getStatus() {
        return status;
    }

    public User getUser() {
        return user;
    }

    public  String getMessage() {
        return message;
    }
}

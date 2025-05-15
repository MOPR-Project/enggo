package com.example.enggo.data;

public class UpdateProfileRequest {
    private String username;
    private String password;
    private String name;
    private String dateOfBirth;
    private String gender;

    public UpdateProfileRequest(String username, String password, String name, String dateOfBirth, String gender) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }
}

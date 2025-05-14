package com.example.enggo.models;

import java.io.Serializable;
import org.threeten.bp.LocalDate;

public class User implements Serializable {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String name;
    private String dateOfBirth;
    private String gender;
    private String avatar;
    private Integer streak;

    public User() {
    }

    public User(Long id, String username, String password, String email, String name,
                String dateOfBirth, String gender, String avatar) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.avatar = avatar;
    }

    public User(Long id, String username, String password, String email, String name,
                String dateOfBirth, String gender, String avatar, Integer streak) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.avatar = avatar;
        this.streak = streak;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getStreak()
    {
        return streak;
    }
}

package com.codepath.fbu_newsfeed;

import java.io.File;

public class User {
    private String username;
    private String fullName;
    private String email;
    private String password;
    private File profileImage;

    public User(String username, String fullName, String email, String password, File profileImage) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public File getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(File profileImage) {
        this.profileImage = profileImage;
    }
}

package com.codepath.fbu_newsfeed;

import java.io.File;

public class User {
    private String Username;
    private String fullName;
    private String Email;
    private String Password;
    private File profileImage;

    public User(String username, String fullName, String email, String password, File profileImage) {
        Username = username;
        this.fullName = fullName;
        Email = email;
        Password = password;
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public File getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(File profileImage) {
        this.profileImage = profileImage;
    }
}

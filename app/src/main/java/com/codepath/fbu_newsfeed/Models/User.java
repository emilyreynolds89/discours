package com.codepath.fbu_newsfeed.Models;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("_User")
public class User extends ParseUser {
    public static final String KEY_FULLNAME = "fullName";
    public static final String KEY_PROFILEIMAGE = "profileImage";
    public static final String KEY_BIO = "bio";
    public static final String KEY_USERNAME = "username";

    public String getUsers() {
        return getString(KEY_USERNAME);
    }
    public void setUsers(String userName) {
        put(KEY_USERNAME, userName);
    }

    public void setFullName(String fullName) {
        put(KEY_FULLNAME, fullName);
    }

    public String getFullName() {
        return getString(KEY_FULLNAME);
    }

    public void setProfileImage(ParseFile file) {
        put(KEY_PROFILEIMAGE, file);
    }

    public ParseFile getProfileImage() {
        return getParseFile(KEY_PROFILEIMAGE);
    }

}

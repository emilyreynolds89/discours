package com.codepath.fbu_newsfeed.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Reaction")
public class Reaction extends ParseObject {

    // types are : LIKE, DISLIKE, HAPPY, SAD, ANGRY

    public static final String KEY_USER = "user";
    public static final String KEY_SHARE = "share";
    public static final String KEY_TYPE = "type";

    private ParseUser user;
    private Share share;
    private String type;

    public Reaction() {
        super();
    }

    public Reaction(ParseUser user, Share share, String type) {
        super();

        this.user = user;
        put(KEY_USER, user);
        this.share = share;
        put(KEY_SHARE, share);
        this.type = type;
        put(KEY_TYPE, type);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        this.user = user;
        put(KEY_USER, user);
    }

    public Share getShare() {
        return (Share) getParseObject(KEY_SHARE) ;
    }

    public void setShare(Share share) {
        this.share = share;
        put(KEY_SHARE, share);
    }

    public String getType() {
        return getString(KEY_TYPE);
    }

    public void setType(String type) {
        this.type = type;
        put(KEY_TYPE, type);
    }

}

package com.codepath.fbu_newsfeed.Models;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String KEY_TEXT = "text";
    public static final String KEY_USER = "user";
    public static final String KEY_SHARE = "share";
    public static final String KEY_CLAP_COUNT = "clapCount";

    public Comment() {
        super();
    }

    public Comment(String text, User user, Share share) {
        super();
        put(KEY_TEXT, text);
        put(KEY_USER, user);
        put(KEY_SHARE, share);
        put(KEY_CLAP_COUNT, 0);
    }

    public String getText() {
        return getString(KEY_TEXT);
    }

    public void setText(String text) {
        put(KEY_TEXT, text);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public Share getShare() {
        return (Share) getParseObject(KEY_SHARE);
    }

    public void setShare(Share share) {
        put(KEY_SHARE, share);
    }

    public int getClapCount() {
        return (int) getNumber(KEY_CLAP_COUNT);
    }

    public void setClapCount(int count) {
        put(KEY_CLAP_COUNT, count);
    }

    public String getRelativeTime() {
        return (String) DateUtils.getRelativeTimeSpanString(getCreatedAt().getTime());
    }

    public int updateCount(boolean increment) {
        int count = getClapCount();
        if (increment) {
            count += 1;
        } else {
            count -= 1;
        }
        put(KEY_CLAP_COUNT, count);
        return count;
    }


}

package com.codepath.fbu_newsfeed.Models;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Notification")
public class Notification extends ParseObject {

    public static final String KEY_SEND_USER = "sendUser";
    public static final String KEY_RECEIVE_USER = "receiveUser";
    public static final String KEY_TYPE = "type";
    public static final String KEY_SHARE = "share";
    public static final String KEY_TYPE_TEXT = "typeText";
    public static final String KEY_CREATEDAT = "createdAt";
    public static final int LIMIT = 30;

    User sendUser;
    User receiveUser;
    String type; // REACTION or COMMENT
    Share share;
    String typeText;

    public Notification() {
        super();
    }

    public Notification(String type, User sender, User receiver, Share share, String typeText) {
        super();

        this.type = type;
        put(KEY_TYPE, type);

        this.sendUser = sender;
        put(KEY_SEND_USER, sender);

        this.receiveUser = receiver;
        put(KEY_RECEIVE_USER, receiver);

        this.share = share;
        put(KEY_SHARE, share);

        this.typeText = typeText;
        put(KEY_TYPE_TEXT, typeText);
    }


    public User getSendUser() {
        return (User) getParseUser(KEY_SEND_USER);
    }

    public void setSendUser(User user) {
        this.sendUser = user;
        put(KEY_SEND_USER, user);
    }

    public User getReceiveUser() {
        return (User) getParseUser(KEY_RECEIVE_USER);
    }

    public void setReceiveUser(User user) {
        this.receiveUser = user;
        put(KEY_RECEIVE_USER, user);
    }

    public String getType() {
        return getString(KEY_TYPE);
    }

    public void setType(String type) {
        this.type = type;
        put(KEY_TYPE, type);
    }

    public Share getShare() {
        return (Share) getParseObject(KEY_SHARE);
    }

    public void setShare(Share share) {
        this.share = share;
        put(KEY_SHARE, share);
    }

    public String getTypeText() {
        return getString(KEY_TYPE_TEXT);
    }

    public void setTypeText(String typeText) {
        this.typeText = typeText;
        put(KEY_TYPE_TEXT, typeText);
    }


    public String notificationText(String type) {
        if (type.equals("REACTION")) {
            return " reacted to your post";
        } else {
            return " commented on your post";
        }
    }

    public String getRelativeTime() {
        return (String) DateUtils.getRelativeTimeSpanString(getCreatedAt().getTime());
    }

}

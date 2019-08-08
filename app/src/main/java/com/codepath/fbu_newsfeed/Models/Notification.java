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
    public static final int LIMIT = 20;

    public static final String REACTION = "REACTION";
    public static final String COMMENT = "COMMENT";
    public static final String FRIEND_REQUEST = "FRIEND_REQUEST";
    public static final String ACCEPT_REQUEST = "ACCEPT_REQUEST";
    public static final String COMMENT_REACTION = "COMMENT_REACTION";

    private User sendUser;
    private User receiveUser;
    private String type; // REACTION, COMMENT, FRIEND_REQUEST, ACCEPT_REQUEST, COMMENT_REACTION
    private Share share;
    private String typeText;

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

    // for friend notifications
    public Notification(String type, User sender, User receiver, String typeText) {
        super();

        this.type = type;
        put(KEY_TYPE, type);

        this.sendUser = sender;
        put(KEY_SEND_USER, sender);

        this.receiveUser = receiver;
        put(KEY_RECEIVE_USER, receiver);

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
        switch (type) {
            case REACTION:
                return " reacted to your post";
            case COMMENT:
                return " commented on your post";
            case FRIEND_REQUEST:
                return " sent you a friend request";
            case ACCEPT_REQUEST:
                return " accepted your friend request";
            case COMMENT_REACTION:
                return " clapped to your comment on this post";
            default:
                return "";
        }
    }

    public String getRelativeTime() {
        return (String) DateUtils.getRelativeTimeSpanString(getCreatedAt().getTime());
    }

}

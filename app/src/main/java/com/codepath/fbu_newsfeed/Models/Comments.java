package com.codepath.fbu_newsfeed.Models;

public class Comments {
    private String text;
    private User user_id;
    private Shares share_id;

    public Comments(String text, User user_id, Shares share_id) {
        this.text = text;
        this.user_id = user_id;
        this.share_id = share_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser_id() {
        return user_id;
    }

    public void setUser_id(User user_id) {
        this.user_id = user_id;
    }

    public Shares getShare_id() {
        return share_id;
    }

    public void setShare_id(Shares share_id) {
        this.share_id = share_id;
    }
}

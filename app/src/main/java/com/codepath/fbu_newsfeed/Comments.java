package com.codepath.fbu_newsfeed;

public class Comments {
    private String Text;
    private int user_ID;
    private int share_ID;

    public Comments(String text, int user_ID, int share_ID) {
        Text = text;
        this.user_ID = user_ID;
        this.share_ID = share_ID;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public int getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(int user_ID) {
        this.user_ID = user_ID;
    }

    public int getShare_ID() {
        return share_ID;
    }

    public void setShare_ID(int share_ID) {
        this.share_ID = share_ID;
    }
}

package com.codepath.fbu_newsfeed.Models;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Share")
public class Share extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_ARTICLE = "article";
    public static final String KEY_CAPTION = "caption";
    public static final String KEY_IMAGE = "image";
    public static final int LIMIT = 20;

    private ParseUser user;
    private Article article;
    private String caption;

    public Share() {
        super();
    }

    public Share(ParseUser user, Article article, String caption) {
        super();

        this.user = user;
        put(KEY_USER, user);

        this.article = article;
        put(KEY_ARTICLE, article);

        this.caption = caption;
        put(KEY_CAPTION, caption);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        this.user = user;
        put(KEY_USER, user);
    }
    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }


    public Article getArticle() {
        return (Article) getParseObject(KEY_ARTICLE);
    }

    public void setArticle(Article article) {
        this.article = article;
        put(KEY_ARTICLE, article);
    }


    public String getCaption() {
        return getString(KEY_CAPTION);
    }

    public void setCaption(String caption) {
        this.caption = caption;
        put(KEY_CAPTION, caption);
    }

    public String getRelativeTime() {
        return (String) DateUtils.getRelativeTimeSpanString(getCreatedAt().getTime());
    }
    public static class Query extends ParseQuery<Share> {
        public Query() {
            super(Share.class);
        }

        public Query getTop(){
            setLimit(20);
            return this;
        }

        public Query withUser(){
            include("user");
            return this;
        }
    }
}

package com.codepath.fbu_newsfeed.Models;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;

@ParseClassName("Share")
public class Share extends ParseObject implements Serializable {
    private static final String KEY_USER = "user";
    private static final String KEY_ARTICLE = "article";
    private static final String KEY_CAPTION = "caption";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_LIKE_COUNT = "likeCount";
    private static final String KEY_DISLIKE_COUNT = "dislikeCount";
    private static final String KEY_HAPPY_COUNT = "happyCount";
    private static final String KEY_SAD_COUNT = "sadCount";
    private static final String KEY_ANGRY_COUNT = "angryCount";
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

        put(KEY_LIKE_COUNT, 0);
        put(KEY_DISLIKE_COUNT, 0);
        put(KEY_HAPPY_COUNT, 0);
        put(KEY_SAD_COUNT, 0);
        put(KEY_ANGRY_COUNT, 0);
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

    public int incrementCount(String type) {
        switch (type) {
            case "LIKE":
                int likeCount = getCount("LIKE");
                likeCount += 1;
                put(KEY_LIKE_COUNT, likeCount);
                return likeCount;
            case "DISLIKE":
                int dislikeCount = getCount("DISLIKE");
                dislikeCount += 1;
                put(KEY_DISLIKE_COUNT, dislikeCount);
                return dislikeCount;
            case "HAPPY":
                int happyCount = getCount("HAPPY");
                happyCount += 1;
                put(KEY_HAPPY_COUNT, happyCount);
                return happyCount;
            case "SAD":
                int sadCount = getCount("SAD");
                sadCount += 1;
                put(KEY_SAD_COUNT, sadCount);
                return sadCount;
            case "ANGRY":
                int angryCount = getCount("ANGRY");
                angryCount += 1;
                put(KEY_ANGRY_COUNT, angryCount);
                return angryCount;
            default:
                return 0;
        }
    }

    public int decrementCount(String type) {
        switch (type) {
            case "LIKE":
                int likeCount = getCount("LIKE");
                likeCount -= 1;
                put(KEY_LIKE_COUNT, likeCount);
                return likeCount;
            case "DISLIKE":
                int dislikeCount = getCount("DISLIKE");
                dislikeCount -= 1;
                put(KEY_DISLIKE_COUNT, dislikeCount);
                return dislikeCount;
            case "HAPPY":
                int happyCount = getCount("HAPPY");
                happyCount -= 1;
                put(KEY_HAPPY_COUNT, happyCount);
                return happyCount;
            case "SAD":
                int sadCount = getCount("SAD");
                sadCount -= 1;
                put(KEY_SAD_COUNT, sadCount);
                return sadCount;
            case "ANGRY":
                int angryCount = getCount("ANGRY");
                angryCount -= 1;
                put(KEY_ANGRY_COUNT, angryCount);
                return angryCount;
            default:
                return 0;
        }
    }

    public int getCount(String type) {
        switch (type) {
            case "LIKE":
                return (int) getNumber(KEY_LIKE_COUNT);
            case "DISLIKE":
                return (int) getNumber(KEY_DISLIKE_COUNT);
            case "HAPPY":
                return (int) getNumber(KEY_HAPPY_COUNT);
            case "SAD":
                return (int) getNumber(KEY_SAD_COUNT);
            case "ANGRY":
                return (int) getNumber(KEY_ANGRY_COUNT);
            default:
                return 0;
        }
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

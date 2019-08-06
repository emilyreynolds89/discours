package com.codepath.fbu_newsfeed.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Annotation")
public class Annotation extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_ARTICLE = "article";
    public static final String KEY_POSITIONX = "positionX";
    public static final String KEY_POSITIONY = "positionY";
    public static final String KEY_TEXT = "text";

    public Annotation() { super(); }

    public Annotation(ParseUser user, Article article, int positionX, int positionY, String text) {
        put(KEY_USER, user);
        put(KEY_ARTICLE, article);
        put(KEY_POSITIONX, positionX);
        put(KEY_POSITIONY, positionY);
        put(KEY_TEXT, text);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public Article getArticle() {
        return (Article) getParseObject(KEY_ARTICLE);
    }

    public void setArticle(Article article) {
        put(KEY_ARTICLE, article);
    }

    public int getPositionX() {
        return (int) getNumber(KEY_POSITIONX);
    }

    public void setPositionX(int positionX) {
        put(KEY_POSITIONX, positionX);
    }

    public int getPositionY() {
        return (int) getNumber(KEY_POSITIONY);
    }

    public void setPositionY(int positionY) {
        put(KEY_POSITIONY, positionY);
    }

    public String getText() {
        return getString(KEY_TEXT);
    }

    public void setText(String text) {
        put(KEY_TEXT, text);
    }


}

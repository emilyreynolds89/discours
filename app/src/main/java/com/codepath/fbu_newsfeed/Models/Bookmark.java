package com.codepath.fbu_newsfeed.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Bookmark")
public class Bookmark extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_ARTICLE = "article";

    public Bookmark() { super(); }

    public Bookmark(ParseUser user, Article article) {
        super();
        put(KEY_USER, user);
        put(KEY_ARTICLE, article);
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


}

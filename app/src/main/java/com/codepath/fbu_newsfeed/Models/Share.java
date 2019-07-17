package com.codepath.fbu_newsfeed.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Share")
public class Share extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_ARTICLE = "article";
    public static final String KEY_CAPTION = "caption";

    private ParseUser user;
    private Article article;
    private String caption;

    public Share() {
        super();
    }

    public Share(ParseUser user, Article article, String caption) {
        super();

        this.user = user;
        this.article = article;
        this.caption = caption;
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        this.user = user;
        put(KEY_USER, user);
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
}

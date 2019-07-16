package com.codepath.fbu_newsfeed.Models;

public class Shares {

    private User user_id;
    private Article article_id;
    private String caption;

    public Shares(User user_id, Article article_id, String caption) {
        this.user_id = user_id;
        this.article_id = article_id;
        this.caption = caption;
    }

    public User getUser_id() {
        return user_id;
    }

    public void setUser_id(User user_id) {
        this.user_id = user_id;
    }


    public Article getArticle_id() {
        return article_id;
    }

    public void setArticle_id(Article article_id) {
        this.article_id = article_id;
    }


    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}

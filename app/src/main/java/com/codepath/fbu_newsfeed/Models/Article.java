package com.codepath.fbu_newsfeed.Models;

import java.io.File;

public class Article {

    private String url;
    private String title;
    private File image;
    private String summary;
    private Bias bias;
    private Float truth;
    private Tag tag;

    public Article(String url, String title, File image, String summary, Bias bias, Float truth, Tag tag) {
        this.url = url;
        this.title = title;
        this.image = image;
        this.summary = summary;
        this.bias = bias;
        this.truth = truth;
        this.tag = tag;
    }

    enum Bias {
        LIBERAL, SLIGHTLY_LIBERAL, MODERATE, SLIGHTLY_CONSERVATIVE, CONSERVATIVE;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }


    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }


    public Bias getBias() {
        return bias;
    }

    public void setBias(Bias bias) {
        this.bias = bias;
    }


    public Float getTruth() {
        return truth;
    }

    public void setTruth(Float truth) {
        this.truth = truth;
    }


    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

}

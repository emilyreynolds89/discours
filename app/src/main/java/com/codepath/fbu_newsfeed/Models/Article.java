package com.codepath.fbu_newsfeed.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.File;

@ParseClassName("Article")
public class Article extends ParseObject {

    public static final String KEY_URL = "URL";
    public static final String KEY_TITLE = "title";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_SUMMARY = "summary";
    public static final String KEY_BIAS = "bias";
    public static final String KEY_TRUTH = "truth";

    private String url;
    private String title;
    private ParseFile image;
    private String summary;
    private Bias bias;
    private Float truth;
    private Tag tag;

    public Article() {
        super();
    }

    public Article(String url, String title, ParseFile image, String summary, Bias bias, Float truth) {
        super();
        this.url = url;
        put(KEY_URL, url);
        this.title = title;
        put(KEY_TITLE, title);
        this.image = image;
        put(KEY_IMAGE, image);
        this.summary = summary;
        put(KEY_SUMMARY, summary);
        this.bias = bias;
        put(KEY_BIAS, biasEnumToInt(bias));
        this.truth = truth;
        put(KEY_TRUTH, truth);
        //this.tag = tag;
    }

    enum Bias {
        LIBERAL, SLIGHTLY_LIBERAL, MODERATE, SLIGHTLY_CONSERVATIVE, CONSERVATIVE;
    }


    public String getUrl() {
        return getString(KEY_URL);
    }

    public void setUrl(String url) {
        this.url = url;
        put(KEY_URL, url);
    }


    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        this.title = title;
        put(KEY_TITLE, title);
    }


    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        this.image = image;
        put(KEY_IMAGE, image);
    }


    public String getSummary() {
        return getString(KEY_SUMMARY);
    }

    public void setSummary(String summary) {
        this.summary = summary;
        put(KEY_SUMMARY, summary);
    }


    public Bias getBias() {
        return biasIntToEnum((int) getNumber(KEY_BIAS));
    }

    public void setBias(Bias bias) {
        this.bias = bias;
        put(KEY_BIAS, biasEnumToInt(bias));
    }


    public Float getTruth() {
        return truth;
    }

    public void setTruth(Float truth) {
        this.truth = truth;
        put(KEY_TRUTH, truth);
    }

//
//    public Tag getTag() {
//        return tag;
//    }
//
//    public void setTag(Tag tag) {
//        this.tag = tag;
//    }

    private Bias biasIntToEnum(int i) {
        Bias res;
        switch(i) {
            case 1:
                res = Bias.LIBERAL;
                break;
            case 2:
                res = Bias.SLIGHTLY_LIBERAL;
                break;
            case 3:
                res = Bias.MODERATE;
                break;
            case 4:
                res = Bias.SLIGHTLY_CONSERVATIVE;
                break;
            case 5:
                res = Bias.CONSERVATIVE;
                break;
            default:
                res = Bias.MODERATE;
        }
        return res;
    }

    private int biasEnumToInt(Bias bias) {
        int res;
        switch(bias) {
            case LIBERAL:
                res = 1;
                break;
            case SLIGHTLY_LIBERAL:
                res = 2;
                break;
            case MODERATE:
                res = 3;
                break;
            case SLIGHTLY_CONSERVATIVE:
                res = 4;
                break;
            case CONSERVATIVE:
                res = 5;
                break;
            default:
                res = 3;
        }
        return res;
    }

}

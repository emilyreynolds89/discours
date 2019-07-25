package com.codepath.fbu_newsfeed.Models;

import android.graphics.Color;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.Serializable;

@ParseClassName("Article")
public class Article extends ParseObject implements Serializable {

    public static final String KEY_URL = "URL";
    public static final String KEY_TITLE = "title";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_SUMMARY = "summary";
    public static final String KEY_BIAS = "bias";
    public static final String KEY_TRUTH = "truth";
    public static final String KEY_SOURCE = "source";
    public static final String KEY_TAG = "tag";
    public static final String KEY_COUNT = "count";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final int LIMIT = 20;
    public static final int liberalColor = Color.parseColor("#3385FF");
    public static final int slightlyLiberalColor = Color.parseColor("#ABCDFF");
    public static final int moderateColor = Color.parseColor("#E0E0E0");
    public static final int slightlyConservativeColor = Color.parseColor("#FFA7A7");
    public static final int conservativeColor = Color.parseColor("#FF6C6C");

    private String url;
    private String title;
    private ParseFile image;
    private String summary;
    private Bias bias;
    private String truth;
    private String tag;
    private String source;
    private int count;

    public Article() {
        super();
    }

    public Article(String url, String title, ParseFile image,
                   String summary, Bias bias, String truth, String source, String tag) {
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
        this.tag = tag;
        put(KEY_TAG, tag);
        this.source = source;
        put(KEY_SOURCE, source);
        this.count = 0;
        put(KEY_COUNT, count);
    }

    public enum Bias {
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

    public int getIntBias() { return (int) getNumber(KEY_BIAS); }

    public void setBias(Bias bias) {
        this.bias = bias;
        put(KEY_BIAS, biasEnumToInt(bias));
    }


    public String getTruth() {
        return getString(KEY_TRUTH);
    }

    public void setTruth(String truth) {
        this.truth = truth;
        put(KEY_TRUTH, truth);
    }

    public String getSource() {
        return getString(KEY_SOURCE);
    }

    public void setSource(String source) {
        this.source = source;
        put(KEY_SOURCE, source);
    }

    public String getTag() {
        return getString(KEY_TAG);
    }

    public void setTag(String tag) {
        this.tag = tag;
        put(KEY_TAG, tag);
    }

    public int getCount() {
        return (int) getNumber(KEY_COUNT);
    }

    public void setCount(int count) {
        this.count = count;
        put(KEY_COUNT, count);
    }

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

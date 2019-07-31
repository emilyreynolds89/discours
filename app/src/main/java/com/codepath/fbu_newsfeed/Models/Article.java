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
    public static final String KEY_SOURCE = "sourceObject";
    public static final String KEY_TAG = "tag";
    public static final String KEY_IMAGEURL = "imageUrl";
    public static final int LIMIT = 20;
    public static final String KEY_COUNT = "count";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final int liberalColor = Color.parseColor("#3385FF");
    public static final int slightlyLiberalColor = Color.parseColor("#ABCDFF");
    public static final int moderateColor = Color.parseColor("#E0E0E0");
    public static final int slightlyConservativeColor = Color.parseColor("#FFA7A7");
    public static final int conservativeColor = Color.parseColor("#FF6C6C");

    private String url;
    private String title;
    private ParseFile image;
    private String summary;
    private Bias.BiasType bias;
    private Fact.TruthLevel truth;
    private String tag;
    private Source source;
    private String imageUrl;
    private int count;

    public Article() {
        super();
    }

    public Article(String url, String title, ParseFile image,
                   String summary, Bias.BiasType bias, Fact.TruthLevel truth, Source source, String tag) {
        super();
        this.url = url;
        put(KEY_URL, url);
        this.title = title;
        put(KEY_TITLE, title);
        this.imageUrl = null;

        this.image = image;
        put(KEY_IMAGE, image);
        this.summary = summary;
        put(KEY_SUMMARY, summary);
        this.bias = bias;
        put(KEY_BIAS, Bias.enumToInt(bias));
        this.truth = truth;
        put(KEY_TRUTH, Fact.enumToString(truth));
        this.tag = tag;
        put(KEY_TAG, tag);
        this.source = source;
        put(KEY_SOURCE, source);
        this.count = 0;
        put(KEY_COUNT, count);
    }

    public Article(String url, String title, String imageUrl,
                   String summary, Bias.BiasType bias, Fact.TruthLevel truth, Source source, String tag) {
        super();
        this.url = url;
        put(KEY_URL, url);
        this.title = title;
        put(KEY_TITLE, title);
        this.image = null;

        this.imageUrl = imageUrl;
        put(KEY_IMAGEURL, imageUrl);
        this.summary = summary;
        put(KEY_SUMMARY, summary);
        this.bias = bias;
        put(KEY_BIAS, Bias.enumToInt(bias));
        this.truth = truth;
        put(KEY_TRUTH, Fact.enumToString(truth));
        this.tag = tag;
        put(KEY_TAG, tag);
        this.source = source;
        put(KEY_SOURCE, source);
        this.count = 0;
        put(KEY_COUNT, 0);
    }

    public String getUrl() {
        return getString(KEY_URL);
    }

    public void setUrl(String url) {
        this.url = url;
        put(KEY_URL, url);
    }

    public String getImageUrl() {
        return getString(KEY_IMAGEURL);
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        put(KEY_IMAGEURL, imageUrl);
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


    public Bias.BiasType getBias() {
        return Bias.intToEnum((int) getNumber(KEY_BIAS));
    }

    public int getIntBias() { return (int) getNumber(KEY_BIAS); }

    public void setBias(Bias.BiasType bias) {
        this.bias = bias;
        put(KEY_BIAS, Bias.enumToInt(bias));
    }


    public Fact.TruthLevel getTruth() {
        return Fact.stringToEnum(getString(KEY_TRUTH));
    }

    public void setTruth(Fact.TruthLevel truth) {
        this.truth = truth;
        put(KEY_TRUTH, Fact.enumToString(truth));
    }

    public Source getSource() {
        return (Source) getParseObject(KEY_SOURCE);
    }

    public void setSource(Source source) {
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



}

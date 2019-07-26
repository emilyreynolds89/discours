package com.codepath.fbu_newsfeed.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Source")
public class Source extends ParseObject {
    public static final String KEY_BIAS = "bias";
    public static final String KEY_FACT = "fact";
    public static final String KEY_NAME = "name";

    int bias;
    String fact;
    String name;

    public Source() {
        super();
    }

    public Source(int bias, String fact, String name) {
        this.bias = bias;
        this.fact = fact;
        this.name = name;
    }

    public int getBias() {
        return (int) getNumber(KEY_BIAS);
    }

    public void setBias(int bias) {
        this.bias = bias;
        put(KEY_BIAS, bias);
    }

    public String getFact() {
        return getString(KEY_FACT);
    }

    public void setFact(String fact) {
        this.fact = fact;
        put(KEY_FACT, fact);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        this.name = name;
        put(KEY_NAME, name);
    }
}

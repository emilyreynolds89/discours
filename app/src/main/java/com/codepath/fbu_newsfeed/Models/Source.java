package com.codepath.fbu_newsfeed.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Source")
public class Source extends ParseObject {
    public static final String KEY_BIAS = "bias";
    public static final String KEY_FACT = "fact";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_FULLNAME = "fullName";
    public static final String KEY_URLMATCH = "urlMatch";
    public static final String KEY_LOGO = "logo";

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

    public String getDescription() { return getString(KEY_DESCRIPTION); }

    public void setDescription(String desc) {
        put(KEY_DESCRIPTION, desc);
    }

    public String getFullName() { return getString(KEY_FULLNAME); }

    public void setFullName(String fullName) {
        put(KEY_FULLNAME, fullName);
    }

    public String getUrlMatch() { return getString(KEY_URLMATCH); }

    public ParseFile getLogo() { return getParseFile(KEY_LOGO); }


}

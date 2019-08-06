package com.codepath.fbu_newsfeed.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Quiz")
public class Quiz extends ParseObject {

    public static final String KEY_IMAGE = "image";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_FAKE = "fake";
    public static final String KEY_TITLE = "newsTitle";

    private ParseFile image;
    private String message;
    private boolean fake;
    private String newsTitle;

    public Quiz() {
        super();
    }

    public Quiz(ParseFile image, String message, boolean fake, String newsTitle) {
        super();

        this.image = image;
        put(KEY_IMAGE, image);
        this.message = message;
        put(KEY_MESSAGE, message);
        this.fake = fake;
        put(KEY_FAKE, fake);
        this.newsTitle = newsTitle;
        put(KEY_TITLE, newsTitle);
    }

    public ParseFile getImage() {
        return (ParseFile) getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image) {
        this.image = image;
        put(KEY_IMAGE, image);
    }

    public String getMessage() {
        return getString(KEY_MESSAGE);
    }

    public void setMessage(String message) {
        this.message = message;
        put(KEY_MESSAGE, message);
    }

    public boolean getFake() {
        return getBoolean(KEY_FAKE);
    }

    public void setFake(boolean fake) {
        this.fake = fake;
        put(KEY_FAKE, fake);
    }

    public String getNewsTitle() {
        return getString(KEY_TITLE);
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
        put(KEY_TITLE, newsTitle);
    }

}

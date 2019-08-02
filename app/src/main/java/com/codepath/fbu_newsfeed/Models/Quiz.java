package com.codepath.fbu_newsfeed.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Quiz")
public class Quiz extends ParseObject {

    public static final String KEY_ORDER = "order";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_FAKE = "fake";

    private int order;
    private ParseFile image;
    private String message;
    private boolean fake;

    public Quiz() {
        super();
    }

    public Quiz(int order, ParseFile image, String message, boolean fake) {
        super();

        this.order = order;
        put(KEY_ORDER, order);
        this.image = image;
        put(KEY_IMAGE, image);
        this.message = message;
        put(KEY_MESSAGE, message);
        this.fake = fake;
        put(KEY_FAKE, fake);
    }

    public int getOrder() {
        return (int) getNumber(KEY_ORDER);
    }

    public void setOrder(int order) {
        this.order = order;
        put(KEY_ORDER, order);
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

}

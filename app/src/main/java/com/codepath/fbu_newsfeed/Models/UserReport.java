package com.codepath.fbu_newsfeed.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("UserReport")
public class UserReport extends ParseObject {
    public static final String KEY_REPORTER = "reporter";
    public static final String KEY_OFFENDER = "offender";
    public static final String KEY_TYPE = "type";
    public static final String KEY_COMMENT = "comment";

    public UserReport() {}

    public UserReport(ParseUser reporter, ParseUser offender, String type, String comment) {
        super();
        put(KEY_REPORTER, reporter);
        put(KEY_OFFENDER, offender);
        put(KEY_TYPE, type);
        put(KEY_COMMENT, comment);
    }

    public ParseUser getReporter() {
        return getParseUser(KEY_REPORTER);
    }

    public void setReporter(ParseUser user) {
        put(KEY_REPORTER, user);
    }

    public ParseUser getOffender() {
        return getParseUser(KEY_OFFENDER);
    }

    public void setOffender(ParseUser user) {
        put(KEY_OFFENDER, user);
    }

    public String getType() {
        return getString(KEY_TYPE);
    }

    public void setKeyType(String s) {
        put(KEY_TYPE, s);
    }

    public String getComment() {
        return getString(KEY_COMMENT);
    }

    public void setComment(String s) {
        put(KEY_COMMENT, s);
    }

}

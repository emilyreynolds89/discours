package com.codepath.fbu_newsfeed.Models;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Reaction")
public class Reaction extends ParseObject {

    // types are : LIKE, DISLIKE, HAPPY, SAD, ANGRY

    public static final String KEY_USER = "user";
    public static final String KEY_SHARE = "share";
    public static final String KEY_TYPE = "type";

    private ParseUser user;
    private Share share;
    private String type;

    public Reaction() {
        super();
    }

    public Reaction(ParseUser user, Share share, String type) {
        super();

        this.user = user;
        this.share = share;
        this.type = type;
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        this.user = user;
        put(KEY_USER, user);
    }

    public Share getShare() {
        return (Share) getParseObject(KEY_SHARE) ;
    }

    public void setShare(Share share) {
        this.share = share;
        put(KEY_SHARE, share);
    }

    public String getType() {
        return getString(KEY_TYPE);
    }

    public void setType(String type) {
        this.type = type;
        put(KEY_TYPE, type);
    }

    public ArrayList<Reaction> queryReactions(Share share, String type) {
        ParseQuery<Reaction> queryReaction = new ParseQuery<Reaction>(Reaction.class);
        queryReaction.include(KEY_USER);
        queryReaction.include(KEY_SHARE);
        queryReaction.include(KEY_TYPE);
        queryReaction.whereEqualTo(KEY_SHARE, share);
        queryReaction.whereEqualTo(KEY_TYPE, type);

        final ArrayList<Reaction> reactions = new ArrayList<>();

        queryReaction.findInBackground(new FindCallback<Reaction>() {
            @Override
            public void done(List<Reaction> newReactions, ParseException e) {
                if (e == null) {
                    Log.e("Reaction", "Error in loading reaction count");
                    e.printStackTrace();
                    return;
                } else {
                    reactions.addAll(newReactions);
                }
            }
        });

        return reactions;
    }

}

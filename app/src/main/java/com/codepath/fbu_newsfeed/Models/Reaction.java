package com.codepath.fbu_newsfeed.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Reaction")
public class Reaction extends ParseObject {

    // types are : LIKE, DISLIKE, HAPPY, SAD, ANGRY
    public static final ReactionType TYPES[] = {ReactionType.LIKE, ReactionType.DISLIKE, ReactionType.HAPPY, ReactionType.SAD, ReactionType.ANGRY};

    public static final String KEY_USER = "user";
    public static final String KEY_SHARE = "share";
    public static final String KEY_TYPE = "type";

    private ParseUser user;
    private Share share;
    private ReactionType type;

    public enum ReactionType {
        LIKE, DISLIKE, HAPPY, SAD, ANGRY
    }

    public Reaction() {
        super();
    }

    public Reaction(ParseUser user, Share share, ReactionType type) {
        super();

        this.user = user;
        put(KEY_USER, user);
        this.share = share;
        put(KEY_SHARE, share);
        this.type = type;
        put(KEY_TYPE, enumToString(type));
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

    public void setType(ReactionType type) {
        this.type = type;
        put(KEY_TYPE, enumToString(type));
    }

    public static String enumToString(ReactionType type) {
        switch (type) {
            case LIKE:
                return "LIKE";
            case DISLIKE:
                return "DISLIKE";
            case HAPPY:
                return "HAPPY";
            case SAD:
                return "SAD";
            case ANGRY:
                return "ANGRY";
            default:
                return "LIKE";
        }
    }

    public static ReactionType stringToEnum(String type) {
        switch (type) {
            case "LIKE":
                return ReactionType.LIKE;
            case "DISLIKE":
                return ReactionType.DISLIKE;
            case "HAPPY":
                return ReactionType.HAPPY;
            case "SAD":
                return ReactionType.SAD;
            case "ANGRY":
                return ReactionType.ANGRY;
            default:
                return null;
        }
    }

}

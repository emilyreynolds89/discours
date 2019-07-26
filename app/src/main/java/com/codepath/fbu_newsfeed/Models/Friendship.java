package com.codepath.fbu_newsfeed.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Friendship")
public class Friendship extends ParseObject {
    public static final String KEY_USER1 = "user1";
    public static final String KEY_USER2 = "user2";
    public static final String KEY_STATE = "state";

    private ParseUser user1; // NOTE: user1 is always the requester
    private ParseUser user2; // NOTE: user2 is always the requested one
    private State state;

    public Friendship() {
        super();
    }

    public Friendship(ParseUser user1, ParseUser user2, State state) {
        super();
        this.user1 = user1;
        put(KEY_USER1, user1);

        this.user2 = user2;
        put(KEY_USER2, user2);

        this.state = state;
        put(KEY_STATE, stateEnumToInt(state));

    }

    public ParseUser getUser1() {
        return getParseUser(KEY_USER1);
    }

    public ParseUser getUser2() {
        return getParseUser(KEY_USER2);
    }

    public void setUser1(ParseUser user) {
        user1 = user;
        put(KEY_USER1, user);
    }

    public void setUser2(ParseUser user) {
        user2 = user;
        put(KEY_USER2, user);
    }

    public enum State {
        Requested, Accepted
    }

    public State getState() {
        return stateIntToEnum((int) getNumber(KEY_STATE));
    }

    public void setState(State state) {
        this.state = state;
        put(KEY_STATE, stateEnumToInt(state));
    }

    public boolean isInvolved(ParseUser user ) {
        return user.getObjectId().equals(getUser1().getObjectId()) || user.getObjectId().equals(getUser2().getObjectId());
    }

    public boolean isUser1(ParseUser user) {
        return user.getObjectId().equals(getUser1().getObjectId()) ;
    }

    public static int stateEnumToInt(State state) {
        switch(state) {
            case Requested:
                return 1;
            case Accepted:
                return 2;
            default:
                return 1;
        }
    }

    private static State stateIntToEnum(int i) {
        switch(i) {
            case 1:
                return State.Requested;
            case 2:
                return State.Accepted;
            default:
                return State.Requested;

        }
    }
}

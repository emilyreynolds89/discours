package com.codepath.fbu_newsfeed;

public class Friendship {
    private int UserID_1;
    private int UserID_2;
    private State state;

    private Friendship(int UserID_1, int UserID_2, State stete) {
        this.UserID_1 = UserID_1;
        this.UserID_2 = UserID_2;
    }

    public int getUserID_1() {
        return UserID_1;
    }

    public int getUserID_2() {
        return UserID_2;
    }

    public void setUserID_1(int userID_1) {
        UserID_1 = userID_1;
    }

    public void setUserID_2(int userID_2) {
        UserID_2 = userID_2;
    }

    enum State {
        Requested, Accepeted;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}

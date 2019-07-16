package com.codepath.fbu_newsfeed;

import java.lang.reflect.Type;

public class Reactor {
    private int User_ID;
    private int Share_ID;
    private Type type;

    public Reactor(int user_ID, int share_ID, Type type) {
        User_ID = user_ID;
        Share_ID = share_ID;
        this.type = type;
    }

    public int getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(int user_ID) {
        User_ID = user_ID;
    }

    public int getShare_ID() {
        return Share_ID;
    }

    public void setShare_ID(int share_ID) {
        Share_ID = share_ID;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}

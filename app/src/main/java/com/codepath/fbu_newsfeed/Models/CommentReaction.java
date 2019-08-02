package com.codepath.fbu_newsfeed.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("CommentReaction")
public class CommentReaction extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_COMMENT = "comment";

    private User user;
    private Comment comment;

    public CommentReaction() {
        super();
    }

    public CommentReaction(User user, Comment comment){
        super();

        this.user = user;
        put(KEY_USER, user);
        this.comment = comment;
        put(KEY_COMMENT, comment);
    }

    public User getUser() {
        return (User) getParseObject(KEY_USER);
    }

    public void setUser(User user) {
        this.user = user;
        put(KEY_USER, user);
    }

    public Comment getComment() {
        return (Comment) getParseObject(KEY_COMMENT);
    }

    public void setComment(Comment comment) {
        this.comment = comment;
        put(KEY_COMMENT, comment);
    }


}
